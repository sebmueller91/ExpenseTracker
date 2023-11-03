package com.example.expensetracker.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.expensetracker.R
import com.example.expensetracker.ui.screens.destinations.EventsOverviewScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@OptIn(ExperimentalComposeUiApi::class)
@Destination
@Composable
fun AddEventScreen(
    navigator: DestinationsNavigator
) {
    var eventName by remember { mutableStateOf("") }
    val availableCurrencies = listOf("Euro")
    var selectedCurrency by remember { mutableStateOf(availableCurrencies.first()) }
    var participants by remember { mutableStateOf(listOf<String>()) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val maxParticipants = 15

    val canSave = participants.isNotEmpty() && eventName.isNotBlank()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_new_event)) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                if (canSave) {
                    // TODO: Save to database
                    navigator.navigate(EventsOverviewScreenDestination) // TODO: EventDetailScreen?
                }
            }, backgroundColor = if (canSave) MaterialTheme.colors.primary else Color.LightGray,
                contentColor = if (canSave) MaterialTheme.colors.onPrimary else Color.White
            ) {
                Icon(Icons.Filled.Save, contentDescription = stringResource(R.string.save_event))
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()

                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = eventName,
                onValueChange = { eventName = it },
                label = { Text("Event Name") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
            )
            DropdownMenuCurrencies(
                currencies = availableCurrencies,
                selectedCurrency = selectedCurrency,
                onCurrencySelected = { selectedCurrency = it }
            )
            ParticipantsInput(
                participants = participants,
                onParticipantsChange = { participants = it },
                maxParticipants = maxParticipants,
                keyboardController = keyboardController
            )
        }
    }
}

@Composable
fun DropdownMenuCurrencies(
    currencies: List<String>,
    selectedCurrency: String,
    onCurrencySelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = selectedCurrency,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = { expanded = true })
                .padding(16.dp)
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            currencies.forEach { currency ->
                DropdownMenuItem(onClick = {
                    onCurrencySelected(currency)
                    expanded = false
                }) {
                    Text(text = currency)
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ParticipantsInput(
    participants: List<String>,
    onParticipantsChange: (List<String>) -> Unit,
    maxParticipants: Int,
    keyboardController: SoftwareKeyboardController?
) {
    var newParticipant by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth()) {
        participants.forEachIndexed { index, participant ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = participant,
                    onValueChange = { updated ->
                        onParticipantsChange(
                            participants.toMutableList().apply { set(index, updated) })
                    },
                    label = { Text("Participant ${index + 1}") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
                )
                if (participants.size > 1) {
                    IconButton(onClick = {
                        onParticipantsChange(participants.toMutableList().apply { removeAt(index) })
                    }) {
                        Icon(Icons.Default.Remove, contentDescription = "Remove")
                    }
                }
            }
        }
        if (participants.size < maxParticipants) {
            Button(onClick = {
                onParticipantsChange(participants + newParticipant)
                newParticipant = ""
            }) {
                Text("Add Participant")
            }
        }
    }
}
