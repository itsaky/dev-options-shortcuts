package com.itsaky.devoptions.widget

import android.content.Context
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
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
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

abstract class ToggleWidget : GlanceAppWidget() {

    protected abstract val option: DevOption

    override val sizeMode: SizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val nsdAddress = if (option == DevOption.WirelessDebugging) {
            discoverWadbAddress(context)
        } else {
            null
        }

        provideContent {
            @Suppress("UNUSED_VARIABLE")
            val revision = currentState(RevisionKey)

            GlanceTheme(colors = widgetColors) {
                val ctx = LocalContext.current
                val enabled = readToggle(ctx.contentResolver, option)
                val address = if (option == DevOption.WirelessDebugging && enabled) {
                    nsdAddress ?: wadbConnectionAddress(ctx)
                } else {
                    null
                }
                val toggleAction = actionRunCallback<ToggleAction>(
                    actionParametersOf(OptionKeyParam to option.key),
                )
                val iconTint = ColorFilter.tint(
                    if (enabled) GlanceTheme.colors.primary
                    else GlanceTheme.colors.onSurfaceVariant,
                )

                val currentSize = LocalSize.current
                val isSmall = currentSize.width < 100.dp
                val isMedium = currentSize.width < 180.dp

                Box(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .cornerRadius(16.dp)
                        .background(GlanceTheme.colors.widgetBackground)
                        .clickable(toggleAction),
                    contentAlignment = if (isSmall) {
                        Alignment.Center
                    } else {
                        Alignment.CenterStart
                    },
                ) {
                    if (isSmall) {
                        SmallContent(option, iconTint)
                    } else {
                        ExpandedContent(ctx, option, enabled, iconTint, address, isMedium)
                    }
                }
            }
        }
    }
}

@Composable
private fun SmallContent(option: DevOption, iconTint: ColorFilter) {
    Image(
        provider = ImageProvider(option.iconRes),
        contentDescription = null,
        modifier = GlanceModifier.size(28.dp),
        colorFilter = iconTint,
    )
}

@Composable
private fun ExpandedContent(
    context: Context,
    option: DevOption,
    enabled: Boolean,
    iconTint: ColorFilter,
    address: String?,
    compact: Boolean,
) {
    val iconSize = if (compact) 22.dp else 28.dp

    Row(
        modifier = GlanceModifier.padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            provider = ImageProvider(option.iconRes),
            contentDescription = null,
            modifier = GlanceModifier.size(iconSize),
            colorFilter = iconTint,
        )
        Spacer(GlanceModifier.width(if (compact) 8.dp else 10.dp))
        Column(modifier = GlanceModifier.defaultWeight()) {
            Text(
                text = context.getString(option.labelRes),
                style = TextStyle(
                    color = GlanceTheme.colors.onSurface,
                    fontWeight = FontWeight.Medium,
                    fontSize = if (compact) 14.sp else 16.sp,
                ),
            )
            Text(
                text = if (enabled) {
                    address ?: context.getString(R.string.toggle_on)
                } else {
                    context.getString(R.string.toggle_off)
                },
                style = TextStyle(
                    color = if (enabled) {
                        GlanceTheme.colors.primary
                    } else {
                        GlanceTheme.colors.onSurfaceVariant
                    },
                    fontSize = 12.sp,
                ),
            )
        }
    }
}
