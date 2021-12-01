package com.happyday.android.utils

import android.content.Context
import com.happyday.android.R
import com.happyday.android.model.AlarmModel

fun AlarmModel.readableTime(context: Context) : String {
    return context.resources.getString(R.string.time_format_24hrs, hour, minute)
}
