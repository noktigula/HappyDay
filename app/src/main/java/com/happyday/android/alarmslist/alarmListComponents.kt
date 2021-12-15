package com.happyday.android.compose

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
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
import com.happyday.android.commonui.Screen
import com.happyday.android.repository.AlarmModel
import com.happyday.android.repository.AllAlarms
import com.happyday.android.repository.Weekday
import com.happyday.android.ui.theme.HappyDayTheme
import com.happyday.android.utils.readableTime

@Composable
fun ListContent(activity: AppCompatActivity, allAlarms: AllAlarms, onAddAlarm:()->Unit, onAlarmSelected:(AlarmModel)->Unit) {
    HappyDayTheme {
        // A surface container using the 'background' color from the theme
        Surface(color = MaterialTheme.colors.background) {
            Screen {
                AlarmsList(Modifier.weight(1f), activity, allAlarms, onAlarmSelected)
                Button(onClick = onAddAlarm) {
                    Text("Schedule")
                }
            }
        }
    }
}

@Composable
fun AlarmsList(modifier: Modifier, context: Context, data: List<AlarmModel>, onAlarmSelected: (AlarmModel) -> Unit) {
    LazyColumn(modifier = modifier) {
        items(data) { item ->
            AlarmRow(context, item) {
                onAlarmSelected(item)
            }
        }
    }
}

@Composable
fun AlarmRow(context: Context, item: AlarmModel, onSelected: ()->Unit) {
    Card(
        modifier = Modifier.clickable { onSelected() }
    ) {
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

