package com.happyday.android.alarm

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.happyday.android.utils.loge
import java.util.*

data class Affirmation(val id: Int, @DrawableRes val img: Int, @StringRes val text: Int)
data class LastUsedIndex(val index: Int, val date: Long)

class Affirmations(
    private val persistor: AffirmationsPersistor,
    private val affirmations: List<Affirmation>
) {
    /**
     * @param indexChecker needed only for testing
     */
    fun getNext(indexChecker:(LastUsedIndex)->Boolean = { it.isToday() }) : Affirmation {
        // 1 day 1 affirmation
        //  - list of affirmations must be ordered to keep same order between sessions

        val lastUsedIndex = persistor.loadIndex()
        loge("Last used index: $lastUsedIndex")
        if (indexChecker(lastUsedIndex)) {
            loge("Last used index = today!")
            return affirmations[lastUsedIndex.index]
        }

        loge("Last used index not found, storing...")
        val today = Calendar.getInstance().timeInMillis
        val nextIndex = getAdjustedIndex(lastUsedIndex.index, affirmations.size)
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
        return loadedIndex % affirmationsSize
    }
}

private const val DAY_MILLIS = 86400000
private fun LastUsedIndex.isToday() : Boolean {
    val now = Calendar.getInstance().timeInMillis
    val todayStart = now - (now % DAY_MILLIS)
    return todayStart <= this.date && this.date < todayStart + DAY_MILLIS
}

interface AffirmationsPersistor {
    fun saveIndex(lastUsedIndex: LastUsedIndex)
    fun loadIndex() : LastUsedIndex
}