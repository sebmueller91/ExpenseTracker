package com.example.expensetracker.ui.screens.group_overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.DatabaseRepository
import com.example.core.model.Group
import com.example.expensetracker.services.EventCosts
import com.example.expensetracker.services.LocaleAwareFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GroupOverviewViewModel(
    private val databaseRepository: com.example.data.repository.DatabaseRepository,
    private val eventCost: EventCosts,
    private val localeAwareFormatter: LocaleAwareFormatter
) : ViewModel() {
    private var _uiState = MutableStateFlow(GroupOverviewUiState())
    val uiStateFlow = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            databaseRepository.groups.collect { groups ->
                _uiState.update { GroupOverviewUiState(groups = groups, eventCosts = groups.map { it.formattedEventCosts() }) }
            }
        }
    }

    private fun Group.formattedEventCosts(): String {
        return localeAwareFormatter.formatMoneyAmount(eventCost.execute(transactions), currency)
    }
}