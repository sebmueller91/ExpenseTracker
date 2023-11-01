package com.example.expensetracker.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
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

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.expense_tracker_main_logo),
            contentDescription = "App Logo",
            modifier = Modifier
                .size(300.dp)
                .padding(top = 24.dp),
            contentScale = ContentScale.Crop
        )
        LazyColumn(
            modifier = Modifier
                .background(MaterialTheme.colors.background)
                .padding(horizontal = 24.dp)
        ) {
            items(items = events) {
                EventCard(event = it)
            }
        }
    }
}

@Composable
private fun EventCard(
    event: Event,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    Card(
        elevation = 4.dp,
        modifier = modifier.padding(8.dp)
    ) {
        var expanded by remember { mutableStateOf(false) }
        Column(
            modifier = Modifier.animateContentSize(
                animationSpec = spring(
                    Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        ) {
            Row() {
                Text(event.name)
                ExpandCollapseButton(expanded = expanded, onClick = { expanded = !expanded })
            }
            if (expanded) {
                for (participant in event.participants) {
                    Text(participant.name)
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