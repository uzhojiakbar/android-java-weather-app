package com.example.farrux.weatherapp.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.farrux.weatherapp.R
import com.example.farrux.weatherapp.data.UserPreferences
import com.example.farrux.weatherapp.data.WeatherRepository
import com.example.farrux.weatherapp.databinding.ActivityMainBinding
import com.example.farrux.weatherapp.model.HourlyWeather
import com.example.farrux.weatherapp.model.WeatherResponse
import com.example.farrux.weatherapp.ui.adapter.DailyForecastAdapter
import com.example.farrux.weatherapp.ui.adapter.HourlyForecastAdapter
import com.example.farrux.weatherapp.util.WeatherFormatter
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMainBinding
    private lateinit var preferences: UserPreferences
    private val repository = WeatherRepository()
    private val fusedClient by lazy { LocationServices.getFusedLocationProviderClient(this) }
    private var hourlyData: List<HourlyWeather> = emptyList()
    private var googleMap: GoogleMap? = null
    private var currentLatLng: LatLng? = null

    private lateinit var hourlyAdapter: HourlyForecastAdapter
    private lateinit var dailyAdapter: DailyForecastAdapter

    private val onboardingLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (preferences.hasCompletedOnboarding()) {
                initializeUi()
                checkPermissionAndLoad()
            } else {
                finish()
            }
        }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            if (granted) {
                loadCurrentLocationWeather()
            } else {
                showStatus(getString(R.string.permission_request))
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferences = UserPreferences(this)

        if (!preferences.hasCompletedOnboarding()) {
            onboardingLauncher.launch(Intent(this, OnboardingActivity::class.java))
        } else {
            initializeUi()
            checkPermissionAndLoad()
        }
    }

    private fun initializeUi() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.app_name)
        binding.toolbar.inflateMenu(R.menu.menu_main)
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_refresh -> {
                    checkPermissionAndLoad()
                    true
                }
                R.id.action_search -> {
                    showCitySearchDialog()
                    true
                }
                R.id.action_settings -> {
                    openSettings()
                    true
                }
                else -> false
            }
        }

        binding.recyclerHourly.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerDaily.layoutManager = LinearLayoutManager(this)

        hourlyAdapter = HourlyForecastAdapter(
            emptyList(),
            preferences.getTemperatureUnit(),
            preferences.getWindUnit(),
            preferences.getLanguage()
        )
        dailyAdapter = DailyForecastAdapter(
            emptyList(),
            preferences.getTemperatureUnit(),
            preferences.getWindUnit(),
            preferences.getLanguage()
        )
        binding.recyclerHourly.adapter = hourlyAdapter
        binding.recyclerDaily.adapter = dailyAdapter

        binding.buttonOpen72Hours.setOnClickListener {
            open72HourScreen()
        }

        setupMapFragment()
    }

    private fun setupMapFragment() {
        try {
            MapsInitializer.initialize(applicationContext, MapsInitializer.Renderer.LATEST) {}
        } catch (e: Exception) {
            binding.textMapStatus.visibility = View.VISIBLE
            binding.textMapStatus.text = "Google Maps yuklashda xato: ${e.localizedMessage}"
            binding.buttonRetryMap.visibility = View.VISIBLE
            return
        }
        val fragment = (supportFragmentManager.findFragmentById(R.id.mapContainer) as? SupportMapFragment)
            ?: SupportMapFragment.newInstance().also {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.mapContainer, it)
                    .commit()
            }
        fragment.getMapAsync(this)
        binding.buttonRetryMap.setOnClickListener {
            setupMapFragment()
        }
    }

    private fun checkPermissionAndLoad() {
        if (isLocationGranted()) {
            loadCurrentLocationWeather()
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
            // fall back to last saved location while waiting
            val last = preferences.getLastLocation()
            fetchWeather(last.first, last.second, last.third)
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

    @SuppressLint("MissingPermission")
    private fun loadCurrentLocationWeather() {
        if (!isLocationGranted()) return
        showLoading(true)
        val tokenSource = CancellationTokenSource()
        val request =
            fusedClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, tokenSource.token)
        request.addOnSuccessListener { location ->
            if (location != null) {
                val latLng = LatLng(location.latitude, location.longitude)
                currentLatLng = latLng
                updateMap(latLng)
                fetchWeather(latLng.latitude, latLng.longitude)
            } else {
                showLoading(false)
                showStatus(getString(R.string.permission_request))
            }
        }.addOnFailureListener {
            showLoading(false)
            showStatus(getString(R.string.location_error))
        }
    }

    private fun fetchWeather(lat: Double, lon: Double, name: String? = null) {
        val units = preferences.getTemperatureUnit()
        val lang = preferences.getLanguage()
        lifecycleScope.launch {
            val result = repository.getWeather(lat, lon, units, lang)
            showLoading(false)
            if (result.data == null) {
                showStatus(result.error ?: getString(R.string.status_add_api_key))
                preferences.getCachedWeather()?.let { cached ->
                    repository.deserialize(cached)?.let { cachedWeather ->
                        renderWeather(cachedWeather)
                        showStatus(getString(R.string.status_offline_cache))
                    }
                }
            } else {
                binding.textStatus.visibility = View.GONE
                renderWeather(result.data)
                preferences.saveLastLocation(lat, lon, name ?: result.data.timezone)
                preferences.cacheWeather(repository.serialize(result.data))
            }
        }
    }

    private fun renderWeather(weather: WeatherResponse) {
        val locale = Locale(preferences.getLanguage())
        val current = weather.current
        binding.textLocation.text = weather.timezone ?: getString(R.string.app_name)
        if (current != null) {
            binding.textDateTime.text = WeatherFormatter.formatDateTime(current.dt, "EEE HH:mm", locale)
            binding.textCurrentTemp.text =
                WeatherFormatter.formatTemperature(current.temp, preferences.getTemperatureUnit())
            val description = current.weather.firstOrNull()?.description
                ?.replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
                ?: ""
            binding.textCurrentDescription.text = description
            binding.textFeelsLike.text = getString(
                R.string.feels_like_format,
                WeatherFormatter.formatTemperature(current.feelsLike, preferences.getTemperatureUnit())
            )
            binding.textWind.text = getString(
                R.string.wind_format,
                WeatherFormatter.formatWindSpeed(current.windSpeed, preferences.getWindUnit())
            )
            binding.textHumidity.text = getString(R.string.humidity_format, current.humidity)
            binding.textPressure.text = getString(
                R.string.pressure_format,
                WeatherFormatter.formatPressure(current.pressure, preferences.getPressureUnit())
            )
            binding.textUv.text = getString(R.string.uv_format, current.uvi)
            val pop = weather.hourly.firstOrNull()?.precipitationProbability ?: 0.0
            binding.textPrecipitation.text =
                getString(R.string.precip_format, WeatherFormatter.formatPrecipitationProbability(pop))
            val visibility = WeatherFormatter.formatVisibility(
                current.visibility,
                preferences.getVisibilityUnit()
            )
            binding.textVisibility.text = getString(R.string.visibility_format, visibility)
            currentLatLng = currentLatLng ?: LatLng(preferences.getLastLocation().first, preferences.getLastLocation().second)
            currentLatLng?.let { updateMap(it) }
        }

        // One Call 2.5 returns up to 48 hourly entries
        hourlyData = weather.hourly.take(48)
        hourlyAdapter.submitList(hourlyData.take(12))
        dailyAdapter.submitList(weather.daily.take(7))
    }

    private fun open72HourScreen() {
        if (hourlyData.isEmpty()) {
            Toast.makeText(this, "Ma'lumot yuklanmoqda", Toast.LENGTH_SHORT).show()
            return
        }
        val intent = Intent(this, HourlyDetailActivity::class.java)
        intent.putParcelableArrayListExtra(
            HourlyDetailActivity.EXTRA_HOURLY,
            ArrayList(hourlyData.take(72))
        )
        intent.putExtra(HourlyDetailActivity.EXTRA_TEMP_UNIT, preferences.getTemperatureUnit())
        intent.putExtra(HourlyDetailActivity.EXTRA_WIND_UNIT, preferences.getWindUnit())
        intent.putExtra(HourlyDetailActivity.EXTRA_LANGUAGE, preferences.getLanguage())
        startActivity(intent)
    }

    private fun showLoading(loading: Boolean) {
        binding.loadingIndicator.visibility = if (loading) View.VISIBLE else View.GONE
    }

    private fun showStatus(message: String) {
        binding.textStatus.visibility = View.VISIBLE
        binding.textStatus.text = message
    }

    private fun updateMap(latLng: LatLng) {
        googleMap?.let { map ->
            map.uiSettings.isZoomControlsEnabled = true
            map.uiSettings.isMapToolbarEnabled = false
            map.clear()
            map.addMarker(MarkerOptions().position(latLng).title("Sizning joylashuvingiz"))
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11f))
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        val last = currentLatLng ?: LatLng(preferences.getLastLocation().first, preferences.getLastLocation().second)
        updateMap(last)
    }

    private fun openSettings() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    private fun showCitySearchDialog() {
        val input = android.widget.EditText(this)
        input.hint = "Shahar nomi"
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Shaharni kiriting")
            .setView(input)
            .setPositiveButton("Izlash") { _, _ ->
                val city = input.text.toString()
                if (city.isNotBlank()) {
                    val apiKey = com.example.farrux.weatherapp.BuildConfig.OPEN_WEATHER_API_KEY
                    if (apiKey.isBlank()) {
                        showStatus(getString(R.string.status_add_api_key))
                        return@setPositiveButton
                    }
                    lifecycleScope.launch {
                        showLoading(true)
                        val geo = repository.geocodeCity(city, apiKey.trim())
                        geo.onSuccess { location ->
                            currentLatLng = LatLng(location.lat, location.lon)
                            fetchWeather(location.lat, location.lon, location.name)
                            updateMap(currentLatLng!!)
                        }.onFailure {
                            showStatus(it.localizedMessage ?: "Manzil topilmadi")
                        }
                        showLoading(false)
                    }
                }
            }
            .setNegativeButton("Bekor qilish", null)
            .show()
    }
}
