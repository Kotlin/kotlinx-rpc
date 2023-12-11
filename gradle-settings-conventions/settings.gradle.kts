rootProject.name = "gradle-settings-conventions"

// Code below is a hack because a chicken-egg problem, I can't use myself as a settings-plugin
apply(from="src/main/kotlin/settings-conventions.settings.gradle.kts")
