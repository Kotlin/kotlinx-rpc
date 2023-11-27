rootProject.name = "gradle-settings-conventions"

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

// Code below is a hack because a chicken-egg problem, I can't use myself as a settings-plugin
apply(from="src/main/kotlin/settings-conventions.settings.gradle.kts")
