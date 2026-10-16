package com.example.hanziapp.srs

import com.example.hanziapp.data.CardState
import com.example.hanziapp.data.UserProgress
import kotlin.math.max
import kotlin.math.pow

enum class Rating { AGAIN, HARD, GOOD, EASY }

object FsrsScheduler {
    private val W = doubleArrayOf(
        0.4, 0.6, 2.4, 5.8, 4.93, 0.94, 0.86, 0.01,
        1.49, 0.14, 0.94, 2.18, 0.05, 0.34, 1.26, 0.29, 2.61
    )

    private const val DAY_MS = 86_400_000L

    fun schedule(p: UserProgress, rating: Rating): UserProgress {
        val now = System.currentTimeMillis()
        val daysSince = if (p.lastReview == 0L) 0.0
                        else (now - p.lastReview).toDouble() / DAY_MS

        val r = if (p.stability > 0)
            (1 + daysSince / (9 * p.stability)).pow(-1.0)
        else 0.0

        val newState: CardState
        var s = p.stability
        var d = p.difficulty
        var lapses = p.lapses

        if (p.state == CardState.NEW) {
            s = when (rating) {
                Rating.AGAIN -> W[0]
                Rating.HARD  -> W[1]
                Rating.GOOD  -> W[2]
                Rating.EASY  -> W[3]
            }
            d = (W[4] - (rating.ordinal - 2) * W[5]).coerceIn(1.0, 10.0)
            newState = CardState.LEARNING
        } else {
            val bonus = when (rating) {
                Rating.AGAIN -> W[11]
                Rating.HARD  -> W[12]
                Rating.GOOD  -> W[13]
                Rating.EASY  -> W[14]
            }
            s = s * (1 + bonus * (1 - r))
            d = (d - W[6] * (rating.ordinal - 2)).coerceIn(1.0, 10.0)
            if (rating == Rating.AGAIN) { lapses++; s = max(0.1, s * 0.5) }
            newState = CardState.LEARNING
        }

        val interval = when (rating) {
            Rating.AGAIN -> 0.0
            else -> s * (9 * (1 / 0.9 - 1)).pow(-1.0)
        }.coerceAtLeast(0.0)

        val finalState = if (p.state == CardState.KNOWN || interval >= 21.0)
            CardState.KNOWN else newState

        return p.copy(
            state = finalState,
            stability = s,
            difficulty = d,
            dueDate = now + (interval * DAY_MS).toLong().coerceAtLeast(60_000L),
            lastReview = now,
            reps = p.reps + 1,
            lapses = lapses
        )
    }
}