package com.happyday.android.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.happyday.android.repository.AlarmModel
import com.happyday.android.ui.theme.HappyDayTheme
import com.happyday.android.utils.alarmTimeOrNow
import com.happyday.android.utils.nowHourMinute

@Composable
fun AlarmEditForm(alarm: AlarmModel?, onSave: (AlarmModel) -> Unit, onCancel: () -> Unit) {
    val (hour, minute) = alarmTimeOrNow(alarm)
    HappyDayTheme {
        Surface(color = MaterialTheme.colors.background) {
            Column {
                CancelSaveRow(onCancel={}, onSave={})
                HDTimePicker(hour, minute)
                DaysSelector()
            }
        }
    }

}

@Composable
fun CancelSaveRow(onCancel: ()->Unit, onSave: ()->Unit) {

}

@Composable
fun HDTimePicker(hour: Int, minute: Int) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(12.dp)
            .clip(RoundedCornerShape(25.dp))
    ) {
        TimeScroller(23)
        Text(":")
        TimeScroller(59)
    }
}

@Composable
fun TimeScroller(max: Int) {
    LazyColumn {
        items(max) { item ->
            Text(text = item.toString())
        }
    }
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