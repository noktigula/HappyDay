package com.happyday.android.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.happyday.android.model.AlarmModel
import com.happyday.android.model.SingleAlarm
import com.happyday.android.utils.loge
import kotlin.collections.HashMap

typealias AllAlarms = MutableList<AlarmModel>
typealias AlarmsByMinute = HashMap<Int, SingleAlarm>

class AlarmsViewModel(app: Application) : AndroidViewModel(app) {
    private val alarms = MutableLiveData<AllAlarms>()
    //TODO can't find the way to update it automatically when alarms updates,
    // so will manually update it whenever alarms changes
    private val byMinute = AlarmsByMinute()

    init {
        loadAlarms(alarms, byMinute)
    }

    fun getAlarms(): LiveData<AllAlarms> {
        return this.alarms
    }

    fun getAlarmByHashCode(hashCode: Int) : SingleAlarm? {
        loge("Inside getAlarmByHashCode: hashCode=${hashCode} byMinute=$byMinute")
        return byMinute[hashCode]
    }

    fun addAlarm(alarm: AlarmModel) {
        //TODO sort by time
        alarms.value?.add(alarm)
        updateByMinuteSet(alarm)

        loge("After addAlarm, alarms=${alarms} value=${alarms.value}")
        //TODO move scheduling here as well?
        //TODO add save on disk
    }

    private fun loadAlarms(liveData: MutableLiveData<AllAlarms>, byMinute: AlarmsByMinute) {
        val loadedAlarms: AllAlarms = mutableListOf() // TODO actually load
        liveData.postValue(loadedAlarms)
        loadedAlarms.forEach { alarmModel ->
            updateByMinuteSet(alarmModel)
        }
        //TODO populate byMinute
    }

    private fun updateByMinuteSet(alarm: AlarmModel) {
        alarm.alarms.forEach { (_, singleAlarm) ->
            byMinute[singleAlarm.hashCode()] = singleAlarm
        }
    }
}
