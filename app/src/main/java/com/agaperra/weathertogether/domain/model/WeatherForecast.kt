package com.agaperra.weathertogether.domain.model

import com.agaperra.weathertogether.domain.model.ForecastDay

data class WeatherForecast(
    val location: String = "",
    val currentWeather: String = "",
    val currentWindSpeed: String = "",
    val currentHumidity: String = "",
    val currentPressure: String = "",
    val currentWeatherStatus: String = "",
    val currentWeatherStatusId: Int = -1,
    val forecastDays: List<ForecastDay> = listOf(),
)