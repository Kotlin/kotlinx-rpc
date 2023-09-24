plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("gradle-plugin-api"))
    implementation(kotlin("stdlib"))

    implementation("com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:1.9.0-1.0.11")
}

gradlePlugin {
    plugins {
        create("krpc-gradle-plugin") {
            id = "org.jetbrains.krpc"
            implementationClass = "org.jetbrains.krpc.KRPCGradlePlugin"
        }
    }
}
