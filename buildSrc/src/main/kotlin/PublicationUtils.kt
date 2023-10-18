package org.jetbrains.krpc.buildutils

import org.gradle.api.*
import org.gradle.api.publish.*
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.*

fun Project.configureMppPublication() {
    apply(plugin = "maven-publish")
    val spaceUser = System.getenv("SPACE_USERNAME")
    val spacePassword = System.getenv("SPACE_PASSWORD")

    the<PublishingExtension>().apply {
        if (spaceUser == null || spacePassword == null) return

        repositories {
            maven(url = "https://maven.pkg.jetbrains.space/public/p/krpc/maven") {
                credentials {
                    username = spaceUser
                    password = spacePassword
                }
            }
        }
    }
}

fun Project.configureJvmPublication() {
    configureMppPublication()

    the<PublishingExtension>().apply {
        publications {
            create<MavenPublication>("kotlinJvm") {
                groupId = project.group.toString()
                artifactId = project.name
                version = project.version.toString()

                from(components["kotlin"])
            }
        }
    }
}
