package com.braineer.weatherbilli.utils

fun String.capitalizeWords(): String = split(" ").map { it.capitalize() }.joinToString(" ")