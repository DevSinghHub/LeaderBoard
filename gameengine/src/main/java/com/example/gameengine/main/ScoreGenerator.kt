package com.example.gameengine.main

import com.example.gameengine.data.Player
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dagger.Lazy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ScoreGenerator @Inject constructor(
    private val moshi: Lazy<Moshi>
){
    private val _updatedPlayer = MutableStateFlow<Player?>(null)
    val updatedPlayer = _updatedPlayer.asStateFlow()

    private val randomIntervalRangeMs: LongRange = 500L..3000L
    private val randomPointsRange: IntRange = 1..50
    private var allPlayers : MutableList<Player> = mutableListOf()

    /**
     * Initializes the score generator by loading players from the JSON resource
     * and starting the random background score generation task.
     */
    fun initializeScore(scope: CoroutineScope){
        allPlayers = getPlayers().toMutableList()
        scope.launch {
            startRandomScoreGenerator()
        }
    }

    /**
     * Returns the current in-memory list of all players.
     */
    fun getAllPlayers() : List<Player>{
        return allPlayers
    }

    /** 
     * Reads and parses the initial list of players from the `players.json` asset file using Moshi.
     */
    fun getPlayers() : List<Player>{
        val jsonString = runCatching {
            javaClass.classLoader?.getResourceAsStream("players.json")
                ?.bufferedReader()
                ?.use { it.readText() }
        }.getOrNull()?: return emptyList()
        val listType = Types.newParameterizedType(List::class.java, Player::class.java)
        val adapter = moshi.get().adapter<List<Player>>(listType)

        return runCatching {
            adapter.fromJson(jsonString)
        }.getOrNull()?: emptyList()
    }

    /**
     * Continuously generates random score updates for a random player at random time intervals (0.5s - 3s)
     * and emits the updated player model to [_updatedPlayer].
     */
    suspend fun startRandomScoreGenerator() {
        while (true) {
            delay(randomIntervalRangeMs.random())
            val point = randomPointsRange.random()

            if (allPlayers.isNotEmpty()) {
                val playerIndex = allPlayers.indices.random()
                val updatedPlayerScore = allPlayers[playerIndex].copy(
                    points = allPlayers[playerIndex].points + point
                )
                allPlayers[playerIndex] = updatedPlayerScore
                _updatedPlayer.emit(updatedPlayerScore)
            }
        }
    }
}