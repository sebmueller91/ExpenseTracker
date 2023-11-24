package com.example.expensetracker.ui.screens.add_group

import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
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
        onClose = { navigator.popBackStack() },
        goToNextSubScreen = viewModel::goToNextSubScreen,
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
            when (uiStateFlow.value.subScreen) {
                AddGroupSubScreens.GROUPNAME_CURRENCY -> {
                    navigator.popBackStack()
                }

                AddGroupSubScreens.PARTICIPANTS -> {
                    viewModel.goToPreviousSubScreen()
                }

                else -> {}
            }
        }
    )
}


@Composable
private fun AddGroupScreen(
    uiStateFlow: State<AddGroupUiState>,
    goToNextSubScreen: () -> Unit,
    updateGroupName: (String) -> Unit,
    addParticipant: () -> Unit,
    updateParticipant: (Int, String) -> Unit,
    deleteParticipant: (Int) -> Unit,
    selectCurrency: (Currency) -> Unit,
    onClose: () -> Unit,
    onBack: () -> Unit,
    onFinish: () -> Unit
) {
    BackHandler(enabled = true, onBack = onBack)

    val screenWidth =
        with(LocalDensity.current) {
            LocalConfiguration.current.screenWidthDp.dp.toPx().roundToInt()
        }

    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text(
                    when (uiStateFlow.value.subScreen) {
                        AddGroupSubScreens.GROUPNAME_CURRENCY -> stringResource(R.string.add_new_group)
                        AddGroupSubScreens.PARTICIPANTS -> stringResource(R.string.add_group_members)
                        else -> stringResource(R.string.share_the_group)
                    }
                )
            },
            navigationIcon = {
                when (uiStateFlow.value.subScreen) {
                    AddGroupSubScreens.GROUPNAME_CURRENCY -> {
                        NavigationIcon(imageVector = Icons.Default.Close, onClick = onClose)
                    }

                    AddGroupSubScreens.PARTICIPANTS -> {
                        NavigationIcon(imageVector = Icons.Default.ArrowBack, onClick = onBack)
                    }

                    else -> {
                        NavigationIcon(imageVector = Icons.Default.Close, onClick = onFinish)
                    }
                }
            },
            elevation = 0.dp
        )
    }, floatingActionButton = {
        val fabEnabled by remember(uiStateFlow.value.participantsNames) {
            mutableStateOf(uiStateFlow.value.participantsNames.none { it.isBlank() })
        }
        if (uiStateFlow.value.subScreen == AddGroupSubScreens.PARTICIPANTS) {
            FloatingActionButton(
                onClick = {
                    if (fabEnabled) {
                        addParticipant()
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
                visible = uiStateFlow.value.subScreen == AddGroupSubScreens.GROUPNAME_CURRENCY,
                enter = slideInHorizontally(
                    initialOffsetX = { if (uiStateFlow.value.subScreen == AddGroupSubScreens.GROUPNAME_CURRENCY) -screenWidth else screenWidth },
                    animationSpec = tween(durationMillis = 300, easing = LinearEasing)
                ), exit = slideOutHorizontally(
                    targetOffsetX = { if (uiStateFlow.value.subScreen == AddGroupSubScreens.GROUPNAME_CURRENCY) screenWidth else -screenWidth },
                    animationSpec = tween(durationMillis = 300, easing = LinearEasing)
                )
            ) {
                GroupNameCurrencyScreen(
                    uiStateFlow = uiStateFlow,
                    goToNextSubScreen = goToNextSubScreen,
                    updateGroupName = updateGroupName,
                    selectCurrency = selectCurrency
                )
            }

            AnimatedVisibility(
                visible = uiStateFlow.value.subScreen == AddGroupSubScreens.PARTICIPANTS,
                enter = slideInHorizontally(
                    initialOffsetX = { if (uiStateFlow.value.subScreen == AddGroupSubScreens.GROUPNAME_CURRENCY) -screenWidth else screenWidth },
                    animationSpec = tween(durationMillis = 300, easing = LinearEasing)
                ),
                exit = slideOutHorizontally(
                    targetOffsetX = { if (uiStateFlow.value.subScreen == AddGroupSubScreens.GROUPNAME_CURRENCY) screenWidth else -screenWidth },
                    animationSpec = tween(durationMillis = 300, easing = LinearEasing)
                )
            ) {
                ParticipantsInputScreen(
                    uiStateFlow = uiStateFlow,
                    participantsNames = uiStateFlow.value.participantsNames,
                    goToNextSubScreen = goToNextSubScreen,
                    updateParticipant = updateParticipant,
                    deleteParticipant = deleteParticipant
                )
            }

            AnimatedVisibility(
                visible = uiStateFlow.value.subScreen == AddGroupSubScreens.SHARE,
                enter = slideInHorizontally(
                    initialOffsetX = { if (uiStateFlow.value.subScreen == AddGroupSubScreens.PARTICIPANTS) -screenWidth else screenWidth },
                    animationSpec = tween(durationMillis = 300, easing = LinearEasing)
                )
            ) {
                ShareGroupScreen(
                    uiStateFlow = uiStateFlow,
                    onFinish = onFinish
                )
            }
        }
    }
}

@Composable
private fun GroupNameCurrencyScreen(
    uiStateFlow: State<AddGroupUiState>,
    goToNextSubScreen: () -> Unit,
    updateGroupName: (String) -> Unit,
    selectCurrency: (Currency) -> Unit,
    modifier: Modifier = Modifier
) {
    val groupNameFocusRequester by remember { mutableStateOf(FocusRequester()) }
    LaunchedEffect(key1 = Unit) {
        if (uiStateFlow.value.subScreen == AddGroupSubScreens.GROUPNAME_CURRENCY) {
            groupNameFocusRequester.requestFocus()
        }
    }

    Column(modifier = modifier) {
        GroupNameTextField(
            groupName = uiStateFlow.value.groupName,
            onValueChange = updateGroupName,
            onFinished = goToNextSubScreen,
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
            onClick = goToNextSubScreen, modifier = Modifier
                .align(Alignment.CenterHorizontally),
            enabled = uiStateFlow.value.groupName.isNotBlank()
        ) {
            Text(stringResource(id = R.string.next))
        }
    }
}

@Composable
private fun NavigationIcon(imageVector: ImageVector, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = imageVector,
            contentDescription = null
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
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .heightIn(max = 300.dp)
            ) {
                Currency.entries.forEach { currency ->
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
fun ParticipantsInputScreen(
    uiStateFlow: State<AddGroupUiState>,
    participantsNames: List<String>,
    goToNextSubScreen: () -> Unit,
    updateParticipant: (Int, String) -> Unit,
    deleteParticipant: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    var previousValue by remember { mutableIntStateOf(participantsNames.size) }
    LaunchedEffect(key1 = participantsNames.size) {
        if (previousValue < participantsNames.size) {
            listState.animateScrollToItem(participantsNames.size - 1)
            previousValue = participantsNames.size
        }
    }

    Scaffold(modifier = modifier.fillMaxSize(),
        bottomBar = {
            Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Button(
                    onClick = goToNextSubScreen,
                    modifier = Modifier.padding(top = 16.dp),
                    enabled = uiStateFlow.value.participantsNames.isNotEmpty() && uiStateFlow.value.participantsNames.none { it.isBlank() }
                ) {
                    Text(stringResource(R.string.finish))
                }
            }
        }
    ) { padding ->
        LazyColumn(modifier = modifier.padding(padding), state = listState) {
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
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun ParicipantTextField( // TODO: Fuse this with the group name field?
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
            focusRequester.requestFocus()
        }
    }
}

@Composable
private fun ShareGroupScreen(
    uiStateFlow: State<AddGroupUiState>,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val invitationLink = "fake_url/1234" // TODO: Replace with real link
    val invitationMessage = stringResource(R.string.invitation_text, uiStateFlow.value.groupName, invitationLink)

    Scaffold(
        modifier = modifier,
        bottomBar = {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Button(onClick = onFinish) {
                    Text(stringResource(R.string.go_to_group))
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(R.string.group_created_successfully),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { shareMessageIntent(context = context, text = invitationMessage) },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(Icons.Filled.Share, contentDescription = stringResource(R.string.share))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.invite_friends))
                }
            }
        }
    }
}

