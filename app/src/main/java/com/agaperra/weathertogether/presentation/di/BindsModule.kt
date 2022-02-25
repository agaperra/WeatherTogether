package com.agaperra.weathertogether.presentation.di

import com.agaperra.weathertogether.data.repository.CitiesRepositoryImpl
import com.agaperra.weathertogether.data.repository.WeatherRepositoryImpl
import com.agaperra.weathertogether.domain.interactor.WeatherBackgroundInteractor
import com.agaperra.weathertogether.domain.interactor.WeatherIconsInteractor
import com.agaperra.weathertogether.domain.interactor.WeatherStringsInteractor
import com.agaperra.weathertogether.domain.repository.CitiesRepository
import com.agaperra.weathertogether.domain.repository.WeatherRepository
import com.agaperra.weathertogether.presentation.interactor.WeatherBackgroundInteractorImpl
import com.agaperra.weathertogether.presentation.interactor.WeatherIconsInteractorImpl
import com.agaperra.weathertogether.presentation.interactor.WeatherStringsInteractorImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface BindsModule {

    @Binds
    fun bindForecastRepository(forecastRepositoryImpl: WeatherRepositoryImpl): WeatherRepository

    @Binds
    fun bindIconsInteractor(weatherIconsInteractorImpl: WeatherIconsInteractorImpl): WeatherIconsInteractor

    @Binds
    fun bindCityRepository(cityRepositoryImpl: CitiesRepositoryImpl): CitiesRepository

    @Binds
    fun bindBackgroundInteractor(weatherBackgroundInteractor: WeatherBackgroundInteractorImpl): WeatherBackgroundInteractor

    @Binds
    fun bindWeatherStringsInteractor(weatherStringsInteractorImpl: WeatherStringsInteractorImpl): WeatherStringsInteractor

}