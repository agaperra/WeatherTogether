package com.agaperra.weathertogether.presentation.ui.main

import androidx.lifecycle.ViewModel
import com.agaperra.weathertogether.utils.network.NetworkStatusListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@HiltViewModel
@ExperimentalCoroutinesApi
class MainViewModel @Inject constructor(
    networkStatusListener: NetworkStatusListener
) : ViewModel() {

}