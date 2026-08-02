package com.example.leaderboardengine.main.mapper

import com.example.gameengine.data.Player
import com.example.leaderboardengine.main.data.TopPlayerUIModel
import javax.inject.Inject

class TopPlayerMapper @Inject constructor() : Mapper<Pair<Player,Int>, TopPlayerUIModel> {
    override suspend fun convert(from: Pair<Player,Int>): TopPlayerUIModel {
        return TopPlayerUIModel(
            id = from.first.playerId,
            name = from.first.playerName,
            score = from.first.points,
            rank = from.second
        )
    }
}