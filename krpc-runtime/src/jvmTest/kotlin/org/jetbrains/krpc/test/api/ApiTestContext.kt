/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc.test.api

import java.io.File
import java.nio.file.Path
import java.util.*
import kotlin.io.path.Path
import kotlin.io.path.createTempFile
import kotlin.reflect.KClass

class ApiTestContext {
    val fails = mutableListOf<String>()

    private val sampled = mutableSetOf<KClass<*>>()

    fun KClass<*>.isSampled(): Boolean {
        return sampled.contains(this)
    }

    fun KClass<*>.markSampled() {
        sampled.add(this)
    }

    fun compareAndDump(clazz: KClass<*>, currentContent: String) {
        sampled.add(clazz)

        val (gold, goldPath) = clazz.loadGold() ?: (null to null)

        if (gold == null || gold.removeLineSeparators() != currentContent.removeLineSeparators()) {
            val log = clazz.writeTmp(currentContent, goldPath)

            val action = if (gold == null) "created" else "changed"

            fails.add(
                "Protocol API class '${clazz.simpleName}' was $action. $log"
            )
        }
    }

    private fun KClass<*>.loadGold(): Pair<String, String>? {
        return DUMPS_DIR.resolve("$simpleName.gold").toFile().let { file ->
            if (file.exists()) file.readText(Charsets.UTF_8) to file.absolutePath else null
        }
    }

    private fun KClass<*>.writeTmp(content: String, goldPath: String?): String {
        if (isCI) {
            return "Attempting to write temp files on CI"
        }

        val file = DUMPS_DIR.resolve("$simpleName.tmp").toFile()

        if (file.exists()) {
            file.delete()
        }
        val dir = File(file.parent)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        file.writeText(content, Charsets.UTF_8)

        return """
            Please, review and commit:
            ${
            if (goldPath != null) {
                "Gold File: file://$goldPath"
            } else {
                "No previous Gold file"
            }
        }
            Temp file: file://${file.absolutePath}
            Move to Gold: file://${createMoveToGoldScript(file.absolutePath)}
        """.trimIndent()
    }

    private fun createMoveToGoldScript(tempFile: String): String {
        val goldFile = tempFile.replace(".tmp", ".gold")
        return createTempScriptFile {
            appendText("#!/bin/sh")
            appendText(NewLine)
            val command = "${if (isUnix) "mv -f" else "move /y"} \"$tempFile\" \"$goldFile\""
            appendText(command)
        }
    }

    companion object {
        private val LIBRARY_VERSION = System.getenv("LIBRARY_VERSION")?.versionToDirName()
            ?: error("Expected LIBRARY_VERSION env variable")

        val DUMPS_DIR: Path = Path("src/jvmTest/resources/class_dumps/$LIBRARY_VERSION")

        private fun String.versionToDirName(): String {
            return replace('.', '_').replace('-', '_')
        }

        val NewLine: String = System.lineSeparator()
    }
}

private fun String.removeLineSeparators() =
    replace("\n", "")
        .replace("\r", "")

private fun createTempScriptFile(write: File.() -> Unit): String {
    val realExtension = if (isUnix) "sh" else "bat"
    val tmpExtension = "tmp" // cannot create temp file with .sh extension (too short)

    val tempFile = createTempFile(tmpExtension).toFile().apply(write)

    val realFile = File(tempFile.absolutePath.replace(".$tmpExtension", ".$realExtension"))
    tempFile.renameTo(realFile)
    realFile.setExecutable(true)

    return realFile.absolutePath
}

private val isUnix = !System.getProperty("os.name").lowercase(Locale.getDefault()).contains("win")
private val isCI = System.getenv("TEAMCITY_VERSION") != null

private class SemVer(val value: String) : Comparable<SemVer> {
    override fun compareTo(other: SemVer): Int {
        val isBeta = value.contains("beta")
        val otherIsBeta = other.value.contains("beta")

        return when {
            isBeta && otherIsBeta -> value.compareTo(other.value)
            isBeta -> -1
            otherIsBeta -> 1
            else -> value.compareTo(other.value)
        }
    }
}
