package com.example.expensetracker.ui.components

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.expensetracker.ui.theme.ExpenseTrackerTheme

@Composable
fun RoundFloatingActionButton(
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    FloatingActionButton(
        onClick = onClick,
        shape = CircleShape,
        containerColor = MaterialTheme.colorScheme.primary
    ) {
        content()
    }
}

@Preview
@Composable
fun RoundFloatingActionButtonPreview() {
    ExpenseTrackerTheme {
        RoundFloatingActionButton(onClick = {}) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null)
        }
    }
}