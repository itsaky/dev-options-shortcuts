package com.itsaky.devoptions

import android.os.Build

enum class DevOption(val key: String, val minSdk: Int) {
    DeveloperOptions("development_settings_enabled", Build.VERSION_CODES.O),
    UsbDebugging("adb_enabled", Build.VERSION_CODES.O),
    WirelessDebugging("adb_wifi_enabled", Build.VERSION_CODES.R),
}
