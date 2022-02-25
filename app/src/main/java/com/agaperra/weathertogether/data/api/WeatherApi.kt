package com.agaperra.weathertogether.data.api

import com.agaperra.weathertogether.BuildConfig
import com.agaperra.weathertogether.data.api.dto.day_forecast.DayForecastResponse
import com.agaperra.weathertogether.data.api.dto.week_forecast.ForecastResponse
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Singleton

@Singleton
interface WeatherApi {

    @GET(value = "/data/2.5/onecall")
    suspend fun getWeekForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String,
        @Query("appid") appid: String = BuildConfig.weather_key
    ): ForecastResponse

    @GET(value = "/data/2.5/weather")
    suspend fun getDayForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String,
        @Query("appid") appid: String = BuildConfig.weather_key
    ): DayForecastResponse
}