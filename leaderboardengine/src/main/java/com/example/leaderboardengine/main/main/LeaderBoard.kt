package com.example.leaderboardengine.main.main

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.gameengine.data.Player
import com.example.gameengine.main.ScoreGenerator
import com.example.leaderboardengine.main.data.TopPlayerUIModel
import com.example.leaderboardengine.main.mapper.TopPlayerMapper
import dagger.Lazy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.TreeMap
import javax.inject.Inject
import kotlin.collections.iterator

@RequiresApi(Build.VERSION_CODES.N)
class LeaderBoard @Inject constructor(
    val scoreGenerator: Lazy<ScoreGenerator>,
    val topPlayerMapper: Lazy<TopPlayerMapper>
) {
    private val _topPlayerList = MutableStateFlow<List<TopPlayerUIModel>>(emptyList())
    val topPlayerList = _topPlayerList.asStateFlow()

    private val scoreMap = TreeMap<Int, MutableSet<Player>>(compareByDescending { it })
    private val playerScores = mutableMapOf<Int, Int>()

    private var scoreListenerJob: Job? = null
    private var isInitialized = false

    /**
     * Bootstraps the LeaderBoard engine by launching the score generator, loading initial players,
     * computing the initial Top 20 list, and listening for real-time score updates.
     */
    suspend fun initialize(scope: CoroutineScope){
        if (isInitialized) return
        scoreGenerator.get().initializeScore(scope)
        isInitialized = true
    }

    /**
     * Registers all players provided by the [ScoreGenerator] into the internal data structures.
     */
    fun registerAllPlayers(){
        scoreGenerator.get().getAllPlayers().forEach {
            registerPlayer(it)
        }
    }

    /**
     * Registers a single [player] into the score-bucketed [scoreMap] and updates the [playerScores] lookup map.
     */
    fun registerPlayer(player: Player) {
        scoreMap.getOrPut(player.points) { mutableSetOf() }.add(player)
        playerScores[player.playerId] = player.points
    }

    /**
     * Updates an existing player's score by removing them from their previous score bucket
     * in [scoreMap] and adding them to the new bucket corresponding to [updatedPlayer.points].
     */
    fun updateScore(updatedPlayer: Player) {
        val oldPoints = playerScores[updatedPlayer.playerId]
        if (oldPoints != null) {
            scoreMap[oldPoints]?.removeIf { it.playerId == updatedPlayer.playerId }
            if (scoreMap[oldPoints].isNullOrEmpty()) scoreMap.remove(oldPoints)
        }
        scoreMap.getOrPut(updatedPlayer.points) { mutableSetOf() }.add(updatedPlayer)
        playerScores[updatedPlayer.playerId] = updatedPlayer.points
    }

    /**
     * Computes the top 20 players based on descending scores, assigns dense ranks (with tie-handling),
     * and emits the resulting list to [_topPlayerList].
     */
    suspend fun getTop20(){
        val result = mutableListOf<TopPlayerUIModel>()
        var rank = 1
        for ((_, players) in scoreMap) {
            for (p in players) {
                result.add(topPlayerMapper.get().convert(p to rank))
                if (result.size >= 20) break
            }
            if (result.size >= 20) break
            rank += players.size
        }
        _topPlayerList.emit(result)
    }

    /**
     * Listens for real-time player score updates emitted by [ScoreGenerator], updates internal
     * leaderboard state, and recalculates the top 20 rankings.
     */
    fun startPlayerScoreListener(scope: CoroutineScope) {
        scoreListenerJob?.cancel()
        scoreListenerJob = scope.launch {
            scoreGenerator.get().updatedPlayer.collect { updatedPlayer ->
                updatedPlayer?.let {
                    updateScore(updatedPlayer)
                    getTop20()
                }
            }
        }
    }

    /**
     * Pauses leaderboard processing when the app enters the background.
     * Cancels the live score listener job so top 20 is not calculated while in background.
     */
    fun pauseLeaderBoard() {
        scoreListenerJob?.cancel()
        scoreListenerJob = null
    }

    /**
     * Resumes leaderboard processing when the app returns to the foreground.
     * Clears stale local state, syncs the latest player scores from [ScoreGenerator],
     * recalculates top 20, and restarts listening for live updates.
     */
    suspend fun resumeLeaderBoard(scope: CoroutineScope) {
        if (!isInitialized) {
            initialize(scope)
        }
        scoreMap.clear()
        playerScores.clear()
        registerAllPlayers()
        getTop20()
        startPlayerScoreListener(scope)
    }
}