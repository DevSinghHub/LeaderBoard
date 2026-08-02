package com.example.leaderboardengine.main.mapper

interface Mapper<F, T> {
    suspend fun convert(from: F): T
}