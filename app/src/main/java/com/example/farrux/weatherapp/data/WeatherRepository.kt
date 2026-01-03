package com.example.farrux.weatherapp.data

import com.example.farrux.weatherapp.BuildConfig
import com.example.farrux.weatherapp.model.DailyWeather
import com.example.farrux.weatherapp.model.ForecastApiResponse
import com.example.farrux.weatherapp.model.GeoLocation
import com.example.farrux.weatherapp.model.ForecastItem
import com.example.farrux.weatherapp.model.HourlyWeather
import com.example.farrux.weatherapp.model.TemperatureRange
import com.example.farrux.weatherapp.model.WeatherResponse
import com.example.farrux.weatherapp.network.OpenWeatherService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Locale
import com.google.gson.Gson

class WeatherRepository {

    private val service: OpenWeatherService by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
        Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenWeatherService::class.java)
    }
    private val gson = Gson()

    data class WeatherResult(val data: WeatherResponse?, val error: String? = null)

    suspend fun getWeather(
        latitude: Double,
        longitude: Double,
        units: String,
        language: String
    ): WeatherResult = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.OPEN_WEATHER_API_KEY.trim()
        if (apiKey.isBlank()) {
            return@withContext WeatherResult(null, "API kaliti topilmadi. local.properties ga qo'shing.")
        }
        return@withContext try {
            val current = service.getCurrent(latitude, longitude, units, language, apiKey)
            val forecast = service.getForecast(latitude, longitude, units, language, apiKey)

            if (!current.isSuccessful) {
                val msgBody = current.errorBody()?.string()?.takeIf { it.isNotBlank() } ?: ""
                val msg = "OpenWeather current xato: ${current.code()} ${current.message()} $msgBody".trim()
                return@withContext WeatherResult(null, msg)
            }
            if (!forecast.isSuccessful) {
                val msgBody = forecast.errorBody()?.string()?.takeIf { it.isNotBlank() } ?: ""
                val msg = "OpenWeather forecast xato: ${forecast.code()} ${forecast.message()} $msgBody".trim()
                return@withContext WeatherResult(null, msg)
            }

            val currentBody = current.body()
            val forecastBody = forecast.body()
            if (currentBody == null || forecastBody == null) {
                return@withContext WeatherResult(null, "Bo'sh javob qaytdi")
            }

            val mapped = mapToWeatherResponse(currentBody, forecastBody)
            WeatherResult(mapped, null)
        } catch (e: Exception) {
            WeatherResult(null, e.localizedMessage ?: "Tarmoq xatosi")
        }
    }

    private fun mapToWeatherResponse(
        current: com.example.farrux.weatherapp.model.CurrentWeatherApiResponse,
        forecast: ForecastApiResponse
    ): WeatherResponse {
        val hourly = forecast.list.map { toHourly(it) }
        val daily = toDaily(forecast.list)

        return WeatherResponse(
            timezone = current.name ?: "Current location",
            current = com.example.farrux.weatherapp.model.CurrentWeather(
                dt = current.dt,
                temp = current.main.temp,
                feelsLike = current.main.feels_like,
                pressure = current.main.pressure,
                humidity = current.main.humidity,
                visibility = current.visibility,
                windSpeed = current.wind?.speed ?: 0.0,
                uvi = 0.0,
                dewPoint = 0.0,
                clouds = 0,
                weather = current.weather
            ),
            hourly = hourly,
            daily = daily
        )
    }

    private fun toHourly(item: ForecastItem): HourlyWeather {
        return HourlyWeather(
            dt = item.dt,
            temp = item.main.temp,
            windSpeed = item.wind?.speed ?: 0.0,
            pressure = item.main.pressure,
            humidity = item.main.humidity,
            visibility = item.visibility,
            precipitationProbability = item.pop,
            weather = item.weather
        )
    }

    private fun toDaily(items: List<ForecastItem>): List<DailyWeather> {
        if (items.isEmpty()) return emptyList()
        val grouped = items.groupBy { dayKey(it.dt) }
        return grouped.values.take(7).map { dayItems ->
            val temps = dayItems.map { it.main.temp }
            DailyWeather(
                dt = dayItems.first().dt,
                temp = TemperatureRange(min = temps.minOrNull() ?: 0.0, max = temps.maxOrNull() ?: 0.0),
                windSpeed = dayItems.first().wind?.speed ?: 0.0,
                pressure = dayItems.first().main.pressure,
                humidity = dayItems.first().main.humidity,
                weather = dayItems.first().weather,
                pop = dayItems.map { it.pop }.average()
            )
        }
    }

    private fun dayKey(timestamp: Long): String {
        val locale = Locale.getDefault()
        val formatter = java.text.SimpleDateFormat("yyyy-MM-dd", locale)
        return formatter.format(java.util.Date(timestamp * 1000))
    }

    suspend fun geocodeCity(city: String, apiKey: String): Result<GeoLocation> =
        withContext(Dispatchers.IO) {
            try {
                val response = service.geocode(city, 1, apiKey)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (!body.isNullOrEmpty()) {
                        Result.success(body.first())
                    } else {
                        Result.failure(Exception("Manzil topilmadi"))
                    }
                } else {
                    Result.failure(Exception("Geocoding xato: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    fun serialize(response: WeatherResponse): String = gson.toJson(response)
    fun deserialize(json: String): WeatherResponse? = try {
        gson.fromJson(json, WeatherResponse::class.java)
    } catch (e: Exception) {
        null
    }
}
