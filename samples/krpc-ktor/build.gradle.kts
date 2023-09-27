import org.jetbrains.kotlin.gradle.plugin.PLUGIN_CLASSPATH_CONFIGURATION_NAME

val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

plugins {
    kotlin("jvm") version "1.9.10"
    kotlin("plugin.serialization") version "1.9.10"
    id("io.ktor.plugin") version "2.3.4"
    id("com.google.devtools.ksp") version "1.9.10-1.0.13"
}

group = "com.jetbrains.krpc.samples"
version = "0.0.1"

application {
    mainClass.set("com.jetbrains.krpc.samples.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    maven(url = "https://maven.pkg.jetbrains.space/public/p/krpc/maven")
    mavenCentral()
}

dependencies {
    ksp("org.jetbrains.krpc:krpc-ksp-plugin:1.9.10-beta-1")
    PLUGIN_CLASSPATH_CONFIGURATION_NAME("org.jetbrains.krpc:krpc-compiler-plugin:1.9.10-beta-1")

    implementation("org.jetbrains.krpc:krpc-runtime-client:1.9.10-beta-1")
    implementation("org.jetbrains.krpc:krpc-runtime-client:1.9.10-beta-1")

    implementation("org.jetbrains.krpc:krpc-transport-ktor-client:1.9.10-beta-1")
    implementation("org.jetbrains.krpc:krpc-transport-ktor-server:1.9.10-beta-1")

    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-client-cio-jvm")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    testImplementation("io.ktor:ktor-server-tests-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}
