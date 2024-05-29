/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import java.nio.file.Path

object Const {
    const val KOTLIN_MULTIPLATFORM_PLUGIN_ID = "org.jetbrains.kotlin.multiplatform"
    const val KOTLIN_JVM_PLUGIN_ID = "org.jetbrains.kotlin.jvm"

    const val SERVICE_LOADER_MODULE = ":kotlinx-rpc-utils:kotlinx-rpc-utils-service-loader"

    const val CORE_SOURCE_SET = "core"
    const val LATEST_SOURCE_SET = "latest"

    const val RESOURCES = "resources"
}

fun capitalize(string: String): String {
    if (string.isEmpty()) {
        return ""
    }
    val firstChar = string[0]
    return string.replaceFirst(firstChar, Character.toTitleCase(firstChar))
}

fun Path.name() = fileName?.toString().orEmpty()
