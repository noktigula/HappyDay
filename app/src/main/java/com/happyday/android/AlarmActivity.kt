package com.happyday.android

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.happyday.android.repository.AlarmsDb
import com.happyday.android.repository.Repo
import com.happyday.android.repository.SingleAlarm
import com.happyday.android.scheduler.AlarmManagerAlarmScheduler
import com.happyday.android.scheduler.AlarmPlanner
import com.happyday.android.utils.loge
import com.happyday.android.utils.viewModelBuilder
import com.happyday.android.viewmodel.AlarmsViewModel

class AlarmActivity: ComponentActivity() {
    private val viewModel: AlarmsViewModel by viewModelBuilder {
        AlarmsViewModel(application, Repo(AlarmsDb.get(application)))
    }

    private val planner: AlarmPlanner by lazy {
        AlarmPlanner(AlarmManagerAlarmScheduler(applicationContext))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestAppearOnTop()

        // TODO what to do if null?
        //  create new Alarm for snoozing with default sound and with current hour and minute
        viewModel.byMinute.observe(this) { alarmsByMinute ->
            /**
             * TODO schedule next day if repetetive
             * If day is null, then non-repetetive
             * If no days selected when creating alarm, schedules for the same day but next week
             */
            val currentAlarm = alarmsByMinute[intent.extras?.getInt("alarm_id")]  // TODO handle null
            loge("Found alarm: $currentAlarm")
            setUi(currentAlarm!!)
            planner.scheduleNext(currentAlarm)
        }
    }

    private fun requestAppearOnTop() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                        or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }
    }

    private fun setUi(alarm: SingleAlarm) {
        setContent {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "This is alert at 9pm!")
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = {
                        planner.snoozeAlarm(alarm)
                    }) {
                        Text("Snooze")
                    }

                    Button(onClick = {/*TODO handleCancel*/}) {
                        Text("Stop")
                    }
                }
            }
        }
    }
}