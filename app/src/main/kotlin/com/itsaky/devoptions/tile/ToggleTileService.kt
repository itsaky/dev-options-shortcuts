package com.itsaky.devoptions.tile

import android.app.PendingIntent
import android.content.Intent
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.itsaky.devoptions.DevOption
import com.itsaky.devoptions.MainActivity
import com.itsaky.devoptions.grant.isPermissionGranted
import com.itsaky.devoptions.toggle.readToggle
import com.itsaky.devoptions.toggle.writeToggle

fun tileState(settingEnabled: Boolean, permissionGranted: Boolean): Int = when {
    !permissionGranted -> Tile.STATE_UNAVAILABLE
    settingEnabled -> Tile.STATE_ACTIVE
    else -> Tile.STATE_INACTIVE
}

fun wadbTileSubtitle(enabled: Boolean, address: String?): String? = when {
    !enabled -> null
    address != null -> address
    else -> "No network"
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
            val intent = Intent(this, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            val pending = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_IMMUTABLE,
            )
            startActivityAndCollapse(pending)
            return
        }
        try {
            val current = readToggle(contentResolver, option)
            writeToggle(contentResolver, option, !current)
        } catch (_: SecurityException) {
            val intent = Intent(this, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            val pending = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_IMMUTABLE,
            )
            startActivityAndCollapse(pending)
            return
        }
        updateTile()
    }

    protected open fun updateTile() {
        val tile = qsTile ?: return
        val granted = isPermissionGranted(this)
        val enabled = granted && readToggle(contentResolver, option)
        tile.state = tileState(settingEnabled = enabled, permissionGranted = granted)
        tile.updateTile()
    }
}
