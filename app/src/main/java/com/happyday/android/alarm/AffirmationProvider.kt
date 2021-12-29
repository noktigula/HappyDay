package com.happyday.android.alarm

import com.happyday.android.R

interface AffirmationProvider {
    fun getAffirmations() : List<Affirmation>
}

fun affirmationProvider(): AffirmationProvider = HardcodedAffirmationProvider()

class HardcodedAffirmationProvider : AffirmationProvider {
    override fun getAffirmations(): List<Affirmation> {
        return listOf(
            Affirmation(0, R.drawable.image_0, R.string.motivatin_0)
        )
    }
}