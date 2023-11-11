package com.example.expensetracker.util

// TODO: Can this class be removed?
sealed class Screen(val route: String) {
    object GroupOverviewScreen: Screen("events_overview_screen")
    object GroupDetailScreen: Screen("event_detail_screen")
}
