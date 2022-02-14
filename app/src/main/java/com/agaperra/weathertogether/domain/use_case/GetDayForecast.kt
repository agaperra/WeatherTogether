package com.agaperra.weathertogether.domain.use_case

import com.agaperra.weathertogether.domain.model.AppState
import com.agaperra.weathertogether.domain.model.ErrorState
import com.agaperra.weathertogether.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class GetDayForecast @Inject constructor(
    private val forecastRepository: WeatherRepository
) {
    operator fun invoke(lat: Double, lon: Double) = flow {
        try {
            val dayForecast = forecastRepository.getDayForecast(
                lat = lat,
                lon = lon,
                lang = Locale.getDefault().language
            )
            emit(AppState.Success(data = dayForecast))
        } catch (e: Exception) {
            Timber.e(e)
            emit(AppState.Error(error = ErrorState.NO_FORECAST_LOADED))
        }
    }
}