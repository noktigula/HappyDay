package com.happyday.android.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with
val Typography = Typography(
        body1 = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp
        ),
        //alarm time on alarm list card
        h1 = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight(500),
                fontSize = 30.sp,
                color = PrimaryPurple
        ),
        //weekday on alarm list card
        caption = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight(500),
                fontSize = 13.sp,
                color = SecondaryPurple
        ),
        body2 = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight(500),
                fontSize = 15.sp,
                color = PrimaryPurple
        ),
        //title on main header
        h2 = TextStyle(
                fontFamily = FontFamily.Cursive, // TODO add proper font,
                fontWeight = FontWeight(400),
                fontSize = 35.sp,
                color = HeaderTitleColor
        )
        /* Other default text styles to override
    button = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp
    ),
    caption = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )
    */
)