package com.happyday.android

import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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

        loge("AlarmActivity!")
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
            val parent = viewModel.alarmById(currentAlarm?.parentId) ?: viewModel.newAlarm()
            val ringtone = RingtoneManager.getRingtone(applicationContext, parent.model.sound)
            ringtone.play()
            setUi(currentAlarm!!, ringtone)
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

    private fun setUi(alarm: SingleAlarm, ringtone:Ringtone) {
        setContent {
            alarmUi(
                alarm = alarm,
                onSnooze = {
                    planner.snoozeAlarm(alarm)
                    ringtone.stop()
                    finish()
                },
                onStop = {
                    ringtone.stop()
                    finish()
                }
            )
        }
    }

    @Composable
    fun alarmUi(alarm:SingleAlarm?, onSnooze: ()->Unit, onStop:()->Unit) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(painterResource(id = R.drawable.image_0), "Motivation picture")
            Text(stringResource(id = R.string.motivatin_0))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = onSnooze) {
                    Text("Snooze")
                }

                Button(onClick = onStop) {
                    Text("Cancel")
                }
            }
        }
    }

    @Preview
    @Composable
    fun preview() {
        alarmUi(alarm = null, onSnooze = {}, onStop = {})
    }
}