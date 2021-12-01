package com.happyday.android.model

import android.net.Uri
import java.util.*
import kotlin.collections.HashMap

enum class Weekday constructor(val value: Int) {
    Mon(Calendar.MONDAY),
    Tue(Calendar.TUESDAY),
    Wed(Calendar.WEDNESDAY),
    Thu(Calendar.THURSDAY),
    Fri(Calendar.FRIDAY),
    Sat(Calendar.SATURDAY),
    Sun(Calendar.SUNDAY)
}

data class AlarmModel(
    val id: UUID = UUID.randomUUID(),
    val title: String = "",
    val vibrate: Boolean = true,
    val sound: Uri? = null,
    val enabled: Boolean = true,
    val hour: Int = 0,
    val minute: Int = 0,
    val alarms: Map<Weekday, SingleAlarm> = mapOf()
)

data class SingleAlarm(
    val parentId: UUID,
    val day: Weekday,
    val hour: Int, // always same as parent
    val minute: Int, // always same as parent
) {
    override fun hashCode(): Int = day.ordinal * 2000 + hour + minute
    override fun equals(other: Any?): Boolean {
        return if (other is SingleAlarm) {
            parentId == other.parentId &&
            day.value == other.day.value &&
            hour == other.hour &&
            minute == other.minute
        } else false
    }
}
