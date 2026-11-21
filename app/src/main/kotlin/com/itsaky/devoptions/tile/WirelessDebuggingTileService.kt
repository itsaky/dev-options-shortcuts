package com.itsaky.devoptions.tile

import androidx.annotation.RequiresApi
import com.itsaky.devoptions.DevOption
import com.itsaky.devoptions.net.copyWadbCommand
import com.itsaky.devoptions.net.discoverWadbAddress
import com.itsaky.devoptions.net.wadbConnectionAddress
import com.itsaky.devoptions.toggle.readToggle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

@RequiresApi(30)
class WirelessDebuggingTileService : ToggleTileService() {
    override val option = DevOption.WirelessDebugging

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    override fun updateTile() {
        val tile = qsTile ?: return
        applyTileState(tile)
        val enabled = readToggle(contentResolver, option)
        val settingsAddress = if (enabled) wadbConnectionAddress(this) else null
        tile.subtitle = wadbTileSubtitle(this, enabled = enabled, address = settingsAddress)
        tile.updateTile()

        if (enabled) {
            if (settingsAddress != null) {
                copyWadbCommand(this, settingsAddress)
            }
            scope.launch {
                val nsdAddress = discoverWadbAddress(this@WirelessDebuggingTileService)
                if (nsdAddress != null) {
                    copyWadbCommand(this@WirelessDebuggingTileService, nsdAddress)
                    val t = qsTile ?: return@launch
                    t.subtitle = nsdAddress
                    t.updateTile()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
