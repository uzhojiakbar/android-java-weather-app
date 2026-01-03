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

    fun formatPressure(hPa: Int, unit: String): String {
        val value = when (unit) {
            "mmHg" -> hPa * 0.750062
            else -> hPa.toDouble()
        }
        val rounded = if (unit == "mmHg") value.roundToInt() else value.roundToInt()
        return "$rounded $unit"
    }

    fun formatDateTime(timestampSeconds: Long, pattern: String, locale: Locale): String {
        val date = Date(timestampSeconds * 1000)
        return SimpleDateFormat(pattern, locale).format(date)
    }

    fun formatVisibility(meters: Int, unit: String = "km"): String {
        val value = if (unit == "m") meters.toDouble() else meters / 1000.0
        val formatted = String.format(Locale.getDefault(), "%.1f", value)
        return "$formatted $unit"
    }

    fun formatPrecipitationProbability(probability: Double): String {
        return "${(probability * 100).roundToInt()}%"
    }
}
