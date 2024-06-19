/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import util.KOTLINX_RPC_PREFIX
import util.isPublicModule

plugins {
    `java-platform`
    `maven-publish`
    alias(libs.plugins.conventions.common)
}

dependencies {
    constraints {
        rootProject.subprojects.filter {
            it.isPublicModule && it != project
        }.forEach { project ->
            api(project)
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("bom") {
            groupId = project.group.toString()
            artifactId = "$KOTLINX_RPC_PREFIX-${project.name}"
            version = project.version.toString()

            from(components["javaPlatform"])
        }
    }
}