private fun shareMessageIntent(context: Context, text: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    val chooserIntent = Intent.createChooser(intent, "Share via")
    context.startActivity(chooserIntent)
}

@Composable
private fun AddGroupScreenPreview(darkTheme: Boolean, subScreen: AddGroupSubScreens) {
    ExpenseTrackerTheme(darkTheme = darkTheme) {
        val uiState = remember {
            mutableStateOf(
                AddGroupUiState(
                    groupName = "Rock im Park 2023",
                    participantsNames = listOf("Peter", "Michaela", "Gustav"),
                    subScreen = subScreen
                )
            )
        }
        AddGroupScreen(
            uiStateFlow = uiState,
            goToNextSubScreen = {},
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
    AddGroupScreenPreview(darkTheme = true, subScreen = AddGroupSubScreens.GROUPNAME_CURRENCY)
}

@Preview(name = "Add Group Screen 1 - Light Theme")
@Composable
private fun AddGroupScreen1LightPreview() {
    AddGroupScreenPreview(darkTheme = false, subScreen = AddGroupSubScreens.GROUPNAME_CURRENCY)
}

@Preview(name = "Add Group Screen 2 - Dark Theme")
@Composable
private fun AddGroupScreen2DarkPreview() {
    AddGroupScreenPreview(darkTheme = true, subScreen = AddGroupSubScreens.PARTICIPANTS)
}

@Preview(name = "Add Group Screen 2 - Light Theme")
@Composable
private fun AddGroupScreen2LightPreview() {
    AddGroupScreenPreview(darkTheme = false, subScreen = AddGroupSubScreens.PARTICIPANTS)
}