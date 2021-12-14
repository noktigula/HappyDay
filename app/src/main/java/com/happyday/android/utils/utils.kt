package com.happyday.android.utils

import android.content.Context
import android.os.Build
import android.provider.Settings
import android.widget.TimePicker
import androidx.activity.ComponentActivity
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelLazy
import androidx.lifecycle.ViewModelProvider
import com.happyday.android.R
import com.happyday.android.repository.AlarmModel
import java.util.*

fun AlarmModel.readableTime(context: Context) : String {
    return context.resources.getString(R.string.time_format_24hrs, hour, minute)
}

@MainThread
inline fun <reified VM : ViewModel> ComponentActivity.viewModelBuilder(
    noinline provider: ()->VM
) : Lazy<VM> {
    return ViewModelLazy(
        viewModelClass = VM::class,
        storeProducer = { viewModelStore },
        factoryProducer = {
            return@ViewModelLazy object: ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHEKED_CAST")
                    return provider() as T
                }
            }
        }
    )
}

@MainThread
inline fun <reified VM : ViewModel> Fragment.viewModelBuilder(
    noinline provider: ()->VM
) : Lazy<VM> {
    return ViewModelLazy(
        viewModelClass = VM::class,
        storeProducer = { requireActivity().viewModelStore },
        factoryProducer = {
            return@ViewModelLazy object: ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHEKED_CAST")
                    return provider() as T
                }
            }
        }
    )
}

fun nowHourMinute() : Pair<Int, Int> {
    val calendar = Calendar.getInstance()
    return Pair(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))
}

fun alarmTimeOrNow(alarmModel: AlarmModel?) : Pair<Int, Int> {
    return if (alarmModel == null) {
        nowHourMinute()
    } else {
        Pair(alarmModel.hour, alarmModel.minute)
    }
}

fun Calendar.minute() = get(Calendar.MINUTE)
fun Calendar.hour() = get(Calendar.HOUR_OF_DAY)
fun Calendar.day() = get(Calendar.DAY_OF_WEEK)
fun Calendar.isToday(hour: Int, minute: Int) = this.hour() >= hour && this.minute() > minute

fun TimePicker.setTime(hour: Int, minute: Int) {
    if (isM()) {
        this.hour = hour
        this.minute = minute
    } else {
        currentHour = hour
        currentMinute = minute
    }
}

fun isM() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M