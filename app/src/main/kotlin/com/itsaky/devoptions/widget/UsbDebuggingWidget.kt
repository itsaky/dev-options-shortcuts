package com.itsaky.devoptions.widget

import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.itsaky.devoptions.DevOption

class UsbDebuggingWidget : ToggleWidget() {
    override val option = DevOption.UsbDebugging
}

class UsbDebuggingWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = UsbDebuggingWidget()
}
