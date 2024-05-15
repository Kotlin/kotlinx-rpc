/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.test.api.util

import kotlinx.rpc.test.api.util.GoldUtils.GOLD_EXTENSION
import kotlinx.rpc.test.api.util.GoldUtils.TMP_EXTENSION
import java.io.File
import java.nio.file.Path
import java.util.*

interface GoldComparable<T : GoldComparable<T>> {
    fun compare(other: T): GoldComparisonResult

    fun dump(): String
}

@Suppress("ConvertObjectToDataObject") // unavailable in earlier Kotlin versions 
sealed interface GoldComparisonResult {
    object Ok : GoldComparisonResult

    class Failure(val message: String? = null) : GoldComparisonResult
}

class StringGoldContent(private val value: String) : GoldComparable<StringGoldContent> {
    override fun compare(other: StringGoldContent): GoldComparisonResult {
        return when {
            value.removeLineSeparators() == other.value.removeLineSeparators() -> GoldComparisonResult.Ok
            else -> GoldComparisonResult.Failure()
        }
    }

    override fun dump(): String {
        return value
    }

    private fun String.removeLineSeparators() =
        replace("\n", "")
            .replace("\r", "")
}

/**
 * Returns log if tmp file was created, null otherwise
 */
fun <T : GoldComparable<T>> checkGold(
    latestDir: Path,
    currentDir: Path,
    filename: String,
    content: T,
    parseGoldFile: (String) -> T,
): String? {
    val (gold, goldPath) = loadGold(latestDir, filename, parseGoldFile) ?: (null to null)

    val comparison = gold?.let { content.compare(gold) }
        ?: GoldComparisonResult.Failure("No previous Gold file")

    if (comparison is GoldComparisonResult.Failure) {
        return writeTmp(currentDir, filename, content.dump(), goldPath, comparison.message)
    }

    return null
}

// ============ FILE UTILS ============

private fun <T : GoldComparable<T>> loadGold(
    fileDir: Path,
    filename: String,
    parseGoldFile: (String) -> T,
): Pair<T, String>? {
    return fileDir.resolve(filename.goldFile()).toFile().let { file ->
        if (file.exists()) parseGoldFile(file.readText(Charsets.UTF_8)) to file.absolutePath else null
    }
}

private fun writeTmp(
    fileDir: Path,
    filename: String,
    content: String,
    goldPath: String?,
    reason: String?,
): String {
    if (isCI) {
        return "Attempting to write temp files on CI: $fileDir/$filename, reason: $reason"
    }

    val file = fileDir.resolve("$filename.$TMP_EXTENSION").toFile()

    if (file.exists()) {
        file.delete()
    }
    val dir = File(file.parent)
    if (!dir.exists()) {
        dir.mkdirs()
    }
    file.writeText(content, Charsets.UTF_8)

    return """
        ${reason?.let { "Failure reason: $it" } ?: ""}
        Please, review and commit:
        ${goldFileLine(goldPath)}
        Temp file: file://${file.absolutePath}
        Move to Gold: file://${createMoveToGoldScript(file.absolutePath)}
    """.trimIndent()
}

private fun goldFileLine(goldPath: String?): String {
    return if (goldPath != null) {
        "Gold File: file://$goldPath"
    } else {
        "No previous Gold file"
    }
}

private fun createMoveToGoldScript(tempFile: String): String {
    val goldFile = tempFile.replace(".$TMP_EXTENSION", ".$GOLD_EXTENSION")
    return createTempScriptFile {
        appendText("#!/bin/sh")
        appendText(GoldUtils.NewLine)
        val command = "${if (isUnix) "mv -f" else "move /y"} \"$tempFile\" \"$goldFile\""
        appendText(command)
    }
}

private fun createTempScriptFile(write: File.() -> Unit): String {
    val realExtension = if (isUnix) "sh" else "bat"
    val tmpExtension = "tmp" // cannot create temp file with .sh extension (too short)

    val tempFile = kotlin.io.path.createTempFile(tmpExtension).toFile().apply(write)

    val realFile = File(tempFile.absolutePath.replace(".$tmpExtension", ".$realExtension"))
    tempFile.renameTo(realFile)
    realFile.setExecutable(true)

    return realFile.absolutePath
}

private val isUnix = !System.getProperty("os.name").lowercase(Locale.getDefault()).contains("win")
private val isCI = System.getenv("TEAMCITY_VERSION") != null

object GoldUtils {
    val NewLine: String = System.lineSeparator()

    const val GOLD_EXTENSION = "gold"
    const val TMP_EXTENSION = "tmp"
}

fun String.goldFile() = "$this.$GOLD_EXTENSION"
