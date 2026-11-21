package com.itsaky.devoptions

import android.os.Build
import androidx.annotation.StringRes

enum class DevOption(val key: String, val minSdk: Int, @StringRes val labelRes: Int) {
    DeveloperOptions("development_settings_enabled", Build.VERSION_CODES.O, R.string.label_developer_options),
    UsbDebugging("adb_enabled", Build.VERSION_CODES.O, R.string.label_usb_debugging),
    WirelessDebugging("adb_wifi_enabled", Build.VERSION_CODES.R, R.string.label_wireless_debugging),
}
