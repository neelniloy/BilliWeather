package com.braineer.weatherbilli.prefs

import android.content.Context
import android.content.SharedPreferences
import android.location.Location

class WeatherPreference(context: Context) {
    private lateinit var preference: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private val tempStatus = "status"
    private val tempLocation = "location"
    init {
        preference = context.getSharedPreferences("weather_pref", Context.MODE_PRIVATE)
        editor = preference.edit()
    }

    fun setTempUnitStatus(status: Boolean) {
        editor.putBoolean(tempStatus, status)
        editor.commit()
    }

    fun setTempLocation(location: String) {
        editor.putString(tempLocation, location)
        editor.commit()
    }

    fun getTempLocation() : String? {
        return preference.getString(tempLocation,null)
    }

    fun getTempUnitStatus() : Boolean{
        return preference.getBoolean(tempStatus, false)
    }

    fun getWindSpeedInUnit(speed : Double): Double {
        return if (getTempUnitStatus()) {
            speed*1.609
        }else{
            speed*3.6
        }
    }

    fun getTempUnit() : String{
        return if (getTempUnitStatus()) {
            "F"
        }else{
            "C"
        }
    }
}