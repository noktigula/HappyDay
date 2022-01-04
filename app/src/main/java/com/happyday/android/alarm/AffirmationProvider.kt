package com.happyday.android.alarm

import com.happyday.android.R

interface AffirmationProvider {
    fun getAffirmations() : List<Affirmation>
}

fun affirmationProvider(): AffirmationProvider = HardcodedAffirmationProvider()

class HardcodedAffirmationProvider : AffirmationProvider {
    override fun getAffirmations(): List<Affirmation> {
        return listOf(
            Affirmation(0, R.drawable.img_0, R.string.affirmation_0),
            Affirmation(1, R.drawable.img_1, R.string.affirmation_1),
            Affirmation(2, R.drawable.img_2, R.string.affirmation_2),
            Affirmation(3, R.drawable.img_3, R.string.affirmation_3),
            Affirmation(4, R.drawable.img_4, R.string.affirmation_4),
            Affirmation(5, R.drawable.img_5, R.string.affirmation_5),
            Affirmation(6, R.drawable.img_6, R.string.affirmation_6),
            Affirmation(7, R.drawable.img_7, R.string.affirmation_7),
            Affirmation(8, R.drawable.img_8, R.string.affirmation_8),
            Affirmation(9, R.drawable.img_9, R.string.affirmation_9),
            Affirmation(10, R.drawable.img_10, R.string.affirmation_10),
            Affirmation(11, R.drawable.img_11, R.string.affirmation_11),
            Affirmation(12, R.drawable.img_12, R.string.affirmation_12),
            Affirmation(13, R.drawable.img_13, R.string.affirmation_13),
            Affirmation(14, R.drawable.img_14, R.string.affirmation_14),
            Affirmation(15, R.drawable.img_15, R.string.affirmation_15),
            Affirmation(16, R.drawable.img_16, R.string.affirmation_16),
            Affirmation(17, R.drawable.img_17, R.string.affirmation_17),
            Affirmation(18, R.drawable.img_18, R.string.affirmation_18),
            Affirmation(19, R.drawable.img_19, R.string.affirmation_19),
            Affirmation(20, R.drawable.img_20, R.string.affirmation_20),
            Affirmation(21, R.drawable.img_21, R.string.affirmation_21),
            Affirmation(22, R.drawable.img_22, R.string.affirmation_22),
            Affirmation(23, R.drawable.img_23, R.string.affirmation_23),
            Affirmation(24, R.drawable.img_24, R.string.affirmation_24),
            Affirmation(25, R.drawable.img_25, R.string.affirmation_25),
            Affirmation(26, R.drawable.img_26, R.string.affirmation_26),
            Affirmation(27, R.drawable.img_27, R.string.affirmation_27),
            Affirmation(28, R.drawable.img_28, R.string.affirmation_28),
            Affirmation(29, R.drawable.img_29, R.string.affirmation_29),
            Affirmation(30, R.drawable.img_30, R.string.affirmation_30),
            Affirmation(31, R.drawable.img_31, R.string.affirmation_31),
            Affirmation(32, R.drawable.img_32, R.string.affirmation_32),
            Affirmation(33, R.drawable.img_33, R.string.affirmation_33),
            Affirmation(34, R.drawable.img_34, R.string.affirmation_34),
            Affirmation(35, R.drawable.img_35, R.string.affirmation_35),
            Affirmation(36, R.drawable.img_36, R.string.affirmation_36),
            Affirmation(37, R.drawable.img_37, R.string.affirmation_37),
            Affirmation(38, R.drawable.img_38, R.string.affirmation_38),
            Affirmation(39, R.drawable.img_39, R.string.affirmation_39),
            Affirmation(40, R.drawable.img_40, R.string.affirmation_40),
            Affirmation(41, R.drawable.img_41, R.string.affirmation_41),
            Affirmation(42, R.drawable.img_42, R.string.affirmation_42),
            Affirmation(43, R.drawable.img_43, R.string.affirmation_43),
            Affirmation(44, R.drawable.img_44, R.string.affirmation_44),
            Affirmation(45, R.drawable.img_45, R.string.affirmation_45),
            Affirmation(46, R.drawable.img_46, R.string.affirmation_46),
            Affirmation(47, R.drawable.img_47, R.string.affirmation_47),
            Affirmation(48, R.drawable.img_48, R.string.affirmation_48),
        )
    }
}