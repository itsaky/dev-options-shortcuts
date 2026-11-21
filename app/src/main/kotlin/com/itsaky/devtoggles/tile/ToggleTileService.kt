package com.itsaky.devtoggles.tile

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.itsaky.devtoggles.DevOption
import com.itsaky.devtoggles.MainActivity
import com.itsaky.devtoggles.R
import com.itsaky.devtoggles.grant.isPermissionGranted
import com.itsaky.devtoggles.toggle.readToggle
import com.itsaky.devtoggles.toggle.writeToggle

fun tileState(settingEnabled: Boolean, permissionGranted: Boolean): Int = when {
    !permissionGranted -> Tile.STATE_UNAVAILABLE
    settingEnabled -> Tile.STATE_ACTIVE
    else -> Tile.STATE_INACTIVE
}

fun wadbTileSubtitle(context: Context, enabled: Boolean, address: String?): String? = when {
    !enabled -> null
    address != null -> address
    else -> context.getString(R.string.no_network)
}

abstract class ToggleTileService : TileService() {

    protected abstract val option: DevOption

    override fun onStartListening() {
        super.onStartListening()
        updateTile()
    }

    override fun onClick() {
        super.onClick()
        if (!isPermissionGranted(this)) {
            collapseAndOpenMain()
            return
        }
        try {
            val current = readToggle(contentResolver, option)
            writeToggle(contentResolver, option, !current)
        } catch (_: SecurityException) {
            collapseAndOpenMain()
            return
        }
        updateTile()
    }

    private fun collapseAndOpenMain() {
        val intent = Intent(this, MainActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            val pending = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_IMMUTABLE,
            )
            startActivityAndCollapse(pending)
        } else {
            @Suppress("DEPRECATION")
            @SuppressLint("StartActivityAndCollapseDeprecated")
            startActivityAndCollapse(intent)
        }
    }

    protected open fun updateTile() {
        val tile = qsTile ?: return
        applyTileState(tile)
        tile.updateTile()
    }

    protected fun applyTileState(tile: Tile) {
        val granted = isPermissionGranted(this)
        val enabled = granted && readToggle(contentResolver, option)
        tile.state = tileState(settingEnabled = enabled, permissionGranted = granted)
    }
}
