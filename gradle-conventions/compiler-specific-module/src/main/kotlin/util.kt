/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import java.nio.file.Path
import java.util.*

const val KOTLIN_MULTIPLATFORM_PLUGIN_ID = "org.jetbrains.kotlin.multiplatform"
const val KOTLIN_JVM_PLUGIN_ID = "org.jetbrains.kotlin.jvm"

const val MAIN_SOURCE_SET = "main"

const val CORE_SOURCE_DIR = "core"
const val LATEST_SOURCE_DIR = "latest"

const val MAIN_RESOURCES = "main-resources"
const val TEST_RESOURCES = "test-resources"
const val PROCESS_RESOURCES = "processResources"
const val PROCESS_TEST_RESOURCES = "processTestResources"

fun capitalize(string: String): String {
    if (string.isEmpty()) {
        return ""
    }
    val firstChar = string[0]
    return string.replaceFirst(firstChar, Character.toTitleCase(firstChar))
}

@Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN", "NOTHING_TO_INLINE")
inline fun String.lowercase(): String = (this as java.lang.String).toLowerCase(Locale.ROOT)

fun Path.name() = fileName?.toString().orEmpty()
