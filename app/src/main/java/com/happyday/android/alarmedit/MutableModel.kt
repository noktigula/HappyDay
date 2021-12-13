package com.happyday.android.alarmedit

import android.net.Uri
import com.happyday.android.repository.AlarmModel
import com.happyday.android.repository.SingleAlarm
import com.happyday.android.repository.Weekday
import java.util.*

class MutableModel(
    var id: UUID? = null,
    var title: String? = "",
    var vibrate: Boolean = false,
    var sound: Uri? = null,
    var enabled: Boolean = false,
    var hour: Int,
    var minute: Int,
    var alarms: Map<Weekday, SingleAlarm> = mutableMapOf()
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
                alarms = model.alarms
            )
    }

    fun toAlarm() = AlarmModel(
        id ?: UUID.randomUUID(),
        title ?: "",
        vibrate,
        sound,
        enabled,
        hour,
        minute,
        alarms
    )
}