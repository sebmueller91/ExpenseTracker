package com.example.expensetracker.ui.screens.group_overview

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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

// TODO: Refactor and add preview

@RootNavGraph(start = true)
@Destination
@Composable
fun GroupOverviewScreen(
    navigator: DestinationsNavigator
) {
    val viewModel: GroupOverviewViewModel = getViewModel()
    val uiStateFlow = viewModel.uiStateFlow.collectAsState()

    GroupOverviewScreen(
        uiStateFlow = uiStateFlow,
        onAddGroup = { navigator.navigate(AddGroupScreenDestination) },
        onNavigateToDetailScreen = { navigator.navigate(GroupDetailScreenDestination) })
}

@Composable
private fun GroupOverviewScreen(
    uiStateFlow: State<GroupOverviewUiState>,
    onAddGroup: () -> Unit,
    onNavigateToDetailScreen: () -> Unit
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
        }
    }
}

@Composable
private fun GroupCard(
    group: Group,
    onNavigateToDetailScreen: () -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    Card(
        elevation = 4.dp,
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
                .clickable(onClick = onNavigateToDetailScreen)
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(group.name, style = MaterialTheme.typography.h6)
                Text("Costs: 1252.32€", style = MaterialTheme.typography.body2)
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
                        Text("Participants", style = MaterialTheme.typography.body1)
                        for (participant in group.participants) {
                            Text(
                                participant.name,
                                style = MaterialTheme.typography.subtitle2,
                                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp)
                            )
                        }
                    }
                    Text(
                        "Transactions: ${group.transactions.size}",
                        style = MaterialTheme.typography.body1
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
            tint = MaterialTheme.colors.secondary,
            contentDescription = null
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
    ExpenseTrackerTheme(darkTheme = darkMode) {
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