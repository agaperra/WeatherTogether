package com.agaperra.weathertogether.data.api.dto.city_search

import com.google.gson.annotations.SerializedName

data class CitiesResponse(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("metadata")
    val metadata: Metadata
)