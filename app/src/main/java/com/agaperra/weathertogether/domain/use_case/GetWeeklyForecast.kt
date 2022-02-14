package com.agaperra.weathertogether.domain.use_case

import com.agaperra.weathertogether.domain.model.AppState
import com.agaperra.weathertogether.domain.model.ErrorState
import com.agaperra.weathertogether.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

class GetWeeklyForecast @Inject constructor(
    private val forecastRepository: WeatherRepository
) {
    operator fun invoke(
        lat: Double,
        lon: Double,
        lang: String,
    ) = flow {
        emit(AppState.Loading())
        try {
            val response =
                forecastRepository.getWeeklyForecast(lat, lon, lang)
            emit(AppState.Success(data = response))
        } catch (exception: HttpException) {
            if (exception.code() != 400)
                emit(AppState.Error(error = ErrorState.NO_INTERNET_CONNECTION))
            Timber.e(exception)
        } catch (exception: Exception) {
            emit(AppState.Error(error = ErrorState.NO_INTERNET_CONNECTION))
            Timber.e(exception)
        }
    }


}