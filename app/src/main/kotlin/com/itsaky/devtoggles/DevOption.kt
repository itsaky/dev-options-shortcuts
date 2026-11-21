package com.itsaky.devtoggles

import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

enum class DevOption(
    val key: String,
    val minSdk: Int,
    @StringRes val labelRes: Int,
    @DrawableRes val iconRes: Int,
) {
    DeveloperOptions("development_settings_enabled", Build.VERSION_CODES.O, R.string.label_developer_options, R.drawable.ic_dev_options),
    UsbDebugging("adb_enabled", Build.VERSION_CODES.O, R.string.label_usb_debugging, R.drawable.ic_usb),
    WirelessDebugging("adb_wifi_enabled", Build.VERSION_CODES.R, R.string.label_wireless_debugging, R.drawable.ic_wifi),
}
