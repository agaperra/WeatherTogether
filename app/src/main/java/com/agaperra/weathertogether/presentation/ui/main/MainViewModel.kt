package com.agaperra.weathertogether.presentation.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agaperra.weathertogether.domain.model.AppState
import com.agaperra.weathertogether.domain.model.ErrorState
import com.agaperra.weathertogether.domain.model.WeatherForecast
import com.agaperra.weathertogether.domain.use_case.GetWeeklyForecast
import com.agaperra.weathertogether.utils.compare
import com.agaperra.weathertogether.utils.location.LocationListener
import com.agaperra.weathertogether.utils.network.ConnectionState
import com.agaperra.weathertogether.utils.network.NetworkStatusListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
@ExperimentalCoroutinesApi
class MainViewModel @Inject constructor(
    networkStatusListener: NetworkStatusListener,
    private val getWeeklyForecast: GetWeeklyForecast,
    private val locationListener: LocationListener
) : ViewModel() {

    companion object {
        const val WEATHER_UPDATE_TIMER_PERIOD = 5L
    }

    private val _currentLocation = MutableStateFlow(Pair(-200.0, -200.0))

    private val _forecastLoading = MutableStateFlow(true)
    val forecastLoading = _forecastLoading.asStateFlow()

    private val _weatherForecast = MutableStateFlow<AppState<WeatherForecast>>(AppState.Loading())
    val weatherForecast = _weatherForecast.asStateFlow()

    private val _weatherLastUpdate = MutableStateFlow(value = 0)
    val weatherLastUpdate = _weatherLastUpdate.asStateFlow()

    private val scheduledExecutorService = Executors.newScheduledThreadPool(1)
    private var future: ScheduledFuture<*>? = null

    init{
        networkStatusListener.networkStatus.onEach { status ->
            when (status) {
                ConnectionState.Available -> {
                    if (_weatherForecast !is AppState.Loading<*>) {
                        observeCurrentLocation()
                        getWeatherForecast()
                    }
                }
                ConnectionState.Unavailable -> {
                    if (_weatherForecast.value.data != null) _weatherForecast.value =
                        AppState.Error(error = ErrorState.NO_INTERNET_CONNECTION)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun observeCurrentLocation() = locationListener.currentLocation.onEach { locationResult ->
        when (locationResult) {
            is AppState.Error -> {
                Timber.e(locationResult.message?.name)
                if (_weatherForecast.value.data == null) {
                    _weatherForecast.value =
                        AppState.Error(error = ErrorState.NO_LOCATION_AVAILABLE)
                }
            }
            is AppState.Loading -> Timber.e(message = "Location loading")
            is AppState.Success -> {
                locationResult.data?.let { coordinates ->
                    Timber.e(coordinates.toString())
                    if (coordinates.compare(_currentLocation.value)) return@let
                    _currentLocation.value = coordinates
                    getWeatherForecast()
                }
            }
        }
    }.launchIn(viewModelScope)

    fun getWeatherForecast() {
        getWeeklyForecast(
            lat = _currentLocation.value.first,
            lon = _currentLocation.value.second,
            lang = Locale.getDefault().language
        ).onEach { result ->
            when (result) {
                is AppState.Success -> {
                    _weatherForecast.value = result
                    startForecastUpdateTimer()
                    _forecastLoading.value = false
                }
                is AppState.Loading -> {
                    _forecastLoading.value = true
                    if (future?.isCancelled == false) future?.cancel(false)
                    _weatherLastUpdate.value = 0
                }
                is AppState.Error -> {
                    _forecastLoading.value = false
                    _weatherForecast.value = result
                    Timber.e(result.message?.name)
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun startForecastUpdateTimer() {
        future = scheduledExecutorService.scheduleAtFixedRate(
            { _weatherLastUpdate.value += WEATHER_UPDATE_TIMER_PERIOD.toInt() },
            WEATHER_UPDATE_TIMER_PERIOD,
            WEATHER_UPDATE_TIMER_PERIOD,
            TimeUnit.MINUTES
        )
    }

    override fun onCleared() {
        super.onCleared()
        future?.cancel(false)
    }

}