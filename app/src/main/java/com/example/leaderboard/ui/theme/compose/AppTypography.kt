package com.example.leaderboard.ui.theme.compose

import androidx.compose.runtime.Stable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.leaderboard.R

@Stable
data class AppTypography(
    val boldStyle : TextStyle = TextStyle(
        fontFamily = interBold
    ),
    val regularStyle : TextStyle = TextStyle(
        fontFamily = inter
    ),
    val mediumStyle : TextStyle = TextStyle(
        fontFamily = interMedium
    ),
    val semiBoldStyle : TextStyle = TextStyle(
        fontFamily = interSemiBold
    ),
)

private val inter = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal)
)

private val interMedium = FontFamily(
    Font(R.font.inter_medium, FontWeight.Normal)
)

private val interSemiBold = FontFamily(
    Font(R.font.inter_semi_bold, FontWeight.Normal)
)

private val interBold = FontFamily(
    Font(R.font.inter_bold, FontWeight.Normal)
)