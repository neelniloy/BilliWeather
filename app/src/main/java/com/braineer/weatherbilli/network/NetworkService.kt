package com.braineer.weatherbilli.network


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.birjuvachhani.locus.*
import com.braineer.weatherbilli.models.CurrentModel
import com.braineer.weatherbilli.models.ForecastModel
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Url
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates


const val weather_api_key = "23af92282bb962583c907e205e353249"
const val base_url = "https://api.openweathermap.org/data/2.5/"
const val icon_prefix = "https://openweathermap.org/img/wn/"
const val icon_suffix = "@2x.png"

fun getFormattedDate(dt: Long, pattern: String) =
    SimpleDateFormat(pattern).format(Date(dt * 1000))


@RequiresApi(Build.VERSION_CODES.M)
@SuppressLint("MissingPermission")
fun detectUserLocation(activity : Activity, callback: (Location) -> Unit) {

    if (isOnline(activity)) {
        Locus.getCurrentLocation(activity) { result ->
            result.location?.let {
                callback(it)
            }
            result.error?.let {
                when {
                    it.isDenied -> { /* Permission denied */
                    }
                    it.isPermanentlyDenied -> { /* Permission is permanently denied */
                    }
                    it.isFatal -> { /* Something else went wrong! */
                    }
                    it.isSettingsDenied -> { /* Settings resolution denied by the user */
                    }
                    it.isSettingsResolutionFailed -> { /* Settings resolution failed! */
                    }
                }
            }
        }
    } else {
        Snackbar.make(activity.findViewById(android.R.id.content), "No Internet Connection", Snackbar.LENGTH_INDEFINITE)
            .setAction("Retry") {

                detectUserLocation(activity){

                }

            }.show()
    }

}


@RequiresApi(Build.VERSION_CODES.M)
fun isOnline(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val capabilities =
        connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
    if (capabilities != null) {
        when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                return true
            }
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                return true
            }
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                return true
            }
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI_AWARE) -> {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                return true
            }
            else -> {
                return true
            }
        }
    }
    return false
}

val retrofit = Retrofit.Builder()
    .baseUrl(base_url)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

interface WeatherServiceApi {
    @GET()
    suspend fun getCurrentWeatherData(@Url endUrl: String): CurrentModel

    @GET()
    suspend fun getForecastWeatherData(@Url endUrl: String): ForecastModel
}

object NetworkService {
    val weatherServiceApi: WeatherServiceApi by lazy {
        retrofit.create(WeatherServiceApi::class.java)
    }
}
