import org.gradle.configurationcache.extensions.capitalized
import org.jetbrains.kotlin.gradle.plugin.NATIVE_COMPILER_PLUGIN_CLASSPATH_CONFIGURATION_NAME
import org.jetbrains.kotlin.gradle.plugin.PLUGIN_CLASSPATH_CONFIGURATION_NAME
import org.jetbrains.krpc.buildutils.allTargets

plugins {
    kotlin("multiplatform")
    id("com.google.devtools.ksp")
}

kotlin {
    val targets = allTargets()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
                implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.0")
                implementation("io.github.oshai:kotlin-logging:5.1.0")

                implementation(project(":krpc-runtime:krpc-runtime-client"))
                implementation(project(":krpc-runtime:krpc-runtime-server"))
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }

    dependencies {
        val kspPlugin = ":krpc-codegen:krpc-ksp-plugin"

        targets.forEach { target ->
            val mainConfiguration = "ksp${target.name.capitalized()}"
            add(mainConfiguration, project(kspPlugin))

            val testConfiguration = "ksp${target.name.capitalized()}Test"
            add(testConfiguration, project(kspPlugin))
        }

        PLUGIN_CLASSPATH_CONFIGURATION_NAME(project(":krpc-codegen:krpc-compiler-plugin"))
        NATIVE_COMPILER_PLUGIN_CLASSPATH_CONFIGURATION_NAME(project(":krpc-codegen:krpc-compiler-plugin"))
    }
}

