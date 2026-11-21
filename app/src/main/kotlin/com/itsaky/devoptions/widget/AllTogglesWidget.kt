package com.itsaky.devoptions.widget

import android.content.Context
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.itsaky.devoptions.DevOption
import com.itsaky.devoptions.R
import com.itsaky.devoptions.net.discoverWadbAddress
import com.itsaky.devoptions.net.wadbConnectionAddress
import com.itsaky.devoptions.toggle.readToggle
import com.itsaky.devoptions.widget.theme.widgetColors

class AllTogglesWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val resolver = context.contentResolver
        val states = DevOption.entries
            .filter { it.minSdk <= Build.VERSION.SDK_INT }
            .map { it to readToggle(resolver, it) }

        val wadbEnabled = states.any { it.first == DevOption.WirelessDebugging && it.second }
        val wadbAddress = if (wadbEnabled) {
            discoverWadbAddress(context) ?: wadbConnectionAddress(context)
        } else {
            null
        }

        provideContent {
            GlanceTheme(colors = widgetColors) {
                val ctx = LocalContext.current
                Column(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .cornerRadius(16.dp)
                        .background(GlanceTheme.colors.surface)
                        .padding(12.dp),
                ) {
                    Text(
                        text = ctx.getString(R.string.app_name),
                        style = TextStyle(
                            color = GlanceTheme.colors.onSurface,
                            fontWeight = FontWeight.Bold,
                        ),
                    )
                    Spacer(GlanceModifier.height(8.dp))
                    states.forEachIndexed { index, (option, enabled) ->
                        val address = if (option == DevOption.WirelessDebugging && enabled) {
                            wadbAddress
                        } else {
                            null
                        }
                        ToggleRow(ctx, option, enabled, address)
                        if (index < states.lastIndex) {
                            Spacer(GlanceModifier.height(6.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ToggleRow(
    context: Context,
    option: DevOption,
    enabled: Boolean,
    address: String?,
) {
    val toggleAction = actionRunCallback<ToggleAction>(
        actionParametersOf(OptionKeyParam to option.key),
    )
    val iconTint = ColorFilter.tint(
        if (enabled) GlanceTheme.colors.primary
        else GlanceTheme.colors.onSurfaceVariant,
    )

    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .clickable(toggleAction),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            provider = ImageProvider(option.iconRes),
            contentDescription = null,
            modifier = GlanceModifier.size(20.dp),
            colorFilter = iconTint,
        )
        Spacer(GlanceModifier.width(8.dp))
        Column(modifier = GlanceModifier.defaultWeight()) {
            Text(
                text = context.getString(option.labelRes),
                style = TextStyle(
                    color = GlanceTheme.colors.onSurface,
                    fontWeight = FontWeight.Medium,
                ),
            )
            if (address != null) {
                Text(
                    text = address,
                    style = TextStyle(color = GlanceTheme.colors.primary),
                )
            }
        }
        Text(
            text = if (enabled) {
                context.getString(R.string.toggle_on)
            } else {
                context.getString(R.string.toggle_off)
            },
            style = TextStyle(
                color = if (enabled) {
                    GlanceTheme.colors.primary
                } else {
                    GlanceTheme.colors.onSurfaceVariant
                },
                fontWeight = FontWeight.Medium,
            ),
        )
    }
}

class AllTogglesWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = AllTogglesWidget()
}
