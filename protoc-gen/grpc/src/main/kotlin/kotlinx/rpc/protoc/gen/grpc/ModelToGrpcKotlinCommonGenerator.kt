/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protoc.gen.grpc

import kotlinx.rpc.protoc.gen.core.AModelToKotlinCommonGenerator
import kotlinx.rpc.protoc.gen.core.CodeGenerator
import kotlinx.rpc.protoc.gen.core.model.FileDeclaration
import kotlinx.rpc.protoc.gen.core.model.Model
import kotlinx.rpc.protoc.gen.core.model.ServiceDeclaration
import org.slf4j.Logger

class ModelToGrpcKotlinCommonGenerator(
    model: Model,
    logger: Logger,
) : AModelToKotlinCommonGenerator(model, logger) {
    override val FileDeclaration.hasPublicGeneratedContent: Boolean get() = serviceDeclarations.isNotEmpty()
    override val FileDeclaration.hasInternalGeneratedContent: Boolean get() = false

    override fun CodeGenerator.generatePublicDeclaredEntities(fileDeclaration: FileDeclaration) {
        fileDeclaration.serviceDeclarations.forEach { generatePublicService(it) }
    }

    override fun CodeGenerator.generateInternalDeclaredEntities(fileDeclaration: FileDeclaration) { }

    init {
        additionalPublicImports.add("kotlinx.coroutines.flow.Flow")
    }

    @Suppress("detekt.LongMethod")
    private fun CodeGenerator.generatePublicService(service: ServiceDeclaration) {
        code("@kotlinx.rpc.grpc.annotations.Grpc")
        clazz(service.name.simpleName, declarationType = CodeGenerator.DeclarationType.Interface) {
            service.methods.forEach { method ->
                val inputType = method.inputType
                val outputType = method.outputType
                function(
                    name = method.name,
                    modifiers = if (method.dec.isServerStreaming) "" else "suspend",
                    args = "message: ${inputType.name.safeFullName().wrapInFlowIf(method.dec.isClientStreaming)}",
                    returnType = outputType.name.safeFullName().wrapInFlowIf(method.dec.isServerStreaming),
                )
            }
        }
    }
}
