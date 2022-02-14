package com.agaperra.weathertogether.domain.repository

import com.agaperra.weathertogether.domain.model.ForecastDay
import com.agaperra.weathertogether.domain.model.WeatherForecast

interface WeatherRepository {

    suspend fun getWeeklyForecast(
        lat: Double,
        lon: Double,
        lang: String,
    ): WeatherForecast

    suspend fun getDayForecast(
        lat: Double,
        lon: Double,
        lang: String,
    ) : ForecastDay

}