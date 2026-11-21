package com.itsaky.devoptions.tile

import androidx.annotation.RequiresApi
import com.itsaky.devoptions.DevOption
import com.itsaky.devoptions.net.wadbConnectionAddress
import com.itsaky.devoptions.toggle.readToggle

@RequiresApi(30)
class WirelessDebuggingTileService : ToggleTileService() {
    override val option = DevOption.WirelessDebugging

    override fun updateTile() {
        super.updateTile()
        val tile = qsTile ?: return
        val enabled = readToggle(contentResolver, option)
        val address = if (enabled) wadbConnectionAddress(this) else null
        tile.subtitle = wadbTileSubtitle(this, enabled = enabled, address = address)
        tile.updateTile()
    }
}
