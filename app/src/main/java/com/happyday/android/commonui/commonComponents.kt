package com.happyday.android.commonui

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.happyday.android.ui.theme.Spacing

@Composable
fun Screen(content: @Composable ColumnScope.() -> Unit) {
   Column(modifier = Modifier
       .fillMaxWidth()
       .fillMaxHeight()
       .padding(
           horizontal = Spacing.Medium.size,
           vertical = Spacing.Small.size
       ),
       content = content
   )
}