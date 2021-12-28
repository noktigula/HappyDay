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

    private val _overlayPermission = MutableLiveData<Boolean>().apply {
        value = if (isM()) Settings.canDrawOverlays(app) else true
    }

    val listState = MediatorLiveData<ListState>().apply {
        addSource(_alarms) {
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
    }

    fun addOrUpdate(alarm: AlarmModel, selectedId: String?=null) {
        viewModelScope.launch {
            if (!updateAlarm(alarm, selectedId)) {
                addAlarm(alarm)
            }
        }
    }

    private suspend fun updateAlarm(newAlarm: AlarmModel, selectedId: String?=null) : Boolean {
        val oldAlarm = (if (selectedId == null) null else _alarms.value?.find { it.model.id == UUID.fromString(selectedId)}?.model)
            ?: return false

        planner.updateAlarm(oldAlarm, newAlarm)
        repository.update(newAlarm)
        return true
    }

    fun deleteAlarms(alarms:List<AlarmUi>) {
        viewModelScope.launch {
            alarms.forEach {
                repository.delete(it.model)
                planner.cancel(it.model)
            }
        }
    }

    fun updateAlarmEnabled(alarm: AlarmUi, enabled:Boolean) {
        val newModel = alarm.model.copy(enabled = enabled)

        viewModelScope.launch {
            updateAlarm(newModel, newModel.id.toString())
        }
    }

    private fun soundTitle(soundUri: Uri?) : String {
        val context = getApplication<Application>()
        return if (soundUri == null || soundUri == RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)) {
            context.getString(R.string.melody_default)
        } else {
            RingtoneManager.getRingtone(context, soundUri).getTitle(context)
        }
    }
}

data class ListState(val alarms: List<AlarmUi> = emptyList(), val overlayPermission: Boolean = false)
data class AlarmUi(val model: AlarmModel, val soundTitle: (Uri?)->String/*, val selected:Boolean = false*/)