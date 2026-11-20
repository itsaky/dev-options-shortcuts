package com.itsaky.devoptions.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
class MainScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun showsGrantScreenWhenPermissionNotGranted() {
        composeTestRule.setContent {
            MainScreen(permissionGranted = false, onGranted = {})
        }
        composeTestRule.onNodeWithText("adb shell pm grant", substring = true).assertExists()
    }

    @Test
    fun showsToggleScreenWhenPermissionGranted() {
        composeTestRule.setContent {
            MainScreen(permissionGranted = true, onGranted = {})
        }
        composeTestRule.onNodeWithText("Developer Options").assertExists()
        composeTestRule.onNodeWithText("USB Debugging").assertExists()
    }

    @Test
    @Config(sdk = [28])
    fun hidesWadbOnApiBelow30() {
        composeTestRule.setContent {
            MainScreen(permissionGranted = true, onGranted = {})
        }
        composeTestRule.onNodeWithText("Wireless Debugging").assertDoesNotExist()
    }
}
