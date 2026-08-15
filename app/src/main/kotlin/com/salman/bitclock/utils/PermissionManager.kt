package com.salman.bitclock.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PermissionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    sealed class PermissionState {
        object Granted : PermissionState()
        object Denied : PermissionState()
        object RationaleRequired : PermissionState()
    }

    data class PermissionInfo(
        val permission: String,
        val title: String,
        val rationale: String,
        val isRequired: Boolean = true
    )

    private val _permissions = MutableStateFlow<Map<String, PermissionState>>(emptyMap())
    val permissions: StateFlow<Map<String, PermissionState>> = _permissions.asStateFlow()

    /**
     * Refreshes the internal state for a specific permission.
     */
    fun refreshPermission(permission: String) {
        val isGranted = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        val newState = if (isGranted) PermissionState.Granted else PermissionState.Denied
        _permissions.value = _permissions.value.toMutableMap().apply {
            put(permission, newState)
        }
    }

    /**
     * Returns true if the given permission is currently granted.
     */
    fun isGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        /**
         * List of permissions that the app might request, with associated rationale.
         */
        val PERMISSION_DEFINITIONS = buildList {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(PermissionInfo(
                    Manifest.permission.POST_NOTIFICATIONS,
                    "Notifications",
                    "Required to show alarm alerts when the app is in the background."
                ))
            }
            add(PermissionInfo(
                Manifest.permission.VIBRATE,
                "Vibration",
                "Used to provide tactile feedback for alarms and timers."
            ))
            add(PermissionInfo(
                Manifest.permission.CAMERA,
                "Camera",
                "Required to scan barcodes or take photos for alarm missions."
            ))
        }
    }
}
