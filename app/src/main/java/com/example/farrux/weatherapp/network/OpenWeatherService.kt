package com.example.farrux.weatherapp.network

import com.example.farrux.weatherapp.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherService {
    @GET("data/3.0/onecall")
    suspend fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String,
        @Query("lang") lang: String,
        @Query("appid") apiKey: String
    ): WeatherResponse
}
