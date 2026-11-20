package com.itsaky.devoptions.widget

import android.content.Context
import android.content.Intent
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.updateAll
import com.itsaky.devoptions.DevOption
import com.itsaky.devoptions.MainActivity
import com.itsaky.devoptions.grant.isPermissionGranted
import com.itsaky.devoptions.toggle.readToggle
import com.itsaky.devoptions.toggle.writeToggle

val OptionKeyParam = ActionParameters.Key<String>("option_key")

class ToggleAction : ActionCallback {

    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        val key = parameters[OptionKeyParam] ?: return
        val option = DevOption.entries.firstOrNull { it.key == key } ?: return

        if (!isPermissionGranted(context)) {
            context.startActivity(
                Intent(context, MainActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
            )
            return
        }

        try {
            val current = readToggle(context.contentResolver, option)
            writeToggle(context.contentResolver, option, !current)
        } catch (_: SecurityException) {
            context.startActivity(
                Intent(context, MainActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
            )
            return
        }

        when (option) {
            DevOption.DeveloperOptions -> DevOptionsWidget().updateAll(context)
            DevOption.UsbDebugging -> UsbDebuggingWidget().updateAll(context)
            DevOption.WirelessDebugging -> WirelessDebuggingWidget().updateAll(context)
        }
    }
}
