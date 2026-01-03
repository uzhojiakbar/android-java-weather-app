package com.example.farrux.weatherapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.farrux.weatherapp.databinding.ActivityHourlyDetailBinding
import com.example.farrux.weatherapp.model.HourlyWeather
import com.example.farrux.weatherapp.ui.adapter.HourlyDetailAdapter

class HourlyDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHourlyDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHourlyDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val temperatureUnit = intent.getStringExtra(EXTRA_TEMP_UNIT) ?: "metric"
        val windUnit = intent.getStringExtra(EXTRA_WIND_UNIT) ?: "m/s"
        val language = intent.getStringExtra(EXTRA_LANGUAGE) ?: "en"
        val hourlyData = intent.getParcelableArrayListExtra<HourlyWeather>(EXTRA_HOURLY) ?: arrayListOf()

        binding.detailToolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val adapter = HourlyDetailAdapter(hourlyData, temperatureUnit, windUnit, language)
        binding.recyclerHourlyDetail.layoutManager = LinearLayoutManager(this)
        binding.recyclerHourlyDetail.adapter = adapter
    }

    companion object {
        const val EXTRA_HOURLY = "extra_hourly"
        const val EXTRA_TEMP_UNIT = "extra_temp_unit"
        const val EXTRA_WIND_UNIT = "extra_wind_unit"
        const val EXTRA_LANGUAGE = "extra_language"
    }
}
