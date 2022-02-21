package com.agaperra.weathertogether.presentation.adapter

import androidx.recyclerview.widget.DiffUtil
import com.agaperra.weathertogether.domain.model.ForecastDay

class WeekForecastDiffUtil : DiffUtil.ItemCallback<ForecastDay>() {

    override fun areItemsTheSame(oldItem: ForecastDay, newItem: ForecastDay) =
        oldItem.dayName == newItem.dayName

    override fun areContentsTheSame(oldItem: ForecastDay, newItem: ForecastDay) =
        oldItem == newItem

}