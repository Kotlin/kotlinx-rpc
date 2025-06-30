/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util.csm

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.nio.file.Path
import javax.inject.Inject
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import kotlin.io.path.readLines
import kotlin.io.path.walk
import kotlin.io.path.writeLines

abstract class ProcessCsmTemplate @Inject constructor(
    @get:Input val kotlinComplierVersion: String,
    @get:InputDirectory val templatesDir: Provider<Path>,
    @get:OutputDirectory val sourcesDir: Provider<Path>,
) : DefaultTask() {
    @OptIn(ExperimentalPathApi::class)
    @TaskAction
    fun process() {
        val templates = templatesDir.get()
        if (!templates.exists()) {
            return
        }

        val sources = sourcesDir.get()

        templates.walk().forEach { file ->
            if (file.isDirectory()) {
                return@forEach
            }

            val out = sources.resolve(templates.relativize(file))
            val lines = CsmTemplateProcessor.process(
                lines = file.readLines(Charsets.UTF_8),
                kotlinCompilerVersion = kotlinComplierVersion,
                logger = logger,
            )
            out.parent.toFile().mkdirs()
            out.writeLines(lines, charset = Charsets.UTF_8)
        }
    }
}
