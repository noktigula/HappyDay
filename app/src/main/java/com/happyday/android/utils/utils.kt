package com.happyday.android.utils

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelLazy
import androidx.lifecycle.ViewModelProvider
import com.happyday.android.R
import com.happyday.android.repository.AlarmModel

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