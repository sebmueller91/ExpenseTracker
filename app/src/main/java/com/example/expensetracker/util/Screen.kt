package com.example.expensetracker.util

// TODO: Can this class be removed?
sealed class Screen(val route: String) {
    object EventsOverviewScreen: Screen("events_overview_screen")
    object EventDetailScreen: Screen("event_detail_screen")
}
