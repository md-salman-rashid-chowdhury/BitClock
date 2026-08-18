package com.salman.bitclock.ui.sleep

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.salman.bitclock.data.models.SleepSession
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SleepSummaryScreen(
    viewModel: SleepViewModel = hiltViewModel()
) {
    val sessions by viewModel.sleepSessions.collectAsState()

    Scaffold(
        topBar = {
            LargeTopAppBar(title = { Text("Sleep Analytics") })
        }
    ) { padding ->
        if (sessions.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("No sleep data yet. Track your sleep!")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(sessions) { session ->
                    SleepSessionCard(session)
                }
            }
        }
    }
}

@Composable
fun SleepSessionCard(session: SleepSession) {
    val locale = androidx.compose.ui.platform.LocalConfiguration.current.locales[0]
    val dateFormat = remember(locale) { SimpleDateFormat("MMM dd, HH:mm", locale) }
    val durationHrs = (session.endTime - session.startTime) / (1000 * 60 * 60f)

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = dateFormat.format(Date(session.startTime)), style = MaterialTheme.typography.titleMedium)
            Text(
                text = "Duration: ${String.format(locale, "%.1f", durationHrs)} hrs | Score: ${session.wakeQualityScore}",
                style = MaterialTheme.typography.bodySmall
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            SleepMovementGraph(session.movementData)
        }
    }
}

@Composable
fun SleepMovementGraph(movementData: String) {
    val dataPoints = movementData.split(";").mapNotNull { 
        val parts = it.split(",")
        if (parts.size == 2) parts[1].toFloat() else null
    }

    if (dataPoints.size < 2) {
        Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = androidx.compose.ui.Alignment.Center) {
            Text("Insufficient movement data", style = MaterialTheme.typography.labelSmall)
        }
        return
    }

    Canvas(modifier = Modifier.fillMaxWidth().height(100.dp)) {
        val path = Path()
        val stepX = size.width / (dataPoints.size - 1)
        val maxY = (dataPoints.maxOrNull() ?: 1f).coerceAtLeast(0.1f)
        
        dataPoints.forEachIndexed { index, value ->
            val x = index * stepX
            val y = size.height - (value / maxY * size.height)
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        
        drawPath(path, color = Color(0xFF00BCD4), style = Stroke(width = 2.dp.toPx()))
    }
}
