/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("detekt.all")

package kotlinx.rpc.protobuf

import kotlinx.rpc.protobuf.CodeGenerator.DeclarationType
import kotlinx.rpc.protobuf.model.*
import org.slf4j.Logger

private const val RPC_INTERNAL_PACKAGE_SUFFIX = "_rpc_internal"
private const val MSG_INTERNAL_SUFFIX = "Internal"
private const val PB_PKG = "kotlinx.rpc.grpc.pb"
private const val INTERNAL_RPC_API_ANNO = "kotlinx.rpc.internal.utils.InternalRpcApi"
private const val WITH_CODEC_ANNO = "kotlinx.rpc.grpc.codec.WithCodec"

class ModelToKotlinCommonGenerator(
    private val model: Model,
    private val logger: Logger,
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

        return file(logger = logger) {
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

        return file(logger = logger) {
            filename = this@generateInternalKotlinFile.name
            packageName = this@generateInternalKotlinFile.packageName.safeFullName()
            packagePath =
                this@generateInternalKotlinFile.packageName.safeFullName()
                    .packageNameSuffixed(RPC_INTERNAL_PACKAGE_SUFFIX)

            fileOptIns = listOf("ExperimentalRpcApi::class", "$INTERNAL_RPC_API_ANNO::class")

            dependencies.forEach { dependency ->
                importPackage(dependency.packageName.safeFullName())
            }

            generateInternalDeclaredEntities(this@generateInternalKotlinFile)

            import("kotlinx.rpc.grpc.pb.*")
            import("kotlinx.rpc.internal.utils.*")
            import("kotlinx.coroutines.flow.*")

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
        generateInternalMessageEntities(fileDeclaration.messageDeclarations)

        val allEnums =
            fileDeclaration.enumDeclarations + fileDeclaration.messageDeclarations.flatMap { it.allEnumsRecursively() }
        allEnums.forEach { enum ->
            generateInternalEnumConstructor(enum)
        }
    }

    private fun CodeGenerator.generateInternalMessageEntities(messages: List<MessageDeclaration>) {
        messages.forEach { generateInternalMessage(it) }

        // emit all required functions in the outer scope
        val allMsgs = messages + messages.flatMap(MessageDeclaration::allNestedRecursively)
        allMsgs.forEach {
            generateMessageConstructor(it)
        }
        allMsgs.forEach {
            generateRequiredCheck(it)
            generateMessageEncoder(it)
            generateMessageDecoder(it)
            generateInternalComputeSize(it)
            generateInternalCastExtension(it)
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
        val internalClassName = declaration.internalClassName()
        clazz(
            name = internalClassName,
            annotations = listOf("@$INTERNAL_RPC_API_ANNO", "@$WITH_CODEC_ANNO($internalClassName.CODEC::class)"),
            declarationType = DeclarationType.Class,
            superTypes = listOf(
                declaration.name.safeFullName(),
                "$PB_PKG.InternalMessage(fieldsWithPresence = ${declaration.presenceMaskSize})"
            ),
        ) {

            generatePresenceIndicesObject(declaration)

            code("override val _size: Int by lazy { computeSize() }")

            declaration.fields().forEach { (fieldDeclaration, field) ->
                val value = when {
                    field.nullable -> {
                        "= null"
                    }

                    field.type is FieldType.Message ->
                        "by MsgFieldDelegate(PresenceIndices.${field.name}) { ${field.type.dec.value.internalClassFullName()}() }"

                    else -> {
                        val fieldPresence = if (field.presenceIdx != null) "PresenceIndices.${field.name}" else ""
                        "by MsgFieldDelegate($fieldPresence) { ${field.type.defaultValue} }"
                    }
                }

                code("override var $fieldDeclaration $value")
                newLine()
            }

            declaration.nestedDeclarations.forEach { nested ->
                generateInternalMessage(nested)
            }

            generateCodecObject(declaration)
        }
    }

    private fun CodeGenerator.generatePresenceIndicesObject(declaration: MessageDeclaration) {
        if (declaration.presenceMaskSize == 0) {
            return
        }
        scope("private object PresenceIndices") {
            declaration.fields().forEach { (_, field) ->
                if (field.presenceIdx != null) {
                    code("const val ${field.name} = ${field.presenceIdx}")
                    newLine()
                }
            }
        }
    }

    private fun CodeGenerator.generateCodecObject(declaration: MessageDeclaration) {
        val msgFqName = declaration.name.safeFullName()
        val downCastErrorStr =
            "\${value::class.simpleName} implements ${msgFqName}, which is prohibited."
        val sourceFqName = "kotlinx.io.Source"
        val bufferFqName = "kotlinx.io.Buffer"
        scope("object CODEC : kotlinx.rpc.grpc.codec.MessageCodec<$msgFqName>") {
            function("encode", modifiers = "override", args = "value: $msgFqName", returnType = sourceFqName) {
                code("val buffer = $bufferFqName()")
                code("val encoder = $PB_PKG.WireEncoder(buffer)")
                code("value.asInternal().encodeWith(encoder)")
                code("encoder.flush()")
                code("return buffer")
            }

            function("decode", modifiers = "override", args = "stream: $sourceFqName", returnType = msgFqName) {
                scope("$PB_PKG.WireDecoder(stream as $bufferFqName).use") {
                    code("val msg = ${declaration.internalClassFullName()}()")
                    code("${declaration.internalClassFullName()}.CODEC.decodeWith(msg, it)")
                    code("msg.checkRequiredFields()")
                    code("return msg")
                }
            }
        }
    }

    private fun CodeGenerator.generateMessageConstructor(declaration: MessageDeclaration) = function(
        name = "invoke",
        modifiers = "operator",
        args = "body: ${declaration.internalClassFullName()}.() -> Unit",
        contextReceiver = "${declaration.name.safeFullName()}.Companion",
        returnType = declaration.name.safeFullName(),
    ) {
        code("val msg = ${declaration.internalClassFullName()}().apply(body)")
        // check if the user set all required fields
        code("msg.checkRequiredFields()")
        code("return msg")
    }

    private fun CodeGenerator.generateMessageDecoder(declaration: MessageDeclaration) = function(
        name = "decodeWith",
        modifiers = "internal",
        args = "msg: ${declaration.internalClassFullName()}, decoder: $PB_PKG.WireDecoder",
        annotations = listOf("@$INTERNAL_RPC_API_ANNO"),
        contextReceiver = "${declaration.internalClassFullName()}.CODEC"
    ) {
        whileBlock("!decoder.hadError()") {
            code("val tag = decoder.readTag() ?: break // EOF, we read the whole message")
            whenBlock {
                declaration.fields().forEach { (_, field) -> readMatchCase(field) }
                whenCase("else") { code("TODO(\"Handle unknown fields\")") }
            }
        }
        ifBranch(
            condition = "decoder.hadError()",
            ifBlock = { code("error(\"Error during decoding of ${declaration.name.simpleName}\")") }
        )

        // TODO: Make a lists immutable
    }

    private fun CodeGenerator.readMatchCase(
        field: FieldDeclaration,
        lvalue: String = "msg.${field.name}",
        wrapperCtor: (String) -> String = { it }
    ) {
        when (val fieldType = field.type) {
            is FieldType.IntegralType -> whenCase("tag.fieldNr == ${field.number} && tag.wireType == $PB_PKG.WireType.${field.type.wireType.name}") {
                generateDecodeFieldValue(fieldType, lvalue, wrapperCtor = wrapperCtor)
            }

            is FieldType.List -> if (field.dec.isPacked) {
                whenCase("tag.fieldNr == ${field.number} && tag.wireType == $PB_PKG.WireType.LENGTH_DELIMITED") {
                    generateDecodeFieldValue(fieldType, lvalue, isPacked = true, wrapperCtor = wrapperCtor)
                }
            } else {
                whenCase("tag.fieldNr == ${field.number} && tag.wireType == $PB_PKG.WireType.${fieldType.value.wireType.name}") {
                    generateDecodeFieldValue(fieldType, lvalue, isPacked = false, wrapperCtor = wrapperCtor)
                }
            }

            is FieldType.Enum -> whenCase("tag.fieldNr == ${field.number} && tag.wireType == $PB_PKG.WireType.VARINT") {
                generateDecodeFieldValue(fieldType, lvalue, wrapperCtor = wrapperCtor)
            }

            is FieldType.OneOf -> {
                fieldType.dec.variants.forEach { variant ->
                    val variantName = "${fieldType.dec.name.safeFullName()}.${variant.name}"
                    readMatchCase(
                        field = variant,
                        lvalue = lvalue,
                        wrapperCtor = { "$variantName($it)" }
                    )
                }
            }

            is FieldType.Message -> {
                whenCase("tag.fieldNr == ${field.number} && tag.wireType == $PB_PKG.WireType.LENGTH_DELIMITED") {
                    // check if the the current sub message object
                    ifBranch(condition = "!msg.presenceMask[${field.presenceIdx}]", ifBlock = {
                        code("$lvalue = ${fieldType.dec.value.internalClassFullName()}()")
                    })
                    generateDecodeFieldValue(fieldType, lvalue, wrapperCtor = wrapperCtor)
                }
            }

            is FieldType.Map -> TODO()
        }
    }

    private fun CodeGenerator.generateDecodeFieldValue(
        fieldType: FieldType,
        lvalue: String,
        isPacked: Boolean = false,
        wrapperCtor: (String) -> String = { it }
    ) {
        when (fieldType) {
            is FieldType.IntegralType -> {
                val raw = "decoder.read${fieldType.decodeEncodeFuncName()}()"
                code("$lvalue = ${wrapperCtor(raw)}")
            }

            is FieldType.List -> if (isPacked) {
                code("$lvalue = decoder.readPacked${fieldType.value.decodeEncodeFuncName()}()")
            } else {
                when (val elemType = fieldType.value) {
                    is FieldType.Message -> {
                        code("val elem = ${elemType.dec.value.internalClassFullName()}()")
                        generateDecodeFieldValue(fieldType.value, "elem", wrapperCtor = wrapperCtor)
                    }

                    else -> generateDecodeFieldValue(fieldType.value, "val elem", wrapperCtor = wrapperCtor)
                }
                code("($lvalue as ArrayList).add(elem)")
            }

            is FieldType.Enum -> {
                val fromNum = "${fieldType.dec.name.safeFullName()}.fromNumber"
                val raw = "$fromNum(decoder.read${fieldType.decodeEncodeFuncName()}())"
                code("$lvalue = ${wrapperCtor(raw)}")
            }

            is FieldType.OneOf -> {
                fieldType.dec.variants.forEach { variant ->
                    val variantName = "${fieldType.dec.name.safeFullName()}.${variant.name}"
                    readMatchCase(
                        field = variant,
                        lvalue = lvalue,
                        wrapperCtor = { "$variantName($it)" }
                    )
                }
            }

            is FieldType.Message -> {
                val internalClassName = fieldType.dec.value.internalClassFullName()
                code("decoder.readMessage($lvalue.asInternal(), $internalClassName::decodeWith)")
            }

            is FieldType.Map -> TODO()
        }
    }

    private fun CodeGenerator.generateMessageEncoder(declaration: MessageDeclaration) = function(
        name = "encodeWith",
        modifiers = "internal",
        annotations = listOf("@$INTERNAL_RPC_API_ANNO"),
        args = "encoder: $PB_PKG.WireEncoder",
        contextReceiver = declaration.internalClassFullName(),
    ) {
        if (declaration.fields().isEmpty()) {
            code("// no fields to encode")
            return@function
        }

        declaration.fields().forEach { (_, field) ->
            val fieldName = field.name
            if (field.nullable) {
                scope("$fieldName?.also") {
                    writeFieldValue(field, "it")
                }
            } else if (!field.dec.hasPresence()) {
                ifBranch(condition = field.notDefaultCheck(), ifBlock = {
                    writeFieldValue(field, field.name)
                })
            } else {
                ifBranch(condition = "presenceMask[${field.presenceIdx}]", ifBlock = {
                    writeFieldValue(field, field.name)
                })
            }
        }
    }

    private fun CodeGenerator.writeFieldValue(field: FieldDeclaration, valueVar: String) {
        var encFunc = field.type.decodeEncodeFuncName()
        val number = field.number
        when (val fieldType = field.type) {
            is FieldType.IntegralType -> code("encoder.write${encFunc!!}(fieldNr = $number, value = $valueVar)")
            is FieldType.List -> {
                encFunc = fieldType.value.decodeEncodeFuncName()
                when {
                    field.dec.isPacked && field.packedFixedSize ->
                        code("encoder.writePacked${encFunc!!}(fieldNr = $number, value = $valueVar)")

                    field.dec.isPacked && !field.packedFixedSize ->
                        code(
                            "encoder.writePacked${encFunc!!}(fieldNr = $number, value = $valueVar, fieldSize = ${
                                field.type.valueSizeCall(valueVar, number, true)
                            })"
                        )

                    fieldType.value is FieldType.Message -> scope("$valueVar.forEach") {
                        code("encoder.writeMessage(fieldNr = ${field.number}, value = it.asInternal()) { encodeWith(it) }")
                    }

                    else -> {
                        require(encFunc != null) { "No encode function for list type: $fieldType" }
                        scope("$valueVar.forEach") {
                            code("encoder.write${encFunc}($number, it)")
                        }
                    }
                }
            }

            is FieldType.Enum -> code("encoder.write${encFunc!!}(fieldNr = $number, value = ${valueVar}.number)")

            is FieldType.OneOf -> whenBlock("val value = $valueVar") {
                fieldType.dec.variants.forEach { variant ->
                    whenCase("is ${fieldType.dec.name.safeFullName()}.${variant.name}") {
                        writeFieldValue(variant, "value.value")
                    }
                }
            }

            is FieldType.Map -> TODO()

            is FieldType.Message -> code("encoder.writeMessage(fieldNr = ${field.number}, value = $valueVar.asInternal()) { encodeWith(it) }")
        }

    }


    private fun CodeGenerator.generateInternalEnumConstructor(enum: EnumDeclaration) {
        function(
            "fromNumber",
            modifiers = "internal",
            args = "number: Int",
            annotations = listOf("@$INTERNAL_RPC_API_ANNO"),
            contextReceiver = "${enum.name.safeFullName()}.Companion",
            returnType = enum.name.safeFullName(),
        ) {
            whenBlock(prefix = "return", condition = "number") {
                enum.originalEntries.forEach { entry ->
                    whenCase("${entry.dec.number}") {
                        code("${entry.name}")
                    }
                }
                whenCase("else") {
                    code("${enum.name.safeFullName()}.UNRECOGNIZED(number)")
                }
            }
        }
    }

    /**
     * Generates a function to check for the presence of all required fields in a message declaration.
     */
    private fun CodeGenerator.generateRequiredCheck(declaration: MessageDeclaration) = function(
        name = "checkRequiredFields",
        modifiers = "internal",
        annotations = listOf("@$INTERNAL_RPC_API_ANNO"),
        contextReceiver = declaration.internalClassFullName(),
    ) {
        val requiredFields = declaration.actualFields.filter { it.dec.isRequired }
        val submessages = declaration.actualFields.filter { it.type is FieldType.Message }

        if (submessages.isEmpty() && requiredFields.isEmpty()) {
            code("// no fields to check")
        }

        requiredFields.forEach { field ->
            ifBranch(condition = "!presenceMask[${field.presenceIdx}]", ifBlock = {
                code("error(\"${declaration.name.simpleName} is missing required field: ${field.name}\")")
            })
        }

        // check submessages
        submessages.forEach { field ->
            ifBranch(condition = "presenceMask[${field.presenceIdx}]", ifBlock = {
                code("${field.name}.asInternal().checkRequiredFields()")
            })
        }
    }

    private fun CodeGenerator.generateInternalComputeSize(declaration: MessageDeclaration) {
        function(
            name = "computeSize",
            modifiers = "private",
            contextReceiver = declaration.internalClassFullName(),
            returnType = "Int",
        ) {
            code("var result = 0")
            declaration.actualFields.forEach { field ->
                val fieldName = field.name
                if (field.nullable) {
                    scope("$fieldName?.also") {
                        generateFieldComputeSizeCall(field, "it")
                    }
                } else if (!field.dec.hasPresence()) {
                    scope("if (${field.notDefaultCheck()})") {
                        generateFieldComputeSizeCall(field, fieldName)
                    }
                } else {
                    scope("if (presenceMask[${field.presenceIdx}])") {
                        generateFieldComputeSizeCall(field, fieldName)
                    }
                }
            }
            code("return result")
        }
    }

    private fun CodeGenerator.generateInternalCastExtension(declaration: MessageDeclaration) {
        function(
            "asInternal",
            modifiers = "internal",
            annotations = listOf("@$INTERNAL_RPC_API_ANNO"),
            contextReceiver = declaration.name.safeFullName(),
            returnType = declaration.internalClassFullName(),
        ) {
            code("return this as? ${declaration.internalClassFullName()} ?: error(\"Message \${this::class.simpleName} is a non-internal message type.\")")
        }
    }


    private fun CodeGenerator.generateFieldComputeSizeCall(field: FieldDeclaration, variable: String) {
        val valueSize by lazy { field.type.valueSizeCall(variable, field.number, field.dec.isPacked) }
        val tagSize = tagSizeCall(field.number, field.type.wireType)

        when (field.type) {
            is FieldType.List -> when {
                // packed fields also have the tag + len
                field.dec.isPacked -> code("result += $valueSize.let { $tagSize + ${int32SizeCall("it")} + it }")
                else -> code("result = $valueSize")
            }

            is FieldType.Message,
            FieldType.IntegralType.STRING,
            FieldType.IntegralType.BYTES -> code("result += $valueSize.let { $tagSize + ${int32SizeCall("it")} + it }")

            is FieldType.Map -> TODO()
            is FieldType.OneOf -> whenBlock("val value = $variable") {
                field.type.dec.variants.forEach { variant ->
                    val variantName = "${field.type.dec.name.safeFullName()}.${variant.name}"
                    whenCase("is $variantName") {
                        generateFieldComputeSizeCall(variant, "value.value")
                    }
                }
            }

            is FieldType.Enum,
            FieldType.IntegralType.BOOL,
            FieldType.IntegralType.FLOAT,
            FieldType.IntegralType.DOUBLE,
            FieldType.IntegralType.INT32,
            FieldType.IntegralType.INT64,
            FieldType.IntegralType.UINT32,
            FieldType.IntegralType.UINT64,
            FieldType.IntegralType.FIXED32,
            FieldType.IntegralType.FIXED64,
            FieldType.IntegralType.SINT32,
            FieldType.IntegralType.SINT64,
            FieldType.IntegralType.SFIXED32,
            FieldType.IntegralType.SFIXED64 -> code("result += ($tagSize + $valueSize)")
        }
    }

    private fun FieldType.valueSizeCall(variable: String, number: Int, isPacked: Boolean = false): String {
        val sizeFunName = decodeEncodeFuncName()?.decapitalize()
        val sizeFunc = "$PB_PKG.WireSize.$sizeFunName($variable)"

        return when (this) {
            is FieldType.IntegralType -> sizeFunc

            is FieldType.List -> when {
                isPacked -> sizeFunc
                else -> {
                    // calculate the size of the values within the list.
                    val valueSize = value.valueSizeCall("it", number)
                    val tagSize = tagSizeCall(number, value.wireType)
                    "$variable.sumOf { $valueSize + $tagSize }"
                }
            }

            is FieldType.Enum -> "$PB_PKG.WireSize.$sizeFunName($variable.number)"
            is FieldType.Map -> TODO()
            is FieldType.OneOf -> error("OneOf fields have no direct valueSizeCall")
            is FieldType.Message -> "$variable.asInternal()._size"
        }
    }

    private fun tagSizeCall(number: Int, wireType: WireType): String {
        return "$PB_PKG.WireSize.tag($number, $PB_PKG.WireType.$wireType)"
    }

    private fun int32SizeCall(number: String): String {
        return "$PB_PKG.WireSize.int32($number)"
    }

    private fun FieldDeclaration.notDefaultCheck(): String {
        return when (val fieldType = type) {
            is FieldType.IntegralType -> when (fieldType) {
                FieldType.IntegralType.BYTES, FieldType.IntegralType.STRING -> "$name.isNotEmpty()"
                else -> "$name != ${fieldType.defaultValue}"
            }

            is FieldType.List -> "$name.isNotEmpty()"
            is FieldType.Message -> "<TODO: Implement Reference defaultCheck>"

            is FieldType.Enum -> "${fieldType.defaultValue} != $name"

            else -> TODO("Field: $name, type: $fieldType")
        }
    }

    private fun FieldType.decodeEncodeFuncName(): String? = when (this) {
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
        is FieldType.Enum -> "Enum"
        is FieldType.Map -> null
        is FieldType.OneOf -> null
        is FieldType.Message -> null
    }

    private fun FieldDeclaration.transformToFieldDeclaration(): String {
        return "${name}: ${typeFqName()}"
    }

    private fun FieldDeclaration.typeFqName(): String {
        return when (type) {
            is FieldType.Message -> {
                type.dec.value.name.safeFullName()
            }

            is FieldType.Enum -> type.dec.name.safeFullName()

            is FieldType.OneOf -> type.dec.name.safeFullName()

            is FieldType.IntegralType -> {
                type.fqName.simpleName
            }

            is FieldType.List -> {
                val fqValue = when (val value = type.value) {
                    is FieldType.Message -> value.dec.value.name
                    is FieldType.IntegralType -> value.fqName
                    else -> error("Unsupported type: $value")
                }

                "List<${fqValue.safeFullName()}>"
            }

            is FieldType.Map -> {
                val entry by type.entry

                val fqKey = when (val key = entry.key) {
                    is FieldType.Message -> key.dec.value.name
                    is FieldType.IntegralType -> key.fqName
                    else -> error("Unsupported type: $key")
                }

                val fqValue = when (val value = entry.value) {
                    is FieldType.Message -> value.dec.value.name
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

        val className = declaration.name.simpleName

        val entriesSorted = declaration.originalEntries.sortedBy { it.dec.number }

        clazz(
            className, "sealed",
            constructorArgs = listOf("open val number: Int"),
        ) {

            declaration.originalEntries.forEach { variant ->
                clazz(
                    name = variant.name.simpleName,
                    declarationType = DeclarationType.Object,
                    superTypes = listOf("$className(number = ${variant.dec.number})"),
                )
            }

            // TODO: Avoid name conflict
            clazz(
                modifiers = "data",
                name = "UNRECOGNIZED",
                constructorArgs = listOf("override val number: Int"),
                superTypes = listOf("$className(number)"),
            )

            newLine()

            clazz("", modifiers = "companion", declarationType = DeclarationType.Object) {
                declaration.aliases.forEach { alias: EnumDeclaration.Alias ->
                    code(
                        "val ${alias.name.simpleName}: $className " +
                                "get() = ${alias.original.name.simpleName}"
                    )
                }

                val entryNamesSorted = entriesSorted.joinToString(", ") { it.name.simpleName }
                code("val entries: List<$className> by lazy { listOf($entryNamesSorted) }")
            }

        }
    }

    @Suppress("detekt.LongMethod")
    private fun CodeGenerator.generatePublicService(service: ServiceDeclaration) {
        code("@kotlinx.rpc.grpc.annotations.Grpc")
        clazz(service.name.simpleName, declarationType = DeclarationType.Interface) {
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

    private fun MessageDeclaration.internalClassFullName(): String {
        return name.safeFullName(MSG_INTERNAL_SUFFIX)
    }

    private fun MessageDeclaration.internalClassName(): String {
        return name.simpleName + MSG_INTERNAL_SUFFIX
    }
}

private fun MessageDeclaration.allEnumsRecursively(): List<EnumDeclaration> =
    enumDeclarations + nestedDeclarations.flatMap(MessageDeclaration::allEnumsRecursively)

private fun MessageDeclaration.allNestedRecursively(): List<MessageDeclaration> =
    nestedDeclarations + nestedDeclarations.flatMap(MessageDeclaration::allNestedRecursively)

private fun String.packageNameSuffixed(suffix: String): String {
    return if (isEmpty()) suffix else "$this.$suffix"
}
