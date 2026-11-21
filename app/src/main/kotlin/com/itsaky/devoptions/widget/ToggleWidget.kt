package com.itsaky.devoptions.widget

import android.content.Context
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
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
        val resolver = context.contentResolver
        val enabled = readToggle(resolver, option)
        val address = if (option == DevOption.WirelessDebugging && enabled) {
            wadbConnectionAddress(resolver)
        } else {
            null
        }

        provideContent {
            GlanceTheme(colors = widgetColors) {
                val glanceContext = LocalContext.current
                val currentSize = LocalSize.current
                val widgetSize = when {
                    currentSize.width < 100.dp -> WidgetSize.SMALL
                    currentSize.width < 180.dp -> WidgetSize.MEDIUM
                    else -> WidgetSize.LARGE
                }
                val content = widgetContentForSize(
                    glanceContext, widgetSize, option, enabled, address,
                )
                val toggleAction = actionRunCallback<ToggleAction>(
                    actionParametersOf(OptionKeyParam to option.key),
                )

                Box(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .padding(8.dp)
                        .clickable(toggleAction),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (content.showIcon) {
                            Text(
                                text = if (enabled) {
                                    glanceContext.getString(R.string.toggle_on)
                                } else {
                                    glanceContext.getString(R.string.toggle_off)
                                },
                                style = TextStyle(
                                    color = GlanceTheme.colors.primary,
                                ),
                            )
                        }
                        if (content.showLabel) {
                            Text(
                                text = glanceContext.getString(option.labelRes),
                                style = TextStyle(
                                    color = GlanceTheme.colors.onSurface,
                                ),
                            )
                        }
                        if (content.showAddress && content.addressText != null) {
                            Text(
                                text = content.addressText,
                                style = TextStyle(
                                    color = GlanceTheme.colors.onSurfaceVariant,
                                ),
                            )
                        }
                        if (content.showDescription && content.descriptionText != null) {
                            Text(
                                text = content.descriptionText,
                                style = TextStyle(
                                    color = GlanceTheme.colors.onSurfaceVariant,
                                ),
                            )
                        }
                    }
                }
            }
        }
    }
}
