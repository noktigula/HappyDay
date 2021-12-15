package com.happyday.android.ui.theme

import androidx.annotation.Dimension
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

sealed class Spacing(@Dimension val size:Dp) {
    object Small: Spacing(8.dp)
    object Medium: Spacing(16.dp)
    object Large: Spacing(24.dp)
}