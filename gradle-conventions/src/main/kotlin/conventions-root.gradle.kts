/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import util.other.libs
import util.tasks.configureNpm
import util.tasks.registerChangelogTask
import util.tasks.registerDumpPlatformTableTask
import util.tasks.registerVerifyPlatformTableTask
import java.time.Year
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name
import kotlin.io.path.readText
import java.nio.file.Path
import kotlin.io.path.createFile

plugins {
    id("org.jetbrains.dokka")
}

// useful for dependencies introspection
// run ./gradlew htmlDependencyReport
// Report can normally be found in build/reports/project/dependencies/index.html
allprojects {
    plugins.apply("project-report")
}

dokka {
    val libVersion = libs.versions.kotlinx.rpc.get()

    moduleVersion.set(libVersion)

    val pagesDirectory = layout.projectDirectory
        .dir("docs")
        .dir("pages")

    val dokkaVersionsDirectory = pagesDirectory
        .dir("api")
        .asFile

    val templatesDirectory = pagesDirectory
        .dir("templates")

    pluginsConfiguration {
        html {
            customAssets.from(
                "docs/pages/assets/logo-icon.svg",
                "docs/pages/assets/homepage.svg", // Doesn't work due to https://github.com/Kotlin/dokka/issues/4007
            )

            footerMessage = "© ${Year.now()} JetBrains s.r.o and contributors. Apache License 2.0"
            homepageLink = "https://kotlin.github.io/kotlinx-rpc/get-started.html"

            // replace with homepage.svg once the mentioned issue is resolved
            templatesDir.set(templatesDirectory)
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

fun Project.forEachSubproject(action: (String, Path, Path) -> Unit) {
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
    forEachSubproject { rootProperties, _, subProjectProperties ->
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

    forEachSubproject { rootProperties, parent, subProjectProperties ->
        if (!subProjectProperties.exists() || subProjectProperties.readText() != rootProperties) {
            throw GradleException(
                "'gradle.properties' file in ${parent.name} included project is not up-to-date with root. " +
                        "Please, run `./gradlew ${updateProperties.name}"
            )
        }
    }
}
