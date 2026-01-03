package com.example.farrux.weatherapp.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class WeatherResponse(
    val timezone: String? = null,
    val current: CurrentWeather? = null,
    val hourly: List<HourlyWeather> = emptyList(),
    val daily: List<DailyWeather> = emptyList()
) : Parcelable

@Parcelize
data class CurrentWeather(
    val dt: Long = 0L,
    val temp: Double = 0.0,
    @SerializedName("feels_like")
    val feelsLike: Double = 0.0,
    val pressure: Int = 0,
    val humidity: Int = 0,
    val visibility: Int = 0,
    @SerializedName("wind_speed")
    val windSpeed: Double = 0.0,
    val uvi: Double = 0.0,
    @SerializedName("dew_point")
    val dewPoint: Double = 0.0,
    val clouds: Int = 0,
    val weather: List<WeatherCondition> = emptyList()
) : Parcelable

@Parcelize
data class HourlyWeather(
    val dt: Long = 0L,
    val temp: Double = 0.0,
    @SerializedName("wind_speed")
    val windSpeed: Double = 0.0,
    val pressure: Int = 0,
    val humidity: Int = 0,
    val visibility: Int = 0,
    @SerializedName("pop")
    val precipitationProbability: Double = 0.0,
    val weather: List<WeatherCondition> = emptyList()
) : Parcelable

@Parcelize
data class DailyWeather(
    val dt: Long = 0L,
    val temp: TemperatureRange? = null,
    @SerializedName("wind_speed")
    val windSpeed: Double = 0.0,
    val pressure: Int = 0,
    val humidity: Int = 0,
    val weather: List<WeatherCondition> = emptyList(),
    val pop: Double = 0.0
) : Parcelable

@Parcelize
data class TemperatureRange(
    val min: Double = 0.0,
    val max: Double = 0.0
) : Parcelable

@Parcelize
data class WeatherCondition(
    val id: Int = 0,
    val main: String = "",
    val description: String = "",
    val icon: String = ""
) : Parcelable

// API models for /weather and /forecast
data class CurrentWeatherApiResponse(
    val coord: Coord = Coord(),
    val weather: List<WeatherCondition> = emptyList(),
    val main: MainInfo = MainInfo(),
    val wind: WindInfo? = null,
    val visibility: Int = 10000,
    val dt: Long = 0L,
    val name: String? = null
)

data class ForecastApiResponse(
    val list: List<ForecastItem> = emptyList()
)

data class ForecastItem(
    val dt: Long = 0L,
    val main: MainInfo = MainInfo(),
    val weather: List<WeatherCondition> = emptyList(),
    val wind: WindInfo? = null,
    val visibility: Int = 10000,
    val pop: Double = 0.0
)

data class Coord(val lat: Double = 0.0, val lon: Double = 0.0)
data class MainInfo(
    val temp: Double = 0.0,
    val feels_like: Double = 0.0,
    val pressure: Int = 0,
    val humidity: Int = 0
)

data class WindInfo(val speed: Double = 0.0)

data class GeoLocation(
    val name: String? = null,
    val lat: Double = 0.0,
    val lon: Double = 0.0,
    val country: String? = null
)
