package com.braineer.weatherbilli

import android.app.Activity
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.braineer.weatherbilli.adapter.ForecastAdapter
import com.braineer.weatherbilli.databinding.FragmentWeatherBinding
import com.braineer.weatherbilli.models.CurrentModel
import com.braineer.weatherbilli.network.detectUserLocation
import com.braineer.weatherbilli.network.getFormattedDate
import com.braineer.weatherbilli.network.icon_prefix
import com.braineer.weatherbilli.network.icon_suffix
import com.braineer.weatherbilli.prefs.WeatherPreference
import com.braineer.weatherbilli.utils.capitalizeWords
import com.braineer.weatherbilli.viewmodels.LocationViewModel
import com.bumptech.glide.Glide
import com.ferfalk.simplesearchview.SimpleSearchView
import io.ghyeok.stickyswitch.widget.StickySwitch
import java.io.IOException
import java.util.*
import kotlin.math.roundToInt


class WeatherFragment : Fragment() {
    private val TAG = "WeatherFragment"
    private lateinit var binding: FragmentWeatherBinding
    private lateinit var preference: WeatherPreference
    private val locationViewModel: LocationViewModel by activityViewModels()
    private var tempStatus = false


    private fun convertCityToLatLng(query: String) {
        val geocoder = Geocoder(requireActivity())
        val addressList = geocoder.getFromLocationName(query, 1)
        if (addressList != null) {
            if (addressList.isNotEmpty()) {
                val lat = addressList[0].latitude
                val lng = addressList[0].longitude
                Log.e(TAG, "lat: $lat, lng: $lng")
                val location = Location("").apply {
                    latitude = lat
                    longitude = lng
                }
                locationViewModel.setNewLocation(location)
            }else {
                Toast.makeText(requireActivity(), "Invalid city name", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        preference = WeatherPreference(requireContext())
        binding = FragmentWeatherBinding.inflate(inflater, container, false)
        val adapter = ForecastAdapter()
        val llm = LinearLayoutManager(requireActivity())
        llm.orientation = LinearLayoutManager.HORIZONTAL
        binding.forecastRV.layoutManager = llm
        binding.forecastRV.adapter = adapter

        binding.nestedScroll.visibility = View.GONE
        binding.searchLayout.visibility = View.GONE
        binding.greetingLayout.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE

        locationViewModel.locationLiveData.observe(viewLifecycleOwner) {

            Log.e(TAG, "${it.latitude} ${it.longitude}")
            locationViewModel.fetchData(status = preference.getTempUnitStatus())

            setCurrentCity(it)

        }
        locationViewModel.currentModelLD.observe(viewLifecycleOwner) {
            setCurrentData(it,preference)
            binding.nestedScroll.visibility = View.VISIBLE
            binding.greetingLayout.visibility = View.VISIBLE
            binding.searchLayout.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
        }
        locationViewModel.forecastModelLD.observe(viewLifecycleOwner) {
            Log.e(TAG, "${it.list.size}")
            adapter.submitList(it.list)
        }



        //binding.tempSwitch.isChecked = preference.getTempUnitStatus()
        if (preference.getTempUnitStatus()){
            binding.tempSwitch.setDirection(StickySwitch.Direction.RIGHT, false, false)
            tempStatus = true
        }else{
            binding.tempSwitch.setDirection(StickySwitch.Direction.LEFT, false, false)
            tempStatus = false
        }

        binding.tempSwitch.onSelectedChangeListener = object : StickySwitch.OnSelectedChangeListener {
            override fun onSelectedChange(direction: StickySwitch.Direction, text: String) {

                tempStatus = direction.name != "LEFT"
                preference.setTempUnitStatus(tempStatus)
                locationViewModel.fetchData(tempStatus)
            }
        }


        //setting swiperefreshlistener
        binding.swiperefreshlayout.setOnRefreshListener{

            binding.swiperefreshlayout.isRefreshing = true

            Handler().postDelayed({
                detectUserLocation(requireContext() as Activity) {
                    locationViewModel.setNewLocation(it)
                    Toast.makeText(requireActivity(), "Current Location is Updated", Toast.LENGTH_SHORT).show()
                    binding.swiperefreshlayout.isRefreshing = false
                }
                binding.swiperefreshlayout.isRefreshing = false

            }, 1500)

        }

        binding.swiperefreshlayout.setColorSchemeColors(

            resources.getColor(R.color.material_blue),
            resources.getColor(R.color.green_success),
            resources.getColor(R.color.scrim),

            )

        binding.greeting.text = getGreetingMessage()

        if (getGreetingMessage() == "Good Morning"){
            binding.greetingImage.setImageResource(R.drawable.morning)
        }else if (getGreetingMessage() == "Good Afternoon"){
            binding.greetingImage.setImageResource(R.drawable.afternoon)
        }else if (getGreetingMessage() == "Good Evening"){
            binding.greetingImage.setImageResource(R.drawable.evening)
        }else if (getGreetingMessage() == "Good Night"){
            binding.greetingImage.setImageResource(R.drawable.night)
        }

        binding.searchIcon.setOnClickListener {

            binding.searchView.showSearch()


        }


        binding.searchView.setOnQueryTextListener(object : SimpleSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                Log.d("SimpleSearchView", "Submit:$query")

                convertCityToLatLng(query)

                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                Log.d("SimpleSearchView", "Text changed:$newText")
                return false
            }

            override fun onQueryTextCleared(): Boolean {
                Log.d("SimpleSearchView", "Text cleared")
                return false
            }
        })

        return binding.root
    }


    private fun setCurrentCity(location: Location) {



        locationViewModel.currentModelLD.observe(viewLifecycleOwner) {

                val geocoder = Geocoder(activity!!, Locale.getDefault())
                try {
                    val addresses: List<Address> =
                        geocoder.getFromLocation(location.latitude, location.longitude, 10)!!
                    val address = addresses[0].subLocality
                    val cityName = addresses[0].locality
                    val stateName = addresses[0].adminArea
                    val state = addresses[0].adminArea
                    val country = addresses[0].countryName
                    val postalCode = addresses[0].postalCode
                    val knownName = addresses[0].featureName

                    if (cityName!=null){
                        binding.currentCity.text = cityName
                    }else{
                        binding.currentCity.text = "${it.name}, ${it.sys.country}"
                    }



                } catch (e: IOException) {
                    e.printStackTrace()


                }

        }







    }


    private fun setCurrentData(it: CurrentModel, preference: WeatherPreference) {
        binding.dateTV.text = getFormattedDate(System.currentTimeMillis()/1000, "d MMMM yyyy, EEEE, hh:mm a")

        val iconUrl = "$icon_prefix${it.weather[0].icon}$icon_suffix"
        Glide.with(requireActivity()).load(iconUrl).into(binding.iconIV)
        binding.tempTV.text = "${it.main.temp.roundToInt()}\u00B0"
        binding.feelsLikeTV.text = "Feels like: ${it.main.feelsLike.roundToInt()}°${preference.getTempUnit()}"
        binding.conditionTV.text = "${it.weather[0].description.capitalizeWords()}"
        binding.currentDayTV.text = getDateText()

        binding.maxTemp.text = "${it.main.tempMax.roundToInt()}\u00B0${preference.getTempUnit()}"
        binding.minTemp.text = "${it.main.tempMin.roundToInt()}\u00B0${preference.getTempUnit()}"
        binding.pressureTV.text = "~ ${((it.main.pressure)*0.0145037738).roundToInt()} psi"
        binding.humidityTV.text = "${it.main.humidity} %"
        binding.windSpeed.text = "~ ${preference.getWindSpeedInUnit(it.wind.speed).roundToInt()} km/h"

        binding.sunRise.text = getFormattedDate(it.sys.sunrise, "hh:mm a")
        binding.sunSet.text = getFormattedDate(it.sys.sunset, "hh:mm a")

        setOvercastImage(it)

    }

    private fun setOvercastImage(it: CurrentModel) {
        when(it.weather[0].main) {
            "Thunderstorm" -> {
                binding.overcastImage.setImageResource(R.drawable.thunderstorm)
            }
            "Drizzle" -> {
                binding.overcastImage.setImageResource(R.drawable.drizzle)
            }
            "Rain" -> {
                binding.overcastImage.setImageResource(R.drawable.rain)
            }
            "Snow" -> {
                binding.overcastImage.setImageResource(R.drawable.snow)
            }
            "Clear" -> {
                binding.overcastImage.setImageResource(R.drawable.clear)
            }
            "Clouds" -> {
                binding.overcastImage.setImageResource(R.drawable.clouds)
            }
            else -> {
                binding.overcastImage.setImageResource(R.drawable.haze)
            }
        }
    }


    private fun getDateText():String{
        val c = Calendar.getInstance()

        return when (c.get(Calendar.HOUR_OF_DAY)) {
            in 0..5 -> "Tonight"
            in 6..17 -> "Today"
            in 17..20 -> "Tonight"
            in 21..23 -> "Tonight"
            else -> ""
        }
    }

    private fun getGreetingMessage():String{
        val c = Calendar.getInstance()

        return when (c.get(Calendar.HOUR_OF_DAY)) {
            in 0..5 -> "Good Night"
            in 6..11 -> "Good Morning"
            in 12..16 -> "Good Afternoon"
            in 17..21 -> "Good Evening"
            in 22..23 -> "Good Night"
            else -> "Hello!"
        }
    }

}

