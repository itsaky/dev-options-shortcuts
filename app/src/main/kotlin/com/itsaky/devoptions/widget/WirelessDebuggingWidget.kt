package com.itsaky.devoptions.widget

import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.itsaky.devoptions.DevOption

class WirelessDebuggingWidget : ToggleWidget() {
    override val option = DevOption.WirelessDebugging
}

class WirelessDebuggingWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = WirelessDebuggingWidget()
}
