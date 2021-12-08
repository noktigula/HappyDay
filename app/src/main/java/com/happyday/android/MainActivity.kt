package com.happyday.android

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.android.material.timepicker.MaterialTimePicker
import com.happyday.android.compose.ListContent
import com.happyday.android.repository.*
import com.happyday.android.ui.theme.HappyDayTheme
import com.happyday.android.utils.loge
import com.happyday.android.utils.readableTime
import com.happyday.android.utils.viewModelBuilder
import com.happyday.android.viewmodel.AlarmsViewModel
import java.util.*

class MainActivity : AppCompatActivity() {

    private val viewModel: AlarmsViewModel by viewModelBuilder {
        AlarmsViewModel(application, Repo(AlarmsDb.get(application)))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val calendar = Calendar.getInstance(Locale.GERMANY)
        loge("Today is ${calendar.get(Calendar.DAY_OF_WEEK)}")

        viewModel.getAlarms().observe(this) { allAlarms ->
            setContent {
                ListContent(activity = this, allAlarms = allAlarms) {
                    val picker = MaterialTimePicker.Builder().build()
                    picker.addOnPositiveButtonClickListener {
                        loge("Selected ${picker.hour}:${picker.minute}")
                        val id = UUID.randomUUID()
                        val alarm = AlarmModel(
                            id,
                            "",
                            true,
                            null,
                            true,
                            picker.hour,
                            picker.minute,
                            alarms = mapOf(
//                                            Weekday.Mon to SingleAlarm(id, Weekday.Mon, picker.hour, picker.minute),
//                                            Weekday.Tue to SingleAlarm(id, Weekday.Tue, picker.hour, picker.minute),
                                Weekday.Wed to SingleAlarm(id, Weekday.Wed, picker.hour, picker.minute),
//                                            Weekday.Thu to SingleAlarm(id, Weekday.Thu, picker.hour, picker.minute),
//                                            Weekday.Fri to SingleAlarm(id, Weekday.Fri, picker.hour, picker.minute),
//                                            Weekday.Sat to SingleAlarm(id, Weekday.Sat, picker.hour, picker.minute),
//                                            Weekday.Sun to SingleAlarm(id, Weekday.Sun, picker.hour, picker.minute),
                            )
                        )
                        viewModel.addAlarm(alarm)
                    }
                    picker.show(this@MainActivity.supportFragmentManager, "picker")
                }
            }
        }

    }
}
