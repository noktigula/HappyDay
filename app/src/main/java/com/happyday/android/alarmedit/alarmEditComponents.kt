package com.happyday.android.compose

import android.os.Build
import android.view.LayoutInflater
import android.widget.TimePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.happyday.android.repository.AlarmModel
import com.happyday.android.ui.theme.HappyDayTheme
import com.happyday.android.utils.alarmTimeOrNow
import com.happyday.android.utils.nowHourMinute
import com.happyday.android.R
import com.happyday.android.alarmedit.MutableModel
import com.happyday.android.repository.Weekday
import com.happyday.android.utils.loge
import com.happyday.android.utils.setTime

fun createMutableModel(alarm: AlarmModel?, hour: Int, minute: Int) =
    if (alarm == null) MutableModel(
        hour = hour,
        minute = minute
    ) else MutableModel.fromAlarm(alarm)

@Composable
@Preview
fun EditFormPreview() {
    AlarmEditForm(alarm = null, onSave = { /*TODO*/ }) {
        //do nothing
    }
}

@Composable
fun AlarmEditForm(alarm: AlarmModel?, onSave: (AlarmModel) -> Unit, onCancel: () -> Unit) {
    val (hour, minute) = alarmTimeOrNow(alarm)

    val (mutableModel, setModel) = remember {
        mutableStateOf(createMutableModel(alarm, hour, minute))
    }

    HappyDayTheme {
        Surface(color = MaterialTheme.colors.background) {
            Column {
                CancelSaveRow(onCancel=onCancel, onSave={
                    loge("onSave, model=${mutableModel.hashCode()}")
                    onSave(mutableModel.toAlarm())
                })
                HDTimePicker(hour, minute) { hour, minute ->
                    setModel(mutableModel.copy(hour = hour, minute = minute))
                }
                DaysSelector(mutableModel.alarms) { selectedDay ->
                    val copy = mutableModel.copy(alarms = mutableModel.alarms.toMutableSet().apply {
                        if (!add(selectedDay)) {
                            remove(selectedDay)
                        }
                    })
                    loge("DaysSelector: $copy")
                    setModel(copy)
                }
                VibrateSelector(checked = mutableModel.vibrate) { setModel(mutableModel.copy(vibrate = it)) }
            }
        }
    }

}

@Composable
fun CancelSaveRow(onCancel: ()->Unit, onSave: ()->Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(onClick=onCancel) {
            Text("Cancel")
        }
        Button(onClick=onSave) {
            Text("Save")
        }
    }
}

@Composable
fun HDTimePicker(hour: Int, minute: Int, onChanged: (Int, Int)->Unit) {
    AndroidView(
        modifier = Modifier.fillMaxWidth(),
        factory = { context ->
            val view = LayoutInflater.from(context).inflate(R.layout.time_picker, null)
            val timePicker = view.findViewById<TimePicker>(R.id.time_picker).apply {
                setIs24HourView(true)
                setTime(hour, minute)
                setOnTimeChangedListener { _, hour, minute ->
                    onChanged(hour, minute)
                }
            }

            timePicker
        }
    )
//    Row(
//        horizontalArrangement = Arrangement.SpaceEvenly,
//        verticalAlignment = Alignment.CenterVertically,
//        modifier = Modifier
//            .fillMaxWidth()
//            .background(Color.White)
//            .padding(12.dp)
//            .clip(RoundedCornerShape(25.dp))
//    ) {
//        TimeScroller(23)
//        Text(":")
//        TimeScroller(59)
//    }
}

@Composable
fun TimeScroller(max: Int) {
//    LazyColumn(modifier = Modifier
//        .weight()
//    ) {
//        items(max) { item ->
//            Text(text = item.toString())
//        }
//    }

}

/**
 * @param selected if day is present in set - it's selected
 */
@Composable
fun DaysSelector(selected: Set<Weekday>, onSelected: (Weekday)->Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Weekday.weekdays().forEach { day -> Day(title = day.name, selected = selected.contains(day)) {
            onSelected(day)
        }}
    }
}

@Composable
fun Day(title: String, selected: Boolean, onSelected: ()->Unit) {
    Box(modifier = Modifier
        .background(color = if (selected) Color.Magenta else Color.White)
        .clickable { onSelected() }
    ) {
        Text(
            text = title,
            color = if (selected) Color.White else Color.Black,
            modifier = Modifier.padding(horizontal= 8.dp, vertical =16.dp)
        )
    }
}

@Composable
fun MelodySelector() {

}

@Composable
fun VibrateSelector(checked: Boolean, onChecked: (Boolean)->Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text("Vibrate")
        Switch(checked = checked, onCheckedChange = onChecked)
    }
}