package com.example.expensetracker.ui.components

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.Composable

@Composable
fun RoundFloatingActionButton(
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    FloatingActionButton(onClick = onClick, shape = CircleShape) {
        content()
    }
}