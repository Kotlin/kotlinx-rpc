/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util.targets

import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.konan.target.Family

/**
 * Adds source sets for the targets that the Swift client backend does not serve.
 *
 * `nonSwiftMain` and `nonSwiftTest` are shared by the JVM and by every Native target that
 * still uses the C-core backend (all non-iOS targets today). `nonSwiftNativeMain` and
 * `nonSwiftNativeTest` also give those Native targets access to Native APIs.
 *
 * When a target migrates to the Swift backend, it leaves these sets, so the names state the
 * backend, not the platforms.
 */
fun KotlinMultiplatformExtension.configureNonSwiftSourceSets() {
    // The template must create the standard shared source sets before the branches below link to them.
    applyDefaultHierarchyTemplate()

    val commonMain = sourceSets.named("commonMain")
    val nonSwiftMain = sourceSets.maybeCreate("nonSwiftMain").apply {
        dependsOn(commonMain.get())
    }
    sourceSets.named("jvmMain").configure {
        dependsOn(nonSwiftMain)
    }

    val nativeMain = sourceSets.named("nativeMain")
    val nonSwiftNativeMain = sourceSets.maybeCreate("nonSwiftNativeMain").apply {
        dependsOn(nativeMain.get())
        dependsOn(nonSwiftMain)
    }

    val commonTest = sourceSets.named("commonTest")
    val nonSwiftTest = sourceSets.maybeCreate("nonSwiftTest").apply {
        dependsOn(commonTest.get())
    }
    sourceSets.named("jvmTest").configure {
        dependsOn(nonSwiftTest)
    }

    val nativeTest = sourceSets.named("nativeTest")
    val nonSwiftNativeTest = sourceSets.maybeCreate("nonSwiftNativeTest").apply {
        dependsOn(nativeTest.get())
        dependsOn(nonSwiftTest)
    }

    targets.withType(KotlinNativeTarget::class.java).configureEach {
        if (konanTarget.family != Family.IOS) {
            compilations.named(KotlinCompilation.MAIN_COMPILATION_NAME).configure {
                defaultSourceSet.dependsOn(nonSwiftNativeMain)
            }
            compilations.named(KotlinCompilation.TEST_COMPILATION_NAME).configure {
                defaultSourceSet.dependsOn(nonSwiftNativeTest)
            }
        }
    }
}
