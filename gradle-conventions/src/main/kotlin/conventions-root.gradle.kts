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

// useful for dependencies introspection
// run ./gradlew htmlDependencyReport
// Report can normally be found in build/reports/project/dependencies/index.html
allprojects {
    plugins.apply("project-report")
}

tasks.register<ValidatePublishedArtifactsTask>(ValidatePublishedArtifactsTask.NAME) {
    dependsOn(subprojects.filter { it.isPublicModule })
}

dokka {
    val libVersion = libs.versions.kotlinx.rpc.get()

    moduleVersion.set(libVersion)

    val globalRootDir: String by project.extra

    val pagesDirectory = kotlin.io.path.Path(globalRootDir)
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
        outputDirectory = dokkaVersionsDirectory
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
}

configureNpm()

registerDumpPlatformTableTask()
registerVerifyPlatformTableTask()
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

gradle.afterProject {
    if (gradle.startParameter.taskNames.singleOrNull() == updateProperties.name) {
        return@afterProject
    }

    forEachIncludedProject { rootProperties, parent, subProjectProperties ->
        if (!subProjectProperties.exists() || subProjectProperties.readText() != rootProperties) {
            throw GradleException(
                "'gradle.properties' file in ${parent.name} included project is not up-to-date with root. " +
                        "Please, run `./gradlew ${updateProperties.name}"
            )
        }
    }
}


tasks.register<CheckExecutableTask>("checkBazel") {
    exec = "bazel"
    helpMessage = "Install Bazel: https://bazel.build/"
}