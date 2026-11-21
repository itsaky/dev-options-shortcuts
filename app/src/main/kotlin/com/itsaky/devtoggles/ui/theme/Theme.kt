package com.itsaky.devtoggles.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

fun tealLightColorScheme(): ColorScheme = lightColorScheme(
    primary = TealC500,
    onPrimary = Color.White,
    primaryContainer = TealC100,
    onPrimaryContainer = TealC900,
    secondary = TealC700,
    onSecondary = Color.White,
    tertiary = TealC300,
    background = SnowBackground,
    onBackground = SnowTextStrong,
    surface = SnowSurface,
    onSurface = SnowTextStrong,
    surfaceVariant = SnowSurfaceRaised,
    onSurfaceVariant = SnowTextMuted,
    surfaceContainerLowest = SnowBackground,
    surfaceContainerLow = SnowSurface,
    surfaceContainer = SnowSurface,
    surfaceContainerHigh = SnowSurfaceRaised,
    surfaceContainerHighest = SnowSurfaceRaised,
    surfaceBright = SnowSurfaceRaised,
    surfaceDim = SnowBackground,
    surfaceTint = Color.Transparent,
    outline = SnowBorder,
    outlineVariant = SnowBorderStrong,
    error = ErrorLight,
    onError = Color.White,
)

fun tealDarkColorScheme(): ColorScheme = darkColorScheme(
    primary = TealC300,
    onPrimary = TealC900,
    primaryContainer = TealC700,
    onPrimaryContainer = TealC100,
    secondary = TealC500,
    onSecondary = TealC900,
    tertiary = TealC300,
    background = GraphiteBackground,
    onBackground = GraphiteTextStrong,
    surface = GraphiteSurface,
    onSurface = GraphiteTextStrong,
    surfaceVariant = GraphiteSurfaceRaised,
    onSurfaceVariant = GraphiteTextMuted,
    surfaceContainerLowest = GraphiteBackground,
    surfaceContainerLow = GraphiteSurface,
    surfaceContainer = GraphiteSurface,
    surfaceContainerHigh = GraphiteSurfaceRaised,
    surfaceContainerHighest = GraphiteSurfaceRaised,
    surfaceBright = GraphiteSurfaceRaised,
    surfaceDim = GraphiteBackground,
    surfaceTint = Color.Transparent,
    outline = GraphiteBorder,
    outlineVariant = GraphiteBorderStrong,
    error = ErrorDark,
    onError = GraphiteBackground,
)

@Composable
fun DevOptionsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) tealDarkColorScheme() else tealLightColorScheme()
    MaterialExpressiveTheme(
        colorScheme = colorScheme,
        motionScheme = MotionScheme.expressive(),
        content = content,
    )
}
