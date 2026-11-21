package com.itsaky.devtoggles.ui

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable

@Composable
fun MainScreen(
    permissionGranted: Boolean,
    onGranted: () -> Unit,
    onPermissionLost: () -> Unit = {},
) {
    Scaffold { contentPadding ->
        if (permissionGranted) {
            ToggleScreen(contentPadding = contentPadding, onPermissionLost = onPermissionLost)
        } else {
            GrantScreen(contentPadding = contentPadding, onGranted = onGranted)
        }
    }
}
