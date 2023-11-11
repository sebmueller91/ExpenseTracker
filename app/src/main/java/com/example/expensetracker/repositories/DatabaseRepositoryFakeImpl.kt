package com.example.expensetracker.repositories

import com.example.expensetracker.model.CURRENCIES
import com.example.expensetracker.model.Group
import com.example.expensetracker.model.Participant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

class DatabaseRepositoryFakeImpl : DatabaseRepository {
    private var _events = MutableStateFlow(
        listOf(
            Group(
                currency = CURRENCIES.get(0), name = "Summer Breeze",
                participants = listOf(Participant("Dennis"), Participant("Johnny")),
                transactions = listOf()
            ),
            Group(
                currency = CURRENCIES.get(0), name = "Rock im Park",
                participants = listOf(
                    Participant("Dennis"),
                    Participant("Johnny"),
                    Participant("Alisa")
                ),
                transactions = listOf()
            )
        )
    )
    override val groups = _events.asStateFlow()

    override fun addEvent(group: Group) {
        _events.update {
            val newList = it.toMutableList()
            newList.add(group)
            newList.toList()
        }
    }

    override fun deleteEvent(groupId: UUID) {
        _events.update {
            val newList = it.toMutableList()
            newList.removeIf { it.id == groupId }
            newList.toList()
        }
    }
}