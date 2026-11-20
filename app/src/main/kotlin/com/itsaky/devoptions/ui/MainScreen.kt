package com.itsaky.devoptions.ui

import androidx.compose.runtime.Composable

@Composable
fun MainScreen(
    permissionGranted: Boolean,
    onGranted: () -> Unit,
    onPermissionLost: () -> Unit = {},
) {
    if (permissionGranted) {
        ToggleScreen(onPermissionLost = onPermissionLost)
    } else {
        GrantScreen(onGranted = onGranted)
    }
}
