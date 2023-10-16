package com.example.expensetracker.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.expensetracker.R
import com.example.expensetracker.model.CURRENCIES
import com.example.expensetracker.model.Event
import com.example.expensetracker.model.Participant
import com.example.expensetracker.ui.screens.destinations.EventDetailScreenDestination
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@RootNavGraph(start = true)
@Destination
@Composable
fun EventsOverviewScreen(
    navigator: DestinationsNavigator
) {
    Button(onClick = {
        navigator.navigate(EventDetailScreenDestination())
    }) {
        Text("Go to details")
    }

    val events = listOf(
        Event(
            currency = CURRENCIES.get(0), name = "Summer Breeze",
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
            modifier = Modifier.background(MaterialTheme.colors.background)
        ) {
            items(items = events) {
                EventCard(eventName = it.name)
            }
        }
    }
}

@Composable
private fun EventCard(
    eventName: String,
    modifier: Modifier = Modifier
) {
    Card(
        elevation = 4.dp,
        modifier = modifier.padding(8.dp)
    ) {
        Text(eventName)
    }
}