package com.happyday.android.alarm

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.happyday.android.HappyDayApp
import com.happyday.android.utils.loge

class AlarmReceiver : BroadcastReceiver() {
    companion object {
        const val ACTION_ALARM = "com.happyday.alarm.ACTION_ALARM"
    }
    @SuppressLint("WakelockTimeout")
    override fun onReceive(context: Context, intent: Intent) {
        loge("ALarmReceiver: intent=${intent.action}")
        if (intent.action == ACTION_ALARM) {
            (context.applicationContext as HappyDayApp).wakeLock.acquire()
            context.startActivity(Intent(context, AlarmActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                putExtra("alarm_id", intent.getIntExtra("alarm_id", -1))
            })
        }
    }
}