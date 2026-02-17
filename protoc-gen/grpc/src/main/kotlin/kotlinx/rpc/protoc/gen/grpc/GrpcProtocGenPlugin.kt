/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protoc.gen.grpc

import kotlinx.rpc.protoc.gen.core.Config
import kotlinx.rpc.protoc.gen.core.FileGenerator
import kotlinx.rpc.protoc.gen.core.ProtocGenPlugin
import kotlinx.rpc.protoc.gen.core.model.Model

object GrpcProtocGenPlugin : ProtocGenPlugin() {
    override fun generateKotlinByModel(
        config: Config,
        model: Model,
    ): List<FileGenerator> {
        return ModelToGrpcKotlinCommonGenerator(config, model).generateKotlinFiles()
    }
}
