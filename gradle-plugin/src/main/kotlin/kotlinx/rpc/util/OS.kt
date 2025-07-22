/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.util

import org.gradle.api.GradleException
import java.io.File

internal object OS {
    internal val javaExePath: String by lazy {
        val java = File(System.getProperty("java.home"), if (isWindows) "bin/java.exe" else "bin/java")

        if (!java.exists()) {
            throw GradleException("Could not find java executable at " + java.path)
        }

        java.path
    }

    internal val isWindows: Boolean by lazy {
        System.getProperty("os.name").lowercase().contains("win")
    }
}
