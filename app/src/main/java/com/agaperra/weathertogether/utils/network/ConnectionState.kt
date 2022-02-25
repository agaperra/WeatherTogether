package com.agaperra.weathertogether.utils.network

sealed class ConnectionState {
    object Available : ConnectionState()
    object Unavailable : ConnectionState()
}