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

    fun setOnboardingCompleted(completed: Boolean) {
        prefs.edit { putBoolean(KEY_ONBOARDING_DONE, completed) }
    }

    fun hasCompletedOnboarding(): Boolean = prefs.getBoolean(KEY_ONBOARDING_DONE, false)

    companion object {
        private const val KEY_LANGUAGE = "language"
        private const val KEY_TEMPERATURE_UNIT = "temperature_unit"
        private const val KEY_WIND_UNIT = "wind_unit"
        private const val KEY_ONBOARDING_DONE = "onboarding_done"
    }
}
