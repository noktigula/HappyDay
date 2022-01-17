package com.happyday.android.repository

import android.media.RingtoneManager
import android.net.Uri
import androidx.room.*
import java.util.*

enum class Weekday constructor(val value: Int) {
    Mon(Calendar.MONDAY),
    Tue(Calendar.TUESDAY),
    Wed(Calendar.WEDNESDAY),
    Thu(Calendar.THURSDAY),
    Fri(Calendar.FRIDAY),
    Sat(Calendar.SATURDAY),
    Sun(Calendar.SUNDAY),
    None(100);

    companion object {
        //TODO add support for i18n
        fun weekdays() = arrayOf(Mon, Tue, Wed, Thu, Fri, Sat, Sun)
    }
}

typealias AllAlarms = List<AlarmModel>
typealias AlarmsByMinute = HashMap<Int, SingleAlarm>

@Entity(tableName = ALARMS_TABLE)
@TypeConverters(UUIDConverter::class, UriConverter::class, DayAlarmsConverter::class)
data class AlarmModel(
    @PrimaryKey(autoGenerate = false)
    val id: UUID,
    val title: String,
    val vibrate: Boolean,
    val sound: Uri?,
    val enabled: Boolean,
    val hour: Int,
    val minute: Int,
    val alarms: Map<Weekday, SingleAlarm>
) {
    constructor()  : this(UUID.randomUUID(), "", false, null, true, 0, 0, mapOf())
}

data class SingleAlarm(
    val parentId: UUID,
    val day: Weekday,
    val hour: Int, // always same as parent
    val minute: Int, // always same as parent
) {
    /**
     * Simple and straightforward way of calculating request code
     * Each minute in a week gets it's own id
     * In case of multiple alarms set for the same time, only last one will be executed
     */
    override fun hashCode(): Int = day.ordinal * 2000 + hour*100 + minute

    override fun equals(other: Any?): Boolean {
        return if (other is SingleAlarm) {
            parentId == other.parentId &&
            day.value == other.day.value &&
            hour == other.hour &&
            minute == other.minute
        } else false
    }
}
