package com.example.composition.data

import com.example.composition.domain.entities.GameSettings
import com.example.composition.domain.entities.Level
import com.example.composition.domain.entities.Question
import com.example.composition.domain.repository.GameRepository
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

object GameRepositoryImpl: GameRepository {

    private const val MIN_SUM_VALUE = 2
    private const val DIFF_IN_RESPONSE = 8
    private const val MIN_ANSWER_VALUE = 1

    override fun generateQuestion(maxSumValue: Int, countOfOptions: Int): Question {
        val sum = Random.nextInt(MIN_SUM_VALUE, maxSumValue+1)
        val visibleNumber = Random.nextInt(1, sum)
        val rightAnswer = sum - visibleNumber

        val options = HashSet<Int>()
        options.add(rightAnswer)

        val optionFrom = max(rightAnswer - DIFF_IN_RESPONSE, MIN_ANSWER_VALUE)
        val optionUntil = min(rightAnswer + DIFF_IN_RESPONSE, maxSumValue)

        while(options.size < countOfOptions) {
            val option = Random.nextInt(optionFrom, optionUntil)
            options.add(option)
        }

        return Question(sum, visibleNumber, options.toList())
    }

    override fun getGameSettings(level: Level): GameSettings {
        return when(level) {
            Level.TEST -> GameSettings(10, 3, 50, 5)
            Level.EASY -> GameSettings(10, 10, 50, 60)
            Level.NORMAL -> GameSettings(50, 20, 75, 40)
            Level.HARD -> GameSettings(100, 30, 100, 20)
        }
    }
}