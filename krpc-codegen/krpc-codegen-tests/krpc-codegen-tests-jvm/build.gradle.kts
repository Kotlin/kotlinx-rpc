import org.jetbrains.kotlin.gradle.plugin.PLUGIN_CLASSPATH_CONFIGURATION_NAME
import org.jetbrains.krpc.buildutils.optInForInternalKRPCApi

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("com.google.devtools.ksp")
}

repositories {
    mavenCentral()
}

kotlin {
    optInForInternalKRPCApi()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))

    implementation(project(":krpc-runtime:krpc-runtime-client"))
    implementation(project(":krpc-runtime:krpc-runtime-server"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.6.0")

    implementation(project(":krpc-codegen:krpc-codegen-tests:krpc-codegen-tests-mpp"))

    testImplementation(kotlin("test"))

    ksp(project(":krpc-codegen:krpc-ksp-plugin"))
    PLUGIN_CLASSPATH_CONFIGURATION_NAME(project(":krpc-codegen:krpc-compiler-plugin"))
}
