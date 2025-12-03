/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.util

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.DynamicFeatureAndroidComponentsExtension
import com.android.build.api.variant.LibraryAndroidComponentsExtension
import com.android.build.api.variant.TestAndroidComponentsExtension
import com.android.build.api.variant.Variant
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.the
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetContainer

private const val KOTLIN_MULTIPLATFORM_PLUGIN_ID = "org.jetbrains.kotlin.multiplatform"
private const val KOTLIN_JVM_PLUGIN_ID = "org.jetbrains.kotlin.jvm"
private const val KOTLIN_ANDROID_PLUGIN_ID = "org.jetbrains.kotlin.android"

private const val ANDROID_APPLICATION = "com.android.application"
private const val ANDROID_LIBRARY = "com.android.library"
private const val ANDROID_DYNAMIC_FEATURE = "com.android.dynamic-feature"
private const val ANDROID_TEST = "com.android.test"

internal fun Project.withKotlinSourceSets(action: (isAndroid: Boolean, KotlinSourceSetContainer) -> Unit) {
    plugins.withId(KOTLIN_JVM_PLUGIN_ID) {
        the<KotlinSourceSetContainer>().apply {
            action(false, this)
        }
    }

    plugins.withId(KOTLIN_MULTIPLATFORM_PLUGIN_ID) {
        the<KotlinSourceSetContainer>().apply {
            // todo huh?
            action(false, this)
        }
    }

    plugins.withId(KOTLIN_ANDROID_PLUGIN_ID) {
        the<KotlinSourceSetContainer>().apply {
            action(true, this)
        }
    }
}

internal fun Project.withLazyAndroidComponentsExtension(
    action: Action<AndroidComponentsExtension<out CommonExtension<*, *, *, *, *, *>, *, out Variant>>,
) {
    plugins.withId(ANDROID_LIBRARY) {
        the<LibraryAndroidComponentsExtension>().apply { action.execute(this) }
    }

    plugins.withId(ANDROID_APPLICATION) {
        the<ApplicationAndroidComponentsExtension>().apply { action.execute(this) }
    }

    plugins.withId(ANDROID_DYNAMIC_FEATURE) {
        the<DynamicFeatureAndroidComponentsExtension>().apply { action.execute(this) }
    }

    plugins.withId(ANDROID_TEST) {
        the<TestAndroidComponentsExtension>().apply { action.execute(this) }
    }
}

internal fun Project.withLazyJavaPluginExtension(action: Action<JavaPluginExtension>) {
    plugins.withType<JavaBasePlugin> {
        the<JavaPluginExtension>().apply { action.execute(this) }
    }
}
