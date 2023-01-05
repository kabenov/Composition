package com.example.composition.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.composition.R
import com.example.composition.databinding.FragmentGameFinishedBinding
import com.example.composition.domain.entities.GameResult

class GameFinishedFragment : Fragment() {

    private val args by navArgs<GameFinishedFragmentArgs>()

    private val gameResult by lazy { args.gameResult }


    private var _binding: FragmentGameFinishedBinding? = null
    private val binding: FragmentGameFinishedBinding
        get() = _binding ?: throw RuntimeException("FragmentGameFinishedBinding == null")


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameFinishedBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        clickListeners()
        bindViews()
    }

    private fun clickListeners() {
        binding.buttonGameFinishedRetry.setOnClickListener {
            retryGame()
        }
    }

    private fun retryGame() {
        findNavController().popBackStack()
    }

    private fun bindViews() {
        with(binding) {
            imageViewGameFinishedSmile.setImageResource(getSmileResId())

            textViewGameFinishedRequiredAnswers.text = String.format(
                getString(R.string.game_finished_fragment_required_answer),
                gameResult.gameSettings.minCountOfRightAnswers
            )

            textViewGameFinishedScoreAnswers.text = String.format(
                getString(R.string.game_finished_fragment_score_answer),
                gameResult.countOfRightAnswers
            )

            textViewGameFinishedRequiredPercent.text = String.format(
                getString(R.string.game_finished_fragment_required_percent),
                gameResult.gameSettings.minPercentOfRightAnswers
            )

            textViewGameFinishedPercent.text = String.format(
                getString(R.string.game_finished_fragment_percent),
                getPercentOfRightAnswers()
            )
        }
    }

    private fun getSmileResId(): Int {
        return if(gameResult.isWinner) {
            R.drawable.ic_smile
        }
        else {
            R.drawable.ic_sad
        }
    }

    private fun getPercentOfRightAnswers() = with(gameResult) {
        if(countOfQuestions == 0) {
            0
        }
        else {
            ((countOfRightAnswers / countOfQuestions.toFloat()) * 100).toInt()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}