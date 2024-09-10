/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util

import org.gradle.api.Project
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

fun Path.name() = fileName?.toString().orEmpty()

fun filterDirectory(sourceSetPath: Path, filter: (Path) -> Boolean): List<File> {
    return Files.newDirectoryStream(sourceSetPath).use { it.toList() }.filter(filter).map { it.toFile() }
}

val Project.files: Array<File> get() = project.projectDir.resolve("src").listFiles() ?: emptyArray()
