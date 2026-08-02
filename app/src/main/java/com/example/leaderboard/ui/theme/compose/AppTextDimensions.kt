package com.example.leaderboard.ui.theme.compose

import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Stable
data class AppTextDimensions(
    val sp1 : TextUnit = 1.sp,
    val sp8 : TextUnit = 8.sp,
    val sp16 : TextUnit = 16.sp,
    val sp28 : TextUnit = 28.sp
)