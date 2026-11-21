package com.itsaky.devtoggles

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.itsaky.devtoggles.grant.isPermissionGranted
import com.itsaky.devtoggles.ui.MainScreen
import com.itsaky.devtoggles.ui.theme.DevOptionsTheme

class MainActivity : ComponentActivity() {

    private var permissionGranted by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
