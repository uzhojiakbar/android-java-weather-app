package com.example.farrux.weatherapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.farrux.weatherapp.databinding.ItemHourlyDetailBinding
import com.example.farrux.weatherapp.model.HourlyWeather
import com.example.farrux.weatherapp.util.WeatherFormatter
import java.util.Locale

class HourlyDetailAdapter(
    private var items: List<HourlyWeather>,
    private val temperatureUnit: String,
    private val windUnit: String,
    private val language: String
) : RecyclerView.Adapter<HourlyDetailAdapter.HourlyDetailViewHolder>() {

    inner class HourlyDetailViewHolder(val binding: ItemHourlyDetailBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyDetailViewHolder {
        val binding = ItemHourlyDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HourlyDetailViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: HourlyDetailViewHolder, position: Int) {
        val item = items[position]
        val locale = Locale(language)
        with(holder.binding) {
            textDetailHour.text = WeatherFormatter.formatDateTime(item.dt, "EEE HH:mm", locale)
            textDetailTemp.text = WeatherFormatter.formatTemperature(item.temp, temperatureUnit)
            textDetailDesc.text = item.weather.firstOrNull()?.description ?: ""
            textDetailWind.text = WeatherFormatter.formatWindSpeed(item.windSpeed, windUnit)
            textDetailPressure.text = "${item.pressure} hPa"
        }
    }
}
