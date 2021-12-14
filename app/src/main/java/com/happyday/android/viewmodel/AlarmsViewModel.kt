package com.happyday.android.viewmodel

import android.app.Application
import android.provider.Settings
import androidx.lifecycle.*
import com.happyday.android.repository.*
import com.happyday.android.scheduler.AlarmManagerAlarmScheduler
import com.happyday.android.scheduler.AlarmPlanner
import com.happyday.android.utils.isM
import com.happyday.android.utils.loge
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

class AlarmsViewModel(app: Application, val repository: Repository) : AndroidViewModel(app) {
    private val _alarms: MutableLiveData<AllAlarms> = MutableLiveData()
    val alarms: LiveData<AllAlarms> = _alarms

    private val _overlayPermission = MutableLiveData<Boolean>().apply {
        value = if (isM()) Settings.canDrawOverlays(app) else true
    }

    val listState = MediatorLiveData<ListState>().apply {
        addSource(alarms) {
            value = value?.copy(alarms = it)
        }
        addSource(_overlayPermission) {
            value = value?.copy(overlayPermission = it)
        }
        value = ListState()
    }

    private val planner = AlarmPlanner(AlarmManagerAlarmScheduler(app))

    init {
        viewModelScope.launch {
            repository.loadAlarms().collect {
                _alarms.value = it
            }
        }
    }

    val byMinute = Transformations.map(_alarms) { allAlarms ->
        val byMinute = AlarmsByMinute()
        allAlarms.forEach { alarm ->
            alarm.alarms.forEach { (_, singleAlarm) ->
                byMinute[singleAlarm.hashCode()] = singleAlarm
            }
        }
        byMinute
    }

    fun updateOverlayPermission() {
        _overlayPermission.value = if(isM()) Settings.canDrawOverlays(getApplication()) else true
    }

    fun alarmById(id: UUID?) : AlarmModel? {
        return _alarms.value?.find { it.id == id }
    }

    fun addAlarm(alarm: AlarmModel) {
        //TODO sort by time
        viewModelScope.launch {
            repository.insert(alarm)
        }
        planner.scheduleAlarm(alarm)
        loge("After addAlarm, alarms=${_alarms} value=${_alarms.value}")
        //TODO move scheduling here as well?
        //TODO add save on disk
    }

    fun addOrUpdate(alarm: AlarmModel, selectedId: String?=null) {
        viewModelScope.launch {
            val oldAlarm = if (selectedId == null) null else _alarms.value?.find { it.id == UUID.fromString(selectedId)}
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

data class ListState(val alarms: AllAlarms = emptyList(), val overlayPermission: Boolean = false)