/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf

import kotlinx.rpc.protobuf.CodeGenerator.DeclarationType
import kotlinx.rpc.protobuf.model.*
import org.slf4j.Logger

class ModelToKotlinGenerator(
    private val model: Model,
    private val logger: Logger,
    private val codeGenerationParameters: CodeGenerationParameters,
) {
    fun generateKotlinFiles(): List<FileGenerator> {
        return model.files.map { it.generateKotlinFile() }
    }

    private fun FileDeclaration.generateKotlinFile(): FileGenerator {
        return file(codeGenerationParameters, logger = logger) {
            filename = name.simpleName
            packageName = name.packageName
            fileOptIns = listOf("ExperimentalRpcApi::class", "InternalRpcApi::class")

            dependencies.forEach { dependency ->
                importPackage(dependency.name.packageName)
            }

            generateDeclaredEntities(this@generateKotlinFile)

            additionalImports.forEach {
                import(it)
            }
            import("kotlinx.rpc.internal.utils.*")
        }
    }

    private val additionalImports = mutableSetOf<String>()

    private fun CodeGenerator.generateDeclaredEntities(fileDeclaration: FileDeclaration) {
        fileDeclaration.messageDeclarations.forEach { generateMessage(it) }
        // KRPC-141 Enum Types
//        fileDeclaration.enumDeclarations.forEach { generateEnum(it) }
        fileDeclaration.serviceDeclarations.forEach { generateService(it) }
    }

    @Suppress("detekt.CyclomaticComplexMethod")
    private fun CodeGenerator.generateMessage(declaration: MessageDeclaration) {
        val fields = declaration.actualFields.map { it.generateFieldDeclaration() to it.type.defaultValue }

        val isInterfaceMode = parameters.messageMode == RpcProtobufPlugin.MessageMode.Interface

        val (declarationType, modifiers) = when {
            isInterfaceMode -> {
                DeclarationType.Interface to ""
            }

            fields.isEmpty() -> {
                DeclarationType.Object to ""
            }

            else -> {
                DeclarationType.Class to "data"
            }
        }

        clazz(
            name = declaration.name.simpleName,
            modifiers = modifiers,
            constructorArgs = if (isInterfaceMode) emptyList() else fields.map { "val ${it.first}" to it.second },
            declarationType = declarationType,
        ) {
            if (isInterfaceMode) {
                fields.forEach {
                    code("val ${it.first}")
                    newLine()
                }
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

            if (isInterfaceMode) {
                clazz("", modifiers = "companion", declarationType = DeclarationType.Object)
            }
        }

        if (isInterfaceMode) {
            clazz(
                name = "${declaration.name.simpleName}Builder",
                declarationType = DeclarationType.Class,
                superTypes = listOf(declaration.name.simpleName),
            ) {
                fields.forEach {
                    code("override var ${it.first} = ${it.second}")
                    newLine()
                }
            }

            function(
                name = "invoke",
                modifiers = "operator",
                args = "body: ${declaration.name.simpleName}Builder.() -> Unit",
                contextReceiver = "${declaration.name.simpleName}.Companion",
                returnType = declaration.name.simpleName,
            ) {
                code("return ${declaration.name.simpleName}Builder().apply(body)")
            }
        }

        val platformType = "${declaration.outerClassName.simpleName}.${declaration.name.simpleName}"

        function(
            name = "toPlatform",
            contextReceiver = declaration.name.simpleName,
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
            returnType = declaration.name.simpleName,
        ) {
            scope("return ${declaration.name.simpleName}") {
                declaration.actualFields.forEach { field ->
                    code("${field.name} = this@toKotlin.${field.name}${field.toKotlinCast()}")
                }
            }
        }
    }

    private fun FieldDeclaration.toPlatformCast(): String {
        val type = type as? FieldType.IntegralType ?: return ""

        return when (type) {
            FieldType.IntegralType.FIXED32 -> ".toInt()"
            FieldType.IntegralType.FIXED64 -> ".toLong()"
            FieldType.IntegralType.UINT32 -> ".toInt()"
            FieldType.IntegralType.UINT64 -> ".toLong()"
            FieldType.IntegralType.BYTES -> ".let { bytes -> com.google.protobuf.ByteString.copyFrom(bytes) }"
            else -> ""
        }
    }

    private fun FieldDeclaration.toKotlinCast(): String {
        val type = type as? FieldType.IntegralType ?: return ""

        return when (type) {
            FieldType.IntegralType.FIXED32 -> ".toUInt()"
            FieldType.IntegralType.FIXED64 -> ".toULong()"
            FieldType.IntegralType.UINT32 -> ".toUInt()"
            FieldType.IntegralType.UINT64 -> ".toULong()"
            FieldType.IntegralType.BYTES -> ".toByteArray()"
            else -> ""
        }
    }

    private fun FieldDeclaration.generateFieldDeclaration(): String {
        return "${name}: ${typeFqName()}"
    }

    private fun FieldDeclaration.typeFqName(): String {
        return when (type) {
            // KRPC-156 Reference Types
//            is FieldType.Reference -> {
//                type.value.simpleName
//            }

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

                additionalImports.add("kotlin.jvm.JvmInline")
            }
        }
    }

    @Suppress("unused")
    private fun CodeGenerator.generateEnum(declaration: EnumDeclaration) {
        clazz(declaration.name.simpleName, "enum") {
            code(declaration.originalEntries.joinToString(", ", postfix = ";") { enumEntry ->
                enumEntry.name.simpleName
            })

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

    @Suppress("detekt.LongMethod")
    private fun CodeGenerator.generateService(service: ServiceDeclaration) {
        code("@kotlinx.rpc.grpc.annotations.Grpc")
        clazz(service.name.simpleName, declarationType = DeclarationType.Interface) {
            service.methods.forEach { method ->
                // no streaming for now
                val inputType by method.inputType
                val outputType by method.outputType
                function(
                    name = method.name.simpleName,
                    modifiers = "suspend",
                    args = "message: ${inputType.name.simpleName}",
                    returnType = outputType.name.simpleName,
                )
            }
        }

        newLine()

        code("@Suppress(\"unused\", \"all\")")
        clazz(
            modifiers = "private",
            name = "${service.name.simpleName}Delegate",
            declarationType = DeclarationType.Object,
            superTypes = listOf("kotlinx.rpc.grpc.descriptor.GrpcDelegate<${service.name.simpleName}>"),
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
                args = "impl: ${service.name.simpleName}",
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
            constructorArgs = listOf("private val impl: ${service.name.simpleName}"),
        ) {
            service.methods.forEach { method ->
                val grpcName = method.name.simpleName.replaceFirstChar { it.lowercase() }

                val inputType by method.inputType
                val outputType by method.outputType

                function(
                    name = grpcName,
                    modifiers = "override suspend",
                    args = "request: ${inputType.toPlatformMessageType()}",
                    returnType = outputType.toPlatformMessageType(),
                ) {
                    code("return impl.${method.name.simpleName}(request.toKotlin()).toPlatform()")
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
                        val grpcName = method.name.simpleName.replaceFirstChar { it.lowercase() }
                        val result = "stub.$grpcName((message as ${inputType.name.simpleName}).toPlatform())"
                        code("\"${method.name.simpleName}\" -> $result.toKotlin() as R")
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
        return "${outerClassName.simpleName}.${name.simpleName.removePrefix(name.parentNameAsPrefix)}"
    }
}
