package com.itsaky.devoptions.widget

import androidx.test.core.app.ApplicationProvider
import com.itsaky.devoptions.DevOption
import com.itsaky.devoptions.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class WidgetContentTest {

    private val context = ApplicationProvider.getApplicationContext<android.app.Application>()

    @Test
    fun smallSizeShowsOnlyIconAndState() {
        val content = widgetContentForSize(context, WidgetSize.SMALL, DevOption.UsbDebugging, enabled = true, address = null)
        assertTrue(content.showIcon)
        assertFalse(content.showLabel)
        assertFalse(content.showAddress)
    }

    @Test
    fun mediumSizeShowsLabelAndState() {
        val content = widgetContentForSize(context, WidgetSize.MEDIUM, DevOption.UsbDebugging, enabled = true, address = null)
        assertTrue(content.showIcon)
        assertTrue(content.showLabel)
        assertFalse(content.showAddress)
    }

    @Test
    fun mediumWadbShowsAddress() {
        val content = widgetContentForSize(context, WidgetSize.MEDIUM, DevOption.WirelessDebugging, enabled = true, address = "192.168.1.42:5555")
        assertTrue(content.showAddress)
        assertEquals("192.168.1.42:5555", content.addressText)
    }

    @Test
    fun largeSizeShowsAllDetails() {
        val content = widgetContentForSize(context, WidgetSize.LARGE, DevOption.WirelessDebugging, enabled = true, address = "192.168.1.42:5555")
        assertTrue(content.showIcon)
        assertTrue(content.showLabel)
        assertTrue(content.showAddress)
        assertTrue(content.showDescription)
    }

    @Test
    fun largeWadbShowsConnectDescription() {
        val content = widgetContentForSize(context, WidgetSize.LARGE, DevOption.WirelessDebugging, enabled = true, address = "192.168.1.42:5555")
        assertNotNull(content.descriptionText)
    }

    @Test
    fun mediumWadbShowsNoNetworkWhenAddressNull() {
        val content = widgetContentForSize(context, WidgetSize.MEDIUM, DevOption.WirelessDebugging, enabled = true, address = null)
        assertTrue(content.showAddress)
        assertEquals(context.getString(R.string.no_network), content.addressText)
    }

    @Test
    fun largeWadbShowsNoNetworkWhenAddressNull() {
        val content = widgetContentForSize(context, WidgetSize.LARGE, DevOption.WirelessDebugging, enabled = true, address = null)
        assertTrue(content.showAddress)
        assertEquals(context.getString(R.string.no_network), content.addressText)
    }
}
