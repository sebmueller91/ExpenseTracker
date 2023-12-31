package com.example.expensetracker.ui.screens.group_detail.tabs

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
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
    LazyColumn(modifier = modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
        item {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(
                    "The event cost ${
                        String.format("%.2f", eventCostFlow.value).toDouble()
                    }${group.currency.symbol}",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }
        item {
            Spacer(Modifier.height(30.dp))
        }
        item {
            PieChart(
                percentageSharesFlow = percentageSharesFlow
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
    var animationFinished by remember { mutableStateOf(false) }
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

private fun getPieChartColor(index: Int): Color {
    return pieChartColors[index % pieChartColors.size]
}

private val pieChartColors = listOf(
    Color.Yellow,
    Color.Red,
    Color.Green,
    Color.Blue,
    Color.Magenta,
    Color.Cyan,
    Color(126, 209, 156, 255),
    Color(255, 123, 104)
)