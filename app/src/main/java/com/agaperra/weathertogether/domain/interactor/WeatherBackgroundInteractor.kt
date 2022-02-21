package com.agaperra.weathertogether.domain.interactor

interface WeatherBackgroundInteractor {
    val cloudsBackground: Int
    val drizzleBackground: Int
    val foggyBackground: Int
    val rainBackground: Int
    val sunBackground: Int
    val thunderstormBackground: Int
    val snowBackground: Int
}