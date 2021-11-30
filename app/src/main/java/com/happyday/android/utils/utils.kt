package com.happyday.android.utils

import android.content.Context
import android.text.format.DateUtils
import com.happyday.android.R
import java.util.concurrent.TimeUnit

infix fun Int.hours(minutes: Int) : Int {
    return this * 60 * 60 + minutes
}

fun Int.minutes() : Int = this * 60
