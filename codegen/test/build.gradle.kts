import org.jetbrains.kotlin.gradle.plugin.PLUGIN_CLASSPATH_CONFIGURATION_NAME

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "1.9.0"

    id("com.google.devtools.ksp") version "1.9.0-1.0.11"
//    id("org.jetbrains.krpc")
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))

    implementation(project(":runtime"))
    implementation(project(":runtime-client"))
    implementation(project(":runtime-server"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.6.0")

    implementation(project(":codegen:test:common"))
    testImplementation(project(":codegen:test:common"))

    testImplementation(kotlin("test"))
    ksp(project(":codegen:sources-generation"))

    PLUGIN_CLASSPATH_CONFIGURATION_NAME(project(":codegen:compiler-plugin"))
}
