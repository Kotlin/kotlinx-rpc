import org.jetbrains.kotlin.gradle.plugin.NATIVE_COMPILER_PLUGIN_CLASSPATH_CONFIGURATION_NAME
import org.jetbrains.kotlin.gradle.plugin.PLUGIN_CLASSPATH_CONFIGURATION_NAME

plugins {
    kotlin("multiplatform")
    id("com.google.devtools.ksp") version "1.9.0-1.0.11"
}

kotlin {
    jvm {
        jvmToolchain(8)
    }

    js {
        nodejs {
            testTask(Action {
                useKarma {
                    useChromeHeadless()
                }
            })
        }
        binaries.executable()
    }

    val hostOs = System.getProperty("os.name")
    val isArm64 = System.getProperty("os.arch") == "aarch64"
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" && isArm64 -> macosArm64("native")
        hostOs == "Mac OS X" && !isArm64 -> macosX64("native")
        hostOs == "Linux" && isArm64 -> linuxArm64("native")
        hostOs == "Linux" && !isArm64 -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    nativeTarget.apply {
        binaries {
            executable {
                entryPoint = "org.jetbrains.krpc.native.main"
                linkerOpts += "-ld64"
            }
            get("debugTest").apply {
                linkerOpts += "-ld64"
            }
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
                implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.0")

                implementation(project(":runtime"))
                implementation(project(":runtime-client"))
                implementation(project(":runtime-server"))
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

dependencies {
    add("kspNative", project(":codegen:sources-generation"))
    add("kspJvm", project(":codegen:sources-generation"))
    add("kspJs", project(":codegen:sources-generation"))
    add("kspNativeTest", project(":codegen:sources-generation"))
    add("kspJvmTest", project(":codegen:sources-generation"))
    add("kspJsTest", project(":codegen:sources-generation"))

    PLUGIN_CLASSPATH_CONFIGURATION_NAME(project(":codegen:compiler-plugin"))
    NATIVE_COMPILER_PLUGIN_CLASSPATH_CONFIGURATION_NAME(project(":codegen:compiler-plugin"))
}
