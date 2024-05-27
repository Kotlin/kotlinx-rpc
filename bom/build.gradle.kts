/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    `java-platform`
    `maven-publish`
    alias(libs.plugins.conventions.common)
}

dependencies {
    constraints {
        rootProject.subprojects.filter {
            it.plugins.hasPlugin("maven-publish") && it != project
        }.forEach { project ->
            api(project)
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("kotlinxRpcPlatform") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()

            from(components["javaPlatform"])
        }
    }
}
