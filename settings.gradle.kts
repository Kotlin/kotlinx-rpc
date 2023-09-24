pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

rootProject.name = "kRPC"

include(":gradle-plugin")

include(":codegen:common")
include(":codegen:compiler-plugin")
include(":codegen:sources-generation")
include(":codegen:test")
include(":codegen:test:common")

include(":runtime")
include(":runtime-client")
include(":runtime-server")
include(":testJvmModule")
include(":transport-ktor")
include(":transport-ktor-server")
include(":transport-ktor-client")
include(":ktor-tests")
