package com.itsaky.devoptions.tile

import android.service.quicksettings.Tile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TileStateTest {

    @Test
    fun tileStateIsActiveWhenSettingEnabled() {
        assertEquals(Tile.STATE_ACTIVE, tileState(settingEnabled = true, permissionGranted = true))
    }

    @Test
    fun tileStateIsInactiveWhenSettingDisabled() {
        assertEquals(Tile.STATE_INACTIVE, tileState(settingEnabled = false, permissionGranted = true))
    }

    @Test
    fun tileStateIsUnavailableWhenPermissionMissing() {
        assertEquals(Tile.STATE_UNAVAILABLE, tileState(settingEnabled = false, permissionGranted = false))
    }

    @Test
    fun wadbTileSubtitleShowsAddressWhenEnabled() {
        assertEquals("192.168.1.42:5555", wadbTileSubtitle(enabled = true, address = "192.168.1.42:5555"))
    }

    @Test
    fun wadbTileSubtitleShowsNoNetworkWhenAddressNull() {
        assertEquals("No network", wadbTileSubtitle(enabled = true, address = null))
    }

    @Test
    fun wadbTileSubtitleIsNullWhenDisabled() {
        assertNull(wadbTileSubtitle(enabled = false, address = "192.168.1.42:5555"))
    }
}
