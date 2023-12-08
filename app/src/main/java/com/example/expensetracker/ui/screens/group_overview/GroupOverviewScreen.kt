package com.example.expensetracker.ui.screens.group_overview

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.BuildConfig
import com.example.expensetracker.R
import com.example.expensetracker.model.Currency
import com.example.expensetracker.model.Group
import com.example.expensetracker.model.Participant
import com.example.expensetracker.ui.screens.destinations.AddGroupScreenDestination
import com.example.expensetracker.ui.screens.destinations.GroupDetailScreenDestination
import com.example.expensetracker.ui.theme.ExpenseTrackerTheme
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.getViewModel
import java.util.UUID

@RootNavGraph(start = true)
@Destination
@Composable
fun GroupOverviewScreen(
    navigator: DestinationsNavigator
) {
    ExpenseTrackerTheme { // Wrap your screen with ExpenseTrackerTheme
        val viewModel: GroupOverviewViewModel = getViewModel()
        val uiStateFlow = viewModel.uiStateFlow.collectAsState()

        GroupOverviewScreen(
            uiStateFlow = uiStateFlow,
            onAddGroup = { navigator.navigate(AddGroupScreenDestination) },
            onNavigateToDetailScreen = { uuid ->
                navigator.navigate(
                    GroupDetailScreenDestination(
                        GroupDetailScreenDestination.NavArgs(uuid)
                    )
                )
            })
    }
}

@Composable
private fun GroupOverviewScreen(
    uiStateFlow: State<GroupOverviewUiState>,
    onAddGroup: () -> Unit,
    onNavigateToDetailScreen: (UUID) -> Unit
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddGroup) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add_group))
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.expense_tracker_main_logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(300.dp)
                    .padding(top = 24.dp, bottom = 16.dp),
                contentScale = ContentScale.Crop
            )
            LazyColumn(
                modifier = Modifier
            ) {
                items(items = uiStateFlow.value.groups) {
                    GroupCard(group = it, onNavigateToDetailScreen = onNavigateToDetailScreen)
                }
            }
            Spacer(Modifier.weight(1f))
            VersionCopyrightLabel()
        }
    }
}

@Composable
private fun GroupCard(
    group: Group,
    onNavigateToDetailScreen: (UUID) -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .padding(vertical = 8.dp, horizontal = 12.dp)
    ) {
        var expanded by remember { mutableStateOf(false) }
        Column(
            modifier = Modifier
                .animateContentSize(
                    animationSpec = spring(
                        Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
                .clickable(onClick = { onNavigateToDetailScreen(group.id) })
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(group.name, style = MaterialTheme.typography.headlineSmall)
                Text("Costs: 1252.32€", style = MaterialTheme.typography.bodyMedium)
                ExpandCollapseButton(expanded = expanded, onClick = { expanded = !expanded })
            }
            if (expanded) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Participants", style = MaterialTheme.typography.bodySmall)
                        for (participant in group.participants) {
                            Text(
                                participant.name,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp)
                            )
                        }
                    }
                    Text(
                        "Transactions: ${group.transactions.size}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

            }
        }
    }
}

@Composable
private fun ExpandCollapseButton(
    expanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(onClick = onClick, modifier = modifier) {
        Icon(
            imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
            contentDescription = null
        )
    }
}

@Composable
private fun VersionCopyrightLabel(modifier: Modifier = Modifier) {
    val infoText = "©2023 DGS Software\nApp version: ${BuildConfig.VERSION_NAME}" // TODO: Move into resources
    Row(
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = infoText,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            fontSize = 12.sp,
            textAlign = TextAlign.End,
            modifier = modifier
                .padding(16.dp)
        )
    }
}

@Composable
private fun GroupOverviewScreenPreview(darkMode: Boolean) {
    val uiState = remember {
        mutableStateOf(
            GroupOverviewUiState(
                groups = listOf(
                    Group(
                        name = "Rock im Park",
                        participants = listOf(
                            Participant("Participant 1"),
                            Participant("Participant 2")
                        ),
                        currency = Currency.EURO,
                        transactions = listOf()
                    ), Group(
                        name = "Summer Breeze",
                        participants = listOf(
                            Participant("Participant 3"),
                            Participant("Participant 4")
                        ),
                        currency = Currency.EURO,
                        transactions = listOf()
                    )
                )
            )
        )
    }
    ExpenseTrackerTheme(darkMode = darkMode) {
        GroupOverviewScreen(uiStateFlow = uiState, onAddGroup = {}, onNavigateToDetailScreen = {})
    }
}

@Preview(name = "Group Overview Screen - Light Mode")
@Composable
private fun GroupOverviewScreenLightPreview() {
    GroupOverviewScreenPreview(darkMode = false)
}

@Preview(name = "Group Overview Screen - Dark Mode")
@Composable
private fun GroupOverviewScreenDarkPreview() {
    GroupOverviewScreenPreview(darkMode = true)
}