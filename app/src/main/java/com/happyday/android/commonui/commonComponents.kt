package com.happyday.android.commonui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.happyday.android.ui.theme.Spacing

@Composable
fun Screen(content: @Composable ColumnScope.() -> Unit) {
   Column(modifier = Modifier
       .fillMaxWidth()
       .fillMaxHeight()
       .background(Color.LightGray),
       content = content
   )
}