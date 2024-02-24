package com.example.expensetracker.ui.screens.group_overview

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.repository.DatabaseRepository
import com.example.expensetracker.model.Group
import com.example.expensetracker.ui.util.UiUtils
import com.example.expensetracker.services.EventCosts
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GroupOverviewViewModel(
    private val databaseRepository: DatabaseRepository,
    private val eventCost: EventCosts
) : ViewModel() {
    private var _uiState = MutableStateFlow(GroupOverviewUiState())
    val uiStateFlow = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            databaseRepository.groups.collect { groups ->
                _uiState.update { GroupOverviewUiState(groups) }
            }
        }
    }

    fun formattedEventCosts(group: Group, context: Context): String {
        return UiUtils.formatMoneyAmount(eventCost.execute(group.transactions), group.currency, context)
    }
}