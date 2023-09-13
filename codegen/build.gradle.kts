plugins {
    kotlin("jvm")


}

dependencies {
    implementation("com.google.devtools.ksp:symbol-processing-api:1.9.0-1.0.11")

    implementation(project(":runtime"))
}

kotlin {
    jvmToolchain(8)
}
