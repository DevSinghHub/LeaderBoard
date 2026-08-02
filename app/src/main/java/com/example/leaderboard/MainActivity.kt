package com.example.leaderboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.leaderboard.ui.screen.LeaderBoardScreen
import com.example.leaderboard.viewmodel.LeaderBoardViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: LeaderBoardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        setContent {
            MainScreen()
        }
    }

    @Composable
    fun MainScreen(){
        val topPlayer = viewModel.getTopPlayerList().collectAsStateWithLifecycle()
        LeaderBoardScreen(
            players = topPlayer.value,
            highlightedIds = viewModel.highlightedIds,
            onPlayersChanged = { viewModel.updateHighlights(it) },
            onHighlightExpired = { viewModel.clearHighlight(it) }
        )
    }

    override fun onStart() {
        super.onStart()
        viewModel.onAppResumed()
    }

    override fun onStop() {
        super.onStop()
        viewModel.onAppPaused()
    }
}