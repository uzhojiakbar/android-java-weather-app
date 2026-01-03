package com.example.farrux.weatherapp.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.farrux.weatherapp.R
import com.example.farrux.weatherapp.data.UserPreferences
import com.example.farrux.weatherapp.databinding.ActivityOnboardingBinding
import android.view.View

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var preferences: UserPreferences
    private var currentStep = 0
    private var hasLocationPermission = false
    private var selectedLanguage = "en"
    private var selectedTemperatureUnit = "metric"
    private var selectedWindUnit = "m/s"
    private var selectedPressureUnit = "hPa"
    private var selectedVisibilityUnit = "km"

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val fine = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
            val coarse = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            hasLocationPermission = fine || coarse
            if (!hasLocationPermission) {
                Toast.makeText(this, R.string.permission_request, Toast.LENGTH_SHORT).show()
            }
            updateStepUi()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferences = UserPreferences(this)

        setupLanguageSpinner()
        setupUnitSelectors()

        binding.buttonRequestPermission.setOnClickListener { requestLocationPermission() }
        binding.buttonNext.setOnClickListener { handleNext() }

        hasLocationPermission = isLocationGranted()
        updateStepUi()
    }

    private fun setupLanguageSpinner() {
        val display = resources.getStringArray(R.array.languages_display)
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            display
        ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        binding.spinnerLanguage.adapter = adapter
        binding.spinnerLanguage.setSelection(0)
    }

    private fun setupUnitSelectors() {
        binding.toggleTemperature.check(binding.buttonMetric.id)
        binding.chipMs.isChecked = true
        binding.toggleTemperature.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                selectedTemperatureUnit =
                    if (checkedId == binding.buttonImperial.id) "imperial" else "metric"
            }
        }
        binding.chipGroupWind.setOnCheckedChangeListener { _, checkedId ->
            selectedWindUnit = when (checkedId) {
                binding.chipKm.id -> "km/h"
                binding.chipMph.id -> "mph"
                else -> "m/s"
            }
        }
        binding.togglePressure.check(binding.buttonHPa.id)
        binding.togglePressure.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                selectedPressureUnit = if (checkedId == binding.buttonMmHg.id) "mmHg" else "hPa"
            }
        }
        binding.toggleVisibility.check(binding.buttonKm.id)
        binding.toggleVisibility.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                selectedVisibilityUnit = if (checkedId == binding.buttonMeter.id) "m" else "km"
            }
        }
    }

    private fun handleNext() {
        when (currentStep) {
            0 -> {
                if (!hasLocationPermission) {
                    requestLocationPermission()
                    return
                }
                currentStep++
            }

            1 -> {
                val values = resources.getStringArray(R.array.languages_values)
                selectedLanguage = values.getOrNull(binding.spinnerLanguage.selectedItemPosition) ?: "en"
                currentStep++
            }

            2 -> {
                preferences.setLanguage(selectedLanguage)
                preferences.setTemperatureUnit(selectedTemperatureUnit)
                preferences.setWindUnit(selectedWindUnit)
                preferences.setPressureUnit(selectedPressureUnit)
                preferences.setVisibilityUnit(selectedVisibilityUnit)
                preferences.setOnboardingCompleted(true)
                setResult(RESULT_OK)
                finish()
                return
            }
        }
        updateStepUi()
    }

    private fun updateStepUi() {
        when (currentStep) {
            0 -> {
                binding.imageStep.setImageResource(android.R.drawable.ic_menu_mylocation)
                binding.textStepTitle.setText(R.string.permission_title)
                binding.textStepDescription.setText(R.string.permission_desc)
                binding.buttonRequestPermission.visibility = View.VISIBLE
                binding.languageContainer.visibility = View.GONE
                binding.unitContainer.visibility = View.GONE
                binding.buttonNext.setText(R.string.next)
            }

            1 -> {
                binding.imageStep.setImageResource(android.R.drawable.ic_menu_manage)
                binding.textStepTitle.setText(R.string.language_title)
                binding.textStepDescription.setText(R.string.language_desc)
                binding.buttonRequestPermission.visibility = View.GONE
                binding.languageContainer.visibility = View.VISIBLE
                binding.unitContainer.visibility = View.GONE
                binding.buttonNext.setText(R.string.next)
            }

            else -> {
                binding.imageStep.setImageResource(android.R.drawable.ic_menu_sort_by_size)
                binding.textStepTitle.setText(R.string.unit_title)
                binding.textStepDescription.setText(R.string.unit_desc)
                binding.buttonRequestPermission.visibility = View.GONE
                binding.languageContainer.visibility = View.GONE
                binding.unitContainer.visibility = View.VISIBLE
                binding.buttonNext.setText(R.string.finish)
            }
        }
    }

    private fun isLocationGranted(): Boolean {
        val fine = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    private fun requestLocationPermission() {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }
}
