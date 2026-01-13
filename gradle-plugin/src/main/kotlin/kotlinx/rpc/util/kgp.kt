/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.util

import com.android.build.api.dsl.AndroidSourceSet
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.DynamicFeatureAndroidComponentsExtension
import com.android.build.api.variant.LibraryAndroidComponentsExtension
import com.android.build.api.variant.TestAndroidComponentsExtension
import com.android.build.api.variant.Variant
import com.android.build.gradle.BaseExtension
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.the
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetContainer

internal enum class KotlinPluginId(val id: String) {
    JVM("org.jetbrains.kotlin.jvm"),
    MULTIPLATFORM("org.jetbrains.kotlin.multiplatform"),
    ANDROID("org.jetbrains.kotlin.android")
}

internal const val ANDROID_APPLICATION = "com.android.application"
internal const val ANDROID_LIBRARY = "com.android.library"
internal const val ANDROID_DYNAMIC_FEATURE = "com.android.dynamic-feature"
internal const val ANDROID_TEST = "com.android.test"
internal const val ANDROID_KOTLIN_MULTIPLATFORM_LIBRARY = "com.android.kotlin.multiplatform.library"

internal val Project.kotlinPluginId: KotlinPluginId?
    get() = plugins.findPlugin(KotlinPluginId.JVM.id)?.let { KotlinPluginId.JVM }
        ?: plugins.findPlugin(KotlinPluginId.MULTIPLATFORM.id)?.let { KotlinPluginId.MULTIPLATFORM }
        ?: plugins.findPlugin(KotlinPluginId.ANDROID.id)?.let { KotlinPluginId.ANDROID }

internal fun Project.withKotlin(action: (id: KotlinPluginId) -> Unit) {
    plugins.withId(KotlinPluginId.JVM.id) {
        action(KotlinPluginId.JVM)
    }

    plugins.withId(KotlinPluginId.MULTIPLATFORM.id) {
        action(KotlinPluginId.MULTIPLATFORM)
    }

    plugins.withId(KotlinPluginId.ANDROID.id) {
        action(KotlinPluginId.ANDROID)
    }
}

internal fun Project.withKotlinSourceSets(action: (KotlinSourceSetContainer) -> Unit) {
    withKotlin {
        action(the())
    }
}

internal val Project.hasLegacyAndroid: Boolean
    get() = plugins.hasPlugin(ANDROID_LIBRARY) ||
            plugins.hasPlugin(ANDROID_APPLICATION) ||
            plugins.hasPlugin(ANDROID_DYNAMIC_FEATURE) ||
            plugins.hasPlugin(ANDROID_TEST)

internal val Project.hasAndroidKmpLibrary: Boolean
    get() = plugins.hasPlugin(ANDROID_KOTLIN_MULTIPLATFORM_LIBRARY)

internal fun Project.withLegacyAndroid(action: LegacyAndroidApplied.() -> Unit) {
    plugins.withId(ANDROID_LIBRARY) {
        action(LegacyAndroidApplied(project, ANDROID_LIBRARY))
    }

    plugins.withId(ANDROID_APPLICATION) {
        action(LegacyAndroidApplied(project, ANDROID_APPLICATION))
    }

    plugins.withId(ANDROID_DYNAMIC_FEATURE) {
        action(LegacyAndroidApplied(project, ANDROID_DYNAMIC_FEATURE))
    }

    plugins.withId(ANDROID_TEST) {
        action(LegacyAndroidApplied(project, ANDROID_TEST))
    }
}

internal class LegacyAndroidApplied(val project: Project, val id: String)

internal fun LegacyAndroidApplied.withAndroidSourceSets(
    action: (NamedDomainObjectContainer<out AndroidSourceSet>) -> Unit,
) {
    action(project.the<BaseExtension>().sourceSets)
}

internal typealias AndroidComponents = AndroidComponentsExtension<out CommonExtension<*, *, *, *, *, *>, *, out Variant>

internal fun LegacyAndroidApplied.withLazyLegacyAndroidComponentsExtension(action: Action<AndroidComponents>) {
    when (id) {
        ANDROID_LIBRARY -> action.execute(project.the<LibraryAndroidComponentsExtension>())
        ANDROID_APPLICATION -> action.execute(project.the<ApplicationAndroidComponentsExtension>())
        ANDROID_DYNAMIC_FEATURE -> action.execute(project.the<DynamicFeatureAndroidComponentsExtension>())
        ANDROID_TEST -> action.execute(project.the<TestAndroidComponentsExtension>())
    }
}

internal fun Project.withLazyJavaPluginExtension(action: Action<JavaPluginExtension>) {
    plugins.withType<JavaBasePlugin> {
        the<JavaPluginExtension>().apply { action.execute(this) }
    }
}
