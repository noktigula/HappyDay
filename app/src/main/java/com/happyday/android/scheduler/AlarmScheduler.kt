package com.happyday.android.scheduler

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.*
import com.happyday.android.AlarmActivity
import com.happyday.android.model.AlarmModel
import com.happyday.android.model.Weekday
import com.happyday.android.utils.loge
import java.util.*
import java.util.concurrent.TimeUnit

class AlarmScheduler(val context: Context) {
    fun scheduleAlarm(alarm: AlarmModel = AlarmModel(
        UUID.randomUUID(),
        time = 100,
        weekdays = setOf(Weekday.Mon, Weekday.Tue, Weekday.Thu),
        enabled = true
    )) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
//        loge("Can schedule alarms? ${alarmManager?.canScheduleExactAlarms()}")
        alarm.weekdays.forEach { day ->
            val pendingIntent = PendingIntent.getActivity(
                context,
                getAlarmRequestCode(day, alarm.hour, alarm.minute),
                Intent(context, AlarmActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                },
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            val calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.DAY_OF_WEEK, day.ordinal)
                set(Calendar.HOUR_OF_DAY, alarm.hour)
                set(Calendar.MINUTE, alarm.minute)
            }
            if (calendar.timeInMillis <= System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_WEEK, 7)
            }

            alarmManager?.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY * 7,
                pendingIntent
            )
        }
        loge("Scheduled!")
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