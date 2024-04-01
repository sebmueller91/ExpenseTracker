package com.example.expensetracker.ui.screens.group_detail.tabs

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.core.model.Transaction
import com.example.expensetracker.R
import com.example.expensetracker.ui.components.AnimatedFloatingActionButton
import com.example.expensetracker.ui.components.RoundFloatingActionButton
import com.example.expensetracker.ui.components.ScreenWithAnimatedOverlay
import com.example.expensetracker.ui.screens.group_detail.GroupDetailUiState
import com.example.expensetracker.ui.screens.group_detail.data.FormattedSettleUpTransaction
import kotlinx.coroutines.delay

@Composable
fun SettleUpTab(
    modifier: Modifier = Modifier,
    uiState: GroupDetailUiState.Success,
    applySettleUpTransaction: (Transaction.Transfer) -> Unit
) {
    var fabExpanded by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            ExpandableOverviewFabs(
                fabExpanded = fabExpanded,
                expandCollapseFab = { fabExpanded = !fabExpanded })
        }
    ) { paddingValues ->
        ScreenWithAnimatedOverlay(
            applyOverlay = fabExpanded,
            modifier = Modifier.padding(paddingValues)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.TopStart
            ) {
                if (uiState.settleUpTransactions.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(stringResource(R.string.all_group_members_are_settled_up))
                    }
                } else {
                    SettleUpTransactions(
                        uiState = uiState,
                        applySettleUpTransaction = applySettleUpTransaction
                    )
                }
            }
        }
    }
}

@Composable
private fun SettleUpTransactions(
    modifier: Modifier = Modifier,
    uiState: GroupDetailUiState.Success,
    applySettleUpTransaction: (Transaction.Transfer) -> Unit
) {
    LazyColumn(modifier = modifier) {
        items(items = uiState.settleUpTransactions) { transaction ->
            SettleUpTransactionCard(
                transaction = transaction,
                modifier = Modifier.fillMaxWidth(),
                applySettleUpTransaction = applySettleUpTransaction
            )
        }
    }
}

@Composable
private fun SettleUpTransactionCard(
    transaction: FormattedSettleUpTransaction,
    modifier: Modifier = Modifier,
    applySettleUpTransaction: (Transaction.Transfer) -> Unit
) {
    val animationDuration = 1000
    var isVisible by remember { mutableStateOf(true) }

    Card(
        modifier = modifier
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = animationDuration,
                    easing = LinearOutSlowInEasing
                )
            )
            .height(if (isVisible) 70.dp else 0.dp)
            .padding(vertical = 6.dp, horizontal = 8.dp),
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(
            modifier = Modifier
                .padding(start = 8.dp, end = 4.dp)
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = transaction.formattedText,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.Start)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                isVisible = false
            }) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Done,
                        contentDescription = null,
                        modifier = Modifier.height(15.dp)
                    )
                    Text(
                        stringResource(R.string.mark_done),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }

    LaunchedEffect(key1 = isVisible) {
        if (!isVisible) {
            delay(animationDuration.toLong())
            applySettleUpTransaction(transaction.transaction)
        }
    }
}

@Composable
private fun ExpandableOverviewFabs(
    fabExpanded: Boolean,
    expandCollapseFab: () -> Unit,
    modifier: Modifier = Modifier
) {
    val animationDelay = 60

    Column(modifier = modifier) {
        AnimatedFloatingActionButton(
            visible = fabExpanded,
            icon = Icons.Filled.Share,
            labelId = R.string.share_group,
            animationDelayEnter = animationDelay,
            animationDelayExit = 0
        ) {
            // TODO
        }
        Spacer(Modifier.height(20.dp))
        AnimatedFloatingActionButton(
            visible = fabExpanded,
            icon = Icons.Filled.Person,
            labelId = R.string.add_group_member,
            animationDelayEnter = 0,
            animationDelayExit = animationDelay
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