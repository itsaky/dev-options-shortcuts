package com.itsaky.devoptions.widget

import android.content.Context
import com.itsaky.devoptions.DevOption
import com.itsaky.devoptions.R

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
    context: Context,
    size: WidgetSize,
    option: DevOption,
    enabled: Boolean,
    address: String?,
): WidgetContent {
    val isWadb = option == DevOption.WirelessDebugging && enabled
    val noNetwork = context.getString(R.string.no_network)
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
            addressText = if (isWadb) (address ?: noNetwork) else null,
        )
        WidgetSize.LARGE -> WidgetContent(
            showIcon = true,
            showLabel = true,
            showAddress = isWadb,
            showDescription = isWadb && address != null,
            addressText = if (isWadb) (address ?: noNetwork) else null,
            descriptionText = if (isWadb && address != null) {
                context.getString(R.string.adb_connect_address, address)
            } else {
                null
            },
        )
    }
}
