package com.itsaky.devoptions.toggle

import android.content.ContentResolver
import android.provider.Settings
import androidx.test.core.app.ApplicationProvider
import com.itsaky.devoptions.DevOption
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SettingsToggleTest {

    private lateinit var resolver: ContentResolver

    @Before
    fun setUp() {
        resolver = ApplicationProvider.getApplicationContext<android.app.Application>().contentResolver
    }

    @Test
    fun readToggleReturnsTrueWhenSettingIsOne() {
        Settings.Global.putInt(resolver, "development_settings_enabled", 1)
        assertTrue(readToggle(resolver, DevOption.DeveloperOptions))
    }

    @Test
    fun readToggleReturnsFalseWhenSettingIsZero() {
        Settings.Global.putInt(resolver, "adb_enabled", 0)
        assertFalse(readToggle(resolver, DevOption.UsbDebugging))
    }

    @Test
    fun writeToggleSetsValueInSettingsGlobal() {
        writeToggle(resolver, DevOption.UsbDebugging, true)
        assertEquals(1, Settings.Global.getInt(resolver, "adb_enabled"))
    }
}
