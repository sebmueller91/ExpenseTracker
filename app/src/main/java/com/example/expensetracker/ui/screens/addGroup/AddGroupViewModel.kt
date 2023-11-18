package com.example.expensetracker.ui.screens.addGroup

import androidx.lifecycle.ViewModel
import com.example.expensetracker.model.Participant
import com.example.expensetracker.repositories.DatabaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AddGroupViewModel(private val databaseRepository: DatabaseRepository) : ViewModel() {
    private var _uiState = MutableStateFlow(AddGroupUiState())
    val uiStateFlow = _uiState.asStateFlow()

    fun updateShowScreen1(showScreen1: Boolean) {
        _uiState.update {
            it.copy(showScreen1 = showScreen1)
        }
    }

    fun updateGroupName(groupName: String) {
        _uiState.update {
            it.copy(groupName = groupName)
        }
    }

    fun updateParticipantsNames(participantsNames: List<String>) {
        _uiState.update {
            it.copy(participantsNames = participantsNames)
        }
    }

    fun addParticipant() {
        _uiState.update {
            it.copy(participantsNames = it.participantsNames.toMutableList().apply { add("") })
        }
    }

    fun createNewGroup() {
        databaseRepository.addGroup(
            com.example.expensetracker.model.Group(
                name = _uiState.value.groupName,
                participants = _uiState.value.participantsNames.map { Participant(name = it) },
                currency = _uiState.value.currency,
                transactions = listOf()
            )
        )
    }
}