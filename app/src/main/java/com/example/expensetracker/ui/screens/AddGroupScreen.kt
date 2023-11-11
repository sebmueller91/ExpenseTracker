package com.example.expensetracker.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.expensetracker.R
import com.example.expensetracker.model.Participant
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlin.math.roundToInt

@Destination
@Composable
fun AddGroupScreen(
    navigator: DestinationsNavigator
) {
    var groupName by remember { mutableStateOf("") }
    var isGroupNameVisible by remember { mutableStateOf(true) } // TODO: Better name?
    val screenWidth =
        with(LocalDensity.current) {
            LocalConfiguration.current.screenWidthDp.dp.toPx().roundToInt()
        }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Add new group") },
            navigationIcon = {
                IconButton(onClick = {
                    navigator.popBackStack()
                }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = MaterialTheme.colors.onBackground
                    )
                }
            },
            // TODO: Have action button for finish?
//            actions = {
//                Button(colors = ButtonDefaults.buttonColors(
//                    backgroundColor = Color.Transparent,
//                    contentColor = MaterialTheme.colors.onBackground,
//                ), elevation = ButtonDefaults.elevation(0.dp),
//                    onClick = {
//                        isGroupNameVisible = !isGroupNameVisible
//                    }
//                ) {
//                    Text(stringResource(R.string.next))
//                }
//            },
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
                visible = isGroupNameVisible,
                enter = slideInHorizontally(
                    initialOffsetX = { if (isGroupNameVisible) -screenWidth else screenWidth },
                    animationSpec = tween(durationMillis = 300, easing = LinearEasing)
                ), exit = slideOutHorizontally(
                    targetOffsetX = { if (isGroupNameVisible) screenWidth else -screenWidth },
                    animationSpec = tween(durationMillis = 300, easing = LinearEasing)
                )
            ) {
                GroupNameTextField(
                    groupName = groupName,
                    onValueChange = { groupName = it },
                    onFinished = { isGroupNameVisible = !isGroupNameVisible },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            AnimatedVisibility(
                visible = !isGroupNameVisible,
                enter = slideInHorizontally(
                    initialOffsetX = { if (isGroupNameVisible) -screenWidth else screenWidth },
                    animationSpec = tween(durationMillis = 300, easing = LinearEasing)
                ),
                exit = slideOutHorizontally(
                    targetOffsetX = { if (isGroupNameVisible) screenWidth else -screenWidth },
                    animationSpec = tween(durationMillis = 300, easing = LinearEasing)
                )
            ) {
                ParticipantsEnterField(participants = listOf(), addParticipant = {})
            }
        }
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
                .align(Alignment.End)
        ) {
            Text(stringResource(id = R.string.next))
        }
    }
}

@Composable
private fun ParticipantsEnterField(
    participants: List<Participant>,
    addParticipant: (Participant) -> Unit,
    modifier: Modifier = Modifier
) {
    Text(text = "Fill me with Participants", modifier = modifier)
}

//@OptIn(ExperimentalComposeUiApi::class)
//@Destination
//@Composable
//fun AddEventScreen(
//    navigator: DestinationsNavigator
//) {
//    var eventName by remember { mutableStateOf("") }
//    val availableCurrencies = listOf("Euro")
//    var selectedCurrency by remember { mutableStateOf(availableCurrencies.first()) }
//    var participants by remember { mutableStateOf(listOf<String>()) }
//    val keyboardController = LocalSoftwareKeyboardController.current
//    val maxParticipants = 15
//
//    val canSave = participants.isNotEmpty() && eventName.isNotBlank()
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text(stringResource(R.string.add_new_event)) }
//            )
//        },
//        floatingActionButton = {
//            FloatingActionButton(onClick = {
//                if (canSave) {
//                    // TODO: Save to database
//                    navigator.navigate(EventsOverviewScreenDestination) // TODO: EventDetailScreen?
//                }
//            }, backgroundColor = if (canSave) MaterialTheme.colors.primary else Color.LightGray,
//                contentColor = if (canSave) MaterialTheme.colors.onPrimary else Color.White
//            ) {
//                Icon(Icons.Filled.Save, contentDescription = stringResource(R.string.save_event))
//            }
//        }
//    ) { innerPadding ->
//        Column(
//            modifier = Modifier
//                .padding(innerPadding)
//                .fillMaxSize()
//
//                .padding(16.dp),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            TextField(
//                value = eventName,
//                onValueChange = { eventName = it },
//                label = { Text("Event Name") },
//                modifier = Modifier.fillMaxWidth(),
//                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
//            )
//            DropdownMenuCurrencies(
//                currencies = availableCurrencies,
//                selectedCurrency = selectedCurrency,
//                onCurrencySelected = { selectedCurrency = it }
//            )
//            ParticipantsInput(
//                participants = participants,
//                onParticipantsChange = { participants = it },
//                maxParticipants = maxParticipants,
//                keyboardController = keyboardController
//            )
//        }
//    }
//}
//
//@Composable
//fun DropdownMenuCurrencies(
//    currencies: List<String>,
//    selectedCurrency: String,
//    onCurrencySelected: (String) -> Unit
//) {
//    var expanded by remember { mutableStateOf(false) }
//    Box(modifier = Modifier.fillMaxWidth()) {
//        Text(
//            text = selectedCurrency,
//            modifier = Modifier
//                .fillMaxWidth()
//                .clickable(onClick = { expanded = true })
//                .padding(16.dp)
//        )
//        DropdownMenu(
//            expanded = expanded,
//            onDismissRequest = { expanded = false },
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            currencies.forEach { currency ->
//                DropdownMenuItem(onClick = {
//                    onCurrencySelected(currency)
//                    expanded = false
//                }) {
//                    Text(text = currency)
//                }
//            }
//        }
//    }
//}
//
//@OptIn(ExperimentalComposeUiApi::class)
//@Composable
//fun ParticipantsInput(
//    participants: List<String>,
//    onParticipantsChange: (List<String>) -> Unit,
//    maxParticipants: Int,
//    keyboardController: SoftwareKeyboardController?
//) {
//    var newParticipant by remember { mutableStateOf("") }
//
//    Column(modifier = Modifier.fillMaxWidth()) {
//        participants.forEachIndexed { index, participant ->
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                TextField(
//                    value = participant,
//                    onValueChange = { updated ->
//                        onParticipantsChange(
//                            participants.toMutableList().apply { set(index, updated) })
//                    },
//                    label = { Text("Participant ${index + 1}") },
//                    modifier = Modifier
//                        .weight(1f)
//                        .padding(end = 8.dp),
//                    keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
//                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
//                )
//                if (participants.size > 1) {
//                    IconButton(onClick = {
//                        onParticipantsChange(participants.toMutableList().apply { removeAt(index) })
//                    }) {
//                        Icon(Icons.Default.Remove, contentDescription = "Remove")
//                    }
//                }
//            }
//        }
//        if (participants.size < maxParticipants) {
//            Button(onClick = {
//                onParticipantsChange(participants + newParticipant)
//                newParticipant = ""
//            }) {
//                Text("Add Participant")
//            }
//        }
//    }
//}
