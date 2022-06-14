package com.braineer.weatherbilli.viewmodels

import android.location.Location
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.braineer.weatherbilli.models.CurrentModel
import com.braineer.weatherbilli.models.ForecastModel
import com.braineer.weatherbilli.repos.WeatherRepository
import kotlinx.coroutines.launch

class LocationViewModel : ViewModel(){
    val repository = WeatherRepository()
    val locationLiveData: MutableLiveData<Location> = MutableLiveData()
    val currentModelLD: MutableLiveData<CurrentModel> = MutableLiveData()
    val forecastModelLD: MutableLiveData<ForecastModel> = MutableLiveData()

    fun setNewLocation(location: Location) {
        locationLiveData.value = location
    }

    fun getLastLocation(): Location {
        return locationLiveData.value!!
    }

    fun fetchData(status: Boolean = false) {
        viewModelScope.launch {
            try {
                currentModelLD.value = repository.fetchCurrentWeatherData(locationLiveData.value!!, status = status)
                forecastModelLD.value = repository.fetchForecastWeatherData(locationLiveData.value!!, status = status)
            }catch (e: Exception) {
                Log.e("LocationViewModel", e.localizedMessage)
            }
        }
    }
}

