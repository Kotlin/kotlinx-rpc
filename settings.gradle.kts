pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }

    includeBuild("codegen/gradle-plugin")
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

rootProject.name = "kRPC"

include(":codegen:codegen-test")
include(":codegen:codegen-test:test-submodule")
include(":codegen:ir-extension")
include(":codegen:sources-generation")
include(":runtime")
include(":runtime-client")
include(":runtime-server")
include(":testJvmModule")
include(":transport-ktor")
include(":transport-ktor-server")
include(":transport-ktor-client")
