package com.example.farrux.weatherapp.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.farrux.weatherapp.R
import com.example.farrux.weatherapp.data.UserPreferences
import com.example.farrux.weatherapp.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var prefs: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefs = UserPreferences(this)

        setSupportActionBar(binding.settingsToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.settingsToolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        setupSpinners()
        binding.buttonSaveSettings.setOnClickListener {
            saveSettings()
            finish()
        }
    }

    private fun setupSpinners() {
        setupSpinner(
            binding.spinnerLanguageSettings,
            resources.getStringArray(R.array.languages_display),
            resources.getStringArray(R.array.languages_values),
            prefs.getLanguage()
        )
        setupSpinner(
            binding.spinnerTempSettings,
            arrayOf("°C (Metric)", "°F (Imperial)"),
            arrayOf("metric", "imperial"),
            prefs.getTemperatureUnit()
        )
        setupSpinner(
            binding.spinnerWindSettings,
            arrayOf("m/s", "km/h", "mph"),
            arrayOf("m/s", "km/h", "mph"),
            prefs.getWindUnit()
        )
        setupSpinner(
            binding.spinnerPressureSettings,
            arrayOf("hPa", "mmHg"),
            arrayOf("hPa", "mmHg"),
            prefs.getPressureUnit()
        )
        setupSpinner(
            binding.spinnerVisibilitySettings,
            arrayOf("km", "m"),
            arrayOf("km", "m"),
            prefs.getVisibilityUnit()
        )
    }

    private fun setupSpinner(
        spinner: android.widget.Spinner,
        display: Array<String>,
        values: Array<String>,
        current: String
    ) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, display)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        val index = values.indexOf(current).takeIf { it >= 0 } ?: 0
        spinner.setSelection(index)
    }

    private fun saveSettings() {
        val langValues = resources.getStringArray(R.array.languages_values)
        prefs.setLanguage(
            langValues[binding.spinnerLanguageSettings.selectedItemPosition]
        )
        val tempValues = arrayOf("metric", "imperial")
        prefs.setTemperatureUnit(
            tempValues[binding.spinnerTempSettings.selectedItemPosition]
        )
        val windValues = arrayOf("m/s", "km/h", "mph")
        prefs.setWindUnit(
            windValues[binding.spinnerWindSettings.selectedItemPosition]
        )
        val pressureValues = arrayOf("hPa", "mmHg")
        prefs.setPressureUnit(
            pressureValues[binding.spinnerPressureSettings.selectedItemPosition]
        )
        val visibilityValues = arrayOf("km", "m")
        prefs.setVisibilityUnit(
            visibilityValues[binding.spinnerVisibilitySettings.selectedItemPosition]
        )
    }
}
