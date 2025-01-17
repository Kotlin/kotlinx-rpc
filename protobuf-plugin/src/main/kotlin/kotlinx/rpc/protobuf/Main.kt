/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf

import com.google.protobuf.compiler.PluginProtos

// todo
//  type resolution (avoid over qualified types)
//  comments
//  extensions
//  maps
//  kmp sources sets
//  platform specific bindings
//  common API
//  DSL builders
//  kotlin_multiple_files, kotlin_package options
//  library proto files
//  explicit API mode
//  services
//    unfolded types overloads
//    nested streams
//

fun main() {
    val inputBytes = System.`in`.readBytes()
    val request = PluginProtos.CodeGeneratorRequest.parseFrom(inputBytes)
    val plugin = RpcProtobufPlugin()
    val output: PluginProtos.CodeGeneratorResponse = plugin.run(request)
    output.writeTo(System.out)
}
