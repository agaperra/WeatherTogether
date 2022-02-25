package com.agaperra.weathertogether.presentation.adapter.weather

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.agaperra.weathertogether.R
import com.agaperra.weathertogether.databinding.ItemDailyBinding
import com.agaperra.weathertogether.domain.model.ForecastDay

class WeekForecastListAdapter() :
    ListAdapter<ForecastDay, WeekForecastListAdapter.WeekForecastListViewHolder>(
        WeekForecastDiffUtil()
    ) {

    inner class WeekForecastListViewHolder(private val binding: ItemDailyBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(itemPosition: Int) {

            val dailyForecast = getItem(itemPosition)

            binding.icon.setImageResource(dailyForecast.dayIcon)
            binding.parentLayout.setBackgroundResource(dailyForecast.dayBackground)

            binding.date.text = dailyForecast.dayName
            binding.tempScore.text = dailyForecast.dayTemp
            binding.sunrise.text = dailyForecast.sunrise
            binding.sunset.text = dailyForecast.sunset
            binding.humidity.text = dailyForecast.dayHumidity
            binding.pressure.text = dailyForecast.dayPressure
            binding.feelsLike.text = dailyForecast.tempFeelsLike

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        WeekForecastListViewHolder(
            ItemDailyBinding.bind(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_daily,
                    parent,
                    false
                )
            )
        )

    override fun onBindViewHolder(holder: WeekForecastListViewHolder, position: Int) =
        holder.bind(itemPosition = position)


}