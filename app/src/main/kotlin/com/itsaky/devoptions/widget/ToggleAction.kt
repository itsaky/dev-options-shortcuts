package com.itsaky.devoptions.widget

import android.content.Context
import android.content.Intent
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import com.itsaky.devoptions.DevOption
import com.itsaky.devoptions.MainActivity
import com.itsaky.devoptions.grant.isPermissionGranted
import com.itsaky.devoptions.net.copyWadbCommand
import com.itsaky.devoptions.net.discoverWadbAddress
import com.itsaky.devoptions.net.wadbConnectionAddress
import com.itsaky.devoptions.toggle.readToggle
import com.itsaky.devoptions.toggle.writeToggle

val OptionKeyParam = ActionParameters.Key<String>("option_key")

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

        // Show "Discovering..." immediately while NSD runs
        refreshAllWidgets(context, WadbAddressUpdate.Set(""))

        val address = discoverWadbAddress(context) ?: wadbConnectionAddress(context)
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
