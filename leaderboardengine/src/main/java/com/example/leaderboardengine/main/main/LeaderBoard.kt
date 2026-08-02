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

    suspend fun initialize(scope: CoroutineScope){
        scoreGenerator.get().initializeScore(scope)
        registerAllPlayers()
        getTop20()
        startPlayerScoreListener()
    }

    fun registerAllPlayers(){
        scoreGenerator.get().getAllPlayers().forEach {
            registerPlayer(it)
        }
    }

    fun registerPlayer(player: Player) {
        scoreMap.getOrPut(player.points) { mutableSetOf() }.add(player)
        playerScores[player.playerId] = player.points
    }


    fun updateScore(updatedPlayer: Player) {
        val oldPoints = playerScores[updatedPlayer.playerId]
        if (oldPoints != null) {
            scoreMap[oldPoints]?.removeIf { it.playerId == updatedPlayer.playerId }
            if (scoreMap[oldPoints].isNullOrEmpty()) scoreMap.remove(oldPoints)
        }
        scoreMap.getOrPut(updatedPlayer.points) { mutableSetOf() }.add(updatedPlayer)
        playerScores[updatedPlayer.playerId] = updatedPlayer.points
    }

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

    private suspend fun startPlayerScoreListener() {
        scoreGenerator.get().updatedPlayer.collect { updatedPlayer ->
            updatedPlayer?.let {
                updateScore(updatedPlayer)
                getTop20()
            }
        }
    }
}