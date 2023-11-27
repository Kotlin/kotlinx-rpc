rootProject.name = "gradle-conventions"

pluginManagement {
    includeBuild("../gradle-settings-conventions")
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
    id("settings-conventions")
}
