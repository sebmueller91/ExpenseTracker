package com.example.expensetracker.ui.screens.AddGroup

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.expensetracker.R
import com.example.expensetracker.ui.screens.destinations.GroupDetailScreenDestination
import com.example.expensetracker.ui.theme.ExpenseTrackerTheme
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.getViewModel
import kotlin.math.roundToInt

@Destination
@Composable
fun AddGroupScreen(
    navigator: DestinationsNavigator
) {
    val viewModel: AddGroupViewModel = getViewModel()
    val uiStateFlow = viewModel.uiStateFlow.collectAsState() // TODO: with lifecycle

    AddGroupScreen(
        uiStateFlow = uiStateFlow,
        updateShowScreen1 = viewModel::updateShowScreen1,
        onClose = { navigator.popBackStack() },
        updateGroupName = viewModel::updateGroupName,
        updateParticipantsNames = viewModel::updateParticipantsNames,
        onFinish = {
            viewModel.createNewGroup()
            navigator.navigate(GroupDetailScreenDestination)
        },
        onBack = {
            if (uiStateFlow.value.showScreen1) {
                navigator.popBackStack()
            } else {
                viewModel.updateShowScreen1(true)
            }
        }
    )
}


@Composable
private fun AddGroupScreen(
    uiStateFlow: State<AddGroupUiState>,
    updateShowScreen1: (Boolean) -> Unit,
    updateGroupName: (String) -> Unit,
    updateParticipantsNames: (List<String>) -> Unit,
    onClose: () -> Unit,
    onFinish: () -> Unit,
    onBack: () -> Unit
) {
    BackHandler(enabled = true, onBack = onBack)

    val screenWidth =
        with(LocalDensity.current) {
            LocalConfiguration.current.screenWidthDp.dp.toPx().roundToInt()
        }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Add new group") },
            navigationIcon = {
                if (uiStateFlow.value.showScreen1) {
                    NavigationIcon(imageVector = Icons.Default.Close, onClick = onClose)
                } else {
                    NavigationIcon(imageVector = Icons.Default.ArrowBack) {
                        updateShowScreen1(true)
                    }
                }
            },
            actions = {
                Button(onClick = onFinish) {
                    Text("Save")
                }
            },
            backgroundColor = MaterialTheme.colors.background,
            elevation = 0.dp
        )
    }) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.TopCenter
        ) {

            AnimatedVisibility(
                visible = uiStateFlow.value.showScreen1,
                enter = slideInHorizontally(
                    initialOffsetX = { if (uiStateFlow.value.showScreen1) -screenWidth else screenWidth },
                    animationSpec = tween(durationMillis = 300, easing = LinearEasing)
                ), exit = slideOutHorizontally(
                    targetOffsetX = { if (uiStateFlow.value.showScreen1) screenWidth else -screenWidth },
                    animationSpec = tween(durationMillis = 300, easing = LinearEasing)
                )
            ) {
                GroupNameTextField(
                    groupName = uiStateFlow.value.groupName,
                    onValueChange = updateGroupName,
                    onFinished = { updateShowScreen1(false) },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            AnimatedVisibility(
                visible = !uiStateFlow.value.showScreen1,
                enter = slideInHorizontally(
                    initialOffsetX = { if (uiStateFlow.value.showScreen1) -screenWidth else screenWidth },
                    animationSpec = tween(durationMillis = 300, easing = LinearEasing)
                ),
                exit = slideOutHorizontally(
                    targetOffsetX = { if (uiStateFlow.value.showScreen1) screenWidth else -screenWidth },
                    animationSpec = tween(durationMillis = 300, easing = LinearEasing)
                )
            ) {
                ParticipantsInput(
                    participantsNames = uiStateFlow.value.participantsNames,
                    onParticipantsChange = updateParticipantsNames
                )
            }
        }
    }
}

@Composable
private fun NavigationIcon(imageVector: ImageVector, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = imageVector,
            contentDescription = null,
            tint = MaterialTheme.colors.onBackground
        )
    }
}

