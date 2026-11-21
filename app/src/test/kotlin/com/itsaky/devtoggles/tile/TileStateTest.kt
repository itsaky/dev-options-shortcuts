package com.itsaky.devtoggles.tile

import android.service.quicksettings.Tile
import androidx.test.core.app.ApplicationProvider
import com.itsaky.devtoggles.R
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
        val context = ApplicationProvider.getApplicationContext<android.app.Application>()
        assertEquals("192.168.1.42:5555", wadbTileSubtitle(context, enabled = true, address = "192.168.1.42:5555"))
    }

    @Test
    fun wadbTileSubtitleShowsNoNetworkWhenAddressNull() {
        val context = ApplicationProvider.getApplicationContext<android.app.Application>()
        val expected = context.getString(R.string.no_network)
        assertEquals(expected, wadbTileSubtitle(context, enabled = true, address = null))
    }

    @Test
    fun wadbTileSubtitleIsNullWhenDisabled() {
        val context = ApplicationProvider.getApplicationContext<android.app.Application>()
        assertNull(wadbTileSubtitle(context, enabled = false, address = "192.168.1.42:5555"))
    }
}
