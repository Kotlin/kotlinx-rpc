plugins {
    kotlin("jvm") version "1.9.10"
    id("org.gradle.kotlin.kotlin-dsl") version "4.1.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("gradle-plugin", "1.9.10"))

    implementation(kotlin("stdlib"))
}
