package com.happyday.android.compose

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.widget.TimePicker
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.happyday.android.repository.AlarmModel
import com.happyday.android.R
import com.happyday.android.alarmedit.MutableModel
import com.happyday.android.commonui.Screen
import com.happyday.android.repository.Weekday
import com.happyday.android.ui.theme.*
import com.happyday.android.utils.*
import com.happyday.android.viewmodel.AlarmUi
import java.util.*

fun createMutableModel(alarm: AlarmModel?, hour: Int, minute: Int) =
    if (alarm == null) MutableModel(
        hour = hour,
        minute = minute
    ) else MutableModel.fromAlarm(alarm)

@Composable
@Preview
fun EditFormPreview() {
    AlarmEditForm(alarm = AlarmUi(
        model = AlarmModel(
            id = UUID.randomUUID(),
            title = "",
            vibrate = true,
            sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM),
            enabled = true,
            hour = 12,
            minute = 24,
            alarms = mutableMapOf()
        ),
        soundTitle = { "Default" }
    ), onSave = { /*TODO*/ }) {
        //do nothing
    }
}

@Composable
fun AlarmEditForm(alarm: AlarmUi, onSave: (AlarmModel) -> Unit, onCancel: () -> Unit) {
    val (hour, minute) = alarmTimeOrNow(alarm.model)

    val (mutableModel, setModel) = remember {
        mutableStateOf(createMutableModel(alarm.model, hour, minute))
    }

    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { activityResult ->
        val data = activityResult.data
        if (activityResult.resultCode == RESULT_OK && data != null) {
            setModel(mutableModel.copy(sound = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)))
        }
    }

    val ringtonePickerTitle = stringResource(id = R.string.alarm_picker_title)

    HappyDayTheme {
        Surface(color = MaterialTheme.colors.background) {
            Screen(extraModifiers = {
                padding(horizontal = Spacing.Medium.size)
            }) {
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
                MelodySelector(title =alarm.soundTitle(mutableModel.sound)) {
                    launcher.launch(ringtonePickerIntent(ringtonePickerTitle, mutableModel.sound))
                }
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
        TextButton(onClick=onCancel) {
            Text(stringResource(id = R.string.dialog_edit_cancel), style = MaterialTheme.typography.body2)
        }
        TextButton(onClick=onSave) {
            Text(stringResource(id = R.string.dialog_edit_save), style = MaterialTheme.typography.body2)
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
        .bgColor(selected)
        .clickable { onSelected() }
    ) {
        Text(
            text = title,
            color = if (selected) Color.White else Color.Black,
            modifier = Modifier.padding(horizontal= 8.dp, vertical =16.dp)
        )
    }
}

fun Modifier.bgColor(selected:Boolean) = composed {
    if (selected) {
        background(brush = Brush.linearGradient(HeaderGradients))
    } else {
        background(color = DaySelectorDisabled)
    }
}

@Composable
fun MelodySelector(title: String, onClick:()->Unit) {
    val shape = RoundedCornerShape(Padding.MelodySelector.size)
    Card(
        shape = shape,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.Large.size),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start,
        ) {
            Text(stringResource(id = R.string.melody_selector_title), style=MaterialTheme.typography.body2)
            Spacer(modifier = Modifier.height(Spacing.XSmall.size))
            Text(title, style=MaterialTheme.typography.caption)
        }
    }
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