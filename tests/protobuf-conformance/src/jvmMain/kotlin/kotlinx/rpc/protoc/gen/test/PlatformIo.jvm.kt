/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protoc.gen.test

internal actual fun platformRead(buf: ByteArray, offset: Int, length: Int): Int {
    return System.`in`.read(buf, offset, length)
}

internal actual fun platformWrite(buf: ByteArray) {
    System.out.write(buf)
    System.out.flush()
}

internal actual fun getEnvVariable(name: String): String? {
    return System.getenv(name)
}

internal actual fun writeFile(path: String, bytes: ByteArray) {
    java.io.File(path).writeBytes(bytes)
}
