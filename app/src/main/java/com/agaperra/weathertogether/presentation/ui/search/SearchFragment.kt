package com.agaperra.weathertogether.presentation.ui.search

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.transition.TransitionManager
import com.agaperra.weathertogether.R
import com.agaperra.weathertogether.databinding.FragmentSearchBinding
import com.agaperra.weathertogether.databinding.ItemCityBinding
import com.agaperra.weathertogether.domain.model.AppState
import com.agaperra.weathertogether.domain.model.CityItem
import com.agaperra.weathertogether.domain.model.ForecastDay
import com.agaperra.weathertogether.presentation.adapter.cities.CitiesListAdapter
import com.agaperra.weathertogether.utils.launchWhenStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class SearchFragment : Fragment(R.layout.fragment_search) {

    private val searchViewModel: SearchViewModel by activityViewModels()

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val citiesListAdapter by lazy(LazyThreadSafetyMode.NONE) {
        CitiesListAdapter(activity, requireContext(), searchViewModel, this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        doInitialization()
        setSearching()
    }

    private fun doInitialization() {
        binding.emptyList.visibility = View.VISIBLE
        binding.arrowBack.setOnClickListener {
            requireView().findNavController().navigateUp()
        }
        startObserve(searchViewModel.searchedCitiesList)
        binding.rvCities.adapter = citiesListAdapter
    }

    private fun setResult(result: AppState<List<CityItem>>) {
        when (result) {
            is AppState.Error -> {
                binding.progressBar.isVisible = false
            }
            is AppState.Loading -> {
                binding.progressBar.isVisible = true
            }
            is AppState.Success -> {
                binding.progressBar.isVisible = false
                when(result.data?.isEmpty()){
                    true -> binding.emptyList.visibility = View.VISIBLE
                    else ->{
                        binding.emptyList.visibility = View.INVISIBLE
                        citiesListAdapter.submitList(result.data)
                    }
                }

            }
        }
    }

    private fun setSearching() {

        binding.searchView.setOnSearchClickListener {

            changeSearchViewSize(0.7)
            binding.searchTitle.visibility = View.INVISIBLE
        }
        binding.searchView.setOnCloseListener {

            changeSearchViewSize(0.15)
            binding.searchTitle.visibility = View.VISIBLE
            false
        }

        binding.searchView.apply {
            setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener,
                SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    return when (searchViewModel.searchTextState.value.trim()) {
                        "" -> false
                        else -> {
                            citiesListAdapter.submitList(listOf())
                            searchViewModel.updateTextState(searchViewModel.searchTextState.value.trim())
                            searchViewModel.getCitiesList()
                            onActionViewCollapsed()
                            changeSearchViewSize(0.15)
                            binding.searchTitle.visibility = View.VISIBLE
                            binding.searchView.setOnCloseListener { false }
                            true
                        }
                    }
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    searchViewModel.updateTextState(newText)
                    //searchViewModel.getCitiesList()
                    return true
                }
            })
        }
    }

    private fun changeSearchViewSize(size: Double) {

        val metrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(metrics)

        val sceneRoot = binding.container as ViewGroup
        val square: View = sceneRoot.findViewById(R.id.searchView)
        val newSquareSize = metrics.widthPixels * size

        TransitionManager.beginDelayedTransition(sceneRoot)

        val params = square.layoutParams
        params.width = newSquareSize.toInt()
        square.layoutParams = params
    }

    private fun startObserve(contentSource: StateFlow<AppState<List<CityItem>>>) {

        contentSource.onEach { result ->
            setResult(result)
        }.launchWhenStarted(lifecycleScope)
    }

    fun startObserveDay(contentSource: StateFlow<AppState<ForecastDay>>, binding: ItemCityBinding, context: Context) {

        contentSource.onEach { result ->
            citiesListAdapter.setResult(result, binding, context)
        }.launchWhenStarted(lifecycleScope)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}