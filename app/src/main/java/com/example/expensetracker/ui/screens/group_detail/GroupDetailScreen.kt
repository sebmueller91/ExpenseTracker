package com.example.expensetracker.ui.screens.group_detail

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.expensetracker.R
import com.example.expensetracker.model.Participant
import com.example.expensetracker.ui.components.NavigationIcon
import com.example.expensetracker.ui.screens.destinations.GroupOverviewScreenDestination
import com.example.expensetracker.ui.screens.group_detail.tabs.ExpensesTab
import com.example.expensetracker.ui.screens.group_detail.tabs.SettleUpTab
import com.example.expensetracker.ui.screens.group_detail.tabs.StatisticsTab
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf
import java.util.UUID

@Destination
@Composable
fun GroupDetailScreen(
    groupId: UUID,
    navigator: DestinationsNavigator
) {
    val viewModel: GroupDetailViewModel = getViewModel { parametersOf(groupId) }

    GroupDetailScreen(
        uiStateFlow = viewModel.uiStateFlow.collectAsStateWithLifecycle(),
        eventCostFlow = viewModel.eventCostsFlow.collectAsStateWithLifecycle(),
        individualSharesFlow = viewModel.individualSharesFlow.collectAsStateWithLifecycle(),
        percentageSharesFlow = viewModel.percentageSharesFlow.collectAsStateWithLifecycle(),
        onLeave = { navigator.navigate(GroupOverviewScreenDestination()) })
}

@Composable
private fun GroupDetailScreen(
    uiStateFlow: State<GroupDetailUiState>,
    eventCostFlow: State<Double>,
    individualSharesFlow: State<Map<Participant, Double>>,
    percentageSharesFlow: State<Map<Participant, Double>>,
    onLeave: () -> Unit
) {
    BackHandler(onBack = onLeave)

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (val uiState = uiStateFlow.value) {
            is GroupDetailUiState.Error -> {
                Text(stringResource(R.string.something_went_wrong))
            }

            is GroupDetailUiState.Loading -> {
                CircularProgressIndicator()
            }

            is GroupDetailUiState.Success -> {
                GroupDetailScreenContent(
                    eventCostFlow = eventCostFlow,
                    individualSharesFlow = individualSharesFlow,
                    percentageSharesFlow = percentageSharesFlow,
                    uiState = uiState,
                    onLeave = onLeave
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun GroupDetailScreenContent(
    uiState: GroupDetailUiState.Success,
    eventCostFlow: State<Double>,
    individualSharesFlow: State<Map<Participant, Double>>,
    percentageSharesFlow: State<Map<Participant, Double>>,
    onLeave: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(GroupDetailScreenTabs.EXPENSES) }
    val pagerState = rememberPagerState { GroupDetailScreenTabs.entries.size }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(modifier = modifier
        .fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(uiState.group.name) },
                navigationIcon = {
                    NavigationIcon(imageVector = Icons.Default.ArrowBack, onClick = onLeave)
                })
        },
        bottomBar = {
            NavigationBar {
                GroupDetailScreenTabs.entries.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                            selectedTab = tab
                        },
                        icon = {
                            Icon(
                                imageVector = tab.imageVector,
                                contentDescription = tab.label
                            )
                        },
                        label = { Text(text = tab.label) })
                }
            }
        })
    { innerPadding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) { page ->
            when (GroupDetailScreenTabs.values()[page]) {
                GroupDetailScreenTabs.EXPENSES -> ExpensesTab(
                    transactions = uiState.group.transactions,
                    currency = uiState.group.currency
                )
                GroupDetailScreenTabs.STATISTICS -> StatisticsTab(
                    group = uiState.group,
                    eventCostFlow = eventCostFlow,
                    individualSharesFlow = individualSharesFlow,
                    percentageSharesFlow = percentageSharesFlow
                )
                GroupDetailScreenTabs.SETTLE_UP -> SettleUpTab(group = uiState.group)
            }
        }
    }
}

private enum class GroupDetailScreenTabs(
    val label: String,
    val imageVector: ImageVector
) {
    // TODO: Move strings into resources
    EXPENSES("Expenses", Icons.Filled.Sort),
    STATISTICS("Statistics", Icons.Filled.BarChart),
    SETTLE_UP("Settle up", Icons.Filled.DoneAll)
}

// TODO: Add previews