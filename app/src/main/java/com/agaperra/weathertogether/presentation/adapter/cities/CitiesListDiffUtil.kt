package com.agaperra.weathertogether.presentation.adapter.cities

import androidx.recyclerview.widget.DiffUtil
import com.agaperra.weathertogether.domain.model.CityItem
import com.agaperra.weathertogether.domain.model.ForecastDay

class CitiesListDiffUtil : DiffUtil.ItemCallback<CityItem>() {

    override fun areItemsTheSame(oldItem: CityItem, newItem: CityItem) =
        oldItem.name == newItem.name

    override fun areContentsTheSame(oldItem: CityItem, newItem: CityItem) =
        oldItem == newItem

}