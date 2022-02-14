package com.agaperra.weathertogether.utils

import kotlin.math.abs

fun String.addTempPrefix() = when {
    toInt() > 0 -> "+$this"
    abs(toInt()) == 0 -> "${this.toInt()}"
    else -> this
}