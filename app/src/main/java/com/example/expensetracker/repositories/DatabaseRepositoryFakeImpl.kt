package com.example.expensetracker.repositories

import com.example.expensetracker.model.CURRENCIES
import com.example.expensetracker.model.Event
import com.example.expensetracker.model.Participant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DatabaseRepositoryFakeImpl : DatabaseRepository {
    private var _events = MutableStateFlow(
        listOf(
            Event(
                currency = CURRENCIES.get(0), name = "Summer Breeze",
                participants = listOf(Participant("Dennis"), Participant("Johnny")),
                transactions = listOf()
            ),
            Event(
                currency = CURRENCIES.get(0), name = "Rock im Park",
                participants = listOf(Participant("Dennis"), Participant("Johnny")),
                transactions = listOf()
            )
        )
    )
    override val events = _events.asStateFlow()

    override fun addEvent(event: Event) {
        _events.update {
            
        }
    }
}