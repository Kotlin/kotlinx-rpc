import org.gradle.configurationcache.extensions.capitalized
import org.jetbrains.kotlin.gradle.plugin.NATIVE_COMPILER_PLUGIN_CLASSPATH_CONFIGURATION_NAME
import org.jetbrains.kotlin.gradle.plugin.PLUGIN_CLASSPATH_CONFIGURATION_NAME
import org.jetbrains.krpc.buildutils.*

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("com.google.devtools.ksp")
    id("kotlinx-atomicfu")
}

configureMppPublication()

kmp { allTargets ->
    sourceSets {
        commonMain {
            dependencies {
                api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
                api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
                implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.0")
                implementation("io.github.oshai:kotlin-logging:5.1.0")
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))

                implementation(project(":krpc-runtime:krpc-runtime-client"))
                implementation(project(":krpc-runtime:krpc-runtime-server"))

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
                implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.0")
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation("org.slf4j:slf4j-api:2.0.9")
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation(project(":krpc-runtime:krpc-runtime-test"))

                implementation("org.slf4j:slf4j-api:2.0.9")
                implementation("org.slf4j:slf4j-simple:2.0.9")
            }
        }
    }

    dependencies {
        val kspPlugin = ":krpc-codegen:krpc-ksp-plugin"

        allTargets.forEach { target ->
            val mainConfiguration = "ksp${target.name.capitalized()}"
            add(mainConfiguration, project(kspPlugin))

            val testConfiguration = "ksp${target.name.capitalized()}Test"
            add(testConfiguration, project(kspPlugin))
        }

        PLUGIN_CLASSPATH_CONFIGURATION_NAME(project(":krpc-codegen:krpc-compiler-plugin"))
        NATIVE_COMPILER_PLUGIN_CLASSPATH_CONFIGURATION_NAME(project(":krpc-codegen:krpc-compiler-plugin"))
    }
}
