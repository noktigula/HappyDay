package com.happyday.android.ui.theme

import androidx.annotation.Dimension
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

sealed class Spacing(@Dimension val size:Dp) {
    object Small: Spacing(8.dp)
    object Medium: Spacing(16.dp)
    object Large: Spacing(24.dp)
}

sealed class Padding(@Dimension val size: Dp) {
    object AlarmCard: Padding(25.dp)
    object BetweenCards: Padding(12.dp)
    object BetweenSelectedWeekdays: Padding(2.dp)
}

val Elevation = 4.dp

sealed class RoundCorners(@Dimension val size: Dp) {
    object AlarmCard: RoundCorners(25.dp)
}