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

    fun initializeScore(scope: CoroutineScope){
        allPlayers = getPlayers().toMutableList()
        scope.launch {
            startRandomScoreGenerator()
        }
    }

    fun getAllPlayers() : List<Player>{
        return allPlayers
    }

    /** using a json file as list of players to mock server players**/
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