package com.happyday.android

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.timepicker.MaterialTimePicker
import com.happyday.android.repository.*
import com.happyday.android.scheduler.AlarmScheduler
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

        viewModel.getAlarms().observe(this) { allAlarms ->
            setContent {
                HappyDayTheme {
                    // A surface container using the 'background' color from the theme
                    Surface(color = MaterialTheme.colors.background) {
                        Column {
                            AlarmsList(Modifier.weight(1f), this@MainActivity, allAlarms)
                            Button(onClick = {
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
                                            Weekday.Mon to SingleAlarm(id, Weekday.Mon, picker.hour, picker.minute),
                                            Weekday.Tue to SingleAlarm(id, Weekday.Tue, picker.hour, picker.minute),
                                            Weekday.Wed to SingleAlarm(id, Weekday.Wed, picker.hour, picker.minute),
                                            Weekday.Thu to SingleAlarm(id, Weekday.Thu, picker.hour, picker.minute),
                                            Weekday.Fri to SingleAlarm(id, Weekday.Fri, picker.hour, picker.minute),
                                            Weekday.Sat to SingleAlarm(id, Weekday.Sat, picker.hour, picker.minute),
                                            Weekday.Sun to SingleAlarm(id, Weekday.Sun, picker.hour, picker.minute),
                                        )
                                    )
                                    viewModel.addAlarm(alarm)
                                }
                                picker.show(this@MainActivity.supportFragmentManager, "picker")
                            }) {
                                Text("Schedule")
                            }
                        }
                    }
                }
            }
        }

    }
}

@Composable
fun AlarmsList(modifier: Modifier, context: Context, data: List<AlarmModel>) {
    LazyColumn(modifier = modifier) {
        items(data) { item ->
            AlarmRow(context, item)
        }
    }
}

@Composable
fun AlarmRow(context: Context, item: AlarmModel) {
    Card {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = item.readableTime(context))
            Column (horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = item.title)
                WeekdaysSelector(selectedWeekdays = item.alarms.keys)
            }
            Switch(checked = item.enabled, onCheckedChange = {checked -> /*TODO*/})
        }
    }
}

@Composable
fun WeekdaysSelector(selectedWeekdays: Set<Weekday>) {
    Row {
        Weekday.values().map { WeekdayBox(it.name, selectedWeekdays.contains(it)) { status ->
            //TODO update status in viewmodel
        } }
    }
}

@Composable
fun WeekdayBox(title: String, selected: Boolean, onSelected: (Boolean)->Unit) {
    return Text(text = title, color = if (selected) Color.Blue else Color.Black)
}

//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview() {
//    HappyDayTheme {
//        AlarmsList(listOf(
//                Alarm(AlarmModel(UUID.randomUUID(), "Test", 18 hours 35.minutes(), setOf(Weekday.Mon, Weekday.Tue), enabled = true)) { hrs, min ->
//                    "18:35"
//                },
//        Alarm(AlarmModel(UUID.randomUUID(), "Test 2", 8 hours 0.minutes(), setOf(Weekday.Wed, Weekday.Fri), enabled = true)) { hrs, min ->
//            "08:00"
//        },
//        Alarm(AlarmModel(UUID.randomUUID(), "Test 3", 7 hours 30.minutes(), setOf(Weekday.Sat, Weekday.Sun), enabled = true))  { hrs, min ->
//            "07:30"
//        },
//        ))
//    }
//}
