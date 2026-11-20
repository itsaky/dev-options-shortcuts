package com.itsaky.devoptions.net

import android.content.ContentResolver
import android.provider.Settings
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.Collections
import java.util.Enumeration

@RunWith(RobolectricTestRunner::class)
class DeviceAddressTest {

    private lateinit var resolver: ContentResolver

    @Before
    fun setUp() {
        resolver = ApplicationProvider.getApplicationContext<android.app.Application>().contentResolver
    }

    @Test
    fun deviceIpAddressReturnsIpv4OfNonLoopbackInterface() {
        val addresses = listOf(InterfaceEntry("wlan0", "192.168.1.42", isLoopback = false))
        assertEquals("192.168.1.42", deviceIpAddress { addresses })
    }

    @Test
    fun deviceIpAddressReturnsNullWhenNoNonLoopbackInterface() {
        val addresses = listOf(InterfaceEntry("lo", "127.0.0.1", isLoopback = true))
        assertNull(deviceIpAddress { addresses })
    }

    @Test
    fun deviceIpAddressPrefersWlanInterface() {
        val addresses = listOf(
            InterfaceEntry("rndis0", "192.168.42.1", isLoopback = false),
            InterfaceEntry("wlan0", "192.168.1.42", isLoopback = false),
        )
        assertEquals("192.168.1.42", deviceIpAddress { addresses })
    }

    @Test
    fun wadbPortReadsFromSettingsGlobal() {
        Settings.Global.putInt(resolver, "adb_wifi_port", 5555)
        assertEquals(5555, wadbPort(resolver))
    }
}
