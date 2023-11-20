package com.example.expensetracker.ui.screens.add_group

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.expensetracker.R
import com.example.expensetracker.model.Currency
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
    val uiStateFlow = viewModel.uiStateFlow.collectAsStateWithLifecycle()

    AddGroupScreen(
        uiStateFlow = uiStateFlow,
        updateShowScreen1 = viewModel::updateShowScreen1,
        onClose = { navigator.popBackStack() },
        updateGroupName = viewModel::updateGroupName,
        addParticipant = viewModel::addParticipant,
        updateParticipant = viewModel::updateParticipant,
        deleteParticipant = viewModel::deleteParticipant,
        selectCurrency = viewModel::selectCurrency,
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
    addParticipant: () -> Unit,
    updateParticipant: (Int, String) -> Unit,
    deleteParticipant: (Int) -> Unit,
    selectCurrency: (Currency) -> Unit,
    onClose: () -> Unit,
    onFinish: () -> Unit,
    onBack: () -> Unit
) {
    BackHandler(enabled = true, onBack = onBack)

    val groupNameFocusRequester by remember { mutableStateOf(FocusRequester()) }
    LaunchedEffect(key1 = Unit) {
        if (uiStateFlow.value.showScreen1) {
            groupNameFocusRequester.requestFocus()
        }
    }

    val screenWidth =
        with(LocalDensity.current) {
            LocalConfiguration.current.screenWidthDp.dp.toPx().roundToInt()
        }

    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text(
                    if (uiStateFlow.value.showScreen1) stringResource(R.string.add_new_group)
                    else stringResource(R.string.add_group_members)
                )
            },
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
                if (!uiStateFlow.value.showScreen1) {
                    Button(onClick = onFinish) {
                        Text("Save")
                    }
                }
            },
            backgroundColor = MaterialTheme.colors.background,
            elevation = 0.dp
        )
    }, floatingActionButton = {
        val fabEnabled by remember(uiStateFlow.value.participantsNames) {
            mutableStateOf(uiStateFlow.value.participantsNames.none { it.isBlank() })
        }
        if (!uiStateFlow.value.showScreen1) {
            FloatingActionButton(
                onClick = {
                    if (fabEnabled) addParticipant()
                    else {
                    }
                },
                backgroundColor = if (fabEnabled) MaterialTheme.colors.primary else Color.LightGray
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = null,
                    tint = if (fabEnabled) MaterialTheme.colors.onPrimary else Color.White
                )
            }
        }
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
                Column() {
                    GroupNameTextField(
                        groupName = uiStateFlow.value.groupName,
                        onValueChange = updateGroupName,
                        onFinished = { updateShowScreen1(false) },
                        modifier = Modifier
                            .focusRequester(groupNameFocusRequester)
                            .fillMaxWidth()
                    )
                    CurrencyDropdown(
                        selectedCurrency = uiStateFlow.value.currency,
                        selectCurrency = selectCurrency
                    )
                    Spacer(Modifier.weight(1f))
                    Button(
                        onClick = { updateShowScreen1(false) }, modifier = Modifier
                            .align(Alignment.CenterHorizontally),
                        enabled = uiStateFlow.value.groupName.isNotBlank()
                    ) {
                        Text(stringResource(id = R.string.next))
                    }
                }
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
                    updateParticipant = updateParticipant,
                    deleteParticipant = deleteParticipant
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
    }
}

@Composable
private fun CurrencyDropdown(
    selectedCurrency: Currency,
    selectCurrency: (Currency) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 48.dp, vertical = 32.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Text(stringResource(id = R.string.currency))
        Spacer(modifier = Modifier.weight(1f))

        Box {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { expanded = !expanded }
            ) {
                Text(
                    text = "${selectedCurrency.symbol} (${selectedCurrency.currency_name})",
                    style = MaterialTheme.typography.body1
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                    contentDescription = null
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.align(Alignment.BottomStart)
            ) {
                Currency.values().forEach { currency ->
                    DropdownMenuItem(onClick = {
                        selectCurrency(currency)
                        expanded = false
                    }) {
                        Text(
                            text = "${currency.symbol} (${currency.currency_name})",
                            style = MaterialTheme.typography.body2
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ParticipantsInput(
    participantsNames: List<String>,
    updateParticipant: (Int, String) -> Unit,
    deleteParticipant: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    var previousValue by remember { mutableStateOf(participantsNames.size) }
    LaunchedEffect(key1 = participantsNames.size) {
        if (previousValue < participantsNames.size) {
            listState.animateScrollToItem(participantsNames.size - 1)
            previousValue = participantsNames.size
        }
    }

    LazyColumn(modifier = modifier, state = listState) {
        itemsIndexed(participantsNames) { index, participantName ->
            ParicipantTextField(
                participantName = participantName,
                index = index,
                numberParticipants = participantsNames.size,
                updateParticipant = updateParticipant,
                deleteParticipant = deleteParticipant
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun ParicipantTextField(
    participantName: String,
    index: Int,
    numberParticipants: Int,
    updateParticipant: (Int, String) -> Unit,
    deleteParticipant: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField( // TODO: Limit lines
            value = participantName,
            textStyle = TextStyle(fontSize = 14.sp, lineHeight = 16.sp),
            onValueChange = { updateParticipant(index, it) },
            label = { Text("Participant ${index + 1}") },
            modifier = Modifier
                .then(
                    if (index == numberParticipants - 1) Modifier.focusRequester(
                        focusRequester
                    ) else Modifier
                )
                .weight(1f)
                .padding(horizontal = 8.dp, vertical = 1.dp),
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
        if (numberParticipants > 1) {
            IconButton(
                onClick = { deleteParticipant(index) },
                modifier = Modifier.size(30.dp)
            ) {
                Icon(Icons.Default.RemoveCircleOutline, contentDescription = null)
            }
        } else {
            Spacer(Modifier.size(30.dp))
        }
    }

    LaunchedEffect(key1 = numberParticipants) {
        if (index == numberParticipants - 1) {
            focusRequester?.requestFocus()
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
            addParticipant = {},
            updateParticipant = { _, _ -> },
            deleteParticipant = {},
            selectCurrency = {},
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