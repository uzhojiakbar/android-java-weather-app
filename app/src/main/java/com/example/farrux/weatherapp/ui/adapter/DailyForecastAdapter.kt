package com.example.farrux.weatherapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.farrux.weatherapp.databinding.ItemDailyBinding
import com.example.farrux.weatherapp.model.DailyWeather
import com.example.farrux.weatherapp.util.WeatherFormatter
import java.util.Locale

class DailyForecastAdapter(
    private var items: List<DailyWeather>,
    private val temperatureUnit: String,
    private val windUnit: String,
    private val language: String
) : RecyclerView.Adapter<DailyForecastAdapter.DailyViewHolder>() {

    inner class DailyViewHolder(val binding: ItemDailyBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyViewHolder {
        val binding = ItemDailyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DailyViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: DailyViewHolder, position: Int) {
        val item = items[position]
        val locale = Locale(language)
        with(holder.binding) {
            textDay.text = WeatherFormatter.formatDateTime(item.dt, "EEE", locale)
            val max = item.temp?.max ?: 0.0
            val min = item.temp?.min ?: 0.0
            textDailyTemp.text =
                "${WeatherFormatter.formatTemperature(max, temperatureUnit)} / ${WeatherFormatter.formatTemperature(min, temperatureUnit)}"
            textDailyDesc.text = item.weather.firstOrNull()?.main ?: ""
            textDailyWind.text = WeatherFormatter.formatWindSpeed(item.windSpeed, windUnit)
        }
    }

    fun submitList(data: List<DailyWeather>) {
        items = data
        notifyDataSetChanged()
    }
}
