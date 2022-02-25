package com.agaperra.weathertogether.presentation.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agaperra.weathertogether.domain.model.AppState
import com.agaperra.weathertogether.domain.model.CityItem
import com.agaperra.weathertogether.domain.model.ForecastDay
import com.agaperra.weathertogether.domain.use_case.GetCityList
import com.agaperra.weathertogether.domain.use_case.GetDayForecast
import com.agaperra.weathertogether.utils.Constants.CITY_CARD_ANIMATION_DURATION
import com.agaperra.weathertogether.utils.network.NetworkStatusListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getCityList: GetCityList,
    private val getDayForecast: GetDayForecast
) : ViewModel() {

    private val _searchTextState = MutableStateFlow("")
    val searchTextState = _searchTextState.asStateFlow()


    private val _dayForecast = MutableStateFlow<AppState<ForecastDay>>(AppState.Loading())
    val dayForecast = _dayForecast.asStateFlow()

    private val _searchedCitiesList =
        MutableStateFlow<AppState<List<CityItem>>>(AppState.Success(listOf()))
    val searchedCitiesList = _searchedCitiesList.asStateFlow()

    fun getCitiesList() = getCityList(searchTextState.value).onEach { result ->
        _searchedCitiesList.value = result
    }.launchIn(viewModelScope)


    fun getForecast(coordinates: Pair<Double, Double>) {
        _dayForecast.value = AppState.Loading()
        getDayForecast(
            coordinates.first,
            coordinates.second
        ).onEach { result ->
            delay(CITY_CARD_ANIMATION_DURATION)
            _dayForecast.value = result
        }.launchIn(viewModelScope)
    }


    fun updateTextState(text: String) {
        _searchTextState.value = text
    }


}