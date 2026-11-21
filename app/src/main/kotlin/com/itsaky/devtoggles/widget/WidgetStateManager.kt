package com.itsaky.devtoggles.widget

import android.content.Context
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import com.itsaky.devtoggles.DevOption
import com.itsaky.devtoggles.net.resolveWadbAddress
import com.itsaky.devtoggles.toggle.readToggle

val RevisionKey = intPreferencesKey("revision")

suspend fun populateWadbAddressIfNeeded(context: Context) {
    if (ToggleStateRepository.wadbAddress.value != null) return
    if (!readToggle(context.contentResolver, DevOption.WirelessDebugging)) return
    val address = resolveWadbAddress(context)
    ToggleStateRepository.setWadbAddress(address ?: "")
}

suspend fun refreshAllWidgets(context: Context) {
    refreshWidgetType(context, AllTogglesWidget())
    refreshWidgetType(context, DevOptionsWidget())
    refreshWidgetType(context, UsbDebuggingWidget())
    refreshWidgetType(context, WirelessDebuggingWidget())
}

private suspend inline fun <reified W : GlanceAppWidget> refreshWidgetType(
    context: Context,
    widget: W,
) {
    val manager = GlanceAppWidgetManager(context)
    for (glanceId in manager.getGlanceIds(W::class.java)) {
        updateAppWidgetState(context, glanceId) { prefs ->
            prefs[RevisionKey] = (prefs[RevisionKey] ?: 0) + 1
        }
        widget.update(context, glanceId)
    }
}
