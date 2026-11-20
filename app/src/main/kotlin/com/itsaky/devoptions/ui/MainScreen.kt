package com.itsaky.devoptions.ui

import androidx.compose.runtime.Composable

@Composable
fun MainScreen(permissionGranted: Boolean, onGranted: () -> Unit) {
    if (permissionGranted) {
        ToggleScreen()
    } else {
        GrantScreen(onGranted = onGranted)
    }
}
