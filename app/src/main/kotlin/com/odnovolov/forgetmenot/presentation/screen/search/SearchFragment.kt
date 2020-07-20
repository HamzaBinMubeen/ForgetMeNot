package com.odnovolov.forgetmenot.presentation.screen.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.TooltipCompat
import com.odnovolov.forgetmenot.R
import com.odnovolov.forgetmenot.presentation.common.*
import com.odnovolov.forgetmenot.presentation.common.base.BaseFragment
import com.odnovolov.forgetmenot.presentation.screen.cardseditor.qaeditor.paste
import com.odnovolov.forgetmenot.presentation.screen.search.SearchEvent.BackButtonClicked
import com.odnovolov.forgetmenot.presentation.screen.search.SearchEvent.SearchTextChanged
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.coroutines.launch

class SearchFragment : BaseFragment() {
    init {
        SearchDiScope.reopenIfClosed()
    }

    private var controller: SearchController? = null
    private lateinit var viewModel: SearchViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        viewCoroutineScope!!.launch {
            val diScope = SearchDiScope.get()
            controller = diScope.controller
            viewModel = diScope.viewModel
            observeViewModel()
        }
    }

    private fun setupView() {
        backButton.run {
            setOnClickListener { controller?.dispatch(BackButtonClicked) }
            TooltipCompat.setTooltipText(this, contentDescription)
        }
        searchEditText.observeText { newText: String ->
            controller?.dispatch(SearchTextChanged(newText))
        }
        pasteButton.run {
            setOnClickListener { searchEditText.paste() }
            TooltipCompat.setTooltipText(this, contentDescription)
        }
        clearButton.run {
            setOnClickListener { searchEditText.text.clear() }
            TooltipCompat.setTooltipText(this, contentDescription)
        }
    }

    private fun observeViewModel() {
    }

    override fun onResume() {
        super.onResume()
        searchEditText.showSoftInput()
        hideActionBar()
    }

    override fun onPause() {
        super.onPause()
        searchEditText.hideSoftInput()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (needToCloseDiScope()) {
            SearchDiScope.close()
        }
    }
}