plugins {
    alias(libs.plugins.serialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.krpc) apply false
    alias(libs.plugins.atomicfu) apply false
}

allprojects {
    group = "org.jetbrains.krpc"
    version = rootProject.libs.versions.krpc.full.get()
}

println("Running kRPC project, version: $version")

// necessary for CI js tests
rootProject.plugins.withType<org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin> {
    rootProject.the<org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension>().ignoreScripts = false
}
