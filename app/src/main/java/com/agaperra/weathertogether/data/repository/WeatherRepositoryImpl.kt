package com.agaperra.weathertogether.data.repository

import com.agaperra.weathertogether.data.api.WeatherApi
import com.agaperra.weathertogether.data.api.mapper.DtoToDomain
import com.agaperra.weathertogether.domain.repository.WeatherRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class WeatherRepositoryImpl @Inject constructor(
    private val forecastApi: WeatherApi,
    private val mapper: DtoToDomain
) : WeatherRepository {

    override suspend fun getWeeklyForecast(
        lat: Double,
        lon: Double,
        lang: String,
    ) = mapper.map(forecastApi.getWeekForecast(lat = lat, lon = lon, lang = lang))

    override suspend fun getDayForecast(
        lat: Double,
        lon: Double,
        lang: String
    ) = mapper.map(forecastApi.getDayForecast(lat = lat, lon = lon, lang = lang))

}