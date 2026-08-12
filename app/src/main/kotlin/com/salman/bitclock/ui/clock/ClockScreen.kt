package com.salman.bitclock.ui.clock

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ClockScreen() {
    var currentTime by remember { mutableStateOf(Calendar.getInstance().time) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = Calendar.getInstance().time
            kotlinx.coroutines.delay(1000)
        }
    }

    val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    val dateFormat = SimpleDateFormat("EEE, MMM d", Locale.getDefault())

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = timeFormat.format(currentTime),
                style = MaterialTheme.typography.displayLarge
            )
            Text(
                text = dateFormat.format(currentTime),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}
