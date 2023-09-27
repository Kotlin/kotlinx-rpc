import org.jetbrains.krpc.buildutils.configureJvmPublication

plugins {
    kotlin("jvm")
}

configureJvmPublication()

dependencies {
    compileOnly("org.jetbrains.kotlin:kotlin-compiler-embeddable:1.9.0")
    compileOnly("com.google.auto.service:auto-service-annotations:1.0.1")
}
