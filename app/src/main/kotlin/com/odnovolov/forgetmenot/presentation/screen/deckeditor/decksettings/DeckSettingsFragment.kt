package com.odnovolov.forgetmenot.presentation.screen.deckeditor.decksettings

import android.app.Dialog
import android.os.Bundle
import android.os.Looper
import android.os.MessageQueue.IdleHandler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.NestedScrollView
import com.odnovolov.forgetmenot.R
import com.odnovolov.forgetmenot.domain.entity.*
import com.odnovolov.forgetmenot.presentation.common.base.BaseFragment
import com.odnovolov.forgetmenot.presentation.common.customview.ChoiceDialogCreator
import com.odnovolov.forgetmenot.presentation.common.customview.ChoiceDialogCreator.Item
import com.odnovolov.forgetmenot.presentation.common.customview.ChoiceDialogCreator.ItemAdapter
import com.odnovolov.forgetmenot.presentation.common.customview.ChoiceDialogCreator.ItemForm.AsRadioButton
import com.odnovolov.forgetmenot.presentation.common.inflateAsync
import com.odnovolov.forgetmenot.presentation.common.needToCloseDiScope
import com.odnovolov.forgetmenot.presentation.screen.deckeditor.decksettings.DeckSettingsEvent.*
import kotlinx.android.synthetic.main.fragment_deck_settings.*
import kotlinx.coroutines.launch

class DeckSettingsFragment : BaseFragment() {
    init {
        DeckSettingsDiScope.reopenIfClosed()
    }

    private var controller: DeckSettingsController? = null
    private lateinit var viewModel: DeckSettingsViewModel
    private lateinit var testMethodDialog: Dialog
    private var testMethodAdapter: ItemAdapter? = null
    private var isInflated = false
    private lateinit var diScope: DeckSettingsDiScope
    private var idleHandler: IdleHandler? = null
    lateinit var scrollListener: NestedScrollView.OnScrollChangeListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return if (savedInstanceState == null) {
            inflater.inflateAsync(R.layout.fragment_deck_settings, ::onViewInflated)
        } else {
            inflater.inflate(R.layout.fragment_deck_settings, container, false)
        }
    }

    private fun onViewInflated() {
        if (viewCoroutineScope != null) {
            isInflated = true
            setupIfReady()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState != null) {
            isInflated = true
        }
        viewCoroutineScope!!.launch {
            diScope = DeckSettingsDiScope.getAsync() ?: return@launch
            controller = diScope.controller
            viewModel = diScope.viewModel
            setupIfReady()
        }
    }

    private fun setupIfReady() {
        if (viewCoroutineScope == null || controller == null || !isInflated) return
        presetView.inject(diScope.presetController, diScope.presetViewModel)
        setupPrimary()
        idleHandler = IdleHandler {
            setupSecondary()
            false
        }
        Looper.myQueue().addIdleHandler(idleHandler!!)
    }

    private fun setupPrimary() {
        setFonts()
        with(viewModel) {
            randomOrder.observe { randomOrder: Boolean ->
                selectedRandomOrderTextView.text = getString(
                    if (randomOrder)
                        R.string.on else
                        R.string.off
                )
            }
            selectedTestMethod.observe { selectedTestMethod ->
                selectedTestingMethodTextView.text = when (selectedTestMethod) {
                    TestMethod.Off -> getString(R.string.test_method_label_off)
                    TestMethod.Manual -> getString(R.string.test_method_label_manual)
                    TestMethod.Quiz -> getString(R.string.test_method_label_quiz)
                    TestMethod.Entry -> getString(R.string.test_method_label_entry)
                }
            }
            intervalScheme.observe {
                selectedIntervalsTextView.text = when {
                    it == null -> getString(R.string.off)
                    it.isDefault() -> getString(R.string.default_name)
                    it.isIndividual() -> getString(R.string.individual_name)
                    else -> "'${it.name}'"
                }
            }
            pronunciation.observe { pronunciation: Pronunciation ->
                selectedPronunciationTextView.text = buildString {
                    append(
                        pronunciation.questionLanguage?.displayLanguage
                            ?: getString(R.string.default_language)
                    )
                    if (pronunciation.questionAutoSpeak) {
                        append(" (A)")
                    }
                    append("  |  ")
                    append(
                        pronunciation.answerLanguage?.displayLanguage
                            ?: getString(R.string.default_language)
                    )
                    if (pronunciation.answerAutoSpeak) {
                        append(" (A)")
                    }
                }
            }
            isQuestionDisplayed.observe { isQuestionDisplayed: Boolean ->
                selectedQuestionDisplayTextView.text = getString(
                    if (isQuestionDisplayed)
                        R.string.on else
                        R.string.off
                )
            }
            selectedCardInversion.observe { selectedCardInversion: CardInversion ->
                selectedCardInversionTextView.setText(
                    when (selectedCardInversion) {
                        CardInversion.Off -> R.string.item_card_inversion_off
                        CardInversion.On -> R.string.item_card_inversion_on
                        CardInversion.EveryOtherLap -> R.string.item_card_inversion_every_other_lap
                    }
                )
            }
            pronunciationPlan.observe { pronunciationPlan: PronunciationPlan ->
                selectedPronunciationPlanTextView.text = when {
                    pronunciationPlan.isDefault() -> getString(R.string.default_name)
                    pronunciationPlan.isIndividual() -> getString(R.string.individual_name)
                    else -> "'${pronunciationPlan.name}'"
                }
            }
            timeForAnswer.observe { timeForAnswer: Int ->
                selectedMotivationalTimerTextView.text =
                    if (timeForAnswer == 0)
                        getString(R.string.off) else
                        getString(R.string.time_for_answer, timeForAnswer)
            }
        }
    }

    private fun setFonts() {
        val nunitoExtraboldFont = ResourcesCompat.getFont(requireContext(), R.font.nunito_extrabold)
        generalSectionTitle.typeface = nunitoExtraboldFont
        exerciseSectionTitle.typeface = nunitoExtraboldFont
        autoplaySectionTitle.typeface = nunitoExtraboldFont
        val nunitoBoldFont = ResourcesCompat.getFont(requireContext(), R.font.nunito_bold)
        randomOrderTitle.typeface = nunitoBoldFont
        selectedRandomOrderTextView.typeface = nunitoBoldFont
        pronunciationTitle.typeface = nunitoBoldFont
        selectedPronunciationTextView.typeface = nunitoBoldFont
        cardInversionTitle.typeface = nunitoBoldFont
        selectedCardInversionTextView.typeface = nunitoBoldFont
        questionDisplayTitle.typeface = nunitoBoldFont
        selectedQuestionDisplayTextView.typeface = nunitoBoldFont
        testingMethodTitle.typeface = nunitoBoldFont
        selectedTestingMethodTextView.typeface = nunitoBoldFont
        intervalsTitle.typeface = nunitoBoldFont
        selectedIntervalsTextView.typeface = nunitoBoldFont
        motivationalTimerTitle.typeface = nunitoBoldFont
        selectedMotivationalTimerTextView.typeface = nunitoBoldFont
        pronunciationPlanTitle.typeface = nunitoBoldFont
        selectedPronunciationPlanTextView.typeface = nunitoBoldFont
    }

    private fun setupSecondary() {
        if (viewCoroutineScope == null) return
        initChooseTestMethodDialog()
        setupListeners()
        observeViewModelSecondary()
    }

    private fun initChooseTestMethodDialog() {
        testMethodDialog = ChoiceDialogCreator.create(
            context = requireContext(),
            title = getString(R.string.title_test_method_dialog),
            itemForm = AsRadioButton,
            onItemClick = { item: Item ->
                item as TestMethodItem
                val chosenTestMethod = item.testMethod
                controller?.dispatch(TestMethodIsSelected(chosenTestMethod))
                testMethodDialog.dismiss()
            },
            takeAdapter = { testMethodAdapter = it }
        )
        dialogTimeCapsule.register("testMethodDialog", testMethodDialog)
    }

    private fun setupListeners() {
        randomButton.setOnClickListener {
            controller?.dispatch(RandomOrderSwitchToggled)
        }
        testingMethodButton.setOnClickListener {
            testMethodDialog.show()
        }
        intervalsButton.setOnClickListener {
            controller?.dispatch(IntervalsButtonClicked)
        }
        pronunciationButton.setOnClickListener {
            controller?.dispatch(PronunciationButtonClicked)
        }
        questionDisplayButton.setOnClickListener {
            controller?.dispatch(QuestionDisplayButtonClicked)
        }
        cardInversionButton.setOnClickListener {
            controller?.dispatch(CardInversionButtonClicked)
        }
        pronunciationPlanButton.setOnClickListener {
            controller?.dispatch(PronunciationPlanButtonClicked)
        }
        motivationalTimerButton.setOnClickListener {
            controller?.dispatch(MotivationalTimerButtonClicked)
        }
        scrollView.setOnScrollChangeListener(scrollListener)
        scrollListener.onScrollChange(scrollView, 0, 0, 0, 0)
    }

    private fun observeViewModelSecondary() {
        with(viewModel) {
            selectedTestMethod.observe { selectedTestMethod ->
                val testMethods = TestMethod.values().map {
                    TestMethodItem(
                        testMethod = it,
                        text = when (it) {
                            TestMethod.Off -> getString(R.string.test_method_label_off)
                            TestMethod.Manual -> getString(R.string.test_method_label_manual)
                            TestMethod.Quiz -> getString(R.string.test_method_label_quiz)
                            TestMethod.Entry -> getString(R.string.test_method_label_entry)
                        },
                        isSelected = it === selectedTestMethod
                    )
                }
                testMethodAdapter?.submitList(testMethods)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        idleHandler?.let { idleHandler -> Looper.myQueue().removeIdleHandler(idleHandler) }
        isInflated = false
        testMethodAdapter = null
    }

    override fun onDestroy() {
        super.onDestroy()
        if (needToCloseDiScope()) {
            DeckSettingsDiScope.close()
        }
    }

    data class TestMethodItem(
        val testMethod: TestMethod,
        override val text: String,
        override val isSelected: Boolean
    ) : Item
}