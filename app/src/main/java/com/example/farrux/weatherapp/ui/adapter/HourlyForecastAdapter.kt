package com.example.farrux.weatherapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.farrux.weatherapp.databinding.ItemHourlyBinding
import com.example.farrux.weatherapp.model.HourlyWeather
import com.example.farrux.weatherapp.util.WeatherFormatter
import java.util.Locale

class HourlyForecastAdapter(
    private var items: List<HourlyWeather>,
    private val temperatureUnit: String,
    private val windUnit: String,
    private val language: String
) : RecyclerView.Adapter<HourlyForecastAdapter.HourlyViewHolder>() {

    inner class HourlyViewHolder(val binding: ItemHourlyBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemHourlyBinding.inflate(inflater, parent, false)
        return HourlyViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: HourlyViewHolder, position: Int) {
        val item = items[position]
        val locale = Locale(language)
        with(holder.binding) {
            textHour.text = WeatherFormatter.formatDateTime(item.dt, "HH:mm", locale)
            textHourlyTemp.text = WeatherFormatter.formatTemperature(item.temp, temperatureUnit)
            textHourlyDesc.text = item.weather.firstOrNull()?.description
                ?.replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
                ?: ""
            textHourlyWind.text = WeatherFormatter.formatWindSpeed(item.windSpeed, windUnit)
        }
    }

    fun submitList(data: List<HourlyWeather>) {
        items = data
        notifyDataSetChanged()
    }
}
