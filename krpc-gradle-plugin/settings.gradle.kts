rootProject.name = "krpc-gradle-plugin"

pluginManagement {
    includeBuild("../gradle-conventions")
    includeBuild("../gradle-settings-conventions")
}

plugins {
    id("settings-conventions")
}

include(":krpc-gradle-plugin-api")
include(":krpc-gradle-plugin-all")
include(":krpc-gradle-plugin-platform")
