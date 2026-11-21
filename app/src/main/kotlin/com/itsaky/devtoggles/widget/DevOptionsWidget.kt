package com.itsaky.devtoggles.widget

import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.itsaky.devtoggles.DevOption

class DevOptionsWidget : ToggleWidget() {
    override val option = DevOption.DeveloperOptions
}

class DevOptionsWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = DevOptionsWidget()
}
