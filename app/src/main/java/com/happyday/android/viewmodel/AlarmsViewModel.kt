package com.happyday.android.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.*
import com.happyday.android.repository.*
import com.happyday.android.scheduler.AlarmManagerAlarmScheduler
import com.happyday.android.scheduler.AlarmPlanner
import com.happyday.android.utils.loge
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

class AlarmsViewModel(app: Application, val repository: Repository) : AndroidViewModel(app) {
    private val alarms: MutableLiveData<AllAlarms> = MutableLiveData()
    private val planner = AlarmPlanner(AlarmManagerAlarmScheduler(app))

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

    fun alarmById(id: UUID?) : AlarmModel? {
        return alarms.value?.find { it.id == id }
    }

    fun getAlarms() : LiveData<AllAlarms> = alarms

    fun addAlarm(alarm: AlarmModel) {
        //TODO sort by time
        viewModelScope.launch {
            repository.insert(alarm)
        }
        planner.scheduleAlarm(alarm)
        loge("After addAlarm, alarms=${alarms} value=${alarms.value}")
        //TODO move scheduling here as well?
        //TODO add save on disk
    }

    fun addOrUpdate(alarm: AlarmModel, selectedId: String?=null) {
        viewModelScope.launch {
            val oldAlarm = if (selectedId == null) null else alarms.value?.find { it.id == UUID.fromString(selectedId)}
            if (oldAlarm != null) {
                //TODO find previous alarm so planner can cancel all running tasks
                loge("Modifying old alarm!")
                planner.updateAlarm(oldAlarm, alarm)
                repository.update(alarm)
            } else {
                loge("Adding new alarm!")
                planner.scheduleAlarm(alarm)
                repository.insert(alarm)
            }
        }
    }
}
