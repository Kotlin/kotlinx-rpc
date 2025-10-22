/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import util.csm.ProcessCsmTemplate
import util.other.libs
import kotlin.io.path.createDirectories

val kotlin = the<KotlinJvmProjectExtension>()

val mainSourceSet = kotlin.sourceSets.named("main")
val templatesDir = mainSourceSet.map {
    it.kotlin.srcDirs.single { dir -> dir.name == "kotlin" }.toPath().parent.resolve("templates")
}

templatesDir.get().createDirectories()

val sourcesDir = project.layout.buildDirectory.asFile.map {
    it.toPath().resolve("generated-sources/csm")
}

val processCsmTemplates =
    tasks.register<ProcessCsmTemplate>(
        "processCsmTemplates",
        libs.versions.kotlin.compiler.get(),
        emptyMap<String, String>(),
        templatesDir,
        sourcesDir,
    )

mainSourceSet.configure {
    kotlin.srcDirs(processCsmTemplates.map { it.sourcesDir })
}
