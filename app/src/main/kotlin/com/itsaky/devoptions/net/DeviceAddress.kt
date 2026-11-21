package com.itsaky.devoptions.net

import android.content.ContentResolver
import android.content.Context
import android.net.ConnectivityManager
import android.provider.Settings
import java.net.Inet4Address
import java.net.NetworkInterface

data class InterfaceEntry(
    val name: String,
    val hostAddress: String,
    val isLoopback: Boolean,
)

private fun networkInterfaces(): List<InterfaceEntry> = buildList {
    val interfaces = try {
        NetworkInterface.getNetworkInterfaces()
    } catch (_: Exception) {
        null
    } ?: return@buildList
    for (iface in interfaces) {
        for (addr in iface.inetAddresses) {
            if (addr is Inet4Address) {
                val host = addr.hostAddress ?: continue
                add(InterfaceEntry(iface.name, host, iface.isLoopback))
            }
        }
    }
}

fun deviceIpAddress(interfaces: () -> List<InterfaceEntry> = ::networkInterfaces): String? {
    val entries = interfaces().filter { !it.isLoopback }
    val wlan = entries.firstOrNull { it.name.startsWith("wlan") }
    return (wlan ?: entries.firstOrNull())?.hostAddress
}

private fun connectivityManagerIpAddress(context: Context): String? {
    val cm = context.getSystemService(ConnectivityManager::class.java) ?: return null
    val network = cm.activeNetwork ?: return null
    val props = cm.getLinkProperties(network) ?: return null
    return props.linkAddresses
        .map { it.address }
        .filterIsInstance<Inet4Address>()
        .firstOrNull { !it.isLoopbackAddress }
        ?.hostAddress
}

fun deviceIpAddress(context: Context): String? =
    connectivityManagerIpAddress(context) ?: deviceIpAddress()

fun wadbPort(resolver: ContentResolver): Int {
    val global = Settings.Global.getInt(resolver, "adb_wifi_port", -1)
    if (global > 0) return global
    return Settings.Secure.getInt(resolver, "adb_wifi_port", -1)
}

fun wadbConnectionAddress(context: Context): String? {
    val ip = deviceIpAddress(context) ?: return null
    val port = wadbPort(context.contentResolver)
    if (port < 0) return ip
    return "$ip:$port"
}
