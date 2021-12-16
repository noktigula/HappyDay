package com.happyday.android.commonui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.happyday.android.ui.theme.HeaderGradients
import com.happyday.android.ui.theme.RoundCorners
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

@Composable
fun GradientButton(modifier: Modifier, onClick: ()->Unit, content: @Composable RowScope.()->Unit) {
    val shape = RoundedCornerShape(RoundCorners.GradientButton.size)
    Surface(modifier = modifier, shape = shape) {
        Row(
            modifier = Modifier
                .clickable { onClick() }
                .background(Brush.horizontalGradient(HeaderGradients), shape = shape)
                .padding(horizontal = Spacing.Large.size, vertical = Spacing.Small.size),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            content()
        }
    }
}