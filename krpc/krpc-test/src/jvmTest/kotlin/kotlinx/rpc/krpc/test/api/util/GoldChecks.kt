/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.test.api.util

import kotlinx.rpc.krpc.test.api.util.GoldUtils.GOLD_EXTENSION
import kotlinx.rpc.krpc.test.api.util.GoldUtils.TMP_EXTENSION
import java.io.File
import java.nio.file.Path

interface GoldComparable<T : GoldComparable<T>> {
    fun compare(other: T): GoldComparisonResult

    fun dump(): String
}

@Suppress("ConvertObjectToDataObject") // unavailable in earlier Kotlin versions 
sealed interface GoldComparisonResult {
    object Ok : GoldComparisonResult

    class Failure(val message: String) : GoldComparisonResult
}

class StringGoldContent(private val value: String) : GoldComparable<StringGoldContent> {
    override fun compare(other: StringGoldContent): GoldComparisonResult {
        return when {
            value.removeLineSeparators() == other.value.removeLineSeparators() -> GoldComparisonResult.Ok
            else -> GoldComparisonResult.Failure(
                "Gold comparison failed\n" +
                        "Gold:\n ${other.value.removeLineSeparators()}\n\n" +
                        "Actual:\n ${value.removeLineSeparators()}"
            )
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
        Please, review and commit:
        ${goldFileLine(goldPath)}
        Temp file: file://${file.absolutePath}
        Reason: $reason
        Run 'moveToGold' Gradle task to update gold files.
    """.trimIndent()
}

private fun goldFileLine(goldPath: String?): String {
    return if (goldPath != null) {
        "Gold File: file://$goldPath"
    } else {
        "No previous Gold file"
    }
}

private val isCI = System.getenv("TEAMCITY_VERSION") != null ||
        System.getenv("GITHUB_ACTIONS") != null

object GoldUtils {
    val NewLine: String = System.lineSeparator()

    const val GOLD_EXTENSION = "gold"
    const val TMP_EXTENSION = "tmp"
}

fun String.goldFile() = "$this.$GOLD_EXTENSION"
