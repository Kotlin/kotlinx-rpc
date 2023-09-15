import org.gradle.api.publish.maven.MavenPublication

plugins {
    kotlin("jvm")
    kotlin("kapt")
    publishing
    `maven-publish`
}

dependencies {
    compileOnly("org.jetbrains.kotlin:kotlin-compiler-embeddable:1.8.20")
    compileOnly("com.google.auto.service:auto-service-annotations:1.0.1")
    kapt("com.google.auto.service:auto-service:1.0.1")
}

publishing {
    publications {
        create<MavenPublication>("compiler-plugin") {
            groupId = "org.jetbrains.krpc"
            artifactId = "krpc-compiler-plugin"
            version = "0.0.1"

            artifact(tasks["kotlinSourcesJar"])
            artifact(tasks["jar"])

            pom {
                name.set("krpc-compiler-plugin")
            }
        }
    }
}

tasks.publishToMavenLocal {
    dependsOn(tasks.build)
}
