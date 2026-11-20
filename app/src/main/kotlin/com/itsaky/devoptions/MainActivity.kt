package com.itsaky.devoptions

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.itsaky.devoptions.grant.isPermissionGranted
import com.itsaky.devoptions.ui.MainScreen
import com.itsaky.devoptions.ui.theme.DevOptionsTheme

class MainActivity : ComponentActivity() {

    private var permissionGranted by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionGranted = isPermissionGranted(this)
        setContent {
            DevOptionsTheme {
                MainScreen(
                    permissionGranted = permissionGranted,
                    onGranted = { permissionGranted = isPermissionGranted(this) },
                    onPermissionLost = { permissionGranted = false },
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        permissionGranted = isPermissionGranted(this)
    }
}
