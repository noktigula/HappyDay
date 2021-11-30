package com.happyday.android.model

import android.net.Uri
import java.util.*

enum class Weekday {
    Mon,
    Tue,
    Wed,
    Thu,
    Fri,
    Sat,
    Sun
}

data class AlarmModel(
    val id: UUID,
    val title: String = "",
    val time: Int, // number of seconds since midnight
    val weekdays: Set<Weekday>,
    val vibrate: Boolean = true,
    val sound: Uri? = null,
    val enabled: Boolean,
    val hour: Int = 21,
    val minute: Int = 52
)
