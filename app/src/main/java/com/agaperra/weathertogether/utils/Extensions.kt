package com.agaperra.weathertogether.utils

import android.view.View
import androidx.lifecycle.LifecycleCoroutineScope
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlin.math.abs

fun String.addTempPrefix() = when {
    toInt() > 0 -> "+$this"
    abs(toInt()) == 0 -> "${this.toInt()}"
    else -> this
}

fun <A, B> Pair<A, B>.compare(value: Pair<A, B>): Boolean {
    return this.first == value.first && this.second == value.second
}

fun <T> Flow<T>.launchWhenStarted(lifeCycleScope: LifecycleCoroutineScope) = lifeCycleScope
    .launchWhenStarted {
        this@launchWhenStarted.collect()
    }

fun View.showSnackBar(stringResource: Int, length: Int) =
    Snackbar.make(this, resources.getString(stringResource), length)