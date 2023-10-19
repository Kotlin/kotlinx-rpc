import org.jetbrains.kotlin.gradle.plugin.PLUGIN_CLASSPATH_CONFIGURATION_NAME
import org.jetbrains.krpc.buildutils.allTargets
import org.jetbrains.krpc.buildutils.configureMppPublication

plugins {
    kotlin("multiplatform")
    id("com.google.devtools.ksp")
    kotlin("plugin.serialization")
}

kotlin {
    allTargets()

    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":krpc-runtime:krpc-runtime-server"))

                implementation("io.ktor:ktor-websockets:2.3.4")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
                implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.0")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("io.ktor:ktor-server-netty:2.3.4")
                implementation("io.ktor:ktor-client-cio:2.3.4")

                implementation(project("krpc-transport-ktor-server"))
                implementation(project("krpc-transport-ktor-client"))
            }
        }
    }
}

configureMppPublication()

dependencies {
    add("kspJvmTest", project(":krpc-codegen:krpc-ksp-plugin"))
    PLUGIN_CLASSPATH_CONFIGURATION_NAME(project(":krpc-codegen:krpc-compiler-plugin"))
}