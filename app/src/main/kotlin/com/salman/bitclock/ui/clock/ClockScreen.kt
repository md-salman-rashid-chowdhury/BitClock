package com.salman.bitclock.ui.clock

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.salman.bitclock.data.models.WorldClock
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ClockScreen(
    viewModel: ClockViewModel = hiltViewModel()
) {
    var currentTime by remember { mutableStateOf(Calendar.getInstance().time) }
    val worldClocks by viewModel.worldClocks.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = Calendar.getInstance().time
            delay(1000)
        }
    }

    val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    val dateFormat = SimpleDateFormat("EEE, MMM d", Locale.getDefault())

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add World Clock")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = timeFormat.format(currentTime),
                style = MaterialTheme.typography.displayLarge.copy(fontSize = 72.sp),
                fontWeight = FontWeight.Light
            )
            Text(
                text = dateFormat.format(currentTime),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.secondary
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "World Clocks",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
            }
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            
            if (worldClocks.isEmpty()) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text("No world clocks added", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(worldClocks) { clock ->
                        WorldClockItem(
                            clock = clock,
                            currentTime = currentTime,
                            onDelete = { viewModel.removeWorldClock(clock) }
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp)
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        TimeZonePickerDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { id ->
                val tz = TimeZone.getTimeZone(id)
                viewModel.addWorldClock(id, tz.displayName)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun WorldClockItem(
    clock: WorldClock,
    currentTime: Date,
    onDelete: () -> Unit
) {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    sdf.timeZone = TimeZone.getTimeZone(clock.timeZoneId)
    
    val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
    dayFormat.timeZone = TimeZone.getTimeZone(clock.timeZoneId)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = clock.timeZoneId.split("/").last().replace("_", " "),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "${dayFormat.format(currentTime)}, ${clock.label}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = sdf.format(currentTime),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun TimeZonePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    val ids = remember { TimeZone.getAvailableIDs().sorted() }
    var searchQuery by remember { mutableStateOf("") }
    val filteredIds = remember(searchQuery) {
        ids.filter { it.contains(searchQuery, ignoreCase = true) }.take(50)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Time Zone") },
        text = {
            Column {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn(modifier = Modifier.height(300.dp)) {
                    items(filteredIds) { id ->
                        TextButton(
                            onClick = { onConfirm(id) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(id, modifier = Modifier.fillMaxWidth())
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
