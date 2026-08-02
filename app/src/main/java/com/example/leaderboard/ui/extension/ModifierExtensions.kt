package com.example.leaderboard.ui.extension

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import com.example.leaderboard.ui.theme.compose.AppColor
import com.example.leaderboard.ui.theme.compose.AppTheme

fun Modifier.appGradientBackground(alpha: Float = 1f, colors: AppColor = AppTheme.colors): Modifier {
    return this
        .drawWithCache {
            val colorStops = arrayOf(
                0.0f to colors.colorBrightRed,
                0.35f to colors.colorDarkRed,
                0.85f to colors.colorExtraDarkRed,
                1.1f to colors.colorAppBg,
            )
            val brush = Brush.radialGradient(
                colorStops = colorStops,
                center = Offset(0f, size.height * 0.05f),
                radius = size.height * 1.1f,
            )
            onDrawBehind {
                drawRect(
                    brush = brush,
                    alpha = alpha
                )
            }
        }
}