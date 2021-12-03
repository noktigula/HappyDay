package com.happyday.android.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

internal const val ALARMS_TABLE = "alarms"

@Database(entities = [AlarmModel::class], version = 1)
abstract class AlarmsDb : RoomDatabase() {
    abstract fun alarmsDao() : AlarmsDao
    companion object {
        private var instance: AlarmsDb? = null
        fun get(applicationContext: Context) : AlarmsDb {
            var local = instance
            if (local == null) {
                local = Room.databaseBuilder(
                    applicationContext, AlarmsDb::class.java, "alarmsdb"
                ).build()
                instance = local
            }
            return local
        }
    }
}

@Dao
interface AlarmsDao {
    @Query("SELECT * FROM $ALARMS_TABLE")
    fun loadAlarms() : Flow<AllAlarms>

    @Insert
    fun insert(alarm: AlarmModel)

    @Delete
    fun delete(alarm: AlarmModel)

    @Update
    fun update(alarm: AlarmModel)
}