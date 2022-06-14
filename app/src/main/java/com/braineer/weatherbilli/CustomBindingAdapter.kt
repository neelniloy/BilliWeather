package com.braineer.weatherbilli

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.braineer.weatherbilli.network.getFormattedDate
import com.braineer.weatherbilli.network.icon_prefix
import com.braineer.weatherbilli.network.icon_suffix
import com.braineer.weatherbilli.prefs.WeatherPreference
import com.braineer.weatherbilli.utils.capitalizeWords
import java.util.*
import kotlin.math.roundToInt

@BindingAdapter("app:setTemp")
fun setTemp(tv: TextView, temp: Double) {
    tv.text = "${temp.roundToInt()}\u00B0${WeatherPreference(tv.context).getTempUnit()}"
}

@BindingAdapter("app:setDay")
fun setDay(tv: TextView, dt: Long) {
    tv.text = getFormattedDate(dt, "EEEE")
}

@BindingAdapter("app:setDayTime")
fun setDayTime(tv: TextView, dt: Long) {
    tv.text = getFormattedDate(dt, "hh:mm a")
}

@BindingAdapter("app:setCapitalize")
fun setCapitalize(tv: TextView, text : String) {
    tv.text = text.capitalizeWords()
}

@BindingAdapter("app:setIcon")
fun setIcon(imageView: ImageView, icon: String?) {
    icon?.let {
        val iconUrl = "$icon_prefix$icon$icon_suffix"
        Glide.with(imageView.context)
            .load(iconUrl).into(imageView)
    }
}