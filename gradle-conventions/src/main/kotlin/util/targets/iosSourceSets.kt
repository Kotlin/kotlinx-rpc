/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util.targets

import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.konan.target.Family

/**
 * Adds source-set branches for native implementations and tests which don't target iOS.
 *
 * The default hierarchy already provides `nativeMain`, `nativeTest`, `iosMain`, and `iosTest`.
 * This function adds matching `nonIosNativeMain` and `nonIosNativeTest` branches so platform
 * implementations can diverge without duplicating the target selection in each module.
 */
fun KotlinMultiplatformExtension.configureNonIosNativeSourceSets() {
    // Applying the template here ensures the shared Native source sets exist before the custom
    // branches are configured.
    applyDefaultHierarchyTemplate()

    val nativeMain = sourceSets.named("nativeMain")
    val nonIosNativeMain = sourceSets.maybeCreate("nonIosNativeMain").apply {
        dependsOn(nativeMain.get())
    }
    val nativeTest = sourceSets.named("nativeTest")
    val nonIosNativeTest = sourceSets.maybeCreate("nonIosNativeTest").apply {
        dependsOn(nativeTest.get())
    }

    targets.withType(KotlinNativeTarget::class.java).configureEach {
        if (konanTarget.family != Family.IOS) {
            compilations.named(KotlinCompilation.MAIN_COMPILATION_NAME).configure {
                defaultSourceSet.dependsOn(nonIosNativeMain)
            }
            compilations.named(KotlinCompilation.TEST_COMPILATION_NAME).configure {
                defaultSourceSet.dependsOn(nonIosNativeTest)
            }
        }
    }
}
