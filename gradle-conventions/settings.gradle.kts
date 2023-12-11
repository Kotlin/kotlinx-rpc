rootProject.name = "gradle-conventions"

pluginManagement {
    includeBuild("../gradle-settings-conventions")
}

plugins {
    id("settings-conventions")
}

include(":compiler-specific-module")
