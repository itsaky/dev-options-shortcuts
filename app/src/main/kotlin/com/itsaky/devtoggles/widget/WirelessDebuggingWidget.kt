package com.itsaky.devtoggles.widget

import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.itsaky.devtoggles.DevOption

class WirelessDebuggingWidget : ToggleWidget() {
    override val option = DevOption.WirelessDebugging
}

class WirelessDebuggingWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = WirelessDebuggingWidget()
}
