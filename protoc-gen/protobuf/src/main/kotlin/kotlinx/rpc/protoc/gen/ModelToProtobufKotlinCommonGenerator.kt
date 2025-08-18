/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("detekt.all")

package kotlinx.rpc.protoc.gen

import kotlinx.rpc.protoc.gen.core.AModelToKotlinCommonGenerator
import kotlinx.rpc.protoc.gen.core.CodeGenerator
import kotlinx.rpc.protoc.gen.core.INTERNAL_RPC_API_ANNO
import kotlinx.rpc.protoc.gen.core.PB_PKG
import kotlinx.rpc.protoc.gen.core.WITH_CODEC_ANNO
import kotlinx.rpc.protoc.gen.core.model.EnumDeclaration
import kotlinx.rpc.protoc.gen.core.model.FieldDeclaration
import kotlinx.rpc.protoc.gen.core.model.FieldType
import kotlinx.rpc.protoc.gen.core.model.FileDeclaration
import kotlinx.rpc.protoc.gen.core.model.MessageDeclaration
import kotlinx.rpc.protoc.gen.core.model.Model
import kotlinx.rpc.protoc.gen.core.model.OneOfDeclaration
import kotlinx.rpc.protoc.gen.core.model.WireType
import org.slf4j.Logger

class ModelToProtobufKotlinCommonGenerator(
    model: Model,
    logger: Logger,
    explicitApiModeEnabled: Boolean,
) : AModelToKotlinCommonGenerator(model, logger, explicitApiModeEnabled) {
    override val FileDeclaration.hasPublicGeneratedContent: Boolean
        get() = enumDeclarations.isNotEmpty() || messageDeclarations.isNotEmpty()

    override val FileDeclaration.hasInternalGeneratedContent: Boolean
        get() = hasPublicGeneratedContent

    override fun CodeGenerator.generatePublicDeclaredEntities(fileDeclaration: FileDeclaration) {
        fileDeclaration.messageDeclarations.forEach { generatePublicMessage(it) }
        fileDeclaration.enumDeclarations.forEach { generatePublicEnum(it) }
    }

    override fun CodeGenerator.generateInternalDeclaredEntities(fileDeclaration: FileDeclaration) {
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

    @Suppress("detekt.CyclomaticComplexMethod")
    private fun CodeGenerator.generatePublicMessage(declaration: MessageDeclaration) {
        if (!declaration.isUserFacing) return

        clazz(
            name = declaration.name.simpleName,
            declarationType = CodeGenerator.DeclarationType.Interface,
            annotations = listOf("@$WITH_CODEC_ANNO(${declaration.internalClassFullName()}.CODEC::class)")
        ) {
            declaration.actualFields.forEachIndexed { i, field ->
                property(
                    name = field.name,
                    type = field.typeFqName(),
                    needsNewLineAfterDeclaration = i == declaration.actualFields.lastIndex,
                )
            }

            if (declaration.actualFields.isNotEmpty()) {
                newLine()
            }

            declaration.oneOfDeclarations.forEach { oneOf ->
                generateOneOfPublic(oneOf)
            }

            declaration.nestedDeclarations.forEach { nested ->
                generatePublicMessage(nested)
            }

            declaration.enumDeclarations.forEach { enum ->
                generatePublicEnum(enum)
            }

            clazz("", modifiers = "companion", declarationType = CodeGenerator.DeclarationType.Object)
        }
    }

    @Suppress("detekt.CyclomaticComplexMethod")
    private fun CodeGenerator.generateInternalMessage(declaration: MessageDeclaration) {
        val internalClassName = declaration.internalClassName()

        val superTypes = buildList {
            if (declaration.isUserFacing) {
                add(declaration.name.safeFullName())
            }
            add("$PB_PKG.InternalMessage(fieldsWithPresence = ${declaration.presenceMaskSize})")
        }

        clazz(
            name = internalClassName,
            declarationType = CodeGenerator.DeclarationType.Class,
            superTypes = superTypes,
        ) {

            generatePresenceIndicesObject(declaration)

            property(
                name = "_size",
                modifiers = "override",
                annotations = listOf("@$INTERNAL_RPC_API_ANNO"),
                type = "Int",
                propertyInitializer = CodeGenerator.PropertyInitializer.DELEGATE,
                value = "lazy { computeSize() }"
            )

            val override = if (declaration.isUserFacing) "override" else ""
            declaration.actualFields.forEachIndexed { i, field ->
                val value = when {
                    field.nullable -> {
                        "null"
                    }

                    field.type is FieldType.Message ->
                        "MsgFieldDelegate(PresenceIndices.${field.name}) { " +
                                "${(field.type as FieldType.Message).dec.value.internalClassFullName()}() " +
                                "}"

                    else -> {
                        val fieldPresence = if (field.presenceIdx != null) "(PresenceIndices.${field.name})" else ""
                        "MsgFieldDelegate$fieldPresence { ${field.type.defaultValue} }"
                    }
                }

                property(
                    name = field.name,
                    modifiers = override,
                    value = value,
                    isVar = true,
                    type = field.typeFqName(),
                    propertyInitializer = if (field.nullable) {
                        CodeGenerator.PropertyInitializer.PLAIN
                    } else {
                        CodeGenerator.PropertyInitializer.DELEGATE
                    },
                    needsNewLineAfterDeclaration = i == declaration.actualFields.lastIndex,
                )
            }

            declaration.nestedDeclarations.forEach { nested ->
                generateInternalMessage(nested)
            }

            generateCodecObject(declaration)

            // required for decodeWith extension
            clazz(
                name = "",
                modifiers = "companion",
                annotations = listOf("@$INTERNAL_RPC_API_ANNO"),
                declarationType = CodeGenerator.DeclarationType.Object
            )
        }
    }

    private fun CodeGenerator.generatePresenceIndicesObject(declaration: MessageDeclaration) {
        if (declaration.presenceMaskSize == 0) {
            return
        }

        clazz("PresenceIndices", "private", declarationType = CodeGenerator.DeclarationType.Object) {
            declaration.actualFields.forEachIndexed { i, field ->
                if (field.presenceIdx != null) {
                    property(
                        field.name,
                        modifiers = "const",
                        value = field.presenceIdx.toString(),
                        type = "Int",
                        needsNewLineAfterDeclaration = i == declaration.actualFields.lastIndex,
                    )
                }
            }
        }
    }

    private fun CodeGenerator.generateCodecObject(declaration: MessageDeclaration) {
        if (!declaration.isUserFacing) return

        val msgFqName = declaration.name.safeFullName()
        val inputStreamFqName = "kotlinx.rpc.protobuf.input.stream.InputStream"
        val bufferFqName = "kotlinx.io.Buffer"
        clazz(
            name = "CODEC",
            annotations = listOf("@$INTERNAL_RPC_API_ANNO"),
            declarationType = CodeGenerator.DeclarationType.Object,
            superTypes = listOf("kotlinx.rpc.grpc.codec.MessageCodec<$msgFqName>"),
        ) {
            function("encode", modifiers = "override", args = "value: $msgFqName", returnType = inputStreamFqName) {
                code("val buffer = $bufferFqName()")
                code("val encoder = $PB_PKG.WireEncoder(buffer)")
                scope("${PB_PKG}.checkForPlatformEncodeException", nlAfterClosed = false) {
                    code("value.asInternal().encodeWith(encoder)")
                }
                code("encoder.flush()")
                code("return buffer.asInputStream()")
            }

            function("decode", modifiers = "override", args = "stream: $inputStreamFqName", returnType = msgFqName) {
                scope("$PB_PKG.WireDecoder(stream).use") {
                    code("val msg = ${declaration.internalClassFullName()}()")
                    scope("${PB_PKG}.checkForPlatformDecodeException", nlAfterClosed = false) {
                        code("${declaration.internalClassFullName()}.decodeWith(msg, it)")
                    }
                    code("return msg")
                }
            }
        }

        additionalInternalImports.add("kotlinx.rpc.protobuf.input.stream.asInputStream")
    }

    private fun CodeGenerator.generateMessageConstructor(declaration: MessageDeclaration) {
        if (!declaration.isUserFacing) return

        function(
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
    }

    private fun CodeGenerator.generateMessageDecoder(declaration: MessageDeclaration) = function(
        name = "decodeWith",
        args = "msg: ${declaration.internalClassFullName()}, decoder: $PB_PKG.WireDecoder",
        annotations = listOf("@$INTERNAL_RPC_API_ANNO"),
        contextReceiver = "${declaration.internalClassFullName()}.Companion",
        returnType = "Unit",
    ) {
        whileBlock("true") {
            code("val tag = decoder.readTag() ?: break // EOF, we read the whole message")
            whenBlock {
                declaration.actualFields.forEach { field -> readMatchCase(field) }
                whenCase("else") {
                    code("// we are currently just skipping unknown fields (KRPC-191)")
                    code("decoder.skipValue(tag.wireType)")
                }
            }
        }

        code("msg.checkRequiredFields()")

        // TODO: Make lists and maps immutable (KRPC-190)
    }

    private fun CodeGenerator.readMatchCase(
        field: FieldDeclaration,
        lvalue: String = "msg.${field.name}",
        wrapperCtor: (String) -> String = { it },
        beforeValueDecoding: CodeGenerator.() -> Unit = {},
    ) {
        when (val fieldType = field.type) {
            is FieldType.IntegralType -> whenCase("tag.fieldNr == ${field.number} && tag.wireType == $PB_PKG.WireType.${field.type.wireType.name}") {
                beforeValueDecoding()
                generateDecodeFieldValue(fieldType, lvalue, wrapperCtor = wrapperCtor)
            }

            is FieldType.List -> {
                // Protocol buffer parsers must be able
                // to parse repeated fields that were compiled as packed as if they were not packed,
                // and vice versa.
                if (fieldType.value.isPackable) {
                    whenCase("tag.fieldNr == ${field.number} && tag.wireType == $PB_PKG.WireType.LENGTH_DELIMITED") {
                        beforeValueDecoding()
                        generateDecodeFieldValue(fieldType, lvalue, isPacked = true, wrapperCtor = wrapperCtor)
                    }
                }

                whenCase("tag.fieldNr == ${field.number} && tag.wireType == $PB_PKG.WireType.${fieldType.value.wireType.name}") {
                    beforeValueDecoding()
                    generateDecodeFieldValue(fieldType, lvalue, isPacked = false, wrapperCtor = wrapperCtor)
                }
            }

            is FieldType.Enum -> whenCase("tag.fieldNr == ${field.number} && tag.wireType == $PB_PKG.WireType.VARINT") {
                beforeValueDecoding()
                generateDecodeFieldValue(fieldType, lvalue, wrapperCtor = wrapperCtor)
            }

            is FieldType.OneOf -> {
                fieldType.dec.variants.forEach { variant ->
                    val variantName = "${fieldType.dec.name.safeFullName()}.${variant.name}"
                    if (variant.type is FieldType.Message) {
                        // in case of a message, we must construct an empty message before reading the message
                        val message = variant.type as FieldType.Message
                        readMatchCase(
                            field = variant,
                            lvalue = "field.value",
                            beforeValueDecoding = {
                                beforeValueDecoding()
                                scope("val field = ($lvalue as? $variantName) ?: $variantName(${message.internalConstructor()}).also") {
                                    // write the constructed oneof variant to the field
                                    code("$lvalue = it")
                                }
                            })
                    } else {
                        readMatchCase(
                            field = variant,
                            lvalue = lvalue,
                            wrapperCtor = { "$variantName($it)" },
                            beforeValueDecoding = beforeValueDecoding
                        )
                    }
                }
            }

            is FieldType.Message -> {
                whenCase("tag.fieldNr == ${field.number} && tag.wireType == $PB_PKG.WireType.LENGTH_DELIMITED") {
                    if (field.presenceIdx != null) {
                        // check if the current sub message object was already set, if not, set a new one
                        // to set the field's presence tracker to true
                        ifBranch(condition = "!msg.presenceMask[${field.presenceIdx}]", ifBlock = {
                            code("$lvalue = ${fieldType.dec.value.internalClassFullName()}()")
                        })
                    }
                    beforeValueDecoding()
                    generateDecodeFieldValue(fieldType, lvalue, wrapperCtor = wrapperCtor)
                }
            }

            is FieldType.Map -> whenCase("tag.fieldNr == ${field.number} && tag.wireType == $PB_PKG.WireType.LENGTH_DELIMITED") {
                beforeValueDecoding()
                generateDecodeFieldValue(fieldType, lvalue, wrapperCtor = wrapperCtor)
            }
        }
    }

    private fun CodeGenerator.generateDecodeFieldValue(
        fieldType: FieldType,
        lvalue: String,
        isPacked: Boolean = false,
        wrapperCtor: (String) -> String = { it },
    ) {
        when (fieldType) {
            is FieldType.IntegralType -> {
                val raw = "decoder.read${fieldType.decodeEncodeFuncName()}()"
                code("$lvalue = ${wrapperCtor(raw)}")
            }

            is FieldType.List -> if (isPacked) {
                val conversion = if (fieldType.value is FieldType.Enum){
                    ".map { ${(fieldType.value as FieldType.Enum).dec.name.safeFullName()}.fromNumber(it) }"
                } else {
                    ""
                }

                // Note that although there’s usually no reason
                // to encode more than one key-value pair for a packed repeated field,
                // parsers must be prepared to accept multiple key-value pairs.
                // In this case, the payloads should be concatenated.
                code("$lvalue += decoder.readPacked${fieldType.value.decodeEncodeFuncName()}()$conversion")
            } else {
                when (val elemType = fieldType.value) {
                    is FieldType.Message -> {
                        code("val elem = ${elemType.dec.value.internalClassFullName()}()")
                        generateDecodeFieldValue(fieldType.value, "elem", wrapperCtor = wrapperCtor)
                    }

                    else -> generateDecodeFieldValue(fieldType.value, "val elem", wrapperCtor = wrapperCtor)
                }
                code("($lvalue as MutableList).add(elem)")
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

            is FieldType.Map -> {
                val entryClassName = fieldType.entry.dec.internalClassFullName()
                scope("with($entryClassName())") {
                    generateDecodeFieldValue(
                        fieldType = FieldType.Message(lazy { fieldType.entry.dec }),
                        lvalue = "this",
                        isPacked = false,
                        wrapperCtor = wrapperCtor
                    )
                    code("($lvalue as MutableMap)[key] = value")
                }
            }
        }
    }

    private fun CodeGenerator.generateMessageEncoder(declaration: MessageDeclaration) = function(
        name = "encodeWith",
        annotations = listOf("@$INTERNAL_RPC_API_ANNO"),
        args = "encoder: $PB_PKG.WireEncoder",
        contextReceiver = declaration.internalClassFullName(),
        returnType = "Unit",
    ) {
        if (declaration.actualFields.isEmpty()) {
            code("// no fields to encode")
            return@function
        }

        declaration.actualFields.forEach { field ->
            val fieldName = field.name
            if (field.nullable) {
                scope("$fieldName?.also") {
                    generateEncodeFieldValue(field, "it")
                }
            } else if (field.dec.hasPresence()) {
                ifBranch(condition = "presenceMask[${field.presenceIdx}]", ifBlock = {
                    generateEncodeFieldValue(field, field.name)
                })
            } else {
                ifBranch(condition = field.notDefaultCheck(), ifBlock = {
                    generateEncodeFieldValue(field, field.name)
                })
            }
        }
    }

    private fun CodeGenerator.generateEncodeFieldValue(
        field: FieldDeclaration,
        valueVar: String,
    ) {
        generateEncodeFieldValue(
            valueVar = valueVar,
            type = field.type,
            number = field.number,
            isPacked = field.dec.isPacked,
            packedWithFixedSize = field.packedFixedSize
        )
    }

    private fun CodeGenerator.generateEncodeFieldValue(
        valueVar: String,
        type: FieldType,
        number: Int,
        isPacked: Boolean,
        packedWithFixedSize: Boolean,
    ) {
        var encFunc = type.decodeEncodeFuncName()
        when (type) {
            is FieldType.IntegralType -> code("encoder.write${encFunc!!}(fieldNr = $number, value = $valueVar)")
            is FieldType.List -> {
                val packedValueVar = if (type.value is FieldType.Enum) {
                    "$valueVar.map { it.number }"
                } else {
                    valueVar
                }

                encFunc = type.value.decodeEncodeFuncName()
                when {
                    isPacked && packedWithFixedSize ->
                        code("encoder.writePacked${encFunc!!}(fieldNr = $number, value = $packedValueVar)")

                    isPacked && !packedWithFixedSize ->
                        code(
                            "encoder.writePacked${encFunc!!}(fieldNr = $number, value = $packedValueVar, fieldSize = ${
                                type.valueSizeCall(valueVar, number, true)
                            })"
                        )

                    type.value is FieldType.Message -> scope("$valueVar.forEach") {
                        code("encoder.writeMessage(fieldNr = ${number}, value = it.asInternal()) { encodeWith(it) }")
                    }

                    else -> {
                        require(encFunc != null) { "No encode function for list type: $type" }
                        scope("$valueVar.forEach") {
                            val enumSuffix = if (type.value is FieldType.Enum) ".number" else ""
                            code("encoder.write${encFunc}($number, it$enumSuffix)")
                        }
                    }
                }
            }

            is FieldType.Enum -> code("encoder.write${encFunc!!}(fieldNr = $number, value = ${valueVar}.number)")

            is FieldType.OneOf -> whenBlock("val value = $valueVar") {
                type.dec.variants.forEach { variant ->
                    whenCase("is ${type.dec.name.safeFullName()}.${variant.name}") {
                        generateEncodeFieldValue(variant, "value.value")
                    }
                }
            }

            is FieldType.Map -> {
                scope("$valueVar.forEach", paramDecl = "kEntry ->") {
                    generateMapConstruction(type, "kEntry.key", "kEntry.value")
                    scope(".also", paramDecl = "entry ->") {
                        generateEncodeFieldValue(
                            valueVar = "entry",
                            type = FieldType.Message(lazy { type.entry.dec }),
                            number = number,
                            isPacked = false,
                            packedWithFixedSize = false,
                        )
                    }
                }
            }

            is FieldType.Message -> code("encoder.writeMessage(fieldNr = ${number}, value = $valueVar.asInternal()) { encodeWith(it) }")
        }

    }


    private fun CodeGenerator.generateInternalEnumConstructor(enum: EnumDeclaration) {
        function(
            name = "fromNumber",
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
        annotations = listOf("@$INTERNAL_RPC_API_ANNO"),
        contextReceiver = declaration.internalClassFullName(),
        returnType = "Unit",
    ) {
        val requiredFields = declaration.actualFields.filter { it.dec.isRequired }

        if (requiredFields.isEmpty()) {
            code("// no required fields to check")
        }

        requiredFields.forEach { field ->
            ifBranch(condition = "!presenceMask[${field.presenceIdx}]", ifBlock = {
                code("throw ${PB_PKG}.ProtobufDecodingException.missingRequiredField(\"${declaration.name.simpleName}\", \"${field.name}\")")
            })
        }

        // check submessages
        declaration.actualFields.filter { it.type is FieldType.Message }.forEach { field ->
            ifBranch(condition = "presenceMask[${field.presenceIdx}]", ifBlock = {
                code("${field.name}.asInternal().checkRequiredFields()")
            })
        }

        // check submessages in oneofs
        declaration.actualFields.filter { it.type is FieldType.OneOf }.forEach { field ->
            val oneOfType = field.type as FieldType.OneOf
            val messageVariants = oneOfType.dec.variants.filter { it.type is FieldType.Message }
            if (messageVariants.isEmpty()) return@forEach

            scope("${field.name}?.also") {
                whenBlock {
                    messageVariants.forEach { variant ->
                        val variantClassName =
                            "${(field.type as FieldType.OneOf).dec.name.safeFullName()}.${variant.name}"
                        whenCase("it is $variantClassName") {
                            code("it.value.asInternal().checkRequiredFields()")
                        }
                    }
                }
            }
        }

        // check submessages in lists
        declaration.actualFields.filter { it.type is FieldType.List }.forEach { field ->
            val listType = field.type as FieldType.List
            if (listType.value !is FieldType.Message) return@forEach

            scope("${field.name}.forEach") {
                code("it.asInternal().checkRequiredFields()")
            }
        }

        // check submessage in maps
        declaration.actualFields.filter { it.type is FieldType.Map }.forEach { field ->
            val mapType = field.type as FieldType.Map
            // we only have to check the value, as the key cannot be a message
            if (mapType.entry.value !is FieldType.Message) return@forEach

            scope("${field.name}.values.forEach") {
                code("it.asInternal().checkRequiredFields()")
            }
        }
    }

    private fun CodeGenerator.generateInternalComputeSize(declaration: MessageDeclaration) {
        function(
            name = "computeSize",
            modifiers = "private",
            contextReceiver = declaration.internalClassFullName(),
            returnType = "Int",
        ) {
            code("var __result = 0")
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
            code("return __result")
        }
    }

    private fun CodeGenerator.generateInternalCastExtension(declaration: MessageDeclaration) {
        val internalClassName = declaration.internalClassFullName()
        val ctxReceiver = if (declaration.isUserFacing) declaration.name.safeFullName() else internalClassName

        // we generate the asInternal extension even for non-user-facing message classes (map entry)
        // to avoid edge-cases when generating other code that uses the asInternal() extension.
        function(
            "asInternal",
            annotations = listOf("@$INTERNAL_RPC_API_ANNO"),
            contextReceiver = ctxReceiver,
            returnType = internalClassName,
        ) {
            if (ctxReceiver == internalClassName) {
                code("return this")
            } else {
                code("return this as? $internalClassName ?: error(\"Message \${this::class.simpleName} is a non-internal message type.\")")
            }
        }
    }

    private fun CodeGenerator.generateFieldComputeSizeCall(
        field: FieldDeclaration,
        variable: String,
    ) {
        val valueSize by lazy { field.type.valueSizeCall(variable, field.number, field.dec.isPacked) }
        val tagSize = tagSizeCall(field.number, field.type.wireType)

        when (field.type) {
            is FieldType.List -> when {
                // packed fields also have the tag + len
                field.dec.isPacked -> code("__result += $valueSize.let { $tagSize + ${int32SizeCall("it")} + it }")
                else -> code("__result = $valueSize")
            }

            is FieldType.Message,
            FieldType.IntegralType.STRING,
            FieldType.IntegralType.BYTES,
                -> code("__result += $valueSize.let { $tagSize + ${int32SizeCall("it")} + it }")

            is FieldType.Map -> {
                scope("__result += ${field.name}.entries.sumOf", paramDecl = "kEntry ->") {
                    generateMapConstruction(field.type as FieldType.Map, "kEntry.key", "kEntry.value")
                    code("._size")
                }
            }

            is FieldType.OneOf -> whenBlock("val value = $variable") {
                (field.type as FieldType.OneOf).dec.variants.forEach { variant ->
                    val variantName = "${(field.type as FieldType.OneOf).dec.name.safeFullName()}.${variant.name}"
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
            FieldType.IntegralType.SFIXED64,
                -> code("__result += ($tagSize + $valueSize)")
        }
    }

    private fun CodeGenerator.generateMapConstruction(
        map: FieldType.Map,
        keyVar: String,
        valueVar: String,
    ) {
        val entryClass = map.entry.dec.internalClassFullName()
        scope("$entryClass().apply", nlAfterClosed = false) {
            code("key = $keyVar")
            code("value = $valueVar")
        }
    }

    private fun FieldType.valueSizeCall(variable: String, number: Int, isPacked: Boolean = false): String {
        val sizeFunName = decodeEncodeFuncName()?.replaceFirstChar { it.lowercase() }

        val convertedVariable = if (isPacked && this is FieldType.List && value is FieldType.Enum) {
            "${variable}.map { it.number }"
        } else {
            variable
        }

        val sizeFunc = "$PB_PKG.WireSize.$sizeFunName($convertedVariable)"

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

    @Suppress("SameParameterValue")
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
            is FieldType.Message -> error("Message fields should not be checked for default values.")

            is FieldType.Enum -> "${fieldType.defaultValue} != $name"

            else -> "$name.isNotEmpty()"
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

    private fun CodeGenerator.generateOneOfPublic(declaration: OneOfDeclaration) {
        val interfaceName = declaration.name.simpleName

        clazz(interfaceName, "sealed", declarationType = CodeGenerator.DeclarationType.Interface) {
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
                    declarationType = CodeGenerator.DeclarationType.Object,
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

            clazz("", modifiers = "companion", declarationType = CodeGenerator.DeclarationType.Object) {
                declaration.aliases.forEach { alias: EnumDeclaration.Alias ->
                    property(
                        name = alias.name.simpleName,
                        type = className,
                        propertyInitializer = CodeGenerator.PropertyInitializer.GETTER,
                        value = alias.original.name.simpleName,
                    )
                }

                val entryNamesSorted = entriesSorted.joinToString(", ") { it.name.simpleName }
                property(
                    name = "entries",
                    type = "List<$className>",
                    propertyInitializer = CodeGenerator.PropertyInitializer.DELEGATE,
                    value = "lazy { listOf($entryNamesSorted) }"
                )
            }
        }
    }
}

private fun MessageDeclaration.allEnumsRecursively(): List<EnumDeclaration> =
    enumDeclarations + nestedDeclarations.flatMap(MessageDeclaration::allEnumsRecursively)

private fun MessageDeclaration.allNestedRecursively(): List<MessageDeclaration> =
    nestedDeclarations + nestedDeclarations.flatMap(MessageDeclaration::allNestedRecursively)

