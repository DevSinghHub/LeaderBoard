package com.example.leaderboard.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.example.leaderboard.R
import com.example.leaderboard.constant.Constant.DEFAULT_HIGHLIGHT_TIME
import com.example.leaderboard.ui.extension.appGradientBackground
import com.example.leaderboard.ui.theme.compose.AppThemeData
import com.example.leaderboard.ui.widget.PlayerItem
import com.example.leaderboardengine.main.data.TopPlayerUIModel
import kotlinx.coroutines.delay

@Composable
fun LeaderBoardScreen(
    players: List<TopPlayerUIModel>,
    highlightedIds: Map<Int, Long>,
    onPlayersChanged: (List<TopPlayerUIModel>) -> Unit,
    onHighlightExpired: (Int) -> Unit
){
    val listState = rememberLazyListState()

    LaunchedEffect(players) {
        onPlayersChanged(players)
    }

    AppThemeData().apply {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .appGradientBackground()
                .padding(dimensions.dimen10)
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dimensions.dimen80, bottom = dimensions.dimen20),
                text = stringResource(R.string.leaderboard),
                style = typography.boldStyle.copy(
                    fontSize = textDimensions.sp28,
                    color = colors.titleColor
                ),
                textAlign = TextAlign.Center
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(dimensions.dimen8),
                state = listState,
                contentPadding = PaddingValues(vertical = dimensions.dimen40)
            ) {
                itemsIndexed(
                    items = players,
                    key = { index, item -> item.id }
                ) { index, item ->
                    val isHighlighted = highlightedIds.containsKey(item.id)

                    if (isHighlighted) {
                        LaunchedEffect(highlightedIds[item.id]) {
                            delay(DEFAULT_HIGHLIGHT_TIME)
                            onHighlightExpired(item.id)
                        }
                    }

                    PlayerItem(
                        playerUIModel = item,
                        isHighlighted = isHighlighted
                    )
                }
            }
        }
    }
}