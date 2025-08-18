/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protoc.gen

import kotlinx.rpc.protoc.gen.core.FileGenerator
import kotlinx.rpc.protoc.gen.core.ProtocGenPlugin
import kotlinx.rpc.protoc.gen.core.model.Model
import org.slf4j.Logger

object ProtobufProtocGenPlugin : ProtocGenPlugin() {
    override fun generateKotlinByModel(
        model: Model,
        logger: Logger,
        explicitApiModeEnabled: Boolean,
    ): List<FileGenerator> {
        return ModelToProtobufKotlinCommonGenerator(model, logger, explicitApiModeEnabled).generateKotlinFiles()
    }
}
