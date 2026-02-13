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
import kotlinx.rpc.protoc.gen.core.model.fullName
import kotlinx.rpc.protoc.gen.core.scoped
import kotlinx.rpc.protoc.gen.core.wrapIn

class ModelToGrpcKotlinCommonGenerator(
    config: Config,
    model: Model,
) : AModelToKotlinCommonGenerator(config, model) {
    override val FileDeclaration.hasPublicGeneratedContent: Boolean get() = serviceDeclarations.isNotEmpty()
    override val FileDeclaration.hasInternalGeneratedContent: Boolean get() = false
    override val FileDeclaration.hasExtensionGeneratedContent: Boolean get() = false

    override fun CodeGenerator.generatePublicDeclaredEntities(fileDeclaration: FileDeclaration) {
        fileDeclaration.serviceDeclarations.forEach { generatePublicService(it, fileDeclaration.packageName) }
    }

    override fun CodeGenerator.generateExtensionEntities(fileDeclaration: FileDeclaration) {}

    override fun CodeGenerator.generateInternalDeclaredEntities(fileDeclaration: FileDeclaration) {}

    @Suppress("detekt.LongMethod")
    private fun CodeGenerator.generatePublicService(service: ServiceDeclaration, packageName: FqName.Package) {
        val pkg = service.dec.file.`package`.orEmpty()
        val annotationParams = if (pkg.isNotEmpty() && pkg != packageName.fullName()) """(protoPackage = "$pkg")""" else ""

        clazz(
            name = service.name.simpleName,
            comment = service.doc,
            declarationType = CodeGenerator.DeclarationType.Interface,
            annotations = listOf(
                "@%T$annotationParams".scoped(FqName.Annotations.Grpc),
            ),
            deprecation = if (service.deprecated) DeprecationLevel.WARNING else null,
        ) {
            service.methods.forEach { method ->
                val inputType = method.inputType
                val outputType = method.outputType
                val annotations = when (method.dec.options.idempotencyLevel) {
                    null, DescriptorProtos.MethodOptions.IdempotencyLevel.IDEMPOTENCY_UNKNOWN -> {
                        emptyList()
                    }

                    DescriptorProtos.MethodOptions.IdempotencyLevel.IDEMPOTENT -> {
                        listOf("@%T(idempotent = true)".scoped(FqName.Annotations.GrpcMethod))
                    }

                    DescriptorProtos.MethodOptions.IdempotencyLevel.NO_SIDE_EFFECTS -> {
                        listOf("@%T(idempotent = true, safe = true)".scoped(FqName.Annotations.GrpcMethod))
                    }
                }

                val arg = inputType.value.name.scoped()
                    .wrapInFlowIf(method.dec.isClientStreaming)
                    .wrapIn { "message: $it" }

                function(
                    name = method.name,
                    comment = method.doc,
                    modifiers = if (method.dec.isServerStreaming) "" else "suspend",
                    args = arg,
                    annotations = annotations,
                    deprecation = if (method.deprecated) DeprecationLevel.WARNING else null,
                    returnType = outputType.value.name.scoped().wrapInFlowIf(method.dec.isServerStreaming),
                )
            }
        }
    }
}
