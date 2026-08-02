package com.example.leaderboard.viewmodel

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.leaderboardengine.main.data.PlayerSnapshot
import com.example.leaderboardengine.main.data.TopPlayerUIModel
import com.example.leaderboardengine.main.main.LeaderBoard
import dagger.Lazy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeaderBoardViewModel @Inject constructor(
    private val leaderBoard: Lazy<LeaderBoard>
) : ViewModel() {

    private val previousPlayers = mutableStateMapOf<Int, PlayerSnapshot>()
    val highlightedIds = mutableStateMapOf<Int, Long>()

    init {
        viewModelScope.launch(Dispatchers.IO){
            leaderBoard.get().initialize(this)
        }
    }

    /**
     * Returns the [StateFlow] emitting the top 20 players list from the LeaderBoard engine.
     */
    fun getTopPlayerList(): StateFlow<List<TopPlayerUIModel>> {
        return leaderBoard.get().topPlayerList
    }

    /**
     * Compares incoming [players] against [previousPlayers] snapshots.
     * If a player is new or has updated score or rank, records a timestamp in [highlightedIds]
     * to trigger a temporary UI highlight effect.
     */
    fun updateHighlights(players: List<TopPlayerUIModel>) {
        players.forEach { player ->
            val previous = previousPlayers[player.id]
            val isNew = previous == null
            val scoreChanged = previous != null && previous.score != player.score
            val rankChanged = previous != null && previous.rank != player.rank

            if (isNew || scoreChanged || rankChanged) {
                highlightedIds[player.id] = System.currentTimeMillis()
            }
            previousPlayers[player.id] = PlayerSnapshot(score = player.score, rank = player.rank)
        }
    }

    /**
     * Removes the specified [playerId] from [highlightedIds] once its highlight timer expires.
     */
    fun clearHighlight(playerId: Int) {
        highlightedIds.remove(playerId)
    }

    /**
     * Called when the app enters the background.
     * Pauses top 20 calculation in the LeaderBoard engine while keeping score generator alive.
     */
    fun onAppPaused() {
        leaderBoard.get().pauseLeaderBoard()
    }

    /**
     * Called when the app returns to the foreground.
     * Synchronizes latest player scores from ScoreGenerator, recalculates top 20, and resumes listening.
     */
    fun onAppResumed() {
        viewModelScope.launch(Dispatchers.IO) {
            leaderBoard.get().resumeLeaderBoard(this)
        }
    }
}