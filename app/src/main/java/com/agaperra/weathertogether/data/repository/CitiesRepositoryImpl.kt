package com.agaperra.weathertogether.data.repository

import com.agaperra.weathertogether.data.api.CityApi
import com.agaperra.weathertogether.data.api.mapper.toDomain
import com.agaperra.weathertogether.domain.repository.CitiesRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class CitiesRepositoryImpl @Inject constructor(
    private val cityApi: CityApi
) : CitiesRepository {

    override suspend fun getCities(
        namePrefix: String,
        languageCode: String
    ) = cityApi.getCityList(namePrefix = namePrefix, languageCode = languageCode).toDomain()

}