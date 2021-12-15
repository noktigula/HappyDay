package com.happyday.android.alarmedit

import android.net.Uri
import com.happyday.android.repository.AlarmModel
import com.happyday.android.repository.SingleAlarm
import com.happyday.android.repository.Weekday
import java.util.*

data class MutableModel(
    var id: UUID? = null,
    var title: String? = "",
    var vibrate: Boolean = false,
    var sound: Uri? = null,
    var enabled: Boolean = false,
    var hour: Int,
    var minute: Int,
    var alarms: MutableSet<Weekday> = mutableSetOf()
) {
    companion object {
        fun fromAlarm(model: AlarmModel) =
            MutableModel(
                id = model.id,
                title = model.title,
                vibrate = model.vibrate,
                sound = model.sound,
                enabled = model.enabled,
                hour = model.hour,
                minute = model.minute,
                alarms = model.alarms.keys.toMutableSet()
            )
    }

    fun toAlarm() : AlarmModel {
        val id = id ?: UUID.randomUUID()
        val finalAlarms = if (alarms.isEmpty()) {
            mapOf(Weekday.None to SingleAlarm(id, Weekday.None, hour, minute))
        } else {
            alarms.associateWith { day -> SingleAlarm(id, day, hour, minute) }
        }
        return AlarmModel(
            id ?: UUID.randomUUID(),
            title ?: "",
            vibrate,
            sound,
            enabled,
            hour,
            minute,
            finalAlarms
        )
    }
}