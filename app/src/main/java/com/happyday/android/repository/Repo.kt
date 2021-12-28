package com.happyday.android.repository

import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

interface Repository {
    suspend fun loadAlarms() : Flow<AllAlarms>
    suspend fun insert(alarm: AlarmModel)
    suspend fun delete(alarm: AlarmModel)
    suspend fun update(alarm: AlarmModel)
}

class Repo(
    private val db: AlarmsDb,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : Repository {
    override suspend fun loadAlarms() = db.alarmsDao().loadAlarms()

    override suspend fun insert(alarm: AlarmModel) {
        withContext(dispatcher) {
            db.alarmsDao().insert(alarm)
        }
    }

    override suspend fun delete(alarm: AlarmModel) {
        withContext(dispatcher) {
            db.alarmsDao().delete(alarm)
        }
    }

    override suspend fun update(alarm: AlarmModel) {
        withContext(dispatcher) {
            db.alarmsDao().update(alarm)
        }
    }
}