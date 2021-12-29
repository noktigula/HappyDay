package com.happyday.android.alarm

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class Affirmation(val id: Int, @DrawableRes val img: Int, @StringRes val text: Int)
data class LastUsedIndex(val index: Int, val date: Long)

class Affirmations(
    private val persistor: AffirmationsPersistor,
    private val affirmations: List<Affirmation>
) {
    fun getNext() : Affirmation {
        // 1 day 1 affirmation
        //  - list of affirmations must be ordered to keep same order between sessions

        val lastUsedIndex = persistor.loadIndex()
        if (lastUsedIndex.isToday()) {
            return affirmations[lastUsedIndex.index]
        }

        val today = System.currentTimeMillis()
        val nextIndex = getAdjustedIndex(persistor.loadIndex().index, affirmations.size)
        val todayAffirmation = affirmations[ nextIndex ]
        persistor.saveIndex(LastUsedIndex(nextIndex, today))
        return todayAffirmation
    }

    // amount of affirmations might become smaller than index
    // in this case we will just iterate over the list again.
    // We might just use loadIndex() % affirmations.size, however this might be a problem if
    //  new affirmations will arrive later (new ones will be skipped). So to avoid it let's store valid
    // index all the time
    private fun getAdjustedIndex(loadedIndex: Int, affirmationsSize: Int) : Int {
        return if (loadedIndex >= affirmationsSize) {
            loadedIndex % affirmationsSize
        } else {
            loadedIndex + 1
        }
    }
}

private const val DAY_MILLIS = 86400000
private fun LastUsedIndex.isToday() : Boolean {
    val now = System.currentTimeMillis()
    val todayStart = now - (now % DAY_MILLIS)
    return this.date >= todayStart
}

interface AffirmationsPersistor {
    fun saveIndex(lastUsedIndex: LastUsedIndex)
    fun loadIndex() : LastUsedIndex
}