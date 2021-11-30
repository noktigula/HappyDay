package com.happyday.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import com.happyday.android.model.AlarmModel
import com.happyday.android.model.Weekday
import com.happyday.android.scheduler.AlarmScheduler
import com.happyday.android.ui.theme.HappyDayTheme
import com.happyday.android.utils.hours
import com.happyday.android.utils.minutes
import com.happyday.android.viewmodel.Alarm
import com.happyday.android.viewmodel.Alarms
import com.happyday.android.viewmodel.AlarmsViewModel
import java.util.*

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: AlarmsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider
            .AndroidViewModelFactory(application)
            .create(AlarmsViewModel::class.java)

        setContent {
            HappyDayTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Button(onClick = {
                        AlarmScheduler(applicationContext).scheduleAlarm()
                    }) {
                        Text("Schedule")
                    }
                    AlarmsList(viewModel.getAlarms().value ?: emptyList())
                }
            }
        }
    }
}

@Composable
fun AlarmsList(data: Alarms) {
    LazyColumn {
        items(data) { item ->
            AlarmRow(item)
        }
    }
}

@Composable
fun AlarmRow(item: Alarm) {
    Card {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = item.readableTime())
            Column (horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = item.alarmModel.title)
                WeekdaysSelector(selectedWeekdays = item.alarmModel.weekdays)
            }
            Switch(checked = item.alarmModel.enabled, onCheckedChange = {checked -> /*TODO*/})
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

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    HappyDayTheme {
        AlarmsList(listOf(
                Alarm(AlarmModel(UUID.randomUUID(), "Test", 18 hours 35.minutes(), setOf(Weekday.Mon, Weekday.Tue), enabled = true)) { hrs, min ->
                    "18:35"
                },
        Alarm(AlarmModel(UUID.randomUUID(), "Test 2", 8 hours 0.minutes(), setOf(Weekday.Wed, Weekday.Fri), enabled = true)) { hrs, min ->
            "08:00"
        },
        Alarm(AlarmModel(UUID.randomUUID(), "Test 3", 7 hours 30.minutes(), setOf(Weekday.Sat, Weekday.Sun), enabled = true))  { hrs, min ->
            "07:30"
        },
        ))
    }
}
