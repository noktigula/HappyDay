package com.happyday.android

import android.app.Application
import android.content.Context
import android.os.PowerManager

class HappyDayApp : Application() {
    companion object {
        const val WAKE_LOCK_TAG = "happyday:WakeLock"
    }
    val wakeLock: PowerManager.WakeLock by lazy {
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKE_LOCK_TAG)
    }
}