/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import java.nio.file.Path

// The functional tests of the Gradle plugin compile projects that depend on the libraries of this build.
// This project gives them the build repository as an artifact. To resolve the artifact, Gradle publishes
// the libraries first. See `functionalTestRepository` in gradle-plugin/build.gradle.kts.

val libraries = listOf(":utils", ":core")

// A JVM project needs the root publication for the Gradle module metadata and the JVM publication for the JAR.
val publications = listOf("KotlinMultiplatform", "Jvm")

val globalRootDir = extra["globalRootDir"] as String

val functionalTestRepository = configurations.consumable("functionalTestRepository") {
    attributes {
        attribute(Usage.USAGE_ATTRIBUTE, objects.named("kotlinx-rpc-functional-test-repository"))
    }
}

artifacts {
    add(functionalTestRepository.name, Path.of(globalRootDir, "build", "repo").toFile()) {
        type = "directory"

        libraries.forEach { library ->
            publications.forEach { publication ->
                builtBy("$library:publish${publication}PublicationToBuildRepoRepository")
            }
        }
    }
}
