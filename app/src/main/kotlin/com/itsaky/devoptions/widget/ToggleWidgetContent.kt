package com.itsaky.devoptions.widget

import android.content.Context
import com.itsaky.devoptions.DevOption
import com.itsaky.devoptions.R

enum class WidgetSize { SMALL, MEDIUM, LARGE }

data class WidgetContent(
    val showIcon: Boolean,
    val showLabel: Boolean,
    val showAddress: Boolean,
    val addressText: String? = null,
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
        )
        WidgetSize.MEDIUM -> WidgetContent(
            showIcon = true,
            showLabel = true,
            showAddress = isWadb,
            addressText = if (isWadb) (address ?: noNetwork) else null,
        )
        WidgetSize.LARGE -> WidgetContent(
            showIcon = true,
            showLabel = true,
            showAddress = isWadb,
            addressText = if (isWadb) (address ?: noNetwork) else null,
        )
    }
}
