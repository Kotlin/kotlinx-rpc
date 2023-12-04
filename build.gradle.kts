import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion

plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.serialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.krpc) apply false
    alias(libs.plugins.atomicfu) apply false
}

allprojects {
    group = "org.jetbrains.krpc"
    version = rootProject.libs.versions.krpc.full.get()
}

println("kRPC project version: $version")

// If the prefix of the kPRC version is not Kotlin gradle plugin version - you have a problem :)
// Probably some dependency brings kotlin with higher version.
// To mitigate so, please refer to `gradle/kotlin-version-lookup.json`
// and it's usage in `gradle-settings-conventions/src/main/kotlin/settings-conventions.settings.gradle.kts`
val kotlinVersion = getKotlinPluginVersion()
if (!version.toString().startsWith(kotlinVersion)) {
    error("Kotlin gradle plugin version mismatch: kRPC version: $version, Kotlin gradle plugin version: $kotlinVersion")
}

// necessary for CI js tests
rootProject.plugins.withType<org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin> {
    rootProject.the<org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension>().ignoreScripts = false
}
