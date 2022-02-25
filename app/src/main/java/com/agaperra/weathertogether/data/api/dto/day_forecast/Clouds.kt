package com.agaperra.weathertogether.data.api.dto.day_forecast


import com.google.gson.annotations.SerializedName

data class Clouds(
    @SerializedName("all")
    val all: Int
)