import org.gradle.api.publish.maven.MavenPublication

plugins {
    kotlin("jvm")
}

dependencies {
    compileOnly("org.jetbrains.kotlin:kotlin-compiler-embeddable:1.8.20")
    compileOnly("com.google.auto.service:auto-service-annotations:1.0.1")

    implementation(project(":codegen:common"))
}
