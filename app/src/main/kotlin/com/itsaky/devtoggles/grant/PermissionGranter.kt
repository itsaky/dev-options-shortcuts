package com.itsaky.devtoggles.grant

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

fun isPermissionGranted(context: Context): Boolean =
    ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.WRITE_SECURE_SETTINGS,
    ) == PackageManager.PERMISSION_GRANTED

fun adbGrantCommand(packageName: String): String =
    "adb shell pm grant $packageName android.permission.WRITE_SECURE_SETTINGS"

interface PermissionGranter {
    suspend fun grant(context: Context): Result<Unit>
}
