/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.register
import java.io.File
import kotlin.io.path.createDirectories
import kotlin.io.path.createFile
import kotlin.io.path.exists
import kotlin.io.path.readLines
import kotlin.io.path.writeLines

private val ROOT_CHANGELOG_PATH = File("CHANGELOG.md")
private val DOCS_CHANGELOG_PATH = File("docs/pages/kotlinx-rpc/topics/changelog.md")

private val PULL_REGEX = "https://github.com/Kotlin/kotlinx-rpc/pull/(\\d+)".toRegex()
private val COMPARE_REGEX = "https://github.com/Kotlin/kotlinx-rpc/compare/([+.\\w_-]+)".toRegex()
private val USERNAME_REGEX = "@([\\w_-]+)".toRegex()
private val WHITESPACE = "\\s+".toRegex()

abstract class UpdateDocsChangelog : DefaultTask() {
    @get:InputFile
    abstract val input: Property<File>

    @get:OutputFile
    abstract val output: Property<File>

    @TaskAction
    fun update() {
        val inputPath = input.get().toPath()
        val outputPath = output.get().toPath()

        if (!inputPath.exists()) {
            throw GradleException("fatal error: input file $inputPath does not exist")
        }

        var currentRelease = ""
        val fullChangelogLines = mutableListOf<String>()
        val lines = inputPath.readLines(Charsets.UTF_8).flatMap { line ->
            val updated = line
                .replace(PULL_REGEX) {
                    "[#${it.groupValues[1]}](${it.groupValues[0]})"
                }
                .replace(COMPARE_REGEX) {
                    "[${it.groupValues[1]}](${it.groupValues[0]})"
                }
                .replace(USERNAME_REGEX) {
                    "[${it.groupValues[0]}](https://github.com/${it.groupValues[1]})"
                }.let {
                    if (it.startsWith("#")) {
                        "#$it"
                    } else {
                        it
                    }
                }

            if (updated.startsWith("## ")) {
                currentRelease = updated
                    .drop(3)
                    .replace(".", "_")
            }

            when {
                updated.startsWith("###") -> {
                    val name = updated
                        .dropWhile { it == '#' }
                        .dropLastWhile { !it.isDigit() && !it.isLetter() }
                        .trim()
                        .replace(WHITESPACE, "_")

                    listOf("$updated {id=${name}_$currentRelease}")
                }

                updated.startsWith("**Full Changelog**:") -> {
                    fullChangelogLines.add(updated)
                    emptyList()
                }

                else -> listOf(updated)
            }
        }

        val result = mutableListOf<String>()

        var i = 0
        var fci = 0
        while (i < lines.size) {
            val line = lines[i]
            result.add(line)

            if (line.startsWith("## ")) {
                result.add(lines[i + 1])
                result.add("")
                result.add(fullChangelogLines[fci++])
                i++
            }

            i++
        }

        if (!outputPath.exists()) {
            outputPath.parent.createDirectories()
            outputPath.createFile()
        }

        val header = listOf(
            "# Changelog",
            "",
            "This page contains all changes throughout releases of the library.",
            "",
        )

        outputPath.writeLines(header + result)
    }
}

fun Project.registerChangelogTask() {
    tasks.register<UpdateDocsChangelog>("updateDocsChangelog") {
        input.set(ROOT_CHANGELOG_PATH)
        output.set(DOCS_CHANGELOG_PATH)
    }
}
