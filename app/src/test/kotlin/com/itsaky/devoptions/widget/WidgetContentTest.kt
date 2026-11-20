package com.itsaky.devoptions.widget

import com.itsaky.devoptions.DevOption
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class WidgetContentTest {

    @Test
    fun smallSizeShowsOnlyIconAndState() {
        val content = widgetContentForSize(WidgetSize.SMALL, DevOption.UsbDebugging, enabled = true, address = null)
        assertTrue(content.showIcon)
        assertFalse(content.showLabel)
        assertFalse(content.showAddress)
    }

    @Test
    fun mediumSizeShowsLabelAndState() {
        val content = widgetContentForSize(WidgetSize.MEDIUM, DevOption.UsbDebugging, enabled = true, address = null)
        assertTrue(content.showIcon)
        assertTrue(content.showLabel)
        assertFalse(content.showAddress)
    }

    @Test
    fun mediumWadbShowsAddress() {
        val content = widgetContentForSize(WidgetSize.MEDIUM, DevOption.WirelessDebugging, enabled = true, address = "192.168.1.42:5555")
        assertTrue(content.showAddress)
        assertEquals("192.168.1.42:5555", content.addressText)
    }

    @Test
    fun largeSizeShowsAllDetails() {
        val content = widgetContentForSize(WidgetSize.LARGE, DevOption.WirelessDebugging, enabled = true, address = "192.168.1.42:5555")
        assertTrue(content.showIcon)
        assertTrue(content.showLabel)
        assertTrue(content.showAddress)
        assertTrue(content.showDescription)
    }

    @Test
    fun largeWadbShowsConnectDescription() {
        val content = widgetContentForSize(WidgetSize.LARGE, DevOption.WirelessDebugging, enabled = true, address = "192.168.1.42:5555")
        assertNotNull(content.descriptionText)
    }
}
