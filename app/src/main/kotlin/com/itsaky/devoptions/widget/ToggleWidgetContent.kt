package com.itsaky.devoptions.widget

import com.itsaky.devoptions.DevOption

enum class WidgetSize { SMALL, MEDIUM, LARGE }

data class WidgetContent(
    val showIcon: Boolean,
    val showLabel: Boolean,
    val showAddress: Boolean,
    val showDescription: Boolean,
    val addressText: String? = null,
    val descriptionText: String? = null,
)

fun widgetContentForSize(
    size: WidgetSize,
    option: DevOption,
    enabled: Boolean,
    address: String?,
): WidgetContent {
    val isWadb = option == DevOption.WirelessDebugging && enabled
    return when (size) {
        WidgetSize.SMALL -> WidgetContent(
            showIcon = true,
            showLabel = false,
            showAddress = false,
            showDescription = false,
        )
        WidgetSize.MEDIUM -> WidgetContent(
            showIcon = true,
            showLabel = true,
            showAddress = isWadb,
            showDescription = false,
            addressText = if (isWadb) (address ?: "No network") else null,
        )
        WidgetSize.LARGE -> WidgetContent(
            showIcon = true,
            showLabel = true,
            showAddress = isWadb,
            showDescription = isWadb && address != null,
            addressText = if (isWadb) (address ?: "No network") else null,
            descriptionText = if (isWadb && address != null) "adb connect $address" else null,
        )
    }
}
