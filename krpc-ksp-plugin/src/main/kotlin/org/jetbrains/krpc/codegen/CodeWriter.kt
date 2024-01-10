/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc.codegen

import java.io.BufferedWriter

fun BufferedWriter.codeWriter() = CodeWriter(this)

class CodeWriter(private val bufferedWriter: BufferedWriter, private val depth: Int = 0) {
    fun write(str: String) {
        bufferedWriter.write(TAB.repeat(depth) + str)
    }

    fun newLine() {
        bufferedWriter.newLine()
    }

    fun flush() {
        bufferedWriter.flush()
    }

    fun nested() = CodeWriter(bufferedWriter, depth + 1)

    companion object {
        private const val TAB = "    "
    }
}
