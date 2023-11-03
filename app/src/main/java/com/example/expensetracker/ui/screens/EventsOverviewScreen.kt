package com.example.expensetracker.ui.screens

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.expensetracker.R
import com.example.expensetracker.model.CURRENCIES
import com.example.expensetracker.model.Event
import com.example.expensetracker.model.Participant
import com.example.expensetracker.ui.screens.destinations.AddEventScreenDestination
import com.example.expensetracker.ui.screens.destinations.EventDetailScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@RootNavGraph(start = true)
@Destination
@Composable
fun EventsOverviewScreen(
    navigator: DestinationsNavigator
) {
    val events = listOf(
        Event(
            currency = CURRENCIES.get(0), name = "Summer Breeze",
            participants = listOf(Participant("Dennis"), Participant("Johnny")),
            transactions = listOf()
        ),
        Event(
            currency = CURRENCIES.get(0), name = "Rock im Park",
            participants = listOf(Participant("Dennis"), Participant("Johnny")),
            transactions = listOf()
        )
    )

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navigator.navigate(AddEventScreenDestination)
            }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Event")
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
                items(items = events) {
                    EventCard(event = it, navigator = navigator)
                }
            }
        }
    }
}

@Composable
private fun EventCard(
    event: Event,
    navigator: DestinationsNavigator,
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
                .clickable(onClick = { navigator.navigate(EventDetailScreenDestination) })
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(event.name, style = MaterialTheme.typography.h6)
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
                        for (participant in event.participants) {
                            Text(
                                participant.name,
                                style = MaterialTheme.typography.subtitle2,
                                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp)
                            )
                        }
                    }
                    Text(
                        "Transactions: ${event.transactions.size}",
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