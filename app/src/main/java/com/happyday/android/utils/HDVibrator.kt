package com.happyday.android.utils

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.annotation.RequiresApi

const val DELAY = 0L
const val VIBRATE = 1000L
const val SLEEP = 1000L
const val START = 1

abstract class HDVibrator {
    abstract fun vibrate()
    abstract fun stop()
    companion object {
        fun getVibrator(context: Context) : HDVibrator {
            return if(isS()) VibratorNew.newInstance(context) else VibratorLegacy.newInstance(context)
        }
    }

    internal class VibratorLegacy private constructor(val vibrator: Vibrator) : HDVibrator() {
        override fun vibrate() {
            vibrator.vibrateAlarm()
        }

        override fun stop() {
            vibrator.cancel()
        }

        companion object {
            fun newInstance(context:Context) = VibratorLegacy(context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator)
        }
    }

    internal class VibratorNew private constructor(val vibrator: Vibrator) : HDVibrator() {
        override fun vibrate() {
            vibrator.vibrateAlarm()
        }

        override fun stop() {
            vibrator.cancel()
        }

        companion object {
            @RequiresApi(Build.VERSION_CODES.S)
            fun newInstance(context:Context) = VibratorNew(
                (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
            )
        }

    }
}

private fun Vibrator.vibrateAlarm() {
    val pattern = longArrayOf(DELAY, VIBRATE, SLEEP)
    if (isO()) {
        vibrate(VibrationEffect.createWaveform(pattern, START))
    } else {
        vibrate(pattern, START)
    }
}

