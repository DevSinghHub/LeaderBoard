package com.example.leaderboard.ui.theme.compose

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color

@Stable
data class AppColor(
    val colorAppBg: Color = Color(0xFF120505),
    val colorBrightRed: Color = Color(0xFFA74A4D),
    val colorDarkRed: Color = Color(0xFF71151F),
    val colorExtraDarkRed: Color = Color(0xFF240004),
    val titleColor: Color = Color(0xFFa86032),
    val playerBg:Color = Color(0xFF2C2B2B),
    val primaryColor: Color = Color(0xFFFFFFFF),
    val highLightColor: Color = Color(0xFF98794B)
)