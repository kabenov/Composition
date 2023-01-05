package com.example.composition.presentation.viewmodel

import android.app.Application
import android.os.CountDownTimer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.composition.R
import com.example.composition.data.GameRepositoryImpl
import com.example.composition.domain.entities.GameResult
import com.example.composition.domain.entities.GameSettings
import com.example.composition.domain.entities.Level
import com.example.composition.domain.entities.Question
import com.example.composition.domain.usecases.GenerateQuestionUseCase
import com.example.composition.domain.usecases.GetGameSettingsUseCase

class GameViewModel(
    private val application: Application,
    private val level: Level
    ): ViewModel() {

//    private lateinit var level: Level
    private lateinit var gameSettings: GameSettings

    private var timer: CountDownTimer? = null

    private val context = application
    private val repository = GameRepositoryImpl

    private val getGameSettingsUseCase = GetGameSettingsUseCase(repository)
    private val generateQuestionUseCase = GenerateQuestionUseCase(repository)

    private val _formattedTime = MutableLiveData<String>()
    val formattedTime: LiveData<String>
        get() = _formattedTime

    private val _question = MutableLiveData<Question>()
    val question: LiveData<Question>
        get() = _question

    private val _percentOfRightAnswers = MutableLiveData<Int>()
    val percentOfRightAnswers: LiveData<Int>
        get() = _percentOfRightAnswers

    private val _progressOfAnswers = MutableLiveData<String>()
    val progressOfAnswers: LiveData<String>
        get() = _progressOfAnswers

    private val _enoughCountOfRightAnswers = MutableLiveData<Boolean>()
    val enoughCountOfRightAnswers: LiveData<Boolean>
        get() = _enoughCountOfRightAnswers

    private val _enoughPercentOfRightAnswers = MutableLiveData<Boolean>()
    val enoughPercentOfRightAnswers: LiveData<Boolean>
        get() = _enoughPercentOfRightAnswers

    private val _minPercent = MutableLiveData<Int>()
    val minPercent: LiveData<Int>
        get() = _minPercent

    private val _gameResult = MutableLiveData<GameResult>()
    val gameResult: LiveData<GameResult>
        get() = _gameResult

    private var countOfRightAnswers = 0
    private var countOfQuestion = 0

    init {
        startGame()
    }

    private fun startGame() {
        getGameSettings()
        _minPercent.value = gameSettings.minPercentOfRightAnswers
        startTimer()
        generateQuestion()
        updateProgress()
    }

    private fun getGameSettings() {
        this.gameSettings = getGameSettingsUseCase(level)
    }

    private fun startTimer() {
        timer = object: CountDownTimer(
            gameSettings.gameTimeInSecond * MILLIS_IN_SECONDS,
            MILLIS_IN_SECONDS
        ) {

            override fun onTick(p0: Long) {
                _formattedTime.value = formatTime(p0)
            }

            override fun onFinish() {
                finishGame()
            }
        }

        timer?.start()
    }

    private fun formatTime(millisecond: Long): String {
        val seconds = millisecond / 1000
        val minutes = seconds / SECONDS_IN_MINUTES
        val leftSeconds = seconds - (minutes * SECONDS_IN_MINUTES)

        return String.format("%02d:%02d", minutes, leftSeconds)
    }

    private fun finishGame() {
        _gameResult.value = GameResult(
            enoughCountOfRightAnswers.value == true && enoughPercentOfRightAnswers.value == true,
            countOfRightAnswers,
            countOfQuestion,
            gameSettings
        )
    }

    fun chooseAnswer(number: Int) {
        checkAnswer(number)
        updateProgress()
        generateQuestion()
    }

    private fun checkAnswer(number: Int) {
        val rightAnswer = question.value?.rightAnswer
        if(number == rightAnswer) {
            countOfRightAnswers++
        }
        countOfQuestion++
    }

    private fun updateProgress() {
        val tempPercentOfRightAnswers = calculatePercentOfRightAnswers()
        _percentOfRightAnswers.value = tempPercentOfRightAnswers
        _progressOfAnswers.value = String.format(
            context.resources.getString(R.string.game_fragment_answer_progress),
            countOfRightAnswers,
            gameSettings.minCountOfRightAnswers
        )
        _enoughCountOfRightAnswers.value = countOfRightAnswers >= gameSettings.minCountOfRightAnswers
        _enoughPercentOfRightAnswers.value = tempPercentOfRightAnswers >= gameSettings.minPercentOfRightAnswers
    }

    private fun calculatePercentOfRightAnswers(): Int {
        if(countOfQuestion == 0) {
            return 0
        }
        return ((countOfRightAnswers / countOfQuestion.toFloat()) * 100).toInt()
    }

    private fun generateQuestion() {
        _question.value = generateQuestionUseCase(gameSettings.maxSumValue)
    }

    override fun onCleared() {
        super.onCleared()
        timer?.cancel()
    }

    companion object {
        private const val MILLIS_IN_SECONDS = 1000L
        private const val SECONDS_IN_MINUTES = 60
    }
}