package com.happyday.android.scheduler

import com.happyday.android.repository.AlarmModel
import com.happyday.android.repository.SingleAlarm
import com.happyday.android.repository.Weekday
import com.happyday.android.utils.*
import java.util.*

class AlarmPlanner(private val scheduler: AlarmScheduler) {
    fun scheduleAlarm(alarm: AlarmModel) {
//        loge("Can schedule alarms? ${alarmManager?.canScheduleExactAlarms()}")
        alarm.alarms.forEach { (_, singleAlarm) -> scheduleSingleAlarm(singleAlarm) }
        loge("Scheduled!")
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

    //TODO get snooze value from some config to simplify debug / release
    fun snoozeAlarm(alarm:SingleAlarm?) {
        //making a new copy will override any other alarm scheduled at snoozed time
        //TODO check case when scheduled for last 10m of hour (i.e. it's 7.55, snoozed by 10m, will it become 8.05?)
        if (alarm == null) {
            loge("Alarm is null, can't snooze!")
            return
        }

        //TODO - minute needs to be counted not from alarm but from current time!
        val snoozedAlarm = alarm.copy(minute = alarm.minute + 1)
        scheduleSingleAlarm(snoozedAlarm)
        loge("Snoozed! $snoozedAlarm")
    }

    fun scheduleNext(alarm: SingleAlarm) {
        if (alarm.isRepetetive()) {
            loge("Scheduling next alarm")
            scheduleSingleAlarm(alarm)
        }
    }

    private fun selectWeekDay(singleAlarm: SingleAlarm) : Int {
        return if (singleAlarm.day != Weekday.None) {
            singleAlarm.day.value
        } else {
            val now = Calendar.getInstance()
            if (now.isToday(singleAlarm.hour, singleAlarm.minute)) {
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