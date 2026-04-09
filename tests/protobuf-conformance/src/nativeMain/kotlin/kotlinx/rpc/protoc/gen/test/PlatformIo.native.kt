/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protoc.gen.test

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.toKString
import kotlinx.cinterop.usePinned
import platform.posix.STDIN_FILENO
import platform.posix.STDOUT_FILENO
import platform.posix.fclose
import platform.posix.fopen
import platform.posix.fwrite
import platform.posix.getenv
import platform.posix.read
import platform.posix.write

@OptIn(ExperimentalForeignApi::class)
internal actual fun platformRead(buf: ByteArray, offset: Int, length: Int): Int {
    if (length == 0) return 0
    return buf.usePinned { pinned ->
        val bytesRead = read(STDIN_FILENO, pinned.addressOf(offset), length.convert())
        if (bytesRead <= 0) -1 else bytesRead.toInt()
    }
}

@OptIn(ExperimentalForeignApi::class)
internal actual fun platformWrite(buf: ByteArray) {
    if (buf.isEmpty()) return
    buf.usePinned { pinned ->
        var offset = 0
        while (offset < buf.size) {
            val written = write(STDOUT_FILENO, pinned.addressOf(offset), (buf.size - offset).convert())
            if (written <= 0) error("Failed to write to stdout")
            offset += written.toInt()
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
internal actual fun getEnvVariable(name: String): String? {
    return getenv(name)?.toKString()
}

@OptIn(ExperimentalForeignApi::class)
internal actual fun writeFile(path: String, bytes: ByteArray) {
    val file = fopen(path, "wb") ?: error("Cannot open file: $path")
    try {
        if (bytes.isNotEmpty()) {
            bytes.usePinned { pinned ->
                val written = fwrite(pinned.addressOf(0), 1u.convert(), bytes.size.convert(), file)
                if (written.toInt() != bytes.size) error("Failed to write all bytes to: $path")
            }
        }
    } finally {
        fclose(file)
    }
}
