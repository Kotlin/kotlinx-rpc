/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.test.api

import kotlinx.rpc.krpc.internal.CancellationType
import kotlinx.rpc.krpc.internal.KrpcMessage
import kotlinx.rpc.krpc.internal.KrpcPlugin
import kotlinx.rpc.krpc.internal.KrpcPluginKey
import kotlinx.rpc.krpc.test.api.util.GoldUtils.NewLine
import org.junit.Test
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.test.fail

class ApiVersioningTest {
    @Test
    fun testProtocolApiVersion() {
        val context = checkProtocolApi<KrpcMessage>()

        context.fails.failIfAnyCauses()
    }

    @Test
    fun testRpcPluginEnum() {
        testEnum<KrpcPlugin>()
    }

    @Test
    fun testRpcPluginKeyEnum() {
        testEnum<KrpcPluginKey>()
    }

    @Test
    fun testCancellationType() {
        testEnum<CancellationType>()
    }

    companion object {
        val CLASS_DUMPS_DIR: Path = Path("src/jvmTest/resources/class_dumps/")

        val INDEXED_ENUM_DUMPS_DIR: Path = Path("src/jvmTest/resources/indexed_enum_dumps/")
    }
}

fun List<String>.failIfAnyCauses() {
    if (isNotEmpty()) {
        fail(joinToString(NewLine.repeat(2), NewLine, NewLine))
    }
}
