plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.gradle.kotlin.dsl)
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.gradle.plugin)
}
