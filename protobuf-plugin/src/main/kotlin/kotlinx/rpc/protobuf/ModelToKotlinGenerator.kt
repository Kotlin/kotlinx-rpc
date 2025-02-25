/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf

import kotlinx.rpc.protobuf.CodeGenerator.DeclarationType
import kotlinx.rpc.protobuf.model.*
import org.slf4j.Logger
import kotlin.sequences.forEach

private const val RPC_INTERNAL_PACKAGE_SUFFIX = "_rpc_internal"

class ModelToKotlinGenerator(
    private val model: Model,
    private val logger: Logger,
    private val codeGenerationParameters: CodeGenerationParameters,
) {
    fun generateKotlinFiles(): List<FileGenerator> {
        return model.files.flatMap { it.generateKotlinFiles() }
    }

    private fun FileDeclaration.generateKotlinFiles(): List<FileGenerator> {
        additionalPublicImports.clear()
        additionalInternalImports.clear()

        return listOf(
            generatePublicKotlinFile(),
            generateInternalKotlinFile(),
        )
    }

    private fun FileDeclaration.generatePublicKotlinFile(): FileGenerator {
        return file(codeGenerationParameters, logger = logger) {
            filename = this@generatePublicKotlinFile.name
            packageName = this@generatePublicKotlinFile.packageName.fullName()
            packagePath = this@generatePublicKotlinFile.packageName.fullName()

            dependencies.forEach { dependency ->
                importPackage(dependency.packageName.fullName())
            }

            generatePublicDeclaredEntities(this@generatePublicKotlinFile)

            additionalPublicImports.forEach {
                import(it)
            }
        }
    }

    private fun FileDeclaration.generateInternalKotlinFile(): FileGenerator {
        return file(codeGenerationParameters, logger = logger) {
            filename = this@generateInternalKotlinFile.name
            packageName = this@generateInternalKotlinFile.packageName.fullName()
            packagePath =
                this@generateInternalKotlinFile.packageName.fullName().packageNameSuffixed(RPC_INTERNAL_PACKAGE_SUFFIX)

            fileOptIns = listOf("ExperimentalRpcApi::class", "InternalRpcApi::class")

            dependencies.forEach { dependency ->
                importPackage(dependency.packageName.fullName())
            }

            generateInternalDeclaredEntities(this@generateInternalKotlinFile)

            import("kotlinx.rpc.internal.utils.*")

            additionalInternalImports.forEach {
                import(it)
            }
        }
    }

    private val additionalPublicImports = mutableSetOf<String>()
    private val additionalInternalImports = mutableSetOf<String>()

    private fun CodeGenerator.generatePublicDeclaredEntities(fileDeclaration: FileDeclaration) {
        fileDeclaration.messageDeclarations.forEach { generatePublicMessage(it) }
        fileDeclaration.enumDeclarations.forEach { generatePublicEnum(it) }
        fileDeclaration.serviceDeclarations.forEach { generatePublicService(it) }
    }

    private fun CodeGenerator.generateInternalDeclaredEntities(fileDeclaration: FileDeclaration) {
        fileDeclaration.messageDeclarations.forEach { generateInternalMessage(it) }
        fileDeclaration.enumDeclarations.forEach { generateInternalEnum(it) }
        fileDeclaration.serviceDeclarations.forEach { generateInternalService(it) }
    }

    private fun MessageDeclaration.fields() = actualFields.map {
        it.transformToFieldDeclaration() to it.type
    }

    @Suppress("detekt.CyclomaticComplexMethod")
    private fun CodeGenerator.generatePublicMessage(declaration: MessageDeclaration) {
        clazz(
            name = declaration.name.simpleName,
            declarationType = DeclarationType.Interface,
        ) {
            declaration.fields().forEach { (fieldDeclaration, _) ->
                code("val $fieldDeclaration")
                newLine()
            }

            // KRPC-147 OneOf Types
//            declaration.oneOfDeclarations.forEach { oneOf ->
//                generateOneOf(oneOf)
//            }
//
            // KRPC-146 Nested Types
//            declaration.nestedDeclarations.forEach { nested ->
//                generateMessage(nested)
//            }
//
            // KRPC-141 Enum Types
//            declaration.enumDeclarations.forEach { enum ->
//                generateEnum(enum)
//            }

            clazz("", modifiers = "companion", declarationType = DeclarationType.Object)
        }
    }

    @Suppress("detekt.CyclomaticComplexMethod")
    private fun CodeGenerator.generateInternalMessage(declaration: MessageDeclaration) {
        clazz(
            name = "${declaration.name.simpleName}Builder",
            declarationType = DeclarationType.Class,
            superTypes = listOf(declaration.name.fullName()),
        ) {
            declaration.fields().forEach { (fieldDeclaration, type) ->
                val value = if (type is FieldType.Reference) {
                    additionalInternalImports.add("kotlin.properties.Delegates")
                    "by Delegates.notNull()"
                } else {
                    "= ${type.defaultValue}"
                }

                code("override var $fieldDeclaration $value")
                newLine()
            }
        }

        function(
            name = "invoke",
            modifiers = "operator",
            args = "body: ${declaration.name.simpleName}Builder.() -> Unit",
            contextReceiver = "${declaration.name.fullName()}.Companion",
            returnType = declaration.name.fullName(),
        ) {
            code("return ${declaration.name.simpleName}Builder().apply(body)")
        }

        val platformType = "${declaration.outerClassName.fullName()}.${declaration.name.simpleName}"

        function(
            name = "toPlatform",
            contextReceiver = declaration.name.fullName(),
            returnType = platformType,
        ) {
            scope("return $platformType.newBuilder().apply", ".build()") {
                declaration.actualFields.forEach { field ->
                    val call = "this@toPlatform.${field.name}${field.toPlatformCast()}"
                    code("set${field.name.replaceFirstChar { ch -> ch.uppercase() }}($call)")
                }
            }
        }

        function(
            name = "toKotlin",
            contextReceiver = platformType,
            returnType = declaration.name.fullName(),
        ) {
            scope("return ${declaration.name.simpleName}") {
                declaration.actualFields.forEach { field ->
                    code("${field.name} = this@toKotlin.${field.name}${field.toKotlinCast()}")
                }
            }
        }
    }

    private fun FieldDeclaration.toPlatformCast(): String {
        return when (type) {
            FieldType.IntegralType.FIXED32 -> ".toInt()"
            FieldType.IntegralType.FIXED64 -> ".toLong()"
            FieldType.IntegralType.UINT32 -> ".toInt()"
            FieldType.IntegralType.UINT64 -> ".toLong()"
            FieldType.IntegralType.BYTES -> ".let { bytes -> com.google.protobuf.ByteString.copyFrom(bytes) }"
            is FieldType.Reference -> ".toPlatform()"
            else -> ""
        }
    }

    private fun FieldDeclaration.toKotlinCast(): String {
        return when (type) {
            FieldType.IntegralType.FIXED32 -> ".toUInt()"
            FieldType.IntegralType.FIXED64 -> ".toULong()"
            FieldType.IntegralType.UINT32 -> ".toUInt()"
            FieldType.IntegralType.UINT64 -> ".toULong()"
            FieldType.IntegralType.BYTES -> ".toByteArray()"
            is FieldType.Reference -> ".toKotlin()"
            else -> ""
        }
    }

    private fun FieldDeclaration.transformToFieldDeclaration(): String {
        return "${name}: ${typeFqName()}"
    }

    private fun FieldDeclaration.typeFqName(): String {
        return when (type) {
            // KRPC-156 Reference Types
            is FieldType.Reference -> {
                val value by type.value
                value.fullName()
            }

            is FieldType.IntegralType -> {
                type.fqName.simpleName
            }

            // KRPC-143 Repeated Types
//            is FieldType.List -> {
//                "List<${type.valueName.simpleName}>"
//            }
//
            // KRPC-145 Map Types
//            is FieldType.Map -> {
//                "Map<${type.keyName.simpleName}, ${type.valueName.simpleName}>"
//            }
            else -> {
                error("Unsupported type: $type")
            }
        }
    }

    @Suppress("unused")
    private fun CodeGenerator.generateOneOf(declaration: OneOfDeclaration) {
        val interfaceName = declaration.name.simpleName

        clazz(declaration.name.simpleName, "sealed", declarationType = DeclarationType.Interface) {
            declaration.variants.forEach { variant ->
                clazz(
                    name = variant.name,
                    modifiers = "value",
                    constructorArgs = listOf("val value: ${variant.typeFqName()}"),
                    annotations = listOf("@JvmInline"),
                    superTypes = listOf(interfaceName),
                )

                additionalPublicImports.add("kotlin.jvm.JvmInline")
            }
        }
    }

    private fun CodeGenerator.generatePublicEnum(declaration: EnumDeclaration) {
        clazz(declaration.name.simpleName, modifiers = "enum") {
            declaration.originalEntries.forEach { entry ->
                code("${entry.name.simpleName},")
                newLine()
            }
            code(";")
            newLine()

            if (declaration.aliases.isNotEmpty()) {
                newLine()

                clazz("", modifiers = "companion", declarationType = DeclarationType.Object) {
                    declaration.aliases.forEach { alias: EnumDeclaration.Alias ->
                        code(
                            "val ${alias.name.simpleName}: ${declaration.name.simpleName} " +
                                    "= ${alias.original.name.simpleName}"
                        )
                    }
                }
            }
        }
    }

    @Suppress("unused")
    private fun CodeGenerator.generateInternalEnum(declaration: EnumDeclaration) {
        val platformType = "${declaration.outerClassName.fullName()}.${declaration.name.simpleName}"

        function(
            name = "toPlatform",
            contextReceiver = declaration.name.fullName(),
            returnType = platformType,
        ) {
            scope("return when (this)") {
                declaration.aliases.forEach { field ->
                    code("${declaration.name.simpleName}.${field.name.simpleName} -> $platformType.${field.name.simpleName}")
                }

                declaration.originalEntries.forEach { field ->
                    code("${declaration.name.simpleName}.${field.name.simpleName} -> $platformType.${field.name.simpleName}")
                }
            }
        }

        function(
            name = "toKotlin",
            contextReceiver = platformType,
            returnType = declaration.name.fullName(),
        ) {
            scope("return when (this)") {
                declaration.aliases.forEach { field ->
                    code("$platformType.${field.name.simpleName} -> ${declaration.name.simpleName}.${field.name.simpleName}")
                }

                declaration.originalEntries.forEach { field ->
                    code("$platformType.${field.name.simpleName} -> ${declaration.name.simpleName}.${field.name.simpleName}")
                }
            }
        }
    }

    @Suppress("detekt.LongMethod")
    private fun CodeGenerator.generatePublicService(service: ServiceDeclaration) {
        code("@kotlinx.rpc.grpc.annotations.Grpc")
        clazz(service.name.simpleName, declarationType = DeclarationType.Interface) {
            service.methods.forEach { method ->
                // no streaming for now
                val inputType by method.inputType
                val outputType by method.outputType
                function(
                    name = method.name,
                    modifiers = "suspend",
                    args = "message: ${inputType.name.fullName()}",
                    returnType = outputType.name.fullName(),
                )
            }
        }
    }

    private fun CodeGenerator.generateInternalService(service: ServiceDeclaration) {
        code("@Suppress(\"unused\", \"all\")")
        clazz(
            modifiers = "private",
            name = "${service.name.simpleName}Delegate",
            declarationType = DeclarationType.Object,
            superTypes = listOf("kotlinx.rpc.grpc.descriptor.GrpcDelegate<${service.name.fullName()}>"),
        ) {
            function(
                name = "clientProvider",
                modifiers = "override",
                args = "channel: kotlinx.rpc.grpc.ManagedChannel",
                returnType = "kotlinx.rpc.grpc.descriptor.GrpcClientDelegate",
            ) {
                code("return ${service.name.simpleName}ClientDelegate(channel)")
            }

            function(
                name = "definitionFor",
                modifiers = "override",
                args = "impl: ${service.name.fullName()}",
                returnType = "kotlinx.rpc.grpc.ServerServiceDefinition",
            ) {
                scope("return ${service.name.simpleName}ServerDelegate(impl).bindService()")
            }
        }

        code("@Suppress(\"unused\", \"all\")")
        clazz(
            modifiers = "private",
            name = "${service.name.simpleName}ServerDelegate",
            declarationType = DeclarationType.Class,
            superTypes = listOf("${service.name.simpleName}GrpcKt.${service.name.simpleName}CoroutineImplBase()"),
            constructorArgs = listOf("private val impl: ${service.name.fullName()}"),
        ) {
            service.methods.forEach { method ->
                val grpcName = method.name.replaceFirstChar { it.lowercase() }

                val inputType by method.inputType
                val outputType by method.outputType

                function(
                    name = grpcName,
                    modifiers = "override suspend",
                    args = "request: ${inputType.toPlatformMessageType()}",
                    returnType = outputType.toPlatformMessageType(),
                ) {
                    code("return impl.${method.name}(request.toKotlin()).toPlatform()")
                }
            }
        }

        code("@Suppress(\"unused\", \"all\")")
        clazz(
            modifiers = "private",
            name = "${service.name.simpleName}ClientDelegate",
            declarationType = DeclarationType.Class,
            superTypes = listOf("kotlinx.rpc.grpc.descriptor.GrpcClientDelegate"),
            constructorArgs = listOf("private val channel: kotlinx.rpc.grpc.ManagedChannel"),
        ) {
            val stubType = "${service.name.simpleName}GrpcKt.${service.name.simpleName}CoroutineStub"

            property(
                name = "stub",
                modifiers = "private",
                type = stubType,
                delegate = true,
                value = "lazy",
            ) {
                code("$stubType(channel.platformApi)")
            }

            function(
                name = "call",
                modifiers = "override suspend",
                args = "call: kotlinx.rpc.RpcCall",
                typeParameters = "R",
                returnType = "R",
            ) {
                code("val message = (call.data as kotlinx.rpc.internal.RpcMethodClass).asArray()[0]")
                code("@Suppress(\"UNCHECKED_CAST\")")
                scope("return when (call.callableName)") {
                    service.methods.forEach { method ->
                        val inputType by method.inputType
                        val grpcName = method.name.replaceFirstChar { it.lowercase() }
                        val result = "stub.$grpcName((message as ${inputType.name.fullName()}).toPlatform())"
                        code("\"${method.name}\" -> $result.toKotlin() as R")
                    }

                    code("else -> error(\"Illegal call: \${call.callableName}\")")
                }
            }

            function(
                name = "callAsync",
                modifiers = "override",
                args = "call: kotlinx.rpc.RpcCall",
                typeParameters = "R",
                returnType = "kotlinx.coroutines.Deferred<R>",
            ) {
                code("error(\"Async calls are not supported\")")
            }
        }
    }

    private fun MessageDeclaration.toPlatformMessageType(): String {
        return "${outerClassName.fullName()}.${name.simpleName}"
    }
}

internal fun String.packageNameSuffixed(suffix: String): String {
    return if (isEmpty()) suffix else "$this.$suffix"
}
