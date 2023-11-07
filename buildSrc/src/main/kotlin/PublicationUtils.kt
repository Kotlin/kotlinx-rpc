package org.jetbrains.krpc.buildutils

import org.gradle.api.*
import org.gradle.api.publish.*
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.*

fun Project.configureMppPublication() {
    apply(plugin = "maven-publish")
    setEAPVersion()

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

fun Project.setEAPVersion() {
    if (!System.getenv().containsKey("EAP_VERSION")) return

    val eapVersion = System.getenv("EAP_VERSION")
    project.version = "${project.version}-eap-$eapVersion"
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
