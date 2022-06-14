package com.braineer.weatherbilli.repos


import android.location.Location
import com.braineer.weatherbilli.models.CurrentModel
import com.braineer.weatherbilli.models.ForecastModel
import com.braineer.weatherbilli.network.NetworkService
import com.braineer.weatherbilli.network.weather_api_key

class WeatherRepository {
    suspend fun fetchCurrentWeatherData(location: Location, status: Boolean = false): CurrentModel {
        val unit = if (status) "imperial" else "metric"
        val endUrl = "weather?lat=${location.latitude}&lon=${location.longitude}&units=$unit&appid=$weather_api_key"
        return NetworkService.weatherServiceApi.getCurrentWeatherData(endUrl)
    }

    suspend fun fetchForecastWeatherData(location: Location, status: Boolean = false): ForecastModel {
        val unit = if (status) "imperial" else "metric"
        val endUrl = "forecast?lat=${location.latitude}&lon=${location.longitude}&units=$unit&appid=$weather_api_key"
        return NetworkService.weatherServiceApi.getForecastWeatherData(endUrl)
    }
}