import org.jetbrains.krpc.buildutils.configureJvmPublication

plugins {
    kotlin("jvm")
}

configureJvmPublication()

dependencies {
    implementation("com.google.devtools.ksp:symbol-processing-api:1.9.10-1.0.13")
}

kotlin {
    jvmToolchain(8)
}
