/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf

import com.google.protobuf.compiler.PluginProtos

fun main() {
    val inputBytes = System.`in`.readBytes()
    val request = PluginProtos.CodeGeneratorRequest.parseFrom(inputBytes)
    val plugin = RpcProtobufPlugin()
    val output: PluginProtos.CodeGeneratorResponse = plugin.run(request)
    output.writeTo(System.out)
}
