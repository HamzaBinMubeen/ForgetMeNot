package com.odnovolov.forgetmenot.presentation.screen.cardappearance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import com.odnovolov.forgetmenot.R
import com.odnovolov.forgetmenot.presentation.common.base.BaseFragment
import com.odnovolov.forgetmenot.presentation.common.isFinishing
import com.odnovolov.forgetmenot.presentation.common.observeText
import com.odnovolov.forgetmenot.presentation.common.showSoftInput
import com.odnovolov.forgetmenot.presentation.screen.cardappearance.CardAppearanceEvent.*
import kotlinx.android.synthetic.main.fragment_card_appearance.*
import kotlinx.coroutines.launch

class CardAppearanceFragment : BaseFragment() {
    init {
        CardAppearanceDiScope.reopenIfClosed()
    }

    private var controller: CardAppearanceController? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_card_appearance, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        viewCoroutineScope!!.launch {
            val diScope = CardAppearanceDiScope.getAsync() ?: return@launch
            controller = diScope.controller
            val viewModel = diScope.viewModel
            observeViewModel(viewModel)
        }
    }

    private fun setupView() {
        backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }
        alignQuestionToEdgeButton.setOnClickListener {
            controller?.dispatch(AlignQuestionToEdgeButtonClicked)
        }
        alignQuestionToCenterButton.setOnClickListener {
            controller?.dispatch(AlignQuestionToCenterButtonClicked)
        }
        questionTextSizeEditText.observeText { text: String ->
            controller?.dispatch(QuestionTextSizeTextChanged(text))
        }
        qspTextView.setOnClickListener {
            questionTextSizeEditText.selectAll()
            questionTextSizeEditText.showSoftInput()
        }
        alignAnswerToEdgeButton.setOnClickListener {
            controller?.dispatch(AlignAnswerToEdgeButtonClicked)
        }
        alignAnswerToCenterButton.setOnClickListener {
            controller?.dispatch(AlignAnswerToCenterButtonClicked)
        }
        answerTextSizeEditText.observeText { text: String ->
            controller?.dispatch(AnswerTextSizeTextChanged(text))
        }
        aspTextView.setOnClickListener {
            answerTextSizeEditText.selectAll()
            answerTextSizeEditText.showSoftInput()
        }
    }

    private fun observeViewModel(viewModel: CardAppearanceViewModel) {
        with(viewModel) {
            questionTextAlignment.observe { questionTextAlignment: CardTextAlignment ->
                alignQuestionToEdgeButton.isSelected =
                    questionTextAlignment == CardTextAlignment.Edge
                alignQuestionToCenterButton.isSelected =
                    questionTextAlignment == CardTextAlignment.Center
            }
            answerTextAlignment.observe { answerTextAlignment: CardTextAlignment ->
                alignAnswerToEdgeButton.isSelected =
                    answerTextAlignment == CardTextAlignment.Edge
                alignAnswerToCenterButton.isSelected =
                    answerTextAlignment == CardTextAlignment.Center
            }
            if (isViewFirstCreated) {
                questionTextSizeEditText.setText(questionTextSize)
                answerTextSizeEditText.setText(answerTextSize)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        appBar.post { appBar.isActivated = contentScrollView.canScrollVertically(-1) }
        contentScrollView.viewTreeObserver.addOnScrollChangedListener(scrollListener)
    }

    override fun onPause() {
        super.onPause()
        contentScrollView.viewTreeObserver.removeOnScrollChangedListener(scrollListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing()) {
            CardAppearanceDiScope.close()
        }
    }

    private val scrollListener = ViewTreeObserver.OnScrollChangedListener {
        val canScrollUp = contentScrollView.canScrollVertically(-1)
        if (appBar.isActivated != canScrollUp) {
            appBar.isActivated = canScrollUp
        }
    }
}