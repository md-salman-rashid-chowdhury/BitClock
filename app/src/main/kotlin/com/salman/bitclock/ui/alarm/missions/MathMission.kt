package com.salman.bitclock.ui.alarm.missions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

@Composable
fun MathMission(
    difficulty: Int,
    onComplete: () -> Unit
) {
    var num1 by remember { mutableIntStateOf(generateNum(difficulty)) }
    var num2 by remember { mutableIntStateOf(generateNum(difficulty)) }
    var problemsSolved by remember { mutableIntStateOf(0) }
    var answer by remember { mutableStateOf("") }
    
    val totalProblems = when (difficulty) {
        1 -> 1
        2 -> 3
        else -> 5
    }

    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Solve to Dismiss ($problemsSolved/$totalProblems)",
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            text = "$num1 + $num2 = ?",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = answer,
            onValueChange = { if (it.length <= 5) answer = it },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = { Text("Answer") },
            singleLine = true
        )

        Button(
            onClick = {
                val correct = num1 + num2
                if (answer.toIntOrNull() == correct) {
                    problemsSolved++
                    if (problemsSolved >= totalProblems) {
                        onComplete()
                    } else {
                        num1 = generateNum(difficulty)
                        num2 = generateNum(difficulty)
                        answer = ""
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = answer.isNotEmpty()
        ) {
            Text("Submit")
        }
    }
}

private fun generateNum(difficulty: Int): Int {
    return when (difficulty) {
        1 -> Random.nextInt(1, 10)
        2 -> Random.nextInt(10, 50)
        else -> Random.nextInt(50, 150)
    }
}
