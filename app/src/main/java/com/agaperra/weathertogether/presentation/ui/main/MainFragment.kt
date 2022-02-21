package com.agaperra.weathertogether.presentation.ui.main

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.agaperra.weathertogether.R
import com.agaperra.weathertogether.databinding.FragmentMainBinding
import com.agaperra.weathertogether.domain.model.AppState
import com.agaperra.weathertogether.domain.model.ForecastDay
import com.agaperra.weathertogether.domain.model.WeatherForecast
import com.agaperra.weathertogether.presentation.adapter.WeekForecastListAdapter
import com.agaperra.weathertogether.utils.Constants.REQUEST_CODE_PERMISSIONS
import com.agaperra.weathertogether.utils.SnackBarMaker
import com.agaperra.weathertogether.utils.launchWhenStarted
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import java.util.Arrays.toString

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class MainFragment : Fragment(R.layout.fragment_main), EasyPermissions.PermissionCallbacks {

    private val mainViewModel: MainViewModel by activityViewModels()

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val weekForecastListAdapter by lazy(LazyThreadSafetyMode.NONE) {
        WeekForecastListAdapter(requireContext())
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (!hasLocationPermission()) requestLocationPermission()
        else doInitialization()
    }


    private fun doInitialization() {
        mainViewModel.observeCurrentLocation()
        startObserve(mainViewModel.weatherForecast)
        binding.refresh.setOnRefreshListener {
            refresh(binding.refresh)
        }
        binding.icSearch.setOnClickListener {
            requireView().findNavController()
                .navigate(MainFragmentDirections.openSearch())
        }
    }

    private fun setResult(result: AppState<WeatherForecast>) {
        when (result) {
            is AppState.Error -> {
                binding.progressBarMain.isVisible = false
                binding.iconsGroup.visibility = View.INVISIBLE
                binding.progressBarDaily.isVisible = false
                val snackbar =
                    Snackbar.make(
                        binding.root,
                        R.string.error,
                        Snackbar.LENGTH_LONG
                    )
                val font =
                    Typeface.createFromAsset(requireContext().assets, "raleway_regular.ttf")
                snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text).typeface =
                    font
                SnackBarMaker.createAndShowSnackBar(LayoutInflater.from(context), snackbar)
            }
            is AppState.Loading -> {
                binding.progressBarMain.isVisible = true
                binding.iconsGroup.visibility = View.INVISIBLE
                binding.progressBarDaily.isVisible = true
            }
            is AppState.Success -> {
                binding.progressBarMain.isVisible = false
                binding.iconsGroup.visibility = View.VISIBLE
                binding.progressBarDaily.isVisible = false
                setTodayResult(result)
                weekForecastListAdapter.submitList(result.data?.forecastDays)
            }
        }
    }

    private fun setTodayResult(result: AppState<WeatherForecast>) {

        binding.tempScore.text = result.data?.currentWeather
        binding.locationName.text = result.data?.location
        binding.humidityScore.text = result.data?.currentHumidity
        binding.windSpeed.text = result.data?.currentWindSpeed
        binding.pressureScore.text = result.data?.currentPressure
        binding.mainConstraint.setBackgroundResource(
            result.data?.currentWeatherStatusId ?: R.drawable.back_sunny
        )
        binding.statusName.text = result.data?.currentWeatherStatus

        binding.timeUpdated.text =
            if (mainViewModel.weatherLastUpdate.value == 0)
                resources.getString(R.string.just_updated)
            else resources.getString(
                R.string.updated_time,
                mainViewModel.weatherLastUpdate.value
            )

    }


    private fun startObserve(contentSource: StateFlow<AppState<WeatherForecast>>) {

        contentSource.onEach { result ->
            setResult(result)
        }.launchWhenStarted(lifecycleScope)
    }

    private fun refresh(swipeRefreshLayout: SwipeRefreshLayout) {
        swipeRefreshLayout.isRefreshing = true
        swipeRefreshLayout.postOnAnimationDelayed({
            doInitialization()
            swipeRefreshLayout.isRefreshing = false
        }, 2000)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            SettingsDialog.Builder(requireActivity()).title(R.string.location_permission_denied)
                .build().show()
        } else requestLocationPermission()
        doInitialization()
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        doInitialization()
    }

    private fun hasLocationPermission() =
        EasyPermissions.hasPermissions(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

    private fun requestLocationPermission() {
        EasyPermissions.requestPermissions(
            host = this,
            rationale = resources.getString(R.string.exit_title),
            REQUEST_CODE_PERMISSIONS,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}