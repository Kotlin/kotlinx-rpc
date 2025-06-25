/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.gradle.api.GradleException
import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.extra
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.io.path.bufferedWriter
import kotlin.io.path.createFile
import kotlin.io.path.exists

private const val TERMS_OF_USE_PROPERTY = "kotlinx.rpc.develocity.termsOfUseAgree"

fun Settings.loadAgreement(): String {
    if (isCIRun) {
        return "yes"
    }

    val localProperties = extra["localProperties"] as? java.util.Properties
        ?: throw GradleException("'local.properties' property not found")

    when (val value = localProperties.getProperty(TERMS_OF_USE_PROPERTY)) {
        "yes", "no" -> {
            return value
        }

        "" -> {
            throw GradleException(
                "'$TERMS_OF_USE_PROPERTY' property is not set in file://local.properties'. " +
                        "Please set this property to 'yes' or 'no'."
            )
        }

        null -> {
            val globalRootDir = extra["globalRootDir"] as? String
                ?: throw GradleException("'globalRootDir' property not found. Contact developers.")

            val propFile = Path.of(globalRootDir).resolve("local.properties")
            if (!propFile.exists()) {
                propFile.createFile()
            }

            propFile.bufferedWriter(Charsets.UTF_8, bufferSize = 1024, StandardOpenOption.APPEND).use { writer ->
                writer.appendLine()
                writer.appendLine("# Terms of Gradle use agreement: https://gradle.com/terms-of-service")
                writer.appendLine("# Set to yes or no")
                writer.appendLine("# Only needed for JetBrains maintainers")
                writer.appendLine("$TERMS_OF_USE_PROPERTY=")
            }

            throw GradleException(
                "'$TERMS_OF_USE_PROPERTY' property not found in file://local.properties . " +
                        "Please add this property and set it to 'yes' or 'no'."
            )
        }

        else -> {
            throw GradleException(
                "Invalid value for '$TERMS_OF_USE_PROPERTY' property: $value. " +
                        "Please set this property to 'yes' or 'no'."
            )
        }
    }
}
