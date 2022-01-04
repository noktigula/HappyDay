package com.happyday.android.alarm

import android.media.RingtoneManager
import android.os.*
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.happyday.android.commonui.GradientButton
import com.happyday.android.repository.AlarmModel
import com.happyday.android.repository.AlarmsDb
import com.happyday.android.repository.Repo
import com.happyday.android.repository.SingleAlarm
import com.happyday.android.scheduler.AlarmManagerAlarmScheduler
import com.happyday.android.scheduler.AlarmPlanner
import com.happyday.android.utils.HDVibrator
import com.happyday.android.utils.loge
import com.happyday.android.utils.viewModelBuilder
import com.happyday.android.viewmodel.AlarmsViewModel
import com.happyday.android.R

private typealias RingtoneStop = ()->Unit
private typealias VibrateStop = ()->Unit

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

        loge("Alarm activity started")

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

            val musicStop = playMusic(parent.model)
            val vibratorStop = vibrate(parent.model)

            val affirmation = Affirmations(SharedPrefsPersistor(this), affirmationProvider().getAffirmations()).getNext()
            loge("Loaded affirmation: $affirmation")

            setUi(affirmation, currentAlarm!!) {
                musicStop()
                vibratorStop()
            }

            planner.scheduleNext(currentAlarm)
        }
    }

    private fun playMusic(model: AlarmModel) : RingtoneStop {
        val ringtone = RingtoneManager.getRingtone(applicationContext, model.sound)
        ringtone.play()
        return ringtone::stop
    }

    private fun vibrate(model: AlarmModel) : VibrateStop  {
        if (!model.vibrate) {
            return {}
        }

        val vibrator = HDVibrator.getVibrator(this)
        vibrator.vibrate()
        return vibrator::stop
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

    private fun setUi(affirmation: Affirmation, alarm: SingleAlarm, turnOff:()->Unit) {
        setContent {
            alarmUi(
                affirmation = affirmation,
                onSnooze = {
                    planner.snoozeAlarm(alarm)
                    turnOff()
                    finish()
                },
                onStop = {
                    turnOff()
                    finish()
                }
            )
        }
    }

    @Composable
    fun alarmUi(affirmation:Affirmation, onSnooze: ()->Unit, onStop:()->Unit) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(painterResource(id = affirmation.img), "Affirmation picture")
            Text(stringResource(id = affirmation.text))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                GradientButton(onClick = onSnooze) {
                    Text(stringResource(id = R.string.snooze_alarm), color = Color.White)
                }

                GradientButton(onClick = onStop) {
                    Text(stringResource(id = R.string.stop_alarm), color = Color.White)
                }
            }
        }
    }

    @Preview
    @Composable
    fun preview() {
        alarmUi(affirmation = Affirmation(0, R.drawable.img_0, R.string.affirmation_0), onSnooze = {}, onStop = {})
    }
}