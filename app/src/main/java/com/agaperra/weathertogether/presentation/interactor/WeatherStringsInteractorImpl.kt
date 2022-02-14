package com.agaperra.weathertogether.presentation.interactor

import android.content.Context
import com.agaperra.weathertogether.R
import com.agaperra.weathertogether.domain.interactor.WeatherStringsInteractor
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class WeatherStringsInteractorImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : WeatherStringsInteractor {

    override val unknown: String
        get() = context.getString(R.string.unknown)

    override val today: String
        get() = context.getString(R.string.today)
}