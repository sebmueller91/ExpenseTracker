package com.example.expensetracker.ui.screens.group_detail.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.expensetracker.R
import com.example.expensetracker.model.Transaction
import com.example.expensetracker.ui.components.AnimatedFloatingActionButton
import com.example.expensetracker.ui.components.RoundFloatingActionButton
import com.example.expensetracker.ui.components.ScreenWithAnimatedOverlay
import com.example.expensetracker.ui.screens.group_detail.GroupDetailUiState

@Composable
fun SettleUpTab(
    uiState: GroupDetailUiState.Success,
    modifier: Modifier = Modifier
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
                    Text(stringResource(R.string.all_group_members_are_settled_up))
                } else {
                    SettleUpTransactions(uiState = uiState)
                }
            }
        }
    }
}

@Composable
private fun SettleUpTransactions(
    uiState: GroupDetailUiState.Success,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        uiState.settleUpTransactions.entries.forEach { entry ->
            item {
                SettleUpTransactionCard(entry = entry, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun SettleUpTransactionCard(
    entry: Map.Entry<Transaction.Transfer, String>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(vertical = 6.dp, horizontal = 8.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(start = 8.dp, end = 4.dp).padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = entry.value, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = {
                // TODO
            }) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(imageVector = Icons.Filled.Done, contentDescription = null, Modifier.height(15.dp))
                    Text("Mark done", style = MaterialTheme.typography.labelSmall)
                }
            }
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