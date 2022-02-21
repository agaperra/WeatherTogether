package com.agaperra.weathertogether.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.agaperra.weathertogether.R
import com.agaperra.weathertogether.databinding.ItemDailyBinding
import com.agaperra.weathertogether.domain.model.ForecastDay

class WeekForecastListAdapter(val context: Context?) :
    ListAdapter<ForecastDay, WeekForecastListAdapter.WeekForecastListViewHolder>(WeekForecastDiffUtil()) {

    inner class WeekForecastListViewHolder(private val binding: ItemDailyBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(itemPosition: Int) {

            val dailyForecast = getItem(itemPosition)

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