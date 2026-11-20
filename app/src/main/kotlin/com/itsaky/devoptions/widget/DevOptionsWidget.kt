package com.itsaky.devoptions.widget

import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.itsaky.devoptions.DevOption

class DevOptionsWidget : ToggleWidget() {
    override val option = DevOption.DeveloperOptions
}

class DevOptionsWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = DevOptionsWidget()
}
