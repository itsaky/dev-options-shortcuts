package com.itsaky.devtoggles.widget

import android.content.Context
import android.content.Intent
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import com.itsaky.devtoggles.DevOption
import com.itsaky.devtoggles.MainActivity
import com.itsaky.devtoggles.grant.isPermissionGranted
import com.itsaky.devtoggles.net.copyWadbCommand
import com.itsaky.devtoggles.net.resolveWadbAddress
import com.itsaky.devtoggles.toggle.readToggle
import com.itsaky.devtoggles.toggle.writeToggle
import kotlinx.coroutines.delay

val OptionKeyParam = ActionParameters.Key<String>("option_key")
private const val ADBD_STARTUP_DELAY_MS = 1500L

class ToggleAction : ActionCallback {

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters,
    ) {
        val key = parameters[OptionKeyParam] ?: return
        val option = DevOption.entries.firstOrNull { it.key == key } ?: return

        if (!isPermissionGranted(context)) {
            launchMainActivity(context)
            return
        }

        val current: Boolean
        try {
            current = readToggle(context.contentResolver, option)
            writeToggle(context.contentResolver, option, !current)
        } catch (_: SecurityException) {
            launchMainActivity(context)
            return
        }

        val enabling = !current

        if (option == DevOption.WirelessDebugging) {
            handleWadbToggle(context, enabling)
        } else {
            refreshAllWidgets(context)
        }
    }

    private suspend fun handleWadbToggle(context: Context, enabling: Boolean) {
        if (!enabling) {
            refreshAllWidgets(context, WadbAddressUpdate.Clear)
            return
        }

        refreshAllWidgets(context, WadbAddressUpdate.Set(""))

        delay(ADBD_STARTUP_DELAY_MS)

        val address = resolveWadbAddress(context)
        if (address != null) {
            copyWadbCommand(context, address)
        }

        refreshAllWidgets(context, WadbAddressUpdate.Set(address ?: ""))
    }

    private fun launchMainActivity(context: Context) {
        context.startActivity(
            Intent(context, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
        )
    }
}
