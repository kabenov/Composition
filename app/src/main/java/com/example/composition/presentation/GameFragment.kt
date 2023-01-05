package com.example.composition.presentation

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import com.example.composition.R
import com.example.composition.databinding.FragmentGameBinding
import com.example.composition.domain.entities.GameResult
import com.example.composition.domain.entities.GameSettings
import com.example.composition.domain.entities.Level
import com.example.composition.domain.entities.Question
import com.example.composition.presentation.viewmodel.GameViewModel
import com.example.composition.presentation.viewmodel.GameViewModelFactory

class GameFragment: Fragment() {

    private lateinit var level: Level

    private val gameViewModelFactory by lazy {
        GameViewModelFactory(requireActivity().application, level)
    }
    private val gameViewModel by lazy {
        ViewModelProvider(this, gameViewModelFactory)[GameViewModel::class.java]
    }


    private var _binding: FragmentGameBinding? = null
    private val binding: FragmentGameBinding
        get() = _binding ?: throw RuntimeException("FragmentGameBinding == null")


    private val textViewOptions by lazy {
        mutableListOf<TextView>().apply {
            add(binding.textViewGameOption1)
            add(binding.textViewGameOption2)
            add(binding.textViewGameOption3)
            add(binding.textViewGameOption4)
            add(binding.textViewGameOption5)
            add(binding.textViewGameOption6)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseArgs()
    }

    private fun parseArgs() {
        requireArguments().getParcelable<Level>(KEY_LEVEL)?.let {
            level = it
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        liveDataObserves()
        setClickListenersToOptions()
    }

    private fun liveDataObserves() {
        gameViewModel.formattedTime.observe(viewLifecycleOwner) {
            setGameTimer(it)
        }
        gameViewModel.question.observe(viewLifecycleOwner) {
            setQuestion(it)
        }
        gameViewModel.percentOfRightAnswers.observe(viewLifecycleOwner){
            setProgress(it)
        }
        gameViewModel.progressOfAnswers.observe(viewLifecycleOwner) {
            setGameAnswersProgress(it)
        }
        gameViewModel.enoughCountOfRightAnswers.observe(viewLifecycleOwner) {
            setGameAnswersColor(it)
        }
        gameViewModel.enoughPercentOfRightAnswers.observe(viewLifecycleOwner) {
            setProgressBarColor(it)
        }
        gameViewModel.minPercent.observe(viewLifecycleOwner) {
            setSecondaryProgressBar(it)
        }
        gameViewModel.gameResult.observe(viewLifecycleOwner) {
            launchGameFinishedFragment(it)
        }
    }

    private fun setGameTimer(timer: String) {
        binding.textViewGameTimer.text = timer
    }

    private fun setQuestion(question: Question) {
        binding.textViewGameSum.text = question.sum.toString()
        binding.textViewGameVisibleNumber.text = question.visibleNumber.toString()
        for (i in 0 until textViewOptions.size) {
            textViewOptions[i].text = question.options[i].toString()
        }
    }

    private fun setProgress(percentOfRightAnswers: Int) {
        binding.progressBarGame.setProgress(percentOfRightAnswers, true)
    }

    private fun setGameAnswersProgress(progressOfAnswers: String) {
        binding.textViewGameAnswersProgress.text = progressOfAnswers
    }

    private fun setGameAnswersColor(enoughCount: Boolean) {
        val color = getColorByState(enoughCount)
        binding.textViewGameAnswersProgress.setTextColor(color)
    }

    private fun setProgressBarColor(enoughPercent: Boolean) {
        val color = getColorByState(enoughPercent)
        binding.progressBarGame.progressTintList = ColorStateList.valueOf(color)
    }

    private fun getColorByState(state: Boolean): Int {
        val colorResultId = if(state) {
            android.R.color.holo_green_light
        }
        else {
            android.R.color.holo_red_light
        }

        return ContextCompat.getColor(requireContext(), colorResultId)
    }

    private fun setSecondaryProgressBar(minPercent: Int) {
        binding.progressBarGame.secondaryProgress = minPercent
    }

    private fun launchGameFinishedFragment(gameResult: GameResult) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, GameFinishedFragment.newInstance(gameResult))
            .addToBackStack(null)
            .commit()
    }

    private fun setClickListenersToOptions() {
        for(textViewOption in textViewOptions) {
            textViewOption.setOnClickListener {
                gameViewModel.chooseAnswer(textViewOption.text.toString().toInt())
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    companion object {

        const val NAME = "GameFragment"
        private const val KEY_LEVEL = "LEVEL"

        fun newInstance(level: Level): GameFragment {
            return GameFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_LEVEL, level)
                }
            }
        }
    }
}