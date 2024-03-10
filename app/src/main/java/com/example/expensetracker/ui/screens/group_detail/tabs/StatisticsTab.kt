package com.example.expensetracker.ui.screens.group_detail.tabs

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.model.Currency
import com.example.core.model.Participant
import com.example.expensetracker.ui.screens.group_detail.GroupDetailUiState
import com.example.expensetracker.ui.util.UiUtils
import timber.log.Timber

@Composable
fun StatisticsTab(
    uiState: GroupDetailUiState.Success,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                "Event costs: ${
                    UiUtils.formatMoneyAmount(
                        uiState.eventCosts,
                        uiState.group.currency,
                        LocalContext.current
                    )
                }",
                style = MaterialTheme.typography.headlineSmall
            )
            Divider(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                modifier = Modifier.padding(horizontal = 36.dp, vertical = 24.dp)
            )
        }
        item {
            Text("Expense distribution", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(20.dp))
        }
        item {
            if (uiState.individualShares.any { it.value != 0.0 }) {
                PieChart(
                    percentageShares = uiState.percentageShares
                )
            }
        }
        item {
            BarChart(
                individualShares = uiState.individualShares,
                percentageShares = uiState.percentageShares,
                currency = uiState.group.currency,
                modifier = Modifier.padding(top = 10.dp)
            )
        }
    }
}

@Composable
private fun PieChart(
    percentageShares: Map<Participant, Double>,
    modifier: Modifier = Modifier,
    outerRadius: Dp = 90.dp,
    chartBarWidth: Dp = 20.dp,
    animationDuration: Int = 1000
) {
    var animationFinished by rememberSaveable { mutableStateOf(false) }
    var lastValue = 0f

    val pieChartChunks =
        percentageShares.values.map { value -> ((value / 100.0) * 360.0).toFloat() }

    val animatedSize by animateFloatAsState(
        targetValue = if (animationFinished) outerRadius.value * 2f else 0f,
        animationSpec = tween(
            durationMillis = animationDuration,
            delayMillis = 0,
            easing = LinearOutSlowInEasing
        ), label = ""
    )

    val animatedRotation by animateFloatAsState(
        targetValue = if (animationFinished) 90f * 11f else 0f,
        animationSpec = tween(
            durationMillis = animationDuration,
            delayMillis = 0,
            easing = LinearOutSlowInEasing
        ), label = ""
    )

    LaunchedEffect(null) {
        animationFinished = true
    }

    Box(modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Box(
            modifier = modifier
                .size(animatedSize.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .size(outerRadius * 2f)
                    .rotate(animatedRotation)
            ) {
                pieChartChunks.forEachIndexed { index, value ->
                    drawArc(
                        getPieChartColor(index),
                        lastValue,
                        value,
                        useCenter = false,
                        style = Stroke(chartBarWidth.toPx(), cap = StrokeCap.Butt)
                    )
                    lastValue += value
                }
            }
        }
    }
}

@Composable
private fun BarChart(
    currency: Currency,
    individualShares: Map<Participant, Double>,
    percentageShares: Map<Participant, Double>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        individualShares.entries.sortedByDescending { it.value }
            .forEachIndexed { index, entry ->
                val percentage = percentageShares[entry.key] ?: run {
                    Timber.e("Could not retrieve percentage! This should not happen.")
                    0.0
                }

                BarChartItem(
                    participantName = entry.key.name,
                    amount = "${
                        UiUtils.formatMoneyAmount(
                            entry.value,
                            currency,
                            LocalContext.current
                        )
                    } (${UiUtils.formatPercentage(percentage)})",
                    color = getPieChartColor(index)
                )
            }
    }
}

@Composable
private fun BarChartItem(
    participantName: String,
    amount: String,
    color: Color,
    modifier: Modifier = Modifier,
    height: Dp = 45.dp
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 40.dp),
        color = Color.Transparent,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .background(color = color, shape = RoundedCornerShape(10.dp))
                    .size(height)
            )


            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier.padding(start = 15.dp),
                    text = participantName,
                    fontWeight = FontWeight.Normal,
                    fontSize = 18.sp,
                    color = Color.Black
                )

                Text(
                    modifier = Modifier.padding(start = 15.dp),
                    text = amount,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = Color.Black
                )
            }
        }
    }
}

private fun getPieChartColor(index: Int): Color {
    val offset =
        index / pieChartColors.size // Use offset to avoid having scenarios where last and first element are the same
    return pieChartColors[(index + offset) % pieChartColors.size]
}

private val pieChartColors = listOf(
    Color(173, 216, 230, 255), // Light Blue
    Color(255, 127, 80, 255),  // Coral Pink
    Color(128, 203, 196, 255), // Aqua Green
    Color(159, 121, 238, 255), // Muted Violet
    Color(255, 179, 71, 255),  // Pastel Orange
    Color(111, 207, 151, 255), // Sea foam Green
    Color(112, 128, 144, 255), // Slate Gray
    Color(255, 218, 185, 255), // Peach
    Color(54, 117, 136, 255),  // Teal
    Color(230, 190, 255, 255), // Lavender
    Color(255, 253, 208, 255), // Soft Yellow
    Color(188, 143, 143, 255)  // Dusty Rose
)