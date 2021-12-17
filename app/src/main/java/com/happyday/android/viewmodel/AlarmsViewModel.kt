package com.happyday.android.viewmodel

import android.app.Application
import android.media.RingtoneManager
import android.net.Uri
import android.provider.Settings
import androidx.lifecycle.*
import com.happyday.android.R
import com.happyday.android.repository.*
import com.happyday.android.scheduler.AlarmManagerAlarmScheduler
import com.happyday.android.scheduler.AlarmPlanner
import com.happyday.android.utils.isM
import com.happyday.android.utils.loge
import com.happyday.android.utils.nowHourMinute
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

class AlarmsViewModel(app: Application, val repository: Repository) : AndroidViewModel(app) {
    private val _alarms: MutableLiveData<List<AlarmUi>> = MutableLiveData()
    val alarms: LiveData<List<AlarmUi>> = _alarms

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
            repository.loadAlarms().collect { allAlarms ->
                _alarms.value = allAlarms.map { AlarmUi(it, ::soundTitle)}
            }
        }
    }

    val byMinute = Transformations.map(_alarms) { allAlarms ->
        val byMinute = AlarmsByMinute()
        allAlarms.forEach { alarm ->
            alarm.model.alarms.forEach { (_, singleAlarm) ->
                byMinute[singleAlarm.hashCode()] = singleAlarm
            }
        }
        byMinute
    }

    fun updateOverlayPermission() {
        _overlayPermission.value = if(isM()) Settings.canDrawOverlays(getApplication()) else true
    }

    fun alarmById(id: UUID?) : AlarmUi? {
        return _alarms.value?.find { it.model.id == id }
    }

    fun newAlarm() : AlarmUi {
        val (hour, minute) = nowHourMinute()
        return AlarmUi(
            model = AlarmModel(
                id = UUID.randomUUID(),
                title = "",
                vibrate = true,
                sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM),
                enabled = true,
                hour = hour,
                minute = minute,
                alarms = mutableMapOf()
            ),
            soundTitle = ::soundTitle
        )
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
            val oldAlarm = if (selectedId == null) null else _alarms.value?.find { it.model.id == UUID.fromString(selectedId)}?.model
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

    private fun soundTitle(soundUri: Uri?) : String {
        loge("Resolving title: uri=${soundUri}")
        val context = getApplication<Application>()
        return if (soundUri == null || soundUri == RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)) {
            loge("default!")
            context.getString(R.string.melody_default)
        } else {
            loge("Custom! ${RingtoneManager.getRingtone(context, soundUri).getTitle(context)}")
            RingtoneManager.getRingtone(context, soundUri).getTitle(context)
        }
    }

}

data class ListState(val alarms: List<AlarmUi> = emptyList(), val overlayPermission: Boolean = false)
data class AlarmUi(val model: AlarmModel, val soundTitle: (Uri?)->String)