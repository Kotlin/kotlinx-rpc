import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget

internal fun Project.kotlin(block: KotlinMultiplatformExtension.() -> Unit) {
    configure(block)
}

private fun KotlinMultiplatformExtension.configureTargets(
    jvm: Boolean = true,
    js: Boolean = true,
    native: Boolean = true,
) {
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
    }

    if (jvm) {
        jvm {
            jvmToolchain(8)
        }
    }

    if (js) {
        js(IR) {
            nodejs()
            browser()
        }
    }
}

fun Project.kmp(
    jvm: Boolean = true,
    js: Boolean = true,
    native: Boolean = true,
    action: Action<KotlinMultiplatformExtension> = Action { }
) {
    if (js) {
        configureJs()
    }

    kotlin {
        configureTargets(jvm, js, native)

        action.execute(this)
    }
}
