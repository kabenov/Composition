package com.example.composition.presentation

import android.content.Context
import android.content.res.ColorStateList
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.example.composition.R
import com.example.composition.domain.entities.GameResult

interface OnOptionClickListener {
    fun onOptionClick(option: Int)
}


@BindingAdapter("required_answer")
fun bindingRequiredAnswer(textView: TextView, count: Int) {
    textView.text = String.format(
        textView.context.getString(R.string.game_finished_fragment_required_answer),
        count
    )
}

@BindingAdapter("score_answer")
fun bindingScoreAnswer(textView: TextView, count: Int) {
    textView.text = String.format(
        textView.context.getString(R.string.game_finished_fragment_score_answer),
        count
    )
}

@BindingAdapter("required_percent")
fun bindingRequiredPercent(textView: TextView, count: Int) {
    textView.text = String.format(
        textView.context.getString(R.string.game_finished_fragment_required_percent),
        count
    )
}

@BindingAdapter("score_percent")
fun bindingScorePercent(textView: TextView, gameResult: GameResult) {
    textView.text = String.format(
        textView.context.getString(R.string.game_finished_fragment_score_percent),
        getPercentOfRightAnswers(gameResult)
    )
}

private fun getPercentOfRightAnswers(gameResult: GameResult) = with(gameResult) {
    if(countOfQuestions == 0) {
        0
    }
    else {
        ((countOfRightAnswers / countOfQuestions.toFloat()) * 100).toInt()
    }
}

@BindingAdapter("resultEmoji")
fun bindingResultEmoji(imageView: ImageView, isWinner: Boolean) {
    imageView.setImageResource(getSmileResId(isWinner))
}

private fun getSmileResId(isWinner: Boolean): Int {
    return if(isWinner) {
        R.drawable.ic_smile
    }
    else {
        R.drawable.ic_sad
    }
}



@BindingAdapter("castNumberToText")
fun castIntToString(textView: TextView, number: Int) {
    textView.text = number.toString()
}

@BindingAdapter("setProgressPercent")
fun setProgressPercent(progressBar: ProgressBar, percent: Int) {
    progressBar.setProgress(percent, true)
}

@BindingAdapter("setGameAnswerColor")
fun setGameAnswerColor(textView: TextView, enoughCount: Boolean) {
    val color = getColorByState(textView.context, enoughCount)
    textView.setTextColor(color)
}

@BindingAdapter("setProgressBarColor")
fun setProgressBarColor(progressBar: ProgressBar, enoughPercent: Boolean) {
    val color = getColorByState(progressBar.context, enoughPercent)
    progressBar.progressTintList = ColorStateList.valueOf(color)
}

private fun getColorByState(context: Context, state: Boolean): Int {
    val colorResultId = if(state) {
        android.R.color.holo_green_light
    }
    else {
        android.R.color.holo_red_light
    }

    return ContextCompat.getColor(context, colorResultId)
}


@BindingAdapter("onOptionClickListener")
fun onOptionClickListener(textView: TextView, clickListener: OnOptionClickListener) {
    textView.setOnClickListener {
        clickListener.onOptionClick(textView.text.toString().toInt())
    }
}