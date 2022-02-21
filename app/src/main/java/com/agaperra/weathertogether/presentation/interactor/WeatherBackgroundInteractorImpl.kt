package com.agaperra.weathertogether.presentation.interactor

import com.agaperra.weathertogether.R
import com.agaperra.weathertogether.domain.interactor.WeatherBackgroundInteractor
import com.agaperra.weathertogether.domain.interactor.WeatherIconsInteractor
import javax.inject.Inject

class WeatherBackgroundInteractorImpl @Inject constructor() : WeatherBackgroundInteractor {

    override val cloudsBackground: Int
        get() = R.drawable.back_cloudy

    override val drizzleBackground: Int
        get() = R.drawable.back_drizzle

    override val foggyBackground: Int
        get() = R.drawable.back_windy

    override val rainBackground: Int
        get() = R.drawable.back_rainy

    override val sunBackground: Int
        get() = R.drawable.back_sunny

    override val thunderstormBackground: Int
        get() = R.drawable.back_thunderstorm

    override val snowBackground: Int
        get() = R.drawable.back_snow


}