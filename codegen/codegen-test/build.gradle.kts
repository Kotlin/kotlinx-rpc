import org.jetbrains.kotlin.gradle.plugin.PLUGIN_CLASSPATH_CONFIGURATION_NAME
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")

    id("com.google.devtools.ksp") version "1.9.0-1.0.11"
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":runtime"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.6.0")

    ksp(project(":codegen:sources-generation"))

    PLUGIN_CLASSPATH_CONFIGURATION_NAME(project(":codegen:ir-extension"))
}
