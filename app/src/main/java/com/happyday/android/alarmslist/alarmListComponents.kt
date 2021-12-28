package com.happyday.android.compose

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.happyday.android.R
import com.happyday.android.commonui.Background
import com.happyday.android.commonui.GradientButton
import com.happyday.android.commonui.Screen
import com.happyday.android.repository.*
import com.happyday.android.ui.theme.*
import com.happyday.android.utils.loge
import com.happyday.android.utils.readableTime
import com.happyday.android.viewmodel.AlarmUi
import com.happyday.android.viewmodel.AlarmsViewModel

@Composable
fun ListContent(
    activity: AppCompatActivity,
    onAddAlarm:()->Unit,
    onDelete:(List<AlarmUi>)->Unit,
    onAlarmClicked: (AlarmUi)->Unit,
    onAlarmEnabledChange: (AlarmUi, Boolean)->Unit
) {
    val (selectedItems, setSelectedItems) = remember { mutableStateOf(listOf<AlarmUi>()) }

    val selectedState = selectedItems.isNotEmpty()
    val viewModel  = viewModel(
        modelClass = AlarmsViewModel::class.java,
        viewModelStoreOwner = activity,
        key = "alarmsVM",
        factory = object:ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AlarmsViewModel(activity.application, Repo(AlarmsDb.get(activity.applicationContext))) as T
        }
    })

    val listState = viewModel.listState.observeAsState()
    HappyDayTheme {
        // A surface container using the 'background' color from the theme
        Surface(color = MaterialTheme.colors.background) {
            Screen {
                Box(Modifier.fillMaxSize()) {
                    AlarmsList(
                        modifier = Modifier.fillMaxSize(),
                        context = activity,
                        data = listState.value?.alarms ?: emptyList(),
                        onAlarmSelected = { item ->
                            if (selectedState) {
                                val newItems = selectedItems.toMutableList().apply {
                                    if (!remove(item)) {
                                        add(item)
                                    }
                                }
                                setSelectedItems(newItems)
                            } else {
                                onAlarmClicked(item)
                            }
                        },
                        onLongPress = { item ->
                            setSelectedItems(selectedItems.toMutableList().apply { add(item) })
                        },
                        onEnableChanged = onAlarmEnabledChange,
                        deleteState = selectedState,
                        selectedItems = selectedItems
                    )

                    if (!selectedState) {
                        BottomButton(title = "     +     ") {
                            onAddAlarm()
                        }
                    } else {
                        DeleteBottomButtons(
                            onCancel = {
                               setSelectedItems(listOf())
                            },
                            onDelete = {
                                setSelectedItems(listOf())
                                onDelete(selectedItems)
                            }
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun BoxScope.BottomButton(title: String, onPress: () -> Unit) {
    GradientButton(
        extraModifiers = {
            align(Alignment.BottomCenter)
                .background(Color.Transparent)
                .padding(bottom = Spacing.Small.size)
        },
        onClick = onPress
    ) {
        Text(title, color = Color.White)
    }
}

@Composable
fun BoxScope.DeleteBottomButtons(onCancel:()->Unit, onDelete:()->Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomCenter)
            .padding(Spacing.Small.size),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        GradientButton(onClick = onCancel) {
            Text(stringResource(id = R.string.button_cancel_delete), color = Color.White)
        }

        GradientButton(onClick = onDelete) {
            Text(stringResource(id = R.string.button_delete), color = Color.White)
        }
    }
}

@Composable
fun AlarmsList(
    modifier: Modifier,
    context: Context,
    data: List<AlarmUi>,
    onAlarmSelected: (AlarmUi) -> Unit,
    onLongPress: (AlarmUi)->Unit,
    onEnableChanged: (AlarmUi, Boolean) -> Unit,
    deleteState: Boolean,
    selectedItems: List<AlarmUi>
) {
    LazyColumn(modifier = modifier) {
        item {
            Header()
            Spacer(modifier = Modifier.height(Spacing.Small.size))
        }
        items(data) { item ->
            if (deleteState) {
                DeletableAlarmRow(
                    context = context,
                    selected = selectedItems.contains(item),
                    item = item.model,
                    onSelected = {
                        onAlarmSelected(item)
                    }
                )
            } else {
                AlarmRow(
                    context = context,
                    item = item.model,
                    onLongPress = {
                        onLongPress(item)
                    },
                    onSelected = {
                        onAlarmSelected(item)
                    },
                    onEnableChanged = { enabled ->
                        onEnableChanged(item, enabled)
                    }
                )
            }

            Spacer(modifier = Modifier.height(Padding.BetweenCards.size))
        }
    }
}

@Composable
fun DeletableAlarmRow(context: Context, selected: Boolean, item: AlarmModel, onSelected: ()->Unit) {
    BaseAlarmRow(
        context = context,
        item = item,
        boxComposable = {
            Checkbox(checked = selected, onCheckedChange = { onSelected() }, colors = happyDayCheckbox())
        },
        onLongPress = {},
        onSelected = onSelected
    )
}

@Composable
fun AlarmRow(context: Context, item: AlarmModel, onLongPress: () -> Unit, onSelected: ()->Unit, onEnableChanged: (Boolean)->Unit) {
   BaseAlarmRow(
       context = context,
       item = item,
       boxComposable = {
            Switch(
                checked = item.enabled, onCheckedChange = onEnableChanged,
                colors = happyDaySwitch()
            )
       },
       onLongPress = onLongPress,
       onSelected = onSelected
   )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BaseAlarmRow(context: Context, item: AlarmModel, boxComposable: @Composable ()->Unit, onLongPress: () -> Unit, onSelected: () -> Unit) {
    val shape = RoundedCornerShape(size = RoundCorners.AlarmCard.size)
    Card(
        modifier = Modifier
            .padding(horizontal = Spacing.Medium.size)
            .background(Color.White, shape)
            .combinedClickable(enabled = true,
                onClick = {
                    onSelected()
                },
                onLongClick = {
                    onLongPress()
                }
            ),
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

            boxComposable()
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
            Image(modifier = Modifier
                .height(HeaderSize)
                .width(HeaderSize),
                painter = painterResource(id = R.drawable.ic_header),
                contentDescription = "Header_icon"
            )
        }

    }
}

