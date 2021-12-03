package com.happyday.android.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.happyday.android.repository.*
import com.happyday.android.scheduler.AlarmScheduler
import com.happyday.android.utils.loge
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class AlarmsViewModel(app: Application, val repository: Repository) : AndroidViewModel(app) {
    private val alarms: MutableLiveData<AllAlarms> = MutableLiveData()

    init {
        viewModelScope.launch {
            repository.loadAlarms().collect {
                alarms.value = it
            }
        }
    }

    val byMinute = Transformations.map(alarms) { allAlarms ->
        val byMinute = AlarmsByMinute()
        allAlarms.forEach { alarm ->
            alarm.alarms.forEach { (_, singleAlarm) ->
                byMinute[singleAlarm.hashCode()] = singleAlarm
            }
        }
        byMinute
    }

    fun getAlarms() : LiveData<AllAlarms> = alarms

    fun addAlarm(alarm: AlarmModel) {
        //TODO sort by time
        viewModelScope.launch {
            repository.insert(alarm)
        }
        AlarmScheduler(getApplication()).scheduleAlarm(alarm)
        loge("After addAlarm, alarms=${alarms} value=${alarms.value}")
        //TODO move scheduling here as well?
        //TODO add save on disk
    }
}
