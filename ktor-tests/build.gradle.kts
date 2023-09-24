import org.jetbrains.kotlin.gradle.plugin.PLUGIN_CLASSPATH_CONFIGURATION_NAME

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization") version "1.9.0"
    id("com.google.devtools.ksp") version "1.9.0-1.0.11"
//    id("org.jetbrains.krpc")
}

repositories {
    mavenLocal()
}

kotlin {
    jvm {
        jvmToolchain(8)
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(kotlin("test"))
                implementation(project(":runtime"))
                implementation(project(":runtime-server"))
                implementation(project(":runtime-client"))
                implementation(project(":transport-ktor"))
                implementation(project(":transport-ktor-server"))
                implementation(project(":transport-ktor-client"))

                implementation("io.ktor:ktor-server-core:2.3.4")
                implementation("io.ktor:ktor-server-websockets:2.3.4")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
                implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.0")
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("io.ktor:ktor-server-netty:2.3.4")
                implementation("io.ktor:ktor-client-cio:2.3.4")
            }
        }
    }
}

dependencies {
    add("kspJvm", project(":codegen:sources-generation"))
    add("kspJvmTest", project(":codegen:sources-generation"))
    PLUGIN_CLASSPATH_CONFIGURATION_NAME(project(":codegen:compiler-plugin"))
}
