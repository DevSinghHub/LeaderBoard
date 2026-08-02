package com.example.leaderboardengine

import org.junit.Test

import org.junit.Assert.*

import com.example.gameengine.data.Player
import com.example.gameengine.main.ScoreGenerator
import com.example.leaderboardengine.main.data.TopPlayerUIModel
import com.example.leaderboardengine.main.main.LeaderBoard
import com.example.leaderboardengine.main.mapper.TopPlayerMapper
import dagger.Lazy
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before

class LeaderBoardTest {

    private lateinit var leaderBoard: LeaderBoard

    private fun player(id: Int, points: Int) = Player(playerId = id, playerName = "Player$id", points = points)

    @Before
    fun setup() {
        val scoreGenerator: ScoreGenerator = mockk(relaxed = true)
        val topPlayerMapper: TopPlayerMapper = mockk()

        coEvery { topPlayerMapper.convert(any()) } coAnswers {
            val pair = firstArg<Pair<Player, Int>>()
            val (p, rank) = pair
            TopPlayerUIModel(id = p.playerId, name = p.playerName, score = p.points, rank = rank)
        }

        leaderBoard = LeaderBoard(
            scoreGenerator = Lazy { scoreGenerator },
            topPlayerMapper = Lazy { topPlayerMapper }
        )
    }

    @Test
    fun `getTop20 with no players emits empty list`() = runTest {
        leaderBoard.getTop20()
        assertTrue(leaderBoard.topPlayerList.value.isEmpty())
    }

    @Test
    fun `getTop20 sorts registered players by points descending`() = runTest {
        leaderBoard.registerPlayer(player(1, 50))
        leaderBoard.registerPlayer(player(2, 200))
        leaderBoard.registerPlayer(player(3, 100))

        leaderBoard.getTop20()
        val result = leaderBoard.topPlayerList.value

        assertEquals(listOf(2, 3, 1), result.map { it.id })
        assertEquals(listOf(1, 2, 3), result.map { it.rank })
    }

    @Test
    fun `getTop20 assigns same rank to tied scores and skips next rank`() = runTest {
        leaderBoard.registerPlayer(player(1, 100))
        leaderBoard.registerPlayer(player(2, 100)) // tied with player 1
        leaderBoard.registerPlayer(player(3, 80))

        leaderBoard.getTop20()
        val ranks = leaderBoard.topPlayerList.value.associate { it.id to it.rank }

        assertEquals(1, ranks[1])
        assertEquals(1, ranks[2])
        assertEquals(3, ranks[3]) // rank 2 skipped due to tie
    }

    @Test
    fun `getTop20 caps result at 20 players`() = runTest {
        (1..25).forEach { id -> leaderBoard.registerPlayer(player(id, id * 10)) }

        leaderBoard.getTop20()
        val result = leaderBoard.topPlayerList.value

        assertEquals(20, result.size)
        assertEquals(25, result.first().id) // highest score leads
    }

    @Test
    fun `updateScore moves player into new score bucket reflected in getTop20`() = runTest {
        leaderBoard.registerPlayer(player(1, 50))
        leaderBoard.registerPlayer(player(2, 100))

        leaderBoard.updateScore(player(1, 150)) // player 1 overtakes
        leaderBoard.getTop20()

        val result = leaderBoard.topPlayerList.value
        assertEquals(1, result.first().id)
        assertEquals(150, result.first().score)
    }

    @Test
    fun `updateScore does not leave stale duplicate entries across buckets`() = runTest {
        leaderBoard.registerPlayer(player(1, 50))

        leaderBoard.updateScore(player(1, 90))
        leaderBoard.updateScore(player(1, 120))
        leaderBoard.getTop20()

        val result = leaderBoard.topPlayerList.value
        assertEquals(1, result.size) // only one entry, not one per update
        assertEquals(120, result.first().score)
    }

    @Test
    fun `updateScore for unregistered player still adds them`() = runTest {
        // simulates score update arriving for a player not yet in scoreMap
        leaderBoard.updateScore(player(1, 300))
        leaderBoard.getTop20()

        val result = leaderBoard.topPlayerList.value
        assertEquals(1, result.size)
        assertEquals(300, result.first().score)
    }

    @Test
    fun `updateScore recalculates ranks correctly after tie is broken`() = runTest {
        leaderBoard.registerPlayer(player(1, 100))
        leaderBoard.registerPlayer(player(2, 100)) // tied

        leaderBoard.updateScore(player(2, 150)) // breaks the tie
        leaderBoard.getTop20()

        val result = leaderBoard.topPlayerList.value
        assertEquals(listOf(2, 1), result.map { it.id })
        assertEquals(listOf(1, 2), result.map { it.rank }) // no more tie, sequential ranks
    }
}