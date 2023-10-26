package org.jetbrains.krpc.buildutils

import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget

fun KotlinMultiplatformExtension.allTargets(
    jvm: Boolean = true,
    js: Boolean = false,
    native: Boolean = true,
): List<KotlinTarget> {
    val result = mutableListOf<KotlinTarget>()

    if (native) {
        val nativeTargets = listOf<KotlinTarget>(
//            mingwX64(),
            linuxX64(),
            linuxArm64(),
            iosX64(),
            iosArm64(),
            iosSimulatorArm64(),
            watchosX64(),
//            watchosArm32(),
            watchosArm64(),
            watchosSimulatorArm64(),
//            watchosDeviceArm64(),
            tvosX64(),
            tvosArm64(),
//            tvosSimulatorArm64(),
            macosX64(),
            macosArm64()
        )
        val commonMain = sourceSets.findByName("commonMain")!!
        val commonTest = sourceSets.findByName("commonTest")!!
        val nativeMain = sourceSets.create("nativeMain")
        val nativeTest = sourceSets.create("nativeTest")

        nativeMain.dependsOn(commonMain)
        nativeTest.dependsOn(commonTest)

        nativeTargets.forEach { target ->
            sourceSets.findByName("${target.name}Main")?.dependsOn(nativeMain)
            sourceSets.findByName("${target.name}Test")?.dependsOn(nativeTest)
        }

        result += nativeTargets
    }

    if (jvm) {
        result += jvm {
            jvmToolchain(8)
        }
    }

    if (js) {
        result += js(IR) {
            browser()
            nodejs()
        }
    }

    return result
}


fun KotlinProjectExtension.optInForInternalKRPCApi() {
    sourceSets.all {
        languageSettings.optIn("org.jetbrains.krpc.internal.InternalKRPCApi")
    }
}
