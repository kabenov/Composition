package com.example.composition.presentation

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.composition.R
import com.example.composition.domain.entities.GameResult

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
