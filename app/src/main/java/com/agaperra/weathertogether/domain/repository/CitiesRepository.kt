package com.agaperra.weathertogether.domain.repository

import com.agaperra.weathertogether.domain.model.CityItem
import kotlinx.coroutines.flow.Flow

interface CitiesRepository {

    suspend fun getCities(
        namePrefix: String,
        languageCode: String
    ): List<CityItem>

}