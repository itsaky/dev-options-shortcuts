package com.itsaky.devtoggles.widget

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.itsaky.devtoggles.DevOption
import com.itsaky.devtoggles.net.resolveWadbAddress
import com.itsaky.devtoggles.toggle.readToggle

val RevisionKey = intPreferencesKey("revision")
val WadbAddressKey = stringPreferencesKey("wadb_address")

sealed interface WadbAddressUpdate {
    data object Keep : WadbAddressUpdate
    data object Clear : WadbAddressUpdate
    data class Set(val address: String) : WadbAddressUpdate
}

suspend fun refreshAllWidgets(
    context: Context,
    wadbUpdate: WadbAddressUpdate = WadbAddressUpdate.Keep,
) {
    refreshWidgetType(context, AllTogglesWidget(), wadbUpdate)
    refreshWidgetType(context, DevOptionsWidget(), wadbUpdate)
    refreshWidgetType(context, UsbDebuggingWidget(), wadbUpdate)
    refreshWidgetType(context, WirelessDebuggingWidget(), wadbUpdate)
}

private suspend inline fun <reified W : GlanceAppWidget> refreshWidgetType(
    context: Context,
    widget: W,
    wadbUpdate: WadbAddressUpdate,
) {
    val manager = GlanceAppWidgetManager(context)
    for (glanceId in manager.getGlanceIds(W::class.java)) {
        updateAppWidgetState(context, glanceId) { prefs ->
            prefs[RevisionKey] = (prefs[RevisionKey] ?: 0) + 1
            when (wadbUpdate) {
                WadbAddressUpdate.Keep -> {}
                WadbAddressUpdate.Clear -> prefs.remove(WadbAddressKey)
                is WadbAddressUpdate.Set -> prefs[WadbAddressKey] = wadbUpdate.address
            }
        }
        widget.update(context, glanceId)
    }
}

suspend fun populateWadbAddressIfNeeded(context: Context, id: GlanceId) {
    val state = getAppWidgetState(context, PreferencesGlanceStateDefinition, id)
    if (state.contains(WadbAddressKey)) return

    val wadbEnabled = readToggle(context.contentResolver, DevOption.WirelessDebugging)
    if (!wadbEnabled) return

    val address = resolveWadbAddress(context)
    updateAppWidgetState(context, id) { prefs ->
        prefs[WadbAddressKey] = address ?: ""
    }
}
