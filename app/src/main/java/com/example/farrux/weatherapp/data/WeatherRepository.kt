package com.example.farrux.weatherapp.data

import com.example.farrux.weatherapp.BuildConfig
import com.example.farrux.weatherapp.model.WeatherResponse
import com.example.farrux.weatherapp.network.OpenWeatherService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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

    suspend fun getWeather(
        latitude: Double,
        longitude: Double,
        units: String,
        language: String
    ): WeatherResponse? = withContext(Dispatchers.IO) {
        if (BuildConfig.OPEN_WEATHER_API_KEY.isBlank()) return@withContext null
        try {
            service.getWeather(
                lat = latitude,
                lon = longitude,
                units = units,
                lang = language,
                apiKey = BuildConfig.OPEN_WEATHER_API_KEY
            )
        } catch (e: Exception) {
            null
        }
    }
}
