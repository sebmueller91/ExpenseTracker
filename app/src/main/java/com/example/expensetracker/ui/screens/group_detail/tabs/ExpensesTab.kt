package com.example.expensetracker.ui.screens.group_detail.tabs

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.expensetracker.R
import com.example.expensetracker.model.Currency
import com.example.expensetracker.model.Transaction
import com.example.expensetracker.ui.components.AnimatedFloatingActionButton
import com.example.expensetracker.ui.components.ExpandCollapseButton
import com.example.expensetracker.ui.components.RoundFloatingActionButton
import com.example.expensetracker.ui.components.ScreenWithAnimatedOverlay
import com.example.expensetracker.ui.screens.group_detail.FormattedTransaction
import com.example.expensetracker.ui.screens.group_detail.format
import com.example.expensetracker.ui.theme.ExpenseTrackerTheme


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
            if (transactions.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(stringResource(R.string.no_transactions))
                }
            } else {
                LazyColumn {
                    items(items = transactions) { transaction ->
                        ExpenseEntry(
                            formattedTransaction = transaction.format(
                                currency,
                                LocalContext.current
                            )
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

    Column(modifier = modifier) {
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
private fun ExpenseEntry(
    formattedTransaction: FormattedTransaction,
    modifier: Modifier = Modifier
) {
    Entry(modifier = modifier,
        mainContent = { mainContentModifier ->
            Text(
                modifier = mainContentModifier
                    .padding(6.dp),
                style = MaterialTheme.typography.bodyLarge,
                text = formattedTransaction.mainText
            )
        }, expandedContent = {
            Column(modifier = Modifier.padding(horizontal = 6.dp)) {
                Text(
                    text = formattedTransaction.date,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = formattedTransaction.splitBetween,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    )
}

@Composable
fun Entry(
    modifier: Modifier = Modifier,
    mainContent: @Composable (modifier: Modifier) -> Unit,
    expandedContent: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ),
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                mainContent(modifier = Modifier.weight(1f))
                ExpandCollapseButton(
                    expanded = expanded,
                    onClick = { expanded = !expanded }
                )
            }
            if (expanded) {
                expandedContent()
            }
        }
    }
}

@Composable
@Preview
private fun ExpenseEntryPreview() {
    ExpenseTrackerTheme {
        ExpenseEntry(
            FormattedTransaction(
                mainText = "Peter paid 10€ for Water",
                date = "pain on: 10.11.2023",
                splitBetween = "Peter, Alisa und Michael"
            )
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