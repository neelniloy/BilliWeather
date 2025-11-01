package com.braineer.weatherbilli

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.braineer.weatherbilli.network.detectUserLocation
import com.braineer.weatherbilli.network.isOnline
import com.braineer.weatherbilli.prefs.WeatherPreference
import com.braineer.weatherbilli.viewmodels.LocationViewModel
import com.google.android.material.snackbar.Snackbar


class MainActivity : AppCompatActivity() {
    private val locationViewModel: LocationViewModel by viewModels()
    private lateinit var preference: WeatherPreference

    @RequiresApi(Build.VERSION_CODES.M)
    val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    getLocation()
                    //Toast.makeText(this, "fine location granted", Toast.LENGTH_SHORT).show()
                }
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    getLocation()
                    //Toast.makeText(this, "course location granted", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            requestUserForLocationPermission(this)
            if (isLocationPermissionGranted(this)) {
                getLocation()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // For Android 15+ - use WindowInsetsControllerCompat instead
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = true  // For light status bar icons
        insetsController.isAppearanceLightNavigationBars = true  // For light nav bar icons

        preference = WeatherPreference(this)

        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("MissingPermission")
    private fun getLocation() {


        if (isOnline(this)){

            val latlong = preference.getTempLocation()?.split(",")?.toTypedArray()

            if (latlong!=null){
                val latitude = latlong[0].toDouble()
                val longitude = latlong[1].toDouble()

                val location = Location("")
                location.latitude = latitude
                location.longitude = longitude

                locationViewModel.setNewLocation(location)
                Log.e(TAG, "${location.latitude} ${location.longitude}")

            }else{
                detectUserLocation(this) {
                    locationViewModel.setNewLocation(it)
                }
            }

        }else{
            Snackbar.make(this.findViewById(android.R.id.content), "No Internet Connection", Snackbar.LENGTH_INDEFINITE)
                .setAction("Retry") {

                    getLocation()

                }.show()
        }


    }

    fun isLocationPermissionGranted(context: Context): Boolean {
        return ContextCompat
            .checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat
                    .checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) ==
                PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("NewApi")
    fun requestUserForLocationPermission(activity: Activity) {
        activity.requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            101
        )

    }

    override fun onPause() {
        super.onPause()

        locationViewModel.locationLiveData.observe(this) {

            val latitude = locationViewModel.getLastLocation().latitude
            val longitude = locationViewModel.getLastLocation().longitude

            preference.setTempLocation("${latitude},${longitude}")

        }

    }

}

