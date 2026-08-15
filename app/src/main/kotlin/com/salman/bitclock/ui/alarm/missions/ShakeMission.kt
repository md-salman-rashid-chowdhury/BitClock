package com.salman.bitclock.ui.alarm.missions

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.salman.bitclock.utils.ShakeDetector

@Composable
fun ShakeMission(
    difficulty: Int,
    onComplete: () -> Unit
) {
    val context = LocalContext.current
    val totalShakes = difficulty * 15
    var shakesRemaining by remember { mutableIntStateOf(totalShakes) }

    val shakeDetector = remember {
        ShakeDetector(context) {
            if (shakesRemaining > 0) {
                shakesRemaining--
            }
        }
    }

    DisposableEffect(Unit) {
        shakeDetector.start(threshold = 600f + (difficulty * 100))
        onDispose {
            shakeDetector.stop()
        }
    }

    LaunchedEffect(shakesRemaining) {
        if (shakesRemaining <= 0) {
            onComplete()
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "shake")
    val rotation by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(100, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotation"
    )

    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Shake to Dismiss",
            style = MaterialTheme.typography.titleLarge
        )

        Box(
            modifier = Modifier
                .size(200.dp)
                .graphicsLayer(rotationZ = if (shakesRemaining > 0) rotation else 0f),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Smartphone,
                contentDescription = null,
                modifier = Modifier.size(120.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Text(
            text = "$shakesRemaining",
            fontSize = 64.sp,
            style = MaterialTheme.typography.displayLarge
        )

        LinearProgressIndicator(
            progress = { (totalShakes - shakesRemaining).toFloat() / totalShakes },
            modifier = Modifier.fillMaxWidth().height(12.dp)
        )
    }
}
