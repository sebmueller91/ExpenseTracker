package com.example.expensetracker.repositories

import com.example.expensetracker.model.Event
import kotlinx.coroutines.flow.StateFlow

interface DatabaseRepository {
    val events: StateFlow<List<Event>>
    fun addEvent(event: Event)
}