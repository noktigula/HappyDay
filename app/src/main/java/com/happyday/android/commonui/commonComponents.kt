package com.happyday.android.commonui

import android.view.LayoutInflater
import android.widget.ImageView
import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mmin18.widget.RealtimeBlurView
import com.happyday.android.ui.theme.HeaderGradients
import com.happyday.android.ui.theme.RoundCorners
import com.happyday.android.ui.theme.Spacing
import com.happyday.android.R
import jp.wasabeef.blurry.Blurry

@Composable
fun Screen(content: @Composable ColumnScope.() -> Unit) {
   Box(Modifier.fillMaxSize()) {
       Background()
       Column(modifier = Modifier
           .fillMaxWidth()
           .fillMaxHeight(),
           content = content
       )
   }
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

@Composable
fun Background() {
    Box(Modifier.fillMaxWidth()) {
        Stars()
        Blur()
    }
}

@Composable
fun Blur() {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            LayoutInflater.from(context).inflate(R.layout.blur_overlay, null)
        }
    )
}

//            val imageView = ImageView(context).apply {
//                post {
//                    val bitmap = Blurry.with(context).radius(10).sampling(8).capture(this).get()
//                    this.setImageBitmap(bitmap)
//                }
//            }
//
//            imageView

@Composable
fun Stars() {
    Column(Modifier.fillMaxWidth()) {
        Row(
            Modifier
                .fillMaxWidth()
                .weight(3f)
                .padding(end = Spacing.Medium.size),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Bottom
        ) {
            Image(painterResource(id = R.drawable.ic_star_small), "star small")
        }
        Row(
            Modifier
                .fillMaxWidth()
                .weight(2f)
                .padding(start = Spacing.Medium.size),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Bottom
        ) {
            Image(painterResource(id = R.drawable.ic_star_medium), "star medium")
        }
        Row(
            Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(bottom = Spacing.Medium.size),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Bottom
        ) {
            Image(painterResource(id = R.drawable.ic_star_large), "star large")
        }
    }
}