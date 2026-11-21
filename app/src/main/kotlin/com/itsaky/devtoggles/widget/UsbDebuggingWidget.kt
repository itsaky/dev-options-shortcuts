package com.itsaky.devtoggles.widget

import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.itsaky.devtoggles.DevOption

class UsbDebuggingWidget : ToggleWidget() {
    override val option = DevOption.UsbDebugging
}

class UsbDebuggingWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = UsbDebuggingWidget()
}
