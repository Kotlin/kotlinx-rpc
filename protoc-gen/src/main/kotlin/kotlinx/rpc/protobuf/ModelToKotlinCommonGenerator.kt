/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("detekt.all")

package kotlinx.rpc.protobuf

import kotlinx.rpc.protobuf.CodeGenerator.DeclarationType
import kotlinx.rpc.protobuf.model.*
import org.slf4j.Logger

private const val RPC_INTERNAL_PACKAGE_SUFFIX = "_rpc_internal"

class ModelToKotlinCommonGenerator(
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

    private var currentPackage: FqName = FqName.Package.Root

    private fun FileDeclaration.generatePublicKotlinFile(): FileGenerator {
        currentPackage = packageName

        return file(codeGenerationParameters, logger = logger) {
            filename = this@generatePublicKotlinFile.name
            packageName = this@generatePublicKotlinFile.packageName.safeFullName()
            packagePath = this@generatePublicKotlinFile.packageName.safeFullName()

            dependencies.forEach { dependency ->
                importPackage(dependency.packageName.safeFullName())
            }

            fileOptIns = listOf("ExperimentalRpcApi::class", "InternalRpcApi::class")

            generatePublicDeclaredEntities(this@generatePublicKotlinFile)

            import("kotlinx.rpc.internal.utils.*")
            import("kotlinx.coroutines.flow.*")

            additionalPublicImports.forEach {
                import(it)
            }
        }
    }

    private fun FileDeclaration.generateInternalKotlinFile(): FileGenerator {
        currentPackage = packageName

        return file(codeGenerationParameters, logger = logger) {
            filename = this@generateInternalKotlinFile.name
            packageName = this@generateInternalKotlinFile.packageName.safeFullName()
            packagePath =
                this@generateInternalKotlinFile.packageName.safeFullName()
                    .packageNameSuffixed(RPC_INTERNAL_PACKAGE_SUFFIX)

            fileOptIns = listOf("ExperimentalRpcApi::class", "InternalRpcApi::class")

            dependencies.forEach { dependency ->
                importPackage(dependency.packageName.safeFullName())
            }

            generateInternalDeclaredEntities(this@generateInternalKotlinFile)

            import("kotlinx.rpc.internal.utils.*")
            import("kotlinx.coroutines.flow.*")
            import("kotlinx.rpc.grpc.pb.*")


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

        fileDeclaration.messageDeclarations.forEach {
            generateMessageConstructor(it)
            generateMessageDecoder(it)
            generateMessageEncoder(it)
        }
    }

    private fun MessageDeclaration.fields() = actualFields.map {
        it.transformToFieldDeclaration() to it
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

            newLine()

            declaration.oneOfDeclarations.forEach { oneOf ->
                generateOneOfPublic(oneOf)
            }

            declaration.nestedDeclarations.forEach { nested ->
                generatePublicMessage(nested)
            }

            declaration.enumDeclarations.forEach { enum ->
                generatePublicEnum(enum)
            }

            clazz("", modifiers = "companion", declarationType = DeclarationType.Object)
        }
    }

    @Suppress("detekt.CyclomaticComplexMethod")
    private fun CodeGenerator.generateInternalMessage(declaration: MessageDeclaration) {
        clazz(
            name = "${declaration.name.simpleName}Builder",
            declarationType = DeclarationType.Class,
            superTypes = listOf(declaration.name.safeFullName()),
        ) {
            declaration.fields().forEach { (fieldDeclaration, field) ->
                val value = when {
                    field.nullable -> {
                        "= null"
                    }

                    field.type is FieldType.Reference -> {
                        additionalInternalImports.add("kotlin.properties.Delegates")
                        "by Delegates.notNull()"
                    }

                    else -> {
                        "= ${field.type.defaultValue}"
                    }
                }

                code("override var $fieldDeclaration $value")
                newLine()
            }

            declaration.nestedDeclarations.forEach { nested ->
                generateInternalMessage(nested)
            }
        }
    }

    private fun CodeGenerator.generateMessageConstructor(declaration: MessageDeclaration) = function(
        name = "invoke",
        modifiers = "operator",
        args = "body: ${declaration.name.safeFullName("Builder")}.() -> Unit",
        contextReceiver = "${declaration.name.safeFullName()}.Companion",
        returnType = declaration.name.safeFullName(),
    ) {
        code("return ${declaration.name.safeFullName("Builder")}().apply(body)")
    }

    private fun CodeGenerator.generateMessageDecoder(declaration: MessageDeclaration) = function(
        name = "decodeWith",
        args = "decoder: WireDecoder",
        contextReceiver = "${declaration.name.safeFullName()}.Companion",
        returnType = "${declaration.name.safeFullName()}?"
    ) {
        code("val msg = ${declaration.name.safeFullName("Builder")}()")
        whileBlock("!decoder.hadError()") {
            code("val tag = decoder.readTag() ?: break // EOF, we read the whole message")
            whenBlock {
                declaration.fields().forEach { (_, field) -> readMatchCase(field) }
                whenCase("else") { code("TODO(\"Handle unknown fields\")") }
            }
        }
        ifBranch(
            condition = "decoder.hadError()",
            ifBlock = { code("return null") }
        )

        // TODO: Make a lists immutable
        code("return msg")
    }

    private fun CodeGenerator.readMatchCase(field: FieldDeclaration) {
        val encFuncName = field.type.decodeEncodeFuncName()
        val assignment = "msg.${field.name} ="
        when (val fieldType = field.type) {
            is FieldType.IntegralType -> whenCase("tag.fieldNr == ${field.number} && tag.wireType == WireType.${field.type.wireType.name}") {
                code("$assignment decoder.read$encFuncName()")
            }

            is FieldType.List -> if (field.packed) {
                whenCase("tag.fieldNr == ${field.number} && tag.wireType == WireType.LENGTH_DELIMITED") {
                    code("$assignment decoder.readPacked${fieldType.value.decodeEncodeFuncName()}()")
                }
            } else {
                whenCase("tag.fieldNr == ${field.number} && tag.wireType == WireType.LENGTH_DELIMITED") {
                    code("(msg.${field.name} as ArrayList).add(decoder.read${fieldType.value.decodeEncodeFuncName()}())")
                }
            }

            is FieldType.Map -> TODO()
            is FieldType.OneOf -> TODO()
            is FieldType.Reference -> TODO()
        }
    }

    private fun CodeGenerator.generateMessageEncoder(declaration: MessageDeclaration) = function(
        name = "encodeWith",
        args = "encoder: WireEncoder",
        contextReceiver = declaration.name.safeFullName(),
    ) {

        declaration.fields().forEach { (_, field) ->
            val fieldName = field.name
            if (field.nullable) {
                scope("$fieldName?.also") {
                    code(field.writeValue("it"))
                }
            } else if (!field.hasPresence) {
                ifBranch(condition = field.defaultCheck(), ifBlock = {
                    code(field.writeValue(field.name))
                })
            } else {
                code(field.writeValue(field.name))
            }
        }
    }

    private fun FieldDeclaration.writeValue(variable: String): String {
        return when (val fieldType = type) {
            is FieldType.IntegralType -> "encoder.write${type.decodeEncodeFuncName()}($number, $variable)"
            is FieldType.List -> when {
                packed && packedFixedSize ->
                    "encoder.writePacked${fieldType.value.decodeEncodeFuncName()}($number, $variable)"

                packed && !packedFixedSize ->
                    "encoder.writePacked${fieldType.value.decodeEncodeFuncName()}($number, $variable, ${
                        wireSizeCall(
                            variable
                        )
                    })"

                else ->
                    "$variable.forEach { encoder.write${fieldType.value.decodeEncodeFuncName()}($number, it) }"
            }

            is FieldType.Map -> TODO()
            is FieldType.OneOf -> TODO()
            is FieldType.Reference -> TODO()
        }
    }

    private fun FieldDeclaration.wireSizeCall(variable: String): String {
        val sizeFunc = "WireSize.${type.decodeEncodeFuncName().replaceFirstChar { it.lowercase() }}($variable)"
        return when (val fieldType = type) {
            is FieldType.IntegralType -> when {
                fieldType.wireType == WireType.FIXED32 -> "32"
                fieldType.wireType == WireType.FIXED64 -> "64"
                else -> sizeFunc
            }

            is FieldType.List -> when {
                isPackable && !packedFixedSize -> sizeFunc
                else -> error("Unexpected use of size call for field: $name, type: $fieldType")
            }

            is FieldType.Map -> TODO()
            is FieldType.OneOf -> TODO()
            is FieldType.Reference -> TODO()
        }
    }

    private fun FieldDeclaration.defaultCheck(): String {
        return when (val fieldType = type) {
            is FieldType.IntegralType -> when (fieldType) {
                FieldType.IntegralType.BYTES, FieldType.IntegralType.STRING -> "$name.isNotEmpty()"
                else -> "$name != ${fieldType.defaultValue}"
            }

            is FieldType.List -> "$name.isNotEmpty()"

            else -> TODO("Field: $name, type: $fieldType")
        }
    }

    private fun FieldType.decodeEncodeFuncName(): String = when (this) {
        FieldType.IntegralType.STRING -> "String"
        FieldType.IntegralType.BYTES -> "Bytes"
        FieldType.IntegralType.BOOL -> "Bool"
        FieldType.IntegralType.FLOAT -> "Float"
        FieldType.IntegralType.DOUBLE -> "Double"
        FieldType.IntegralType.INT32 -> "Int32"
        FieldType.IntegralType.INT64 -> "Int64"
        FieldType.IntegralType.UINT32 -> "UInt32"
        FieldType.IntegralType.UINT64 -> "UInt64"
        FieldType.IntegralType.FIXED32 -> "Fixed32"
        FieldType.IntegralType.FIXED64 -> "Fixed64"
        FieldType.IntegralType.SINT32 -> "SInt32"
        FieldType.IntegralType.SINT64 -> "SInt64"
        FieldType.IntegralType.SFIXED32 -> "SFixed32"
        FieldType.IntegralType.SFIXED64 -> "SFixed64"
        is FieldType.List -> "Packed${value.decodeEncodeFuncName()}"
        is FieldType.Map -> error("No encoding/decoding function for map types")
        is FieldType.OneOf -> error("No encoding/decoding function for oneOf types")
        is FieldType.Reference -> error("No encoding/decoding function for sub message types")
    }

    private fun FieldDeclaration.transformToFieldDeclaration(): String {
        return "${name}: ${typeFqName()}"
    }

    private fun FieldDeclaration.typeFqName(): String {
        return when (type) {
            is FieldType.Reference -> {
                val value by type.value
                value.safeFullName()
            }

            is FieldType.OneOf -> {
                val value by type.value
                value.safeFullName()
            }

            is FieldType.IntegralType -> {
                type.fqName.simpleName
            }

            is FieldType.List -> {
                val fqValue = when (val value = type.value) {
                    is FieldType.Reference -> value.value.value
                    is FieldType.IntegralType -> value.fqName
                    else -> error("Unsupported type: $value")
                }

                "List<${fqValue.safeFullName()}>"
            }

            is FieldType.Map -> {
                val entry by type.entry

                val fqKey = when (val key = entry.key) {
                    is FieldType.Reference -> key.value.value
                    is FieldType.IntegralType -> key.fqName
                    else -> error("Unsupported type: $key")
                }

                val fqValue = when (val value = entry.value) {
                    is FieldType.Reference -> value.value.value
                    is FieldType.IntegralType -> value.fqName
                    else -> error("Unsupported type: $value")
                }

                "Map<${fqKey.safeFullName()}, ${fqValue.safeFullName()}>"
            }
        }.withNullability(nullable)
    }

    private fun String.withNullability(nullable: Boolean): String {
        return "$this${if (nullable) "?" else ""}"
    }

    private fun CodeGenerator.generateOneOfPublic(declaration: OneOfDeclaration) {
        val interfaceName = declaration.name.simpleName

        clazz(interfaceName, "sealed", declarationType = DeclarationType.Interface) {
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

    @Suppress("detekt.LongMethod")
    private fun CodeGenerator.generatePublicService(service: ServiceDeclaration) {
        code("@kotlinx.rpc.grpc.annotations.Grpc")
        clazz(service.name.simpleName, declarationType = DeclarationType.Interface) {
            service.methods.forEach { method ->
                val inputType by method.inputType
                val outputType by method.outputType
                function(
                    name = method.name,
                    modifiers = if (method.serverStreaming) "" else "suspend",
                    args = "message: ${inputType.name.safeFullName().wrapInFlowIf(method.clientStreaming)}",
                    returnType = outputType.name.safeFullName().wrapInFlowIf(method.serverStreaming),
                )
            }
        }
    }

    private fun String.wrapInFlowIf(condition: Boolean): String {
        return if (condition) "Flow<$this>" else this
    }

    private fun FqName.safeFullName(classSuffix: String = ""): String {
        importRootDeclarationIfNeeded(this)

        return fullName(classSuffix)
    }

    private fun importRootDeclarationIfNeeded(
        declaration: FqName,
        nameToImport: String = declaration.simpleName,
        internalOnly: Boolean = false,
    ) {
        if (declaration.parent == FqName.Package.Root && currentPackage != FqName.Package.Root && nameToImport.isNotBlank()) {
            additionalInternalImports.add(nameToImport)
            if (!internalOnly) {
                additionalPublicImports.add(nameToImport)
            }
        }
    }
}

private fun String.packageNameSuffixed(suffix: String): String {
    return if (isEmpty()) suffix else "$this.$suffix"
}
