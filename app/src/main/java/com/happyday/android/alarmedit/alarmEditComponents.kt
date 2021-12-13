package com.happyday.android.compose

import android.os.Build
import android.view.LayoutInflater
import android.widget.TimePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.happyday.android.repository.AlarmModel
import com.happyday.android.ui.theme.HappyDayTheme
import com.happyday.android.utils.alarmTimeOrNow
import com.happyday.android.utils.nowHourMinute
import com.happyday.android.R
import com.happyday.android.alarmedit.MutableModel
import com.happyday.android.utils.loge
import com.happyday.android.utils.setTime

fun createMutableModel(alarm: AlarmModel?, hour: Int, minute: Int) =
    if (alarm == null) MutableModel(
        hour = hour,
        minute = minute
    ) else MutableModel.fromAlarm(alarm)

@Composable
fun AlarmEditForm(alarm: AlarmModel?, onSave: (AlarmModel) -> Unit, onCancel: () -> Unit) {
    val (hour, minute) = alarmTimeOrNow(alarm)

    val mutableModel = remember {
        mutableStateOf(createMutableModel(alarm, hour, minute))
    }

    HappyDayTheme {
        Surface(color = MaterialTheme.colors.background) {
            Column {
                CancelSaveRow(onCancel=onCancel, onSave={
                    loge("onSave, model=${mutableModel.hashCode()}")
                    onSave(mutableModel.value.toAlarm())
                })
                HDTimePicker(hour, minute) { hour, minute ->
                    mutableModel.value.apply {
                        this.hour = hour
                        this.minute = minute
                    }
                    loge("New mutableModel = ${mutableModel.value.hashCode()}")
                }
                DaysSelector()
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

@Composable
fun DaysSelector() {

}

@Composable
fun MelodySelector() {

}

@Composable
fun VibrateSelector() {

}