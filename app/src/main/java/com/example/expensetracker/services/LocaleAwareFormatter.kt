package com.example.expensetracker.services

import android.content.Context
import com.example.expensetracker.model.Currency
import com.example.expensetracker.model.Participant
import com.example.expensetracker.ui.util.UiUtils
import java.util.Date

interface LocaleAwareFormatter {
    fun formatMoneyAmount(amount: Double, currency: Currency): String
    fun formatPercentage(value: Double): String
    fun formatParticipantsList(participants: List<Participant>): String
    fun formatDate(date: Date): String
}

class LocaleAwareFormatterImpl(
    private val context: Context
): LocaleAwareFormatter {
    override fun formatMoneyAmount(amount: Double, currency: Currency): String = UiUtils.formatMoneyAmount(amount, currency, context)
    override fun formatPercentage(value: Double): String = UiUtils.formatPercentage(value)
    override fun formatParticipantsList(participants: List<Participant>): String = UiUtils.formatParticipantsList(participants, context)
    override fun formatDate(date: Date): String = UiUtils.formatDate(date, context)
}