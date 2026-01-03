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