@Composable
private fun GroupNameTextField(
    groupName: String,
    onValueChange: (String) -> Unit,
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Go
    )
    Column() {
        TextField(
            value = groupName,
            onValueChange = onValueChange,
            label = { Text(stringResource(R.string.group_name)) },
            modifier = modifier,
            maxLines = 1, // TODO: Limit overflow,
            keyboardOptions = keyboardOptions,
            keyboardActions = KeyboardActions(
                onGo = {
                    onFinished()
                }
            ),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = MaterialTheme.colors.secondary,
                unfocusedIndicatorColor = MaterialTheme.colors.secondary,
                cursorColor = MaterialTheme.colors.primary,
                textColor = MaterialTheme.colors.onBackground,
                backgroundColor = Color.Transparent,
            )
        )
        Button(
            onClick = onFinished, modifier = Modifier
                .padding(16.dp)
                .align(Alignment.End),
            enabled = groupName.isNotBlank()
        ) {
            Text(stringResource(id = R.string.next))
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ParticipantsInput(
    participantsNames: List<String>,
    onParticipantsChange: (List<String>) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(modifier = Modifier.fillMaxWidth()) {
        participantsNames.forEachIndexed { index, participant ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                verticalAlignment = CenterVertically
            ) {
                TextField(
                    value = participant,
                    onValueChange = {
                        onParticipantsChange(
                            participantsNames.toMutableList().apply { set(index, it) })
                    },
                    label = { Text("Participant ${index + 1}") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = MaterialTheme.colors.secondary,
                        unfocusedIndicatorColor = MaterialTheme.colors.secondary,
                        cursorColor = MaterialTheme.colors.primary,
                        textColor = MaterialTheme.colors.onBackground,
                        backgroundColor = Color.Transparent,
                    )
                )
                if (participantsNames.size > 1) {
                    IconButton(onClick = {
                        onParticipantsChange(
                            participantsNames.toMutableList().apply { removeAt(index) })
                    }, modifier = Modifier.size(30.dp)) {
                        Icon(Icons.Default.RemoveCircleOutline, contentDescription = null)
                    }
                } else {
                    Spacer(Modifier.size(30.dp))
                }
            }
        }

        IconButton(
            modifier = Modifier.align(CenterHorizontally),
            onClick = {
                onParticipantsChange(participantsNames.toMutableList().apply { add("") })
            }) {
            Icon(
                modifier = Modifier.size(45.dp),
                imageVector = Icons.Default.AddCircle,
                tint = MaterialTheme.colors.secondary,
                contentDescription = null
            )
        }
    }
}

@Composable
private fun AddGroupScreenPreview(darkTheme: Boolean, showScreen1: Boolean) {
    ExpenseTrackerTheme(darkTheme = darkTheme) {
        val uiState = remember {
            mutableStateOf(
                AddGroupUiState(
                    groupName = "Rock im Park 2023",
                    participantsNames = listOf("Peter", "Michaela", "Gustav"),
                    showScreen1 = showScreen1
                )
            )
        }
        AddGroupScreen(
            uiStateFlow = uiState,
            updateShowScreen1 = { },
            updateGroupName = { },
            updateParticipantsNames = { },
            onClose = {},
            onFinish = { },
            onBack = {})
    }
}

@Preview(name = "Add Group Screen 1 - Dark Theme")
@Composable
private fun AddGroupScreen1DarkPreview() {
    AddGroupScreenPreview(darkTheme = true, showScreen1 = true)
}

@Preview(name = "Add Group Screen 1 - Light Theme")
@Composable
private fun AddGroupScreen1LightPreview() {
    AddGroupScreenPreview(darkTheme = false, showScreen1 = true)
}

@Preview(name = "Add Group Screen 2 - Dark Theme")
@Composable
private fun AddGroupScreen2DarkPreview() {
    AddGroupScreenPreview(darkTheme = true, showScreen1 = false)
}

@Preview(name = "Add Group Screen 2 - Light Theme")
@Composable
private fun AddGroupScreen2LightPreview() {
    AddGroupScreenPreview(darkTheme = false, showScreen1 = false)
}