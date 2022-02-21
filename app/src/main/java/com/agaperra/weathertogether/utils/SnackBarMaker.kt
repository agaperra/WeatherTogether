package com.agaperra.weathertogether.utils

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import com.agaperra.weathertogether.R
import com.google.android.material.snackbar.Snackbar


class SnackBarMaker {
    companion object {

        fun createAndShowSnackBar(layoutInflater: LayoutInflater, snackbar: Snackbar) {
            @SuppressLint("InflateParams")
            val customSnackView: View =
                layoutInflater.inflate(R.layout.rounded, null)
            snackbar.view.setBackgroundColor(Color.TRANSPARENT)
            val snackbarLayout = snackbar.view as Snackbar.SnackbarLayout

            snackbarLayout.setPadding(30, 30, 30, 30)
            snackbarLayout.addView(customSnackView, 0)
            snackbar.show()
        }
    }
}