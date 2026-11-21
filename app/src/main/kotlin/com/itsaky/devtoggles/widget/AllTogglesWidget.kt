package com.itsaky.devtoggles.widget

import android.content.Context
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.components.TitleBar
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.itemsIndexed
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
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
import com.itsaky.devtoggles.DevOption
import com.itsaky.devtoggles.R
import com.itsaky.devtoggles.toggle.readToggle
import com.itsaky.devtoggles.widget.theme.widgetColors

class AllTogglesWidget : GlanceAppWidget() {

    override val sizeMode: SizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val options = DevOption.entries.filter { it.minSdk <= Build.VERSION.SDK_INT }

        populateWadbAddressIfNeeded(context, id)

        provideContent {
            @Suppress("UNUSED_VARIABLE")
            val revision = currentState(RevisionKey)
            val wadbAddress = currentState(WadbAddressKey)

            GlanceTheme(colors = widgetColors) {
                val ctx = LocalContext.current
                val resolver = ctx.contentResolver
                val states = options.associateWith { readToggle(resolver, it) }

                val showTitleBar = LocalSize.current.height >= 120.dp

                Scaffold(
                    backgroundColor = GlanceTheme.colors.widgetBackground,
                    modifier = GlanceModifier.padding(
                        top = if (showTitleBar) 0.dp else 12.dp,
                        bottom = 12.dp,
                    ),
                    titleBar = if (showTitleBar) {
                        { WidgetTitleBar(ctx) }
                    } else {
                        null
                    },
                ) {
                    Box(
                        modifier = GlanceModifier
                            .fillMaxSize()
                            .cornerRadius(16.dp),
                    ) {
                        LazyColumn {
                            itemsIndexed(
                                items = options,
                                itemId = { _, option -> option.ordinal.toLong() },
                            ) { index, option ->
                                val enabled = states[option] ?: false
                                Column(modifier = GlanceModifier.fillMaxWidth()) {
                                    ToggleListItem(ctx, option, enabled, wadbAddress)
                                    if (index < options.lastIndex) {
                                        Spacer(GlanceModifier.height(4.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WidgetTitleBar(context: Context) {
    TitleBar(
        startIcon = ImageProvider(R.drawable.ic_dev_options),
        title = context.getString(R.string.app_name),
        iconColor = GlanceTheme.colors.primary,
        textColor = GlanceTheme.colors.onSurface,
    )
}

@Composable
private fun ToggleListItem(
    context: Context,
    option: DevOption,
    enabled: Boolean,
    wadbAddress: String?,
) {
    val toggleAction = actionRunCallback<ToggleAction>(
        actionParametersOf(OptionKeyParam to option.key),
    )
    val backgroundColor = if (enabled) {
        GlanceTheme.colors.primary
    } else {
        GlanceTheme.colors.secondaryContainer
    }
    val iconTint = ColorFilter.tint(
        if (enabled) GlanceTheme.colors.onPrimary
        else GlanceTheme.colors.onSurfaceVariant,
    )
    val titleColor = if (enabled) {
        GlanceTheme.colors.onPrimary
    } else {
        GlanceTheme.colors.onSurface
    }
    val subtitleColor = if (enabled) {
        GlanceTheme.colors.onPrimary
    } else {
        GlanceTheme.colors.onSurfaceVariant
    }

    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .cornerRadius(16.dp)
            .background(backgroundColor)
            .clickable(toggleAction)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = GlanceModifier
                .size(48.dp)
                .cornerRadius(200.dp),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                provider = ImageProvider(option.iconRes),
                contentDescription = null,
                modifier = GlanceModifier.size(24.dp),
                colorFilter = iconTint,
            )
        }
        Spacer(GlanceModifier.width(4.dp))
        Column(modifier = GlanceModifier.defaultWeight()) {
            Text(
                text = context.getString(option.labelRes),
                style = TextStyle(
                    color = titleColor,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                ),
                maxLines = 1,
            )
            Text(
                text = subtitleText(context, option, enabled, wadbAddress),
                style = TextStyle(
                    color = subtitleColor,
                    fontSize = 12.sp,
                ),
                maxLines = 1,
            )
        }
    }
}

private fun subtitleText(
    context: Context,
    option: DevOption,
    enabled: Boolean,
    wadbAddress: String?,
): String {
    if (!enabled) return context.getString(R.string.toggle_off)
    if (option != DevOption.WirelessDebugging) return context.getString(R.string.toggle_on)
    return when {
        wadbAddress == null -> context.getString(R.string.toggle_on)
        wadbAddress.isEmpty() -> context.getString(R.string.wadb_discovering)
        else -> wadbAddress
    }
}

class AllTogglesWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = AllTogglesWidget()
}
