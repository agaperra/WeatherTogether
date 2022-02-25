package com.agaperra.weathertogether.data.api

import com.agaperra.weathertogether.data.api.dto.city_search.CitiesResponse
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Singleton

@Singleton
interface CityApi {

    @GET(value = "/v1/geo/cities")
    suspend fun getCityList(
        @Query("namePrefix") namePrefix: String,
        @Query("languageCode") languageCode: String
    ): CitiesResponse
}