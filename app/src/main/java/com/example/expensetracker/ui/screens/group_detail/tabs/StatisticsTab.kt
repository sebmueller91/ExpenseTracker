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
import androidx.compose.runtime.State
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.model.Currency
import com.example.expensetracker.model.Group
import com.example.expensetracker.model.Participant

@Composable
fun StatisticsTab(
    group: Group,
    eventCostFlow: State<Double>,
    individualSharesFlow: State<Map<Participant, Double>>,
    percentageSharesFlow: State<Map<Participant, Double>>,
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
                    String.format("%.2f", eventCostFlow.value).toDouble()
                }${group.currency.symbol}",
                style = MaterialTheme.typography.headlineSmall
            )
            Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f), modifier = Modifier.padding(horizontal = 36.dp, vertical = 24.dp))
        }
        item {
            Text("Expense distribution", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(20.dp))
        }
        item {
            PieChart(
                percentageSharesFlow = percentageSharesFlow
            )
        }
        item {
            BarChart(
                individualSharesFlow = individualSharesFlow,
                percentageSharesFlow = percentageSharesFlow,
                currency = group.currency,
                modifier = Modifier.padding(top = 10.dp)
            )
        }
    }
}

@Composable
private fun PieChart(
    percentageSharesFlow: State<Map<Participant, Double>>,
    outerRadius: Dp = 90.dp,
    chartBarWidth: Dp = 20.dp,
    animationDuration: Int = 1000,
    modifier: Modifier = Modifier
) {
    var animationFinished by rememberSaveable { mutableStateOf(false) }
    var lastValue = 0f

    val pieChartChunks =
        percentageSharesFlow.value.values.map { value -> ((value / 100.0) * 360.0).toFloat() }

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
    individualSharesFlow: State<Map<Participant, Double>>,
    percentageSharesFlow: State<Map<Participant, Double>>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        individualSharesFlow.value.entries.sortedByDescending { it.value }
            .forEachIndexed { index, entry ->
                BarChartItem(
                    participantName = entry.key.name,
                    percentage = "${
                        String.format("%.2f", entry.value).toDouble()
                    }${currency.symbol} (${
                        String.format(
                            "%.2f",
                            percentageSharesFlow.value[entry.key]
                        )
                    }%)",
                    color = getPieChartColor(index)
                )
            }
    }
}

@Composable
private fun BarChartItem(
    participantName: String,
    percentage: String, // TODO: Percentage + Amount?
    color: Color,
    height: Dp = 45.dp,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth().padding(vertical = 10.dp, horizontal = 40.dp),
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
                    text = percentage,
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
    Color(128, 203, 196, 255), // Aqua Green
    Color(255, 127, 80, 255),  // Coral Pink
    Color(159, 121, 238, 255), // Muted Violet
    Color(255, 179, 71, 255),  // Pastel Orange
    Color(111, 207, 151, 255), // Seafoam Green
    Color(112, 128, 144, 255), // Slate Gray
    Color(255, 218, 185, 255), // Peach
    Color(54, 117, 136, 255),  // Teal
    Color(230, 190, 255, 255), // Lavender
    Color(255, 253, 208, 255), // Soft Yellow
    Color(188, 143, 143, 255)  // Dusty Rose
)