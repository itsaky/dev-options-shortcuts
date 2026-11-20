package com.itsaky.devoptions.net

import android.content.ContentResolver
import android.provider.Settings
import java.net.Inet4Address
import java.net.NetworkInterface

data class InterfaceEntry(
    val name: String,
    val hostAddress: String,
    val isLoopback: Boolean,
)

private fun networkInterfaces(): List<InterfaceEntry> = buildList {
    val interfaces = NetworkInterface.getNetworkInterfaces() ?: return@buildList
    for (iface in interfaces) {
        for (addr in iface.inetAddresses) {
            if (addr is Inet4Address) {
                add(InterfaceEntry(iface.name, addr.hostAddress, iface.isLoopback))
            }
        }
    }
}

fun deviceIpAddress(interfaces: () -> List<InterfaceEntry> = ::networkInterfaces): String? {
    val entries = interfaces().filter { !it.isLoopback }
    val wlan = entries.firstOrNull { it.name.startsWith("wlan") }
    return (wlan ?: entries.firstOrNull())?.hostAddress
}

fun wadbPort(resolver: ContentResolver): Int =
    Settings.Global.getInt(resolver, "adb_wifi_port", -1)

fun wadbConnectionAddress(resolver: ContentResolver): String? {
    val ip = deviceIpAddress() ?: return null
    val port = wadbPort(resolver)
    if (port < 0) return null
    return "$ip:$port"
}
