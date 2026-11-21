package com.itsaky.devoptions.tile

import com.itsaky.devoptions.DevOption
import com.itsaky.devoptions.net.wadbConnectionAddress
import com.itsaky.devoptions.toggle.readToggle

class WirelessDebuggingTileService : ToggleTileService() {
    override val option = DevOption.WirelessDebugging

    override fun updateTile() {
        super.updateTile()
        val tile = qsTile ?: return
        val enabled = readToggle(contentResolver, option)
        val address = if (enabled) wadbConnectionAddress(contentResolver) else null
        tile.subtitle = wadbTileSubtitle(this, enabled = enabled, address = address)
        tile.updateTile()
    }
}
