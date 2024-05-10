/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util

import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.*

internal fun PublishingExtension.configureLibraryPublication() {
    val spaceUser = System.getenv("SPACE_USERNAME")
    val spacePassword = System.getenv("SPACE_PASSWORD")

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

internal fun Project.configureKmpPublication() {
    apply(plugin = "maven-publish")

    the<PublishingExtension>().configureLibraryPublication()
}

internal fun Project.configureJvmPublication(skipJvm: Boolean) {
    configureKmpPublication()

    if (skipJvm) {
        return
    }

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
