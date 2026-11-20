package com.itsaky.devoptions.ui.theme

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Test

class ColorSchemeTest {

    @Test
    fun lightSchemePrimaryIsTealC500() {
        val scheme = tealLightColorScheme()
        assertEquals(Color(0xFF0F6E56), scheme.primary)
    }

    @Test
    fun darkSchemePrimaryIsTealC300() {
        val scheme = tealDarkColorScheme()
        assertEquals(Color(0xFF5DCAA5), scheme.primary)
    }

    @Test
    fun lightSchemePrimaryContainerIsTealC100() {
        val scheme = tealLightColorScheme()
        assertEquals(Color(0xFF9FE1CB), scheme.primaryContainer)
    }

    @Test
    fun darkSchemeBackgroundIsGraphite() {
        val scheme = tealDarkColorScheme()
        assertEquals(Color(0xFF0B0D14), scheme.background)
    }

    @Test
    fun lightSchemeBackgroundIsSnow() {
        val scheme = tealLightColorScheme()
        assertEquals(Color(0xFFF6F7FB), scheme.background)
    }
}
