/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.util

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.the
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

private const val KOTLIN_MULTIPLATFORM_PLUGIN_ID = "org.jetbrains.kotlin.multiplatform"
private const val KOTLIN_JVM_PLUGIN_ID = "org.jetbrains.kotlin.jvm"

internal fun Project.withLazyKotlinJvmExtension(action: Action<KotlinJvmProjectExtension>) {
    plugins.withId(KOTLIN_JVM_PLUGIN_ID) {
        the<KotlinJvmProjectExtension>().apply { action.execute(this) }
    }
}

internal fun Project.withLazyKotlinKmpExtension(action: Action<KotlinMultiplatformExtension>) {
    plugins.withId(KOTLIN_MULTIPLATFORM_PLUGIN_ID) {
        the<KotlinMultiplatformExtension>().apply { action.execute(this) }
    }
}

internal val Project.kotlinJvmExtensionOrNull: KotlinJvmProjectExtension? get() {
    return extensions.findByType<KotlinJvmProjectExtension>()
}

internal val Project.kotlinKmpExtensionOrNull: KotlinMultiplatformExtension? get() {
    return extensions.findByType<KotlinMultiplatformExtension>()
}
