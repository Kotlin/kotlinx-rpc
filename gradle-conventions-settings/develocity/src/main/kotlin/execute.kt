/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

fun execProcess(vararg cmd: String, workDir: java.io.File? = null): String {
    return try {
        val process = ProcessBuilder(*cmd)
            .redirectErrorStream(true)
            .apply { if (workDir != null) directory(workDir) }
            .start()
        val output = process.inputStream.bufferedReader().use { it.readText() }.trim()
        process.waitFor()
        output
    } catch (_: java.io.IOException) {
        ""
    }
}
