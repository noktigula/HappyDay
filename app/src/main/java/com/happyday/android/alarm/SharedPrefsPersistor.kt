package com.happyday.android.alarm

import android.content.Context

private const val PREFS_KEY = "hd_affirmations"
private const val LAST_INDEX = "last_index"
private const val LAST_DATE = "date"

class SharedPrefsPersistor(private val context: Context) : AffirmationsPersistor {
    override fun saveIndex(lastUsedIndex: LastUsedIndex) {
        context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)
            .edit()
            .putInt(LAST_INDEX, lastUsedIndex.index)
            .putLong(LAST_DATE, lastUsedIndex.date)
            .apply()
    }

    override fun loadIndex(): LastUsedIndex {
        val prefs = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)
        return LastUsedIndex(prefs.getInt(LAST_INDEX, -1), prefs.getLong(LAST_DATE, 0))
    }
}