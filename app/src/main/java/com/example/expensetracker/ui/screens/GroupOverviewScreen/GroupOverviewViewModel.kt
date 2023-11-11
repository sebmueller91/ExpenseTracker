package com.example.expensetracker.ui.screens.GroupOverviewScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.repositories.DatabaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GroupOverviewViewModel(private val databaseRepository: DatabaseRepository) : ViewModel() {
    private var _uiState = MutableStateFlow(GroupOverviewUiState())
    val uiStateFlow = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            databaseRepository.groups.collect { groups ->
                _uiState.update { GroupOverviewUiState(groups) }
            }
        }
    }
}