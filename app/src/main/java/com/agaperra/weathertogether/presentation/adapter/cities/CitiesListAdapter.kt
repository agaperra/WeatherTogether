package com.agaperra.weathertogether.presentation.adapter.cities

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContentProviderCompat
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import com.agaperra.weathertogether.R
import com.agaperra.weathertogether.databinding.ItemCityBinding
import com.agaperra.weathertogether.domain.model.AppState
import com.agaperra.weathertogether.domain.model.CityItem
import com.agaperra.weathertogether.domain.model.ForecastDay
import com.agaperra.weathertogether.presentation.ui.search.SearchFragment
import com.agaperra.weathertogether.presentation.ui.search.SearchViewModel
import com.agaperra.weathertogether.utils.SnackBarMaker
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.ExperimentalCoroutinesApi


@ExperimentalCoroutinesApi
class CitiesListAdapter(
    activity: FragmentActivity?,
    context: Context,
    viewModel: SearchViewModel,
    fragment: SearchFragment
) :
    ListAdapter<CityItem, CitiesListAdapter.CitiesListViewHolder>(
        CitiesListDiffUtil()
    ) {

    val adapterActivity = activity

    val adapterContext = context

    val adapterViewModel = viewModel

    private val adapterFragment = fragment

    var expanded = false

    inner class CitiesListViewHolder(private val binding: ItemCityBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(itemPosition: Int) {

            val city = getItem(itemPosition)



            binding.cityName.text = city.name
            binding.cityLatLon.text =
                "%.4f".format(city.latitude) + " " + "%.4f".format(city.longitude)

            binding.arrowDownUp.setOnClickListener {

                when (expanded) {
                    false -> {
                        expanded = true

                        changeSearchViewSize(
                            adapterContext.resources.getDimension(R.dimen.city_view_max_height),
                            binding,
                            adapterActivity
                        )
                        getDayForecast(
                            adapterViewModel,
                            Pair(city.latitude, city.longitude),
                            binding,
                            adapterContext
                        )
                        binding.arrowDownUp.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24)

                    }
                    else -> {
                        expanded = false

                        binding.arrowDownUp.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
                        binding.bigExpandedGroup.visibility = View.INVISIBLE

                        changeSearchViewSize(
                            adapterContext.resources.getDimension(R.dimen.city_view_min_height),
                            binding,
                            adapterActivity
                        )
                    }
                }

            }


        }
    }

    private fun changeSearchViewSize(
        size: Float,
        binding: ItemCityBinding,
        activity: FragmentActivity?
    ) {

        val metrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(metrics)

        val sceneRoot = binding.cardCity as ViewGroup

        TransitionManager.beginDelayedTransition(sceneRoot)

        val params = sceneRoot.layoutParams
        params.height = size.toInt()
        sceneRoot.layoutParams = params

    }

    private fun getDayForecast(
        searchViewModel: SearchViewModel,
        coordinates: Pair<Double, Double>,
        binding: ItemCityBinding,
        context: Context
    ) {
        searchViewModel.getForecast(coordinates)
        adapterFragment.startObserveDay(searchViewModel.dayForecast, binding, context)
    }


    @SuppressLint("SetTextI18n")
    fun setResult(result: AppState<ForecastDay>, binding: ItemCityBinding, context: Context) {
        when (result) {
            is AppState.Error -> {
                val snackbar =
                    Snackbar.make(
                        binding.root,
                        R.string.error,
                        Snackbar.LENGTH_SHORT
                    )
                val font =
                    Typeface.createFromAsset(context.assets, "raleway_regular.ttf")
                snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text).typeface =
                    font
                SnackBarMaker.createAndShowSnackBar(LayoutInflater.from(context), snackbar)

                expanded = false

                binding.arrowDownUp.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
                binding.bigExpandedGroup.visibility = View.INVISIBLE

                changeSearchViewSize(
                    adapterContext.resources.getDimension(R.dimen.city_view_min_height),
                    binding,
                    adapterActivity
                )
            }
            is AppState.Loading -> {

            }
            is AppState.Success -> {


                binding.iconDay.setImageResource(result.data?.dayIcon ?: R.drawable.ic_sunny)
                binding.bigExpandedGroup.visibility = View.VISIBLE
                binding.dateDay.text = result.data?.dayName
                binding.tempScoreDay.text = result.data?.dayTemp
                binding.feelsLikeDay.text = result.data?.tempFeelsLike
                binding.humidityDay.text = result.data?.dayHumidity
                binding.pressureDay.text = result.data?.dayPressure
                binding.sunriseDay.text = result.data?.sunrise
                binding.sunsetDay.text = result.data?.sunset

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        CitiesListViewHolder(
            ItemCityBinding.bind(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_city,
                    parent,
                    false
                )
            )
        )

    override fun onBindViewHolder(holder: CitiesListViewHolder, position: Int) =
        holder.bind(itemPosition = position)



}