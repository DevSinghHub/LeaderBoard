package com.example.leaderboard.ui.widget

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.example.leaderboard.ui.theme.compose.AppThemeData
import com.example.leaderboardengine.main.data.TopPlayerUIModel

@Composable
fun PlayerItem(
    playerUIModel: TopPlayerUIModel,
    isHighlighted: Boolean = false
) {
    AppThemeData().apply {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensions.dimen80)
                .clip(RoundedCornerShape(dimensions.dimen12))
                .background(color = colors.playerBg)
        ) {
            AnimatedVisibility(
                visible = isHighlighted,
                enter = fadeIn(animationSpec = tween(durationMillis = 200)),
                exit = fadeOut(animationSpec = tween(durationMillis = 200))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = colors.highLightColor)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(dimensions.dimen15),
                horizontalArrangement = Arrangement.spacedBy(dimensions.dimen10),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = playerUIModel.rank.toString(),
                    style = typography.semiBoldStyle.copy(
                        fontSize = textDimensions.sp16,
                        color = colors.primaryColor
                    )
                )
                Text(
                    modifier = Modifier.weight(1f),
                    text = playerUIModel.name,
                    style = typography.semiBoldStyle.copy(
                        fontSize = textDimensions.sp16,
                        color = colors.primaryColor
                    )
                )
                Text(
                    text = playerUIModel.score.toString(),
                    style = typography.semiBoldStyle.copy(
                        fontSize = textDimensions.sp16,
                        color = colors.primaryColor
                    )
                )
            }
        }
    }
}