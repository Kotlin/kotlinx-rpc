import org.jetbrains.kotlin.gradle.plugin.PLUGIN_CLASSPATH_CONFIGURATION_NAME
import org.jetbrains.krpc.buildutils.*

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("com.google.devtools.ksp")
}

configureMppPublication()

kotlin {
    allTargets()

    sourceSets {
        val jvmMain by getting {
            dependencies {
                api(project(":krpc-runtime"))
                api(project(":krpc-runtime:krpc-runtime-server"))
                api(project(":krpc-runtime:krpc-runtime-client"))

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
            }
        }
    }
}

dependencies {
    add("kspJvm", project(":krpc-codegen:krpc-ksp-plugin"))
    PLUGIN_CLASSPATH_CONFIGURATION_NAME(project(":krpc-codegen:krpc-compiler-plugin"))
}