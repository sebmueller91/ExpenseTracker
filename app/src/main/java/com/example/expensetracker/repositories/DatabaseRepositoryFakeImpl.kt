package com.example.expensetracker.repositories

import com.example.expensetracker.model.Currency
import com.example.expensetracker.model.Group
import com.example.expensetracker.model.Participant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

class DatabaseRepositoryFakeImpl : DatabaseRepository {
    private var _groups = MutableStateFlow(
        listOf(
            Group(
                currency = Currency.USD, name = "Summer Breeze",
                participants = listOf(Participant("Dennis"), Participant("Johnny")),
                transactions = listOf()
            ),
            Group(
                currency = Currency.EURO, name = "Rock im Park",
                participants = listOf(
                    Participant("Dennis"),
                    Participant("Johnny"),
                    Participant("Alisa")
                ),
                transactions = listOf()
            )
        )
    )
    override val groups = _groups.asStateFlow()

    override fun addGroup(group: Group) {
        _groups.update {
            val newList = it.toMutableList()
            newList.add(group)
            newList.toList()
        }
    }

    override fun Group(groupId: UUID) {
        _groups.update {
            val newList = it.toMutableList()
            newList.removeIf { it.id == groupId }
            newList.toList()
        }
    }
}