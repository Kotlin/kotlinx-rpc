/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util.targets

import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.konan.target.Family

/**
 * Adds source-set branches for implementations and tests which don't target iOS.
 *
 * `nonIosMain` and `nonIosTest` are shared by JVM and all non-iOS Native targets. The
 * `nonIosNativeMain` and `nonIosNativeTest` branches additionally expose Native APIs to the
 * non-iOS Native targets.
 */
fun KotlinMultiplatformExtension.configureNonIosSourceSets() {
    // Applying the template here ensures the standard shared source sets exist before the custom
    // branches are configured.
    applyDefaultHierarchyTemplate()

    val commonMain = sourceSets.named("commonMain")
    val nonIosMain = sourceSets.maybeCreate("nonIosMain").apply {
        dependsOn(commonMain.get())
    }
    sourceSets.named("jvmMain").configure {
        dependsOn(nonIosMain)
    }

    val nativeMain = sourceSets.named("nativeMain")
    val nonIosNativeMain = sourceSets.maybeCreate("nonIosNativeMain").apply {
        dependsOn(nativeMain.get())
        dependsOn(nonIosMain)
    }

    val commonTest = sourceSets.named("commonTest")
    val nonIosTest = sourceSets.maybeCreate("nonIosTest").apply {
        dependsOn(commonTest.get())
    }
    sourceSets.named("jvmTest").configure {
        dependsOn(nonIosTest)
    }

    val nativeTest = sourceSets.named("nativeTest")
    val nonIosNativeTest = sourceSets.maybeCreate("nonIosNativeTest").apply {
        dependsOn(nativeTest.get())
        dependsOn(nonIosTest)
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
