package com.agaperra.weathertogether.data.api.mapper

import android.content.Context
import android.location.Geocoder
import com.agaperra.weathertogether.data.api.dto.city_search.CitiesResponse
import com.agaperra.weathertogether.data.api.dto.day_forecast.DayForecastResponse
import com.agaperra.weathertogether.data.api.dto.week_forecast.Daily
import com.agaperra.weathertogether.data.api.dto.week_forecast.ForecastResponse
import com.agaperra.weathertogether.domain.interactor.WeatherBackgroundInteractor
import com.agaperra.weathertogether.domain.interactor.WeatherIconsInteractor
import com.agaperra.weathertogether.domain.interactor.WeatherStringsInteractor
import com.agaperra.weathertogether.domain.model.CityItem
import com.agaperra.weathertogether.domain.model.ForecastDay
import com.agaperra.weathertogether.domain.model.WeatherForecast
import com.agaperra.weathertogether.utils.Constants
import com.agaperra.weathertogether.utils.addTempPrefix
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class DtoToDomain @Inject constructor(
    @ApplicationContext private val context: Context,
    private val weatherIconsInteractor: WeatherIconsInteractor,
    private val weatherBackgroundInteractor: WeatherBackgroundInteractor,
    private val weatherStringsInteractor: WeatherStringsInteractor
) {

    companion object {
        private const val TIME_FORMAT = "HH:mm"
        private const val DATE_FORMAT = "EEE\nd/M"
        private const val TODAY_DATE_FORMAT = "d/M"

        private const val DOUBLE_NUMBERS_FORMAT = "%.0f"
    }

    private val timeFormatter = SimpleDateFormat(TIME_FORMAT, Locale.getDefault())

    fun map(weekForecast: ForecastResponse) = WeatherForecast(
        location = getLocationName(lat = weekForecast.lat, lon = weekForecast.lon),
        currentWeather = DOUBLE_NUMBERS_FORMAT.format(weekForecast.current.temp)
            .addTempPrefix() + "°",
        currentWindSpeed = DOUBLE_NUMBERS_FORMAT.format(weekForecast.current.wind_speed) + " " + weatherStringsInteractor.ms,
        currentHumidity = "${DOUBLE_NUMBERS_FORMAT.format(weekForecast.current.humidity)} %",
        currentPressure = DOUBLE_NUMBERS_FORMAT.format(weekForecast.current.pressure / 1.333) + "\n" + weatherStringsInteractor.mmHg,
        currentWeatherStatus = if (weekForecast.current.weather.isNotEmpty())
            weekForecast.current.weather[0].description.capitalize()
        else
            weatherStringsInteractor.unknown,
        currentWeatherStatusId = if (weekForecast.current.weather.isNotEmpty())
            selectWeatherStatusBackground(weekForecast.current.weather[0].id.toInt())
        else
            selectWeatherStatusBackground(800),
        forecastDays = weekForecast.daily.map(),
    )

    fun map(dayForecastResponse: DayForecastResponse) = ForecastDay(
        dayName = getTodayDate(),
        dayStatus = dayForecastResponse.weather.first().description,
        dayIcon = selectWeatherStatusIcon(dayForecastResponse.weather.first().id),
        dayBackground = selectWeatherStatusBackground(dayForecastResponse.weather.first().id),
        dayTemp = DOUBLE_NUMBERS_FORMAT.format(dayForecastResponse.main.temp).addTempPrefix()+ "°",
        dayPressure = DOUBLE_NUMBERS_FORMAT.format(dayForecastResponse.main.pressure / 1.333) + "\n" + weatherStringsInteractor.mmHg,
        dayHumidity = "${dayForecastResponse.main.humidity}%",
        tempFeelsLike = DOUBLE_NUMBERS_FORMAT.format(dayForecastResponse.main.feelsLike).addTempPrefix()+ "°",
        dayWindSpeed = DOUBLE_NUMBERS_FORMAT.format(dayForecastResponse.wind.speed) + " " + weatherStringsInteractor.ms,
        sunrise = timeFormatter.format("${dayForecastResponse.sys.sunrise}000".toLong()),
        sunset = timeFormatter.format("${dayForecastResponse.sys.sunset}000".toLong())
    )

    private fun List<Daily>.map() = mapIndexed { index, day ->
        ForecastDay(
            dayName = getDayName(index),
            dayStatus = if (day.weather.isNotEmpty())
                day.weather[0].description
            else
                weatherStringsInteractor.unknown,
            dayTemp = if (day.weather.isNotEmpty())
                DOUBLE_NUMBERS_FORMAT.format(day.temp.day).addTempPrefix() + "°"
            else
                weatherStringsInteractor.unknown,
            dayStatusId = if (day.weather.isNotEmpty()) day.weather.first().id.toInt() else 800,
            sunrise = timeFormatter.format("${day.sunrise}000".toLong()),
            sunset = timeFormatter.format("${day.sunset}000".toLong()),
            tempFeelsLike = DOUBLE_NUMBERS_FORMAT.format(day.feels_like.day).addTempPrefix() + "°",
            dayPressure = DOUBLE_NUMBERS_FORMAT.format(day.pressure / 1.333)+ "\n" + weatherStringsInteractor.mmHg,
            dayHumidity = day.humidity.toString() + " %",
            dayWindSpeed = day.wind_speed.toString() + " " + weatherStringsInteractor.ms,
            dayIcon = selectWeatherStatusIcon(day.weather.first().id.toInt()),
            dayBackground = selectWeatherStatusBackground(day.weather.first().id.toInt())
        )
    }

    private fun getLocationName(
        lat: Double,
        lon: Double
    ): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        return try {
            val addresses = geocoder.getFromLocation(lat, lon, 1)
            addresses.first().subAdminArea
                ?: addresses.first().adminArea
                ?: addresses.first().locality
        } catch (e: Exception) {
            Timber.e(e)
            weatherStringsInteractor.unknown
        }
    }

    private fun getDayName(dayIndex: Int): String =
        if (dayIndex == 0) "${weatherStringsInteractor.today}\n ${
            SimpleDateFormat(
                TODAY_DATE_FORMAT,
                Locale.getDefault()
            ).format(Calendar.getInstance().time)
        }"
        else {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DATE, dayIndex)
            SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(calendar.time).capitalize()
        }

    private fun getTodayDate() = SimpleDateFormat(
        TODAY_DATE_FORMAT,
        Locale.getDefault()
    ).format(Calendar.getInstance().time)

    private fun selectWeatherStatusIcon(weatherStatusId: Int) = when (weatherStatusId) {
        in Constants.rain_ids_range -> weatherIconsInteractor.rainIcon
        in Constants.clouds_ids_range -> weatherIconsInteractor.cloudsIcon
        in Constants.atmosphere_ids_range -> weatherIconsInteractor.foggyIcon
        in Constants.snow_ids_range -> weatherIconsInteractor.snowIcon
        in Constants.drizzle_ids_range -> weatherIconsInteractor.drizzleIcon
        in Constants.thunderstorm_ids_range -> weatherIconsInteractor.thunderstormIcon
        else -> weatherIconsInteractor.sunIcon
    }

    private fun selectWeatherStatusBackground(weatherStatusId: Int) = when (weatherStatusId) {
        in Constants.rain_ids_range -> weatherBackgroundInteractor.rainBackground
        in Constants.clouds_ids_range -> weatherBackgroundInteractor.cloudsBackground
        in Constants.atmosphere_ids_range -> weatherBackgroundInteractor.foggyBackground
        in Constants.snow_ids_range -> weatherBackgroundInteractor.snowBackground
        in Constants.drizzle_ids_range -> weatherBackgroundInteractor.drizzleBackground
        in Constants.thunderstorm_ids_range -> weatherBackgroundInteractor.thunderstormBackground
        else -> weatherBackgroundInteractor.sunBackground
    }
}


fun CitiesResponse.toDomain(): List<CityItem> = data.map { city ->
    CityItem(
        name = "${city.name}, ${city.country}",
        longitude = city.longitude,
        latitude = city.latitude
    )
}.distinctBy { it.name }