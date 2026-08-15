package com.salman.bitclock.ui.diagnostic

import android.app.NotificationManager
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salman.bitclock.utils.AlarmScheduler
import com.salman.bitclock.utils.BatteryOptimizationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiagnosticViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val alarmScheduler: AlarmScheduler
) : ViewModel() {

    data class DiagnosticState(
        val isExactAlarmPermissionGranted: Boolean = false,
        val isNotificationPermissionGranted: Boolean = false,
        val isIgnoringBatteryOptimizations: Boolean = false,
        val isCameraPermissionGranted: Boolean = false,
    )

    private val _state = MutableStateFlow(DiagnosticState())
    val state = _state.asStateFlow()

    init {
        refreshState()
    }

    fun refreshState() {
        viewModelScope.launch {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            
            _state.value = DiagnosticState(
                isExactAlarmPermissionGranted = alarmScheduler.canScheduleExactAlarms(),
                isNotificationPermissionGranted = notificationManager.areNotificationsEnabled(),
                isIgnoringBatteryOptimizations = BatteryOptimizationHelper.isIgnoringBatteryOptimizations(context),
                isCameraPermissionGranted = ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) == android.content.pm.PackageManager.PERMISSION_GRANTED
            )
        }
    }
}
