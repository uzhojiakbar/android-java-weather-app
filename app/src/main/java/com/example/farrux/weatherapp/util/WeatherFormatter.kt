package com.example.farrux.weatherapp.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

object WeatherFormatter {
    fun formatTemperature(value: Double, unit: String): String {
        val symbol = if (unit == "imperial") "°F" else "°C"
        return "${value.roundToInt()}$symbol"
    }

    fun formatWindSpeed(speedMetersPerSecond: Double, windUnit: String): String {
        val speed = when (windUnit) {
            "km/h" -> speedMetersPerSecond * 3.6
            "mph" -> speedMetersPerSecond * 2.23694
            else -> speedMetersPerSecond
        }
        return "${speed.roundToInt()} $windUnit"
    }

    fun formatDateTime(timestampSeconds: Long, pattern: String, locale: Locale): String {
        val date = Date(timestampSeconds * 1000)
        return SimpleDateFormat(pattern, locale).format(date)
    }

    fun formatVisibility(meters: Int): String {
        val kilometers = meters / 1000.0
        return "${String.format(Locale.getDefault(), "%.1f", kilometers)} km"
    }

    fun formatPrecipitationProbability(probability: Double): String {
        return "${(probability * 100).roundToInt()}%"
    }
}
