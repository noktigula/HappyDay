package com.happyday.android.scheduler

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.happyday.android.repository.AlarmsDb
import com.happyday.android.repository.Repo
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    @OptIn(DelicateCoroutinesApi::class)
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val repo = Repo(AlarmsDb.get(context))
            val planner = AlarmPlanner(AlarmManagerAlarmScheduler(context))
            GlobalScope.launch {
                repo.loadAlarms().collect { allAlarms ->
                    allAlarms.forEach { alarm ->
                        planner.scheduleAlarm(alarm)
                    }
                }
            }
        }
    }
}