package com.example.expensetracker.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.expensetracker.ui.screens.destinations.EventDetailScreenDestination
import com.example.expensetracker.ui.screens.destinations.EventsOverviewScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun EventDetailScreen(
    navigator: DestinationsNavigator
) {
    Column() {
        Text("Event details!")
        Button(onClick = {
            navigator.navigate(EventsOverviewScreenDestination())
        }) {
            Text("Go to overview")
        }
    }
}