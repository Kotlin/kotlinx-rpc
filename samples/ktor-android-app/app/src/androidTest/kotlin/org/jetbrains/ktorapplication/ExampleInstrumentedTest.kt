/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.ktorapplication

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.jetbrains.ktorapplication.ui.Screen
import org.jetbrains.ktorapplication.ui.theme.KtorApplicationTheme

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Rule

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun checkTextOnScreen() {
        composeTestRule.setContent {
            KtorApplicationTheme {
                Screen(welcomeData = null)
            }
        }
        composeTestRule.onNodeWithText("Establishing connection...").assertIsDisplayed()

    }
}