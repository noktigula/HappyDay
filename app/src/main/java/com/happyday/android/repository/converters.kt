package com.happyday.android.repository

import android.net.Uri
import androidx.room.TypeConverter
import com.happyday.android.utils.loge
import java.lang.StringBuilder
import java.util.*

class UUIDConverter {
    @TypeConverter
    fun uuidToString(value: UUID) : String {
        return value.toString()
    }

    @TypeConverter
    fun stringToUUID(value: String) : UUID {
        return UUID.fromString(value)
    }
}

class UriConverter {
    @TypeConverter
    fun uriToString(uri: Uri?) : String {
        return uri?.toString() ?: ""
    }

    @TypeConverter
    fun stringToUri(uri: String) : Uri? {
        return if(uri == "") null else Uri.parse(uri)
    }
}

class DayAlarmsConverter {
    @TypeConverter
    fun mapToString(daysMap: Map<Weekday, SingleAlarm>) : String {
        val sb = StringBuilder()
        daysMap.forEach { (day, singleAlarm) ->
            sb.append("#${day}|${singleAlarm.parentId}|${singleAlarm.hour}|${singleAlarm.minute}")
        }
        return sb.toString()
    }

    @TypeConverter
    fun stringToMap(daysString: String) : Map<Weekday, SingleAlarm> {
        val res = mutableMapOf<Weekday, SingleAlarm>()
        val allEntries = daysString.split("#").filter { it.isNotBlank() }
        allEntries.forEach { strEntry ->
            val entry = strEntry.split("|").filter{it.isNotBlank()}
            val day = Weekday.valueOf(entry[0])
            val parentId = UUID.fromString(entry[1])
            val hour = entry[2].toInt()
            val minute = entry[3].toInt()
            res[day] = SingleAlarm(parentId, day, hour, minute)
        }
        return res
    }
}