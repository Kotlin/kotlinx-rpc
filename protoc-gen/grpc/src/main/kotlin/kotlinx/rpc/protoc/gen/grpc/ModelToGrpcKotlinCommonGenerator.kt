/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protoc.gen.grpc

import com.google.protobuf.DescriptorProtos
import kotlinx.rpc.protoc.gen.core.AModelToKotlinCommonGenerator
import kotlinx.rpc.protoc.gen.core.CodeGenerator
import kotlinx.rpc.protoc.gen.core.Config
import kotlinx.rpc.protoc.gen.core.model.FileDeclaration
import kotlinx.rpc.protoc.gen.core.model.FqName
import kotlinx.rpc.protoc.gen.core.model.Model
import kotlinx.rpc.protoc.gen.core.model.ServiceDeclaration

class ModelToGrpcKotlinCommonGenerator(
    config: Config,
    model: Model,
) : AModelToKotlinCommonGenerator(config, model) {
    override val FileDeclaration.hasPublicGeneratedContent: Boolean get() = serviceDeclarations.isNotEmpty()
    override val FileDeclaration.hasInternalGeneratedContent: Boolean get() = false

    override fun CodeGenerator.generatePublicDeclaredEntities(fileDeclaration: FileDeclaration) {
        additionalPublicImports.add("kotlinx.coroutines.flow.Flow")
        fileDeclaration.serviceDeclarations.forEach { generatePublicService(it, fileDeclaration.packageName) }
    }

    override fun CodeGenerator.generateInternalDeclaredEntities(fileDeclaration: FileDeclaration) {}

    @Suppress("detekt.LongMethod")
    private fun CodeGenerator.generatePublicService(service: ServiceDeclaration, packageName: FqName.Package) {
        val pkg = service.dec.file.`package`.orEmpty()
        val annotationParams = if (pkg.isNotEmpty() && pkg != packageName.safeFullName()) """(protoPackage = "$pkg")""" else ""

        clazz(
            name = service.name.simpleName,
            comment = service.doc,
            declarationType = CodeGenerator.DeclarationType.Interface,
            annotations = listOf("@kotlinx.rpc.grpc.annotations.Grpc$annotationParams"),
            deprecation = if (service.deprecated) DeprecationLevel.WARNING else null,
        ) {
            service.methods.forEach { method ->
                val inputType = method.inputType
                val outputType = method.outputType
                val annotations = when (method.dec.options.idempotencyLevel) {
                    null, DescriptorProtos.MethodOptions.IdempotencyLevel.IDEMPOTENCY_UNKNOWN -> emptyList()
                    DescriptorProtos.MethodOptions.IdempotencyLevel.IDEMPOTENT -> listOf("@kotlinx.rpc.grpc.annotations.Grpc.Method(idempotent = true)")
                    DescriptorProtos.MethodOptions.IdempotencyLevel.NO_SIDE_EFFECTS -> listOf("@kotlinx.rpc.grpc.annotations.Grpc.Method(idempotent = true, safe = true)")
                }

                function(
                    name = method.name,
                    comment = method.doc,
                    modifiers = if (method.dec.isServerStreaming) "" else "suspend",
                    args = "message: ${inputType.value.name.safeFullName().wrapInFlowIf(method.dec.isClientStreaming)}",
                    annotations = annotations,
                    deprecation = if (method.deprecated) DeprecationLevel.WARNING else null,
                    returnType = outputType.value.name.safeFullName().wrapInFlowIf(method.dec.isServerStreaming),
                )
            }
        }
    }
}
