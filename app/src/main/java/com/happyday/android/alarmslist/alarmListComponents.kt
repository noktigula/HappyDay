package com.happyday.android.compose

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradient
import androidx.compose.ui.res.painterResource
import com.happyday.android.R
import com.happyday.android.commonui.Screen
import com.happyday.android.repository.AlarmModel
import com.happyday.android.repository.AllAlarms
import com.happyday.android.repository.Weekday
import com.happyday.android.ui.theme.*
import com.happyday.android.utils.readableTime

@Composable
fun ListContent(activity: AppCompatActivity, allAlarms: AllAlarms, onAddAlarm:()->Unit, onAlarmSelected:(AlarmModel)->Unit) {
    HappyDayTheme {
        // A surface container using the 'background' color from the theme
        Surface(color = MaterialTheme.colors.background) {
            Screen {
                Header()
                AlarmsList(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = Spacing.Small.size, horizontal = Spacing.Medium.size),
                    context = activity,
                    data = allAlarms,
                    onAlarmSelected = onAlarmSelected
                )
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
            Spacer(modifier = Modifier.height(Padding.BetweenCards.size))
        }
    }
}

@Composable
fun AlarmRow(context: Context, item: AlarmModel, onSelected: ()->Unit) {
    val shape = RoundedCornerShape(size = RoundCorners.AlarmCard.size)
    Card(
        modifier = Modifier
            .clickable { onSelected() }
            .background(Color.White, shape),
        elevation = Elevation,
        shape = RoundedCornerShape(size = RoundCorners.AlarmCard.size),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(Padding.AlarmCard.size),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = item.readableTime(context), style=MaterialTheme.typography.h1)
            Column (horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = item.title)
                WeekdaysSelector(selectedWeekdays = item.alarms.keys)
            }
            Switch(
                checked = item.enabled, onCheckedChange = {checked -> /*TODO*/},
                colors = SwitchDefaults.colors(
                    checkedThumbColor = SwitchThumbEnabled,
                    checkedTrackColor = SwitchTrackEnabled,
                    checkedTrackAlpha = 1.0f,
                    uncheckedThumbColor = SwitchThumbDisabled,
                    uncheckedTrackColor = SwitchTrackDisabled,
                    uncheckedTrackAlpha = 1.0f
                )
            )
        }
    }
}

@Composable
fun WeekdaysSelector(selectedWeekdays: Set<Weekday>) {
    Row {
        Weekday.values().map {
            if (it in selectedWeekdays) {
                WeekdayBox(it.name)
                Spacer(modifier = Modifier.padding(Padding.BetweenSelectedWeekdays.size))
            }
        }
    }
}

@Composable
fun WeekdayBox(title: String) {
    return Text(text = title, style=MaterialTheme.typography.caption)
}

@Composable
fun Header() {
    val shape = RoundedCornerShape(bottomStart = RoundCorners.Header.size, bottomEnd = RoundCorners.Header.size)

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = shape
    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .background(brush = Brush.linearGradient(HeaderGradients))
            .padding(
                vertical = Padding.HeaderVertical.size,
                horizontal = Padding.HeaderHorizontal.size
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Column{
                Text(text = "Welcome to", style= MaterialTheme.typography.h2, modifier = Modifier.background(Color.Transparent))
                Text(text = "Happy Day", style= MaterialTheme.typography.h2, modifier = Modifier.background(Color.Transparent))
            }
            Spacer(Modifier.width(Spacing.Medium.size))
            Image(modifier = Modifier.height(HeaderSize).width(HeaderSize),
                painter = painterResource(id = R.drawable.ic_header),
                contentDescription = "Header_icon"
            )
        }

    }
}

