rootProject.name = "krpc-gradle-plugin"

pluginManagement {
    includeBuild("../gradle-conventions")
    includeBuild("../gradle-settings-conventions")
}

plugins {
//    id("org.gradle.toolchains.foojay-resolver-convention") version "0.4.0"
    id("settings-conventions")
}

include(":krpc-gradle-plugin-api")
include(":krpc-gradle-plugin-all")
include(":krpc-gradle-plugin-platform")
