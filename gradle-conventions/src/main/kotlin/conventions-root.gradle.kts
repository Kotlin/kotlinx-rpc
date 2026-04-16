/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import util.other.isPublicModule
import util.other.libs
import util.setupPage
import util.tasks.*
import java.nio.file.Path
import kotlin.io.path.*

plugins {
    id("org.jetbrains.dokka")
}

// project-report plugin is applied in conventions-common.gradle.kts per subproject
// run ./gradlew htmlDependencyReport
// Report can normally be found in build/reports/project/dependencies/index.html
plugins.apply("project-report")

tasks.register<ValidatePublishedArtifactsTask>(ValidatePublishedArtifactsTask.NAME) {
    // isPublicModule is set from settings (includePublic), not from build script evaluation,
    // so this filter doesn't force eager configuration of subprojects.
    dependsOn(subprojects.filter { it.isPublicModule })
}

dokka {
    val libVersion = libs.versions.kotlinx.rpc.get()

    moduleVersion.set(libVersion)

    val globalRootDir: String by project.extra

    val pagesDirectory = java.nio.file.Path.of(globalRootDir)
        .resolve("docs")
        .resolve("pages")

    val dokkaVersionsDirectory = pagesDirectory
        .resolve("api")
        .toFile()

    pluginsConfiguration {
        html {
            setupPage(globalRootDir)
        }
    }

    dokkaPublications.html {
        outputDirectory.set(dokkaVersionsDirectory)
    }

    tasks.dokkaGenerate {
        doFirst {
            dokkaVersionsDirectory.mkdirs()
            dokkaVersionsDirectory.resolve("version.txt").writeText(libVersion)
        }
    }

    tasks.clean {
        delete(dokkaVersionsDirectory)
    }
}

dependencies {
    dokkaPlugin(libs.dokka.rpc.plugin)
    dokka("org.jetbrains.kotlinx:gradle-plugin")
}

configureNpm()

registerDumpPlatformTableTask()
registerChangelogTask()

fun Project.forEachIncludedProject(action: (String, Path, Path) -> Unit) {
    val globalRootDir: String by extra
    val root = Path.of(globalRootDir)
    val rootProperties = root.resolve("gradle.properties").readText()
    root.listDirectoryEntries()
        .filter { it.isDirectory() && it.name != "gradle-conventions-settings" && it.name != "gradle-conventions" }
        .forEach {
            val subProjectProperties = it.resolve("gradle.properties")
            val subProjectSettings = it.resolve("settings.gradle.kts")
            if (subProjectSettings.exists()) {
                action(rootProperties, it, subProjectProperties)
            }
        }
}

val updateProperties = tasks.register("updateProperties") {
    forEachIncludedProject { rootProperties, _, subProjectProperties ->
        if (!subProjectProperties.exists()) {
            subProjectProperties.createFile()
        }

        subProjectProperties.toFile().writeText(rootProperties)
    }
}

tasks.register<CheckExecutableTask>("checkBazel") {
    exec = "bazel"
    helpMessage = "Install Bazel: https://bazel.build/"
}

