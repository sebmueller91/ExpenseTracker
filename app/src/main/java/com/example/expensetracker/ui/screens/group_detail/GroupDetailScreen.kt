package com.example.expensetracker.ui.screens.group_detail

import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.expensetracker.R
import com.example.expensetracker.model.Currency
import com.example.expensetracker.model.Group
import com.example.expensetracker.model.Transaction
import com.example.expensetracker.ui.components.NavigationIcon
import com.example.expensetracker.ui.screens.destinations.GroupOverviewScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.UUID
import kotlin.math.absoluteValue

@Destination
@Composable
fun GroupDetailScreen(
    groupId: UUID,
    navigator: DestinationsNavigator
) {
    val viewModel: GroupDetailViewModel = getViewModel { parametersOf(groupId) }
    val uiStateFlow = viewModel.uiStateFlow.collectAsStateWithLifecycle()

    GroupDetailScreen(
        uiStateFlow = uiStateFlow,
        onLeave = { navigator.navigate(GroupOverviewScreenDestination()) })
}

@Composable
private fun GroupDetailScreen(
    uiStateFlow: State<GroupDetailUiState>,
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
                    uiState = uiState,
                    onLeave = onLeave
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GroupDetailScreenContent(
    uiState: GroupDetailUiState.Success,
    onLeave: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dragThreshold = 20.dp
    var selectedTab by remember { mutableStateOf(GroupDetailScreenTabs.EXPENSES) }

    val dragModifier = Modifier.pointerInput(Unit) {
        detectHorizontalDragGestures { _, dragAmount ->
            if (dragAmount.absoluteValue > dragThreshold.toPx()) {
                val newTab =
                    if (dragAmount < 0) GroupDetailScreenTabs.EXPENSES else GroupDetailScreenTabs.OVERVIEW
                if (newTab != selectedTab) {
                    selectedTab = newTab
                }
            }
        }
    }

    Scaffold(modifier = modifier
        .fillMaxSize()
        .then(dragModifier),
        topBar = {
            TopAppBar(
                title = { Text(uiState.group.name) },
                navigationIcon = {
                    NavigationIcon(imageVector = Icons.Default.ArrowBack, onClick = onLeave)
                })
        },
        bottomBar = {
            NavigationBar() {
                GroupDetailScreenTabs.entries.forEach { tab ->
                    val selected = tab == selectedTab

                    NavigationBarItem(
                        selected = selected,
                        onClick = { selectedTab = tab },
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedVisibility(
                visible = selectedTab == GroupDetailScreenTabs.EXPENSES,
                enter = slideInHorizontally { fullWidth -> fullWidth },
                exit = slideOutHorizontally { fullWidth -> fullWidth }
            ) {
                ExpensesTab(
                    transactions = uiState.group.transactions,
                    currency = uiState.group.currency
                )
            }

            AnimatedVisibility(
                visible = selectedTab == GroupDetailScreenTabs.OVERVIEW,
                enter = slideInHorizontally { fullWidth -> -fullWidth },
                exit = slideOutHorizontally { fullWidth -> -fullWidth }
            ) {
                OverviewTab(group = uiState.group)
            }
        }
    }
}


// TODO: Move into separate file
@Composable
private fun OverviewTab(group: Group, modifier: Modifier = Modifier) {
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
                modifier = Modifier.padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Overview")
            }
        }
    }
}

@Composable
private fun ExpensesTab(
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
            FloatingActionButton(onClick = expandCollapseFab, shape = CircleShape) {
                Icon(
                    Icons.Filled.MoreHoriz,
                    contentDescription = null
                )
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

    Column {
        AnimatedFloatingActionButton(
            visible = fabExpanded,
            icon = Icons.Filled.Payment,
            labelId = R.string.add_expense,
            animationDelayEnter = 0,
            animationDelayExit = 0
        ) {
            // TODO
        }
        Spacer(Modifier.height(20.dp))
        Row {
            Spacer(Modifier.weight(1f))
            FloatingActionButton(onClick = expandCollapseFab, shape = CircleShape) {
                Icon(
                    Icons.Filled.MoreHoriz,
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
private fun ScreenWithAnimatedOverlay(
    applyOverlay: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier) {
        content()

        AnimatedVisibility(
            visible = applyOverlay,
            enter = fadeIn(
                animationSpec = tween(durationMillis = 500)
            ),
            exit = fadeOut(animationSpec = tween(durationMillis = 500))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.2f),
                                Color.White.copy(alpha = 0.9f)
                            ), center = Offset(1f, 1f)
                        )
                    )
            )
        }
    }
}

@Composable
private fun AnimatedFloatingActionButton(
    visible: Boolean,
    icon: ImageVector,
    @StringRes labelId: Int,
    animationDelayEnter: Int,
    animationDelayExit: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInHorizontally(
            initialOffsetX = { fullWidth -> fullWidth },
            animationSpec = tween(durationMillis = 500, delayMillis = animationDelayEnter)
        ),
        exit = slideOutHorizontally(
            targetOffsetX = { fullWidth -> fullWidth },
            animationSpec = tween(durationMillis = 500, delayMillis = animationDelayExit)
        )
    ) {
        Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
            Spacer(Modifier.weight(1f))
            Text(stringResource(id = labelId), Modifier.padding(end = 8.dp))
            FloatingActionButton(onClick = onClick, shape = CircleShape) {
                Icon(imageVector = icon, contentDescription = stringResource(id = labelId))
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
                "${expense.paidBy.name} paid ${
                    String.format("%.2f", expense.amount).toDouble()
                }${currency.symbol} for ${expense.purpose}"
            )
            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                Text(SimpleDateFormat("dd.MM.yyyy").format(expense.date))
                Text("Split between ${expense.splitBetween.joinToString(", ") { it.name }}")
            }
        }
    }
}

private enum class GroupDetailScreenTabs(
    val label: String,
    val imageVector: ImageVector
) {
    OVERVIEW("Overview", Icons.Filled.DoneAll),
    EXPENSES("Expenses", Icons.Filled.Sort)
}

// TODO: Add previews