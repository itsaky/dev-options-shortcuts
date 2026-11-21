package com.itsaky.devoptions.widget

import androidx.datastore.preferences.core.intPreferencesKey
import com.itsaky.devoptions.DevOption
import com.itsaky.devoptions.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class WidgetContentTest {

    @Test
    fun revisionKeyHasExpectedName() {
        assertEquals("revision", RevisionKey.name)
    }

    @Test
    fun optionKeyParamHasExpectedName() {
        assertEquals("option_key", OptionKeyParam.name)
    }

    @Test
    fun allDevOptionsHaveDistinctKeys() {
        val keys = DevOption.entries.map { it.key }
        assertEquals(keys.size, keys.toSet().size)
    }

    @Test
    fun allDevOptionsHaveValidResources() {
        for (option in DevOption.entries) {
            assertNotNull(option.labelRes)
            assertNotNull(option.iconRes)
        }
    }

    @Test
    fun allTogglesWidgetUsesSizeModeExact() {
        val widget = AllTogglesWidget()
        assertEquals(
            androidx.glance.appwidget.SizeMode.Exact,
            widget.sizeMode,
        )
    }
}
