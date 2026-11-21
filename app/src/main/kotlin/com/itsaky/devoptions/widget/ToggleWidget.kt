package com.itsaky.devoptions.widget

import android.content.Context
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
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
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.itsaky.devoptions.DevOption
import com.itsaky.devoptions.R
import com.itsaky.devoptions.net.wadbConnectionAddress
import com.itsaky.devoptions.toggle.readToggle
import com.itsaky.devoptions.widget.theme.widgetColors

private val SMALL_SIZE = DpSize(40.dp, 40.dp)
private val MEDIUM_SIZE = DpSize(100.dp, 40.dp)
private val LARGE_SIZE = DpSize(180.dp, 80.dp)

abstract class ToggleWidget : GlanceAppWidget() {

    protected abstract val option: DevOption

    override val sizeMode = SizeMode.Responsive(setOf(SMALL_SIZE, MEDIUM_SIZE, LARGE_SIZE))

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val enabled = readToggle(context.contentResolver, option)
        val address = if (option == DevOption.WirelessDebugging && enabled) {
            wadbConnectionAddress(context)
        } else {
            null
        }

        provideContent {
            GlanceTheme(colors = widgetColors) {
                val ctx = LocalContext.current
                val currentSize = LocalSize.current
                val widgetSize = when {
                    currentSize.width < 100.dp -> WidgetSize.SMALL
                    currentSize.width < 180.dp -> WidgetSize.MEDIUM
                    else -> WidgetSize.LARGE
                }
                val content = widgetContentForSize(
                    ctx, widgetSize, option, enabled, address,
                )
                val toggleAction = actionRunCallback<ToggleAction>(
                    actionParametersOf(OptionKeyParam to option.key),
                )
                val iconTint = ColorFilter.tint(
                    if (enabled) GlanceTheme.colors.primary
                    else GlanceTheme.colors.onSurfaceVariant,
                )

                Box(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .cornerRadius(16.dp)
                        .background(GlanceTheme.colors.surface)
                        .clickable(toggleAction),
                    contentAlignment = Alignment.Center,
                ) {
                    when (widgetSize) {
                        WidgetSize.SMALL -> SmallWidget(option, iconTint)
                        WidgetSize.MEDIUM -> MediumWidget(ctx, option, enabled, iconTint, content)
                        WidgetSize.LARGE -> LargeWidget(ctx, option, enabled, iconTint, content)
                    }
                }
            }
        }
    }
}

@Composable
private fun SmallWidget(option: DevOption, iconTint: ColorFilter) {
    Image(
        provider = ImageProvider(option.iconRes),
        contentDescription = null,
        modifier = GlanceModifier.size(28.dp),
        colorFilter = iconTint,
    )
}

@Composable
private fun MediumWidget(
    context: Context,
    option: DevOption,
    enabled: Boolean,
    iconTint: ColorFilter,
    content: WidgetContent,
) {
    Column(
        modifier = GlanceModifier.padding(horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            provider = ImageProvider(option.iconRes),
            contentDescription = null,
            modifier = GlanceModifier.size(22.dp),
            colorFilter = iconTint,
        )
        Spacer(GlanceModifier.height(4.dp))
        Text(
            text = context.getString(option.labelRes),
            style = TextStyle(
                color = GlanceTheme.colors.onSurface,
                fontWeight = FontWeight.Medium,
            ),
        )
        Text(
            text = if (enabled) {
                content.addressText ?: context.getString(R.string.toggle_on)
            } else {
                context.getString(R.string.toggle_off)
            },
            style = TextStyle(
                color = if (enabled) {
                    GlanceTheme.colors.primary
                } else {
                    GlanceTheme.colors.onSurfaceVariant
                },
            ),
        )
    }
}

@Composable
private fun LargeWidget(
    context: Context,
    option: DevOption,
    enabled: Boolean,
    iconTint: ColorFilter,
    content: WidgetContent,
) {
    Column(
        modifier = GlanceModifier.padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            provider = ImageProvider(option.iconRes),
            contentDescription = null,
            modifier = GlanceModifier.size(24.dp),
            colorFilter = iconTint,
        )
        Spacer(GlanceModifier.height(4.dp))
        Text(
            text = context.getString(option.labelRes),
            style = TextStyle(
                color = GlanceTheme.colors.onSurface,
                fontWeight = FontWeight.Medium,
            ),
        )
        Spacer(GlanceModifier.height(2.dp))
        Text(
            text = if (enabled) {
                content.addressText ?: context.getString(R.string.toggle_on)
            } else {
                context.getString(R.string.toggle_off)
            },
            style = TextStyle(
                color = if (enabled) {
                    GlanceTheme.colors.primary
                } else {
                    GlanceTheme.colors.onSurfaceVariant
                },
            ),
        )
        if (content.showDescription && content.descriptionText != null) {
            Spacer(GlanceModifier.height(2.dp))
            Text(
                text = content.descriptionText,
                style = TextStyle(color = GlanceTheme.colors.onSurfaceVariant),
            )
        }
    }
}
