package com.example.expensetracker.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.example.expensetracker.ui.screens.destinations.GroupOverviewScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun GroupDetailScreen(
    navigator: DestinationsNavigator
) {
    Column() {
        Text("Group details!")
        Button(onClick = {
            navigator.navigate(GroupOverviewScreenDestination())
        }) {
            Text("Go to overview")
        }
    }
}