package com.example.farrux.weatherapp.data

import android.content.Context
import androidx.core.content.edit

class UserPreferences(context: Context) {
    private val prefs = context.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)

    fun setLanguage(language: String) {
        prefs.edit { putString(KEY_LANGUAGE, language) }
    }

    fun getLanguage(): String = prefs.getString(KEY_LANGUAGE, "en") ?: "en"

    fun setTemperatureUnit(unit: String) {
        prefs.edit { putString(KEY_TEMPERATURE_UNIT, unit) }
    }

    fun getTemperatureUnit(): String = prefs.getString(KEY_TEMPERATURE_UNIT, "metric") ?: "metric"

    fun setWindUnit(unit: String) {
        prefs.edit { putString(KEY_WIND_UNIT, unit) }
    }

    fun getWindUnit(): String = prefs.getString(KEY_WIND_UNIT, "m/s") ?: "m/s"

    fun setPressureUnit(unit: String) {
        prefs.edit { putString(KEY_PRESSURE_UNIT, unit) }
    }

    fun getPressureUnit(): String = prefs.getString(KEY_PRESSURE_UNIT, "hPa") ?: "hPa"

    fun setVisibilityUnit(unit: String) {
        prefs.edit { putString(KEY_VISIBILITY_UNIT, unit) }
    }

    fun getVisibilityUnit(): String = prefs.getString(KEY_VISIBILITY_UNIT, "km") ?: "km"

    fun setOpenWeatherKey(key: String) {
        prefs.edit { putString(KEY_OPEN_WEATHER, key) }
    }

    fun getOpenWeatherKey(): String = prefs.getString(KEY_OPEN_WEATHER, "") ?: ""

    fun setMapsKey(key: String) {
        prefs.edit { putString(KEY_MAPS, key) }
    }

    fun getMapsKey(): String = prefs.getString(KEY_MAPS, "") ?: ""

    fun setOnboardingCompleted(completed: Boolean) {
        prefs.edit { putBoolean(KEY_ONBOARDING_DONE, completed) }
    }

    fun hasCompletedOnboarding(): Boolean = prefs.getBoolean(KEY_ONBOARDING_DONE, false)

    fun saveLastLocation(lat: Double, lon: Double, name: String?) {
        prefs.edit {
            putFloat(KEY_LAST_LAT, lat.toFloat())
            putFloat(KEY_LAST_LON, lon.toFloat())
            putString(KEY_LAST_NAME, name)
        }
    }

    fun getLastLocation(): Triple<Double, Double, String?> {
        val lat = prefs.getFloat(KEY_LAST_LAT, 41.2646f).toDouble()
        val lon = prefs.getFloat(KEY_LAST_LON, 69.2163f).toDouble()
        val name = prefs.getString(KEY_LAST_NAME, "Tashkent")
        return Triple(lat, lon, name)
    }

    fun cacheWeather(json: String) {
        prefs.edit { putString(KEY_CACHE, json) }
    }

    fun getCachedWeather(): String? = prefs.getString(KEY_CACHE, null)

    companion object {
        private const val KEY_LANGUAGE = "language"
        private const val KEY_TEMPERATURE_UNIT = "temperature_unit"
        private const val KEY_WIND_UNIT = "wind_unit"
        private const val KEY_ONBOARDING_DONE = "onboarding_done"
        private const val KEY_PRESSURE_UNIT = "pressure_unit"
        private const val KEY_VISIBILITY_UNIT = "visibility_unit"
        private const val KEY_LAST_LAT = "last_lat"
        private const val KEY_LAST_LON = "last_lon"
        private const val KEY_LAST_NAME = "last_name"
        private const val KEY_CACHE = "cache_weather"
        private const val KEY_OPEN_WEATHER = "open_weather_key"
        private const val KEY_MAPS = "maps_key"
    }
}
