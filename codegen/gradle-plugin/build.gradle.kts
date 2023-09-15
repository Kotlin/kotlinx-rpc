plugins {
    `java-gradle-plugin`
    kotlin("jvm") version "1.9.0"
    kotlin("kapt") version "1.9.0"
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

gradlePlugin {
    plugins {
        create("krpc-gradle-plugin") {
            id = "org.jetbrains.krpc"
            implementationClass = "org.jetbrains.krpc.KRPCGradleKotlinCompilerPlugin"
        }
    }
}

dependencies {
    implementation(kotlin("gradle-plugin-api"))
    implementation(kotlin("stdlib"))

    implementation("com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:1.9.0-1.0.11")

    compileOnly("com.google.auto.service:auto-service-annotations:1.0.1")
    kapt("com.google.auto.service:auto-service:1.0.1")
}
