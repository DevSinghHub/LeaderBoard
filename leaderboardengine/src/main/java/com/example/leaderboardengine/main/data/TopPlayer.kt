package com.example.leaderboardengine.main.data

import javax.annotation.concurrent.Immutable

@Immutable
data class TopPlayerUIModel(
    val id : Int,
    val name : String,
    val score : Int,
    val rank : Int = 0
)