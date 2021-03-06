package com.happyday.android.scheduler

import com.happyday.android.repository.AlarmModel
import com.happyday.android.repository.SingleAlarm
import com.happyday.android.repository.Weekday
import com.happyday.android.utils.*
import java.util.*

class AlarmPlanner(private val scheduler: AlarmScheduler) {
    fun scheduleAlarm(alarm: AlarmModel) {
        alarm.alarms.forEach { (_, singleAlarm) -> scheduleSingleAlarm(singleAlarm) }
    }

    fun updateAlarm(oldAlarm: AlarmModel, newAlarm: AlarmModel) {
        cancel(oldAlarm)
        if (newAlarm.enabled) {
            scheduleAlarm(newAlarm)
        }
    }

    fun cancel(alarm: AlarmModel) {
        alarm.alarms.forEach { (_, singleAlarm) -> scheduler.cancel(singleAlarm.hashCode()) }
    }

    fun snoozeAlarm(alarm:SingleAlarm) {
        //TODO - minute needs to be counted not from alarm but from current time!
        scheduleSingleAlarm(alarm)
    }

    fun scheduleNext(alarm: SingleAlarm) {
        if (alarm.isRepetetive()) {
            scheduleSingleAlarm(alarm)
        }
    }

    fun isToday(alarm: AlarmModel) : Boolean {
        return Calendar.getInstance().timeWillHappenToday(alarm.hour, alarm.minute)
    }

    private fun selectWeekDay(singleAlarm: SingleAlarm) : Int {
        return if (singleAlarm.day != Weekday.None) {
            singleAlarm.day.value
        } else {
            val now = Calendar.getInstance()
            if (now.timeWillHappenToday(singleAlarm.hour, singleAlarm.minute)) {
                now.day()
            } else {
                now.add(Calendar.DAY_OF_MONTH, 1)
                now.day()
            }
        }
    }

    private fun scheduleSingleAlarm(singleAlarm: SingleAlarm) {
        val calendar = Calendar.getInstance(Locale.GERMANY).apply {
            timeInMillis = System.currentTimeMillis()
            firstDayOfWeek = Calendar.MONDAY
            set(Calendar.DAY_OF_WEEK, selectWeekDay(singleAlarm))
            set(Calendar.HOUR_OF_DAY, singleAlarm.hour)
            set(Calendar.MINUTE, singleAlarm.minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        //
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_WEEK, 7)
        }

        loge("Alarm scheduled at ${calendar.get(Calendar.DAY_OF_MONTH)} ${calendar.hour()}:${calendar.minute()}")
        scheduler.schedule(calendar.timeInMillis, singleAlarm.hashCode())
    }
}

private fun SingleAlarm.isRepetetive() = day != Weekday.None