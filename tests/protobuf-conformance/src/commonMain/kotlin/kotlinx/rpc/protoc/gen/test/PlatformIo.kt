/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protoc.gen.test

/**
 * Reads up to [length] bytes from stdin into [buf] starting at [offset].
 * Returns the number of bytes actually read, or -1 on EOF.
 */
internal expect fun platformRead(buf: ByteArray, offset: Int, length: Int): Int

/**
 * Writes all bytes of [buf] to stdout and flushes.
 */
internal expect fun platformWrite(buf: ByteArray)

/**
 * Returns the value of the environment variable [name], or null if not set.
 */
internal expect fun getEnvVariable(name: String): String?

/**
 * Writes [bytes] to the file at [path], creating or overwriting as needed.
 */
internal expect fun writeFile(path: String, bytes: ByteArray)
