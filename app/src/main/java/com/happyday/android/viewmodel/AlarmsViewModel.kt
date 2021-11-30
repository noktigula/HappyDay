package com.happyday.android.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.happyday.android.R
import com.happyday.android.model.AlarmModel
import com.happyday.android.model.Weekday
import com.happyday.android.utils.hours
import com.happyday.android.utils.minutes
import java.util.*
import java.util.concurrent.TimeUnit

class Alarm(val alarmModel: AlarmModel, private val stringProvider: (Long, Long)->String) {
    /**
     * //TODO support different time formats like am/pm
     */
    fun readableTime() : String {
        val hrs = TimeUnit.SECONDS.toHours(alarmModel.time.toLong())
        val min = TimeUnit.SECONDS.toMinutes(alarmModel.time.toLong()) % 60

        return stringProvider(hrs, min)
    }
}

typealias Alarms = List<Alarm>

class AlarmsViewModel(app: Application) : AndroidViewModel(app) {
    private val alarms: MutableLiveData<Alarms> by lazy {
        MutableLiveData<Alarms>().also {
            loadAlarms(it)
        }
    }

    fun getAlarms(): LiveData<Alarms> {
        return this.alarms
    }

    private fun loadAlarms(liveData: MutableLiveData<Alarms>) {
        //TODO
    }
}
