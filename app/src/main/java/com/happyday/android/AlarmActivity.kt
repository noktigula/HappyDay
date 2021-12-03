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
import androidx.lifecycle.ViewModelProvider
import com.happyday.android.repository.AlarmsDb
import com.happyday.android.repository.Repo
import com.happyday.android.scheduler.AlarmScheduler
import com.happyday.android.utils.loge
import com.happyday.android.utils.viewModelBuilder
import com.happyday.android.viewmodel.AlarmsViewModel

class AlarmActivity: ComponentActivity() {
    private val viewModel: AlarmsViewModel by viewModelBuilder {
        AlarmsViewModel(application, Repo(AlarmsDb.get(application)))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loge("AlarmActivity: onCreate 0")

        loge("AlarmActivity: onCreate 1")
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

        loge("AlarmActivity: onCreate 1")
        loge("Params: viewModel = $viewModel, intent=$intent, hashCode=${intent.extras?.getInt("alarm_id")}")

        // TODO what to do if null?
        //  create new Alarm for snoozing with default sound and with current hour and minute
        viewModel.byMinute.observe(this) { alarmsByMinute ->
            val currentAlarm = alarmsByMinute[intent.extras?.getInt("alarm_id")] // TODO handle null
            loge("Found alarm: $currentAlarm")
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
                            AlarmScheduler(this@AlarmActivity).snoozeAlarm(currentAlarm)
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

        /**
         * TODO schedule next day if repetetive
         * If day is null, then non-repetetive
         * If no days selected when creating alarm, schedules for the same day but next week
         */
    }
}