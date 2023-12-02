package com.example.expensetracker.ui.screens.group_detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.expensetracker.ui.screens.destinations.GroupOverviewScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
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
    val uiStateFlow = viewModel.uiStateFlow.collectAsStateWithLifecycle()

    GroupDetailScreen(
        uiStateFlow = uiStateFlow,
        navigateBack = { navigator.navigate(GroupOverviewScreenDestination()) })
}

@Composable
private fun GroupDetailScreen(
    uiStateFlow: State<GroupDetailUiState>,
    navigateBack: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Group details! ${uiStateFlow.value.group?.name}")
        Button(onClick = navigateBack) {
            Text("Go to overview")
        }
    }
}