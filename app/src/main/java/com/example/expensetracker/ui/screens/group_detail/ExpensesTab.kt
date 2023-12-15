package com.example.expensetracker.ui.screens.group_detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.expensetracker.R
import com.example.expensetracker.model.Currency
import com.example.expensetracker.model.Transaction
import com.example.expensetracker.ui.components.AnimatedFloatingActionButton
import com.example.expensetracker.ui.components.RoundFloatingActionButton
import com.example.expensetracker.ui.components.ScreenWithAnimatedOverlay
import com.example.expensetracker.ui.theme.ExpenseTrackerTheme
import com.example.expensetracker.util.FakeData.Companion.createFakeExpense
import com.example.expensetracker.util.FakeData.Companion.fakeParticipantsSmall
import java.text.SimpleDateFormat


@Composable
fun ExpensesTab(
    transactions: List<Transaction>,
    currency: Currency,
    modifier: Modifier = Modifier
) {
    var fabExpanded by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            ExpandableExpensesFabs(
                fabExpanded = fabExpanded,
                expandCollapseFab = { fabExpanded = !fabExpanded })
        }

    ) { paddingValues ->
        ScreenWithAnimatedOverlay(
            applyOverlay = fabExpanded,
            modifier = Modifier.padding(paddingValues)
        ) {
            LazyColumn {
                items(items = transactions) { transaction ->
                    when (transaction) {
                        is Transaction.Expense -> ExpenseEntry(
                            expense = transaction,
                            currency = currency
                        )

                        is Transaction.Income -> IncomeEntry(
                            income = transaction,
                            currency = currency
                        )

                        is Transaction.Payment -> PaymentEntry(
                            payment = transaction,
                            currency = currency
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ExpandableExpensesFabs(
    fabExpanded: Boolean,
    expandCollapseFab: () -> Unit,
    modifier: Modifier = Modifier
) {
    val animationDelay = 60

    Column {
        AnimatedFloatingActionButton(
            visible = fabExpanded,
            icon = Icons.Filled.CurrencyExchange,
            labelId = R.string.add_payment,
            animationDelayEnter = animationDelay * 2,
            animationDelayExit = 0
        ) {
            // TODO
        }
        Spacer(Modifier.height(20.dp))
        AnimatedFloatingActionButton(
            visible = fabExpanded,
            icon = Icons.Filled.Savings,
            labelId = R.string.add_income,
            animationDelayEnter = animationDelay,
            animationDelayExit = animationDelay
        ) {
            // TODO
        }
        Spacer(Modifier.height(20.dp))
        AnimatedFloatingActionButton(
            visible = fabExpanded,
            icon = Icons.Filled.Payment,
            labelId = R.string.add_expense,
            animationDelayEnter = 0,
            animationDelayExit = animationDelay * 2
        ) {
            // TODO
        }
        Spacer(Modifier.height(20.dp))
        Row {
            Spacer(Modifier.weight(1f))
            RoundFloatingActionButton(onClick = expandCollapseFab) {
                Icon(
                    Icons.Filled.MoreHoriz,
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
private fun PaymentEntry(
    payment: Transaction.Payment,
    currency: Currency,
    modifier: Modifier = Modifier
) {
    Text(payment.purpose)
}

@Composable
private fun IncomeEntry(
    income: Transaction.Income,
    currency: Currency,
    modifier: Modifier = Modifier
) {
    Text(income.purpose)
}

@Composable
private fun ExpenseEntry(
    expense: Transaction.Expense,
    currency: Currency,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Text(
                modifier = Modifier.padding(6.dp),
                style = MaterialTheme.typography.bodyLarge,
                text = "${expense.paidBy.name} paid ${
                    String.format("%.2f", expense.amount).toDouble()
                }${currency.symbol} for ${expense.purpose}"
            )
            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                Text(modifier = Modifier.padding(horizontal = 6.dp), text = SimpleDateFormat("dd.MM.yyyy").format(expense.date)) // TODO: Move into viewModel
                Text("Split between ${expense.splitBetween.joinToString(", ") { it.name }}")
            }
        }
    }
}

@Composable
@Preview
private fun ExpenseEntryPreview() {
    ExpenseTrackerTheme {
        ExpenseEntry(
            expense = createFakeExpense(fakeParticipantsSmall),
            currency = Currency.EURO
        )
    }
}

@Composable
@Preview
private fun ExpandableExpensesFabsPreviewCollapsed() {
    ExpenseTrackerTheme {
        ExpandableExpensesFabs(
            expandCollapseFab = {},
            fabExpanded = false
        )
    }
}

@Composable
@Preview
private fun ExpandableExpensesFabsPreviewExpanded() {
    ExpenseTrackerTheme {
        ExpandableExpensesFabs(
            expandCollapseFab = {},
            fabExpanded = true
        )
    }
}