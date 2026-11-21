package com.itsaky.devoptions.net

import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentResolver
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.os.Build
import android.provider.Settings
import java.io.IOException
import java.net.Inet4Address
import java.net.InetSocketAddress
import java.net.NetworkInterface
import java.net.ServerSocket
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull

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

private fun ipFromNetwork(cm: ConnectivityManager, network: android.net.Network): String? {
    val props = cm.getLinkProperties(network) ?: return null
    return props.linkAddresses
        .map { it.address }
        .filterIsInstance<Inet4Address>()
        .firstOrNull { !it.isLoopbackAddress }
        ?.hostAddress
}

private fun connectivityManagerIpAddress(context: Context): String? {
    val cm = context.getSystemService(ConnectivityManager::class.java) ?: return null
    @Suppress("DEPRECATION")
    val wifiNetwork = cm.allNetworks.firstOrNull { network ->
        cm.getNetworkCapabilities(network)
            ?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
    }
    if (wifiNetwork != null) {
        ipFromNetwork(cm, wifiNetwork)?.let { return it }
    }
    val active = cm.activeNetwork ?: return null
    return ipFromNetwork(cm, active)
}

fun deviceIpAddress(context: Context): String? =
    connectivityManagerIpAddress(context) ?: deviceIpAddress()

private const val WADB_SERVICE_TYPE = "_adb-tls-connect._tcp"
private const val NSD_TIMEOUT_MS = 5000L

private val nsdManagers = ConcurrentHashMap<String, NsdManager>()

private fun nsdManagerFor(context: Context, serviceType: String): NsdManager =
    nsdManagers.computeIfAbsent(serviceType) {
        context.applicationContext
            .createAttributionContext(null)
            .getSystemService(NsdManager::class.java)
    }

private fun hostFromServiceInfo(si: NsdServiceInfo): String? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        si.hostAddresses.filterIsInstance<Inet4Address>().firstOrNull()?.hostAddress
    } else {
        @Suppress("DEPRECATION")
        si.host?.hostAddress
    }
}

private fun resolveNsdService(
    nsd: NsdManager,
    serviceInfo: NsdServiceInfo,
    onResult: (host: String?, port: Int) -> Unit,
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        nsd.registerServiceInfoCallback(
            serviceInfo,
            Executors.newSingleThreadExecutor(),
            object : NsdManager.ServiceInfoCallback {
                override fun onServiceInfoCallbackRegistrationFailed(errorCode: Int) {
                    onResult(null, -1)
                }
                override fun onServiceInfoCallbackUnregistered() {}
                override fun onServiceLost() { onResult(null, -1) }
                override fun onServiceUpdated(si: NsdServiceInfo) {
                    nsd.unregisterServiceInfoCallback(this)
                    onResult(hostFromServiceInfo(si), si.port)
                }
            },
        )
    } else {
        @Suppress("DEPRECATION")
        nsd.resolveService(serviceInfo, object : NsdManager.ResolveListener {
            override fun onResolveFailed(si: NsdServiceInfo, errorCode: Int) {
                onResult(null, -1)
            }
            override fun onServiceResolved(si: NsdServiceInfo) {
                onResult(hostFromServiceInfo(si), si.port)
            }
        })
    }
}

private fun isLocalAddress(host: String): Boolean {
    val interfaces = try {
        NetworkInterface.getNetworkInterfaces()
    } catch (_: Exception) {
        return false
    }
    return interfaces.asSequence().any { iface ->
        iface.inetAddresses.asSequence().any { it.hostAddress == host }
    }
}

private fun isPortInUse(port: Int): Boolean =
    try {
        ServerSocket().use { it.bind(InetSocketAddress("127.0.0.1", port), 1) }
        false
    } catch (_: IOException) {
        true
    }

suspend fun discoverWadbAddress(context: Context): String? {
    val nsd = nsdManagerFor(context, WADB_SERVICE_TYPE)
    return withTimeoutOrNull(NSD_TIMEOUT_MS) {
        suspendCancellableCoroutine { cont ->
            val listener = object : NsdManager.DiscoveryListener {
                override fun onDiscoveryStarted(serviceType: String) {}
                override fun onDiscoveryStopped(serviceType: String) {}
                override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
                    if (cont.isActive) cont.resume(null)
                }
                override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {}
                override fun onServiceLost(serviceInfo: NsdServiceInfo) {}

                override fun onServiceFound(serviceInfo: NsdServiceInfo) {
                    resolveNsdService(nsd, serviceInfo) { host, port ->
                        if (!cont.isActive) return@resolveNsdService
                        if (host != null && port > 0 &&
                            isLocalAddress(host) && isPortInUse(port)
                        ) {
                            cont.resume("$host:$port")
                        }
                    }
                }
            }

            nsd.discoverServices(WADB_SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, listener)
            cont.invokeOnCancellation {
                try {
                    nsd.stopServiceDiscovery(listener)
                } catch (_: IllegalArgumentException) {
                }
            }
        }
    }
}

fun wadbPort(resolver: ContentResolver): Int {
    val global = Settings.Global.getInt(resolver, "adb_wifi_port", -1)
    if (global > 0) return global
    return Settings.Secure.getInt(resolver, "adb_wifi_port", -1)
}

fun copyWadbCommand(context: Context, address: String) {
    val clipboard = context.getSystemService(ClipboardManager::class.java) ?: return
    clipboard.setPrimaryClip(ClipData.newPlainText("adb connect", "adb connect $address"))
}

suspend fun resolveWadbAddress(context: Context): String? {
    val settingsPort = wadbPort(context.contentResolver)
    val nsdAddress = discoverWadbAddress(context)
    if (nsdAddress != null && settingsPort > 0) {
        val ip = nsdAddress.substringBefore(':')
        return "$ip:$settingsPort"
    }
    if (nsdAddress != null) return nsdAddress
    return wadbConnectionAddress(context)
}

fun wadbConnectionAddress(context: Context): String? {
    val ip = deviceIpAddress(context) ?: return null
    val port = wadbPort(context.contentResolver)
    if (port < 0) return ip
    return "$ip:$port"
}
