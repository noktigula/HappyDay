package com.happyday.android.scheduler

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.happyday.android.AlarmActivity

interface AlarmScheduler {
    fun schedule(whenMillis: Long, requestCode: Int)
    fun cancel(requestCode: Int)
}

class AlarmManagerAlarmScheduler(val context: Context) : AlarmScheduler {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager

    override fun schedule(whenMillis: Long, requestCode: Int) {
        val pendingIntent = pendingIntent(requestCode)

        alarmManager?.setAlarmClock(
            AlarmManager.AlarmClockInfo(
                whenMillis,
                pendingIntent
            ),
            pendingIntent
        )
    }

    override fun cancel(requestCode: Int) {
        alarmManager?.cancel(pendingIntent(requestCode, PendingIntent.FLAG_CANCEL_CURRENT))
    }

    private fun pendingIntent(requestCode: Int, flag: Int=PendingIntent.FLAG_UPDATE_CURRENT) = PendingIntent.getActivity(
        context,
        requestCode,
        Intent(context, AlarmActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra("alarm_id", requestCode)
        },
        PendingIntent.FLAG_UPDATE_CURRENT
    )
}