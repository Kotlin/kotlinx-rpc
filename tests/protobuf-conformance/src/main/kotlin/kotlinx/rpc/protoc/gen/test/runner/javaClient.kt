/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protoc.gen.test.runner

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.PosixFilePermission
import kotlin.io.path.writeText

fun getJavaClient(jarPath: String, mode: String): Path {
    val executable = Files.createTempFile("clientRunner", ".sh")

    Files.setPosixFilePermissions(executable, PosixFilePermission.entries.toSet())

    executable.writeText(
        """
            #!/bin/bash

            java -jar $jarPath $mode
            
        """.trimIndent()
    )

    return executable
}
