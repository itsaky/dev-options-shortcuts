package com.itsaky.devoptions.net

import android.content.ContentResolver
import android.content.Context
import android.net.ConnectivityManager
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.os.Build
import android.provider.Settings
import java.net.Inet4Address
import java.net.NetworkInterface
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

private const val WADB_SERVICE_TYPE = "_adb-tls-connect._tcp"
private const val NSD_TIMEOUT_MS = 5000L

private fun addressFromServiceInfo(si: NsdServiceInfo): String? {
    val port = si.port
    if (port <= 0) return null
    val host = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        si.hostAddresses.filterIsInstance<Inet4Address>().firstOrNull()?.hostAddress
    } else {
        @Suppress("DEPRECATION")
        si.host?.hostAddress
    }
    return if (host != null) "$host:$port" else null
}

private fun resolveNsdService(
    nsd: NsdManager,
    serviceInfo: NsdServiceInfo,
    onResult: (String?) -> Unit,
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        nsd.registerServiceInfoCallback(
            serviceInfo,
            Executors.newSingleThreadExecutor(),
            object : NsdManager.ServiceInfoCallback {
                override fun onServiceInfoCallbackRegistrationFailed(errorCode: Int) {
                    onResult(null)
                }
                override fun onServiceInfoCallbackUnregistered() {}
                override fun onServiceLost() { onResult(null) }
                override fun onServiceUpdated(si: NsdServiceInfo) {
                    nsd.unregisterServiceInfoCallback(this)
                    onResult(addressFromServiceInfo(si))
                }
            },
        )
    } else {
        @Suppress("DEPRECATION")
        nsd.resolveService(serviceInfo, object : NsdManager.ResolveListener {
            override fun onResolveFailed(si: NsdServiceInfo, errorCode: Int) {
                onResult(null)
            }
            override fun onServiceResolved(si: NsdServiceInfo) {
                onResult(addressFromServiceInfo(si))
            }
        })
    }
}

suspend fun discoverWadbAddress(context: Context): String? {
    val nsd = context.getSystemService(NsdManager::class.java) ?: return null
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
                    resolveNsdService(nsd, serviceInfo) { address ->
                        if (cont.isActive) cont.resume(address)
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

fun wadbConnectionAddress(context: Context): String? {
    val ip = deviceIpAddress(context) ?: return null
    val port = wadbPort(context.contentResolver)
    if (port < 0) return ip
    return "$ip:$port"
}
