package com.happyday.android.scheduler

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.text.format.DateUtils
import android.util.Log
import androidx.work.*
import com.happyday.android.AlarmActivity
import com.happyday.android.model.AlarmModel
import com.happyday.android.model.SingleAlarm
import com.happyday.android.model.Weekday
import com.happyday.android.utils.loge
import java.util.*
import java.util.concurrent.TimeUnit

class AlarmScheduler(val context: Context) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager

    fun scheduleAlarm(alarm: AlarmModel) {
//        loge("Can schedule alarms? ${alarmManager?.canScheduleExactAlarms()}")
        alarm.alarms.forEach { (_, singleAlarm) -> scheduleSingleAlarm(singleAlarm) }
        loge("Scheduled!")
    }

    //TODO get snooze value from some config to simplify debug / release
    fun snoozeAlarm(alarm:SingleAlarm) {
        //making a new copy will override any other alarm scheduled at snoozed time
        //TODO check case when scheduled for last 10m of hour (i.e. it's 7.55, snoozed by 10m, will it become 8.05?)
        val snoozedAlarm = alarm.copy(minute = alarm.minute + 1)
        scheduleSingleAlarm(snoozedAlarm)
    }

    private fun scheduleSingleAlarm(singleAlarm: SingleAlarm) {
        val pendingIntent = PendingIntent.getActivity(
            context,
            singleAlarm.hashCode(),
            Intent(context, AlarmActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                putExtra("alarm_id", singleAlarm.hashCode())
            },
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val calendar = Calendar.getInstance(Locale.GERMANY).apply {
            timeInMillis = System.currentTimeMillis()
            firstDayOfWeek = Calendar.MONDAY
            set(Calendar.DAY_OF_WEEK, singleAlarm.day.value)
            set(Calendar.HOUR_OF_DAY, singleAlarm.hour)
            set(Calendar.MINUTE, singleAlarm.minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        loge("Calendar day: ${calendar.get(Calendar.DAY_OF_WEEK)}")
        loge(DateUtils.formatDateTime(context, calendar.timeInMillis,DateUtils.FORMAT_SHOW_TIME or DateUtils.FORMAT_SHOW_DATE));
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            loge("Scheduled in the")
            calendar.add(Calendar.DAY_OF_WEEK, 7)
        }

        alarmManager?.setAlarmClock(
            AlarmManager.AlarmClockInfo(
                calendar.timeInMillis,
                pendingIntent
            ),
            pendingIntent
        )
    }



    /**
     * Simple and straightforward way of calculating request code
     * Each minute in a week gets it's own id
     * In case of multiple alarms set for the same time, only last one will be executed
     */
    private fun getAlarmRequestCode(day: Weekday, hour: Int, minute: Int) =
        day.ordinal * 2000 + hour * minute
}
//
//internal class AlarmWorker(
//    appContext: Context,
//    workerParams: WorkerParameters
//) : Worker(appContext, workerParams) {
//    override fun doWork(): Result {
//        //TODO play music
//        //TODO vibrate
//        loge("Executed!")
//        val intent =
//        intent.flags =
//        applicationContext.startActivity(intent)
//        return Result.success()
//    }
//}