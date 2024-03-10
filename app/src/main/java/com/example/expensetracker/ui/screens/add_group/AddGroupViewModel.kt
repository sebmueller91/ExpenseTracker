package com.example.expensetracker.ui.screens.add_group

import androidx.lifecycle.ViewModel
import com.example.data.model.Currency
import com.example.data.model.Group
import com.example.data.model.Participant
import com.example.data.repository.DatabaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

class AddGroupViewModel(private val databaseRepository: com.example.data.repository.DatabaseRepository) : ViewModel() {
    private var _uiState = MutableStateFlow(AddGroupUiState())
    val uiStateFlow = _uiState.asStateFlow()

    fun goToNextSubScreen() {
        _uiState.update {
            it.copy(
                subScreen = when (uiStateFlow.value.subScreen) {
                    AddGroupSubScreens.GROUP_NAME_CURRENCY -> {
                        AddGroupSubScreens.PARTICIPANTS
                    }

                    AddGroupSubScreens.PARTICIPANTS -> {
                        AddGroupSubScreens.SHARE
                    }

                    else -> {
                        throw IllegalStateException("No next state after participants")
                    }
                }
            )
        }
    }

    fun goToPreviousSubScreen() {
        _uiState.update {
            it.copy(
                subScreen = when (uiStateFlow.value.subScreen) {
                    AddGroupSubScreens.PARTICIPANTS -> {
                        AddGroupSubScreens.GROUP_NAME_CURRENCY
                    }

                    AddGroupSubScreens.SHARE -> {
                        AddGroupSubScreens.PARTICIPANTS
                    }

                    else -> {
                        throw IllegalStateException("No next state before GroupNameParticipants")
                    }
                }
            )
        }
    }

    fun updateGroupName(groupName: String) {
        _uiState.update {
            it.copy(groupName = groupName)
        }
    }

    fun addParticipant() {
        _uiState.update {
            it.copy(participantsNames = it.participantsNames.toMutableList().apply { add("") })
        }
    }

    fun updateParticipant(index: Int, value: String) {
        _uiState.update {
            it.copy(
                participantsNames = it.participantsNames.toMutableList()
                    .apply { set(index, value) })
        }
    }

    fun deleteParticipant(index: Int) {
        _uiState.update {
            it.copy(
                participantsNames = it.participantsNames.toMutableList().apply { removeAt(index) })
        }
    }

    fun selectCurrency(currency: com.example.data.model.Currency) {
        _uiState.update {
            it.copy(currency = currency)
        }
    }

    fun createNewGroup(): UUID {
        val uuid = UUID.randomUUID()
        databaseRepository.addGroup(
            com.example.data.model.Group(
                id = uuid,
                name = _uiState.value.groupName,
                participants = _uiState.value.participantsNames.map {
                    com.example.data.model.Participant(
                        name = it
                    )
                },
                currency = _uiState.value.currency,
                transactions = listOf()
            )
        )
        return uuid
    }
}