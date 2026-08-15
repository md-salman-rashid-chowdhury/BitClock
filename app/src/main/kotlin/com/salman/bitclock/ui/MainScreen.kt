package com.salman.bitclock.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.salman.bitclock.ui.alarm.AlarmDetailScreen
import com.salman.bitclock.ui.alarm.AlarmScreen
import com.salman.bitclock.ui.clock.ClockScreen
import com.salman.bitclock.ui.diagnostic.DiagnosticScreen
import com.salman.bitclock.ui.profiles.ProfileManagementScreen
import com.salman.bitclock.ui.sleep.SleepSummaryScreen
import com.salman.bitclock.ui.stopwatch.StopwatchScreen
import com.salman.bitclock.ui.timer.TimerScreen

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val items = listOf(
        Screen.Alarm,
        Screen.Clock,
        Screen.Sleep,
        Screen.Profiles,
        Screen.Timer,
        Screen.Stopwatch
    )

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            
            if (items.any { it.route == currentRoute }) {
                NavigationBar {
                    items.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = null) },
                            label = { Text(screen.title) },
                            selected = currentRoute == screen.route,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Alarm.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Alarm.route) { 
                AlarmScreen(
                    onAddAlarm = { navController.navigate("alarm_detail") },
                    onEditAlarm = { id -> navController.navigate("alarm_detail?id=$id") },
                    onOpenDiagnostic = { navController.navigate(Screen.Diagnostic.route) }
                ) 
            }
            composable(
                route = "alarm_detail?id={id}",
                arguments = listOf(navArgument("id") { 
                    type = NavType.IntType
                    defaultValue = -1
                })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("id") ?: -1
                AlarmDetailScreen(
                    alarmId = if (id == -1) null else id,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Clock.route) { ClockScreen() }
            composable(Screen.Sleep.route) { SleepSummaryScreen() }
            composable(Screen.Profiles.route) { ProfileManagementScreen() }
            composable(Screen.Timer.route) { TimerScreen() }
            composable(Screen.Stopwatch.route) { StopwatchScreen() }
            composable(Screen.Diagnostic.route) {
                DiagnosticScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Alarm : Screen("alarm", "Alarm", Icons.Default.Alarm)
    object Clock : Screen("clock", "Clock", Icons.Default.Schedule)
    object Sleep : Screen("sleep", "Sleep", Icons.Default.Bed)
    object Profiles : Screen("profiles", "Profiles", Icons.Default.Groups)
    object Timer : Screen("timer", "Timer", Icons.Default.Timer)
    object Stopwatch : Screen("stopwatch", "Stopwatch", Icons.Default.History)
    object Diagnostic : Screen("diagnostic", "Diagnostic", Icons.Default.BugReport)
}
