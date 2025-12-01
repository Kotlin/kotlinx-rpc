/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("detekt.all")

package kotlinx.rpc.protoc.gen

import com.google.protobuf.ByteString
import com.google.protobuf.Descriptors
import kotlinx.rpc.protoc.gen.core.AModelToKotlinCommonGenerator
import kotlinx.rpc.protoc.gen.core.CodeGenerator
import kotlinx.rpc.protoc.gen.core.Comment
import kotlinx.rpc.protoc.gen.core.Config
import kotlinx.rpc.protoc.gen.core.INTERNAL_RPC_API_ANNO
import kotlinx.rpc.protoc.gen.core.PB_PKG
import kotlinx.rpc.protoc.gen.core.WITH_CODEC_ANNO
import kotlinx.rpc.protoc.gen.core.fqName
import kotlinx.rpc.protoc.gen.core.model.EnumDeclaration
import kotlinx.rpc.protoc.gen.core.model.FieldDeclaration
import kotlinx.rpc.protoc.gen.core.model.FieldType
import kotlinx.rpc.protoc.gen.core.model.FileDeclaration
import kotlinx.rpc.protoc.gen.core.model.MessageDeclaration
import kotlinx.rpc.protoc.gen.core.model.Model
import kotlinx.rpc.protoc.gen.core.model.OneOfDeclaration
import kotlinx.rpc.protoc.gen.core.model.WireType
import kotlinx.rpc.protoc.gen.core.model.scalarDefaultSuffix

class ModelToProtobufKotlinCommonGenerator(
    config: Config,
    model: Model,
) : AModelToKotlinCommonGenerator(config, model) {
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
            generatePublicCopy(it)
            generatePublicPresenceGetter(it)
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

        val annotations = mutableListOf<String>()
        if (!declaration.isGroup) {
            annotations.add("@$WITH_CODEC_ANNO(${declaration.internalClassFullName()}.CODEC::class)")
        }

        clazz(
            name = declaration.name.simpleName,
            comment = declaration.doc,
            declarationType = CodeGenerator.DeclarationType.Interface,
            annotations = annotations,
            deprecation = if (declaration.deprecated) DeprecationLevel.WARNING else null,
        ) {
            declaration.actualFields.forEachIndexed { i, field ->
                property(
                    name = field.name,
                    comment = field.doc,
                    type = field.typeFqName(),
                    needsNewLineAfterDeclaration = i == declaration.actualFields.lastIndex,
                    deprecation = if (field.deprecated) DeprecationLevel.WARNING else null,
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
            generateBytesDefaultsObject(declaration)

            property(
                name = "_size",
                modifiers = "override",
                annotations = listOf("@$INTERNAL_RPC_API_ANNO"),
                type = "Int",
                propertyInitializer = CodeGenerator.PropertyInitializer.DELEGATE,
                value = "lazy { computeSize() }"
            )

            if (declaration.isUserFacing && declaration.hasPresenceFields) {
                property(
                    name = "_presence",
                    type = declaration.presenceClassFullName(),
                    value = "Presence.create(this)",
                )
            }

            val override = if (declaration.isUserFacing) "override" else ""
            declaration.actualFields.forEachIndexed { i, field ->
                val value = when {
                    field.nullable && field.presenceIdx == null -> {
                        "null"
                    }

                    field.type is FieldType.Message ->
                        "MsgFieldDelegate(PresenceIndices.${field.name}) { " +
                                "${(field.type as FieldType.Message).dec.value.internalClassFullName()}() " +
                                "}"

                    else -> {
                        val fieldPresence = if (field.presenceIdx != null) "(PresenceIndices.${field.name})" else ""
                        "MsgFieldDelegate$fieldPresence { ${field.safeDefaultValue()} }"
                    }
                }

                property(
                    name = field.name,
                    modifiers = override,
                    value = value,
                    isVar = true,
                    type = field.typeFqName(),
                    propertyInitializer = if (field.nullable && field.presenceIdx == null) {
                        CodeGenerator.PropertyInitializer.PLAIN
                    } else {
                        CodeGenerator.PropertyInitializer.DELEGATE
                    },
                    needsNewLineAfterDeclaration = i == declaration.actualFields.lastIndex,
                )
            }

            generateHashCode(declaration)
            generateOneOfHashCode(declaration)
            generateEquals(declaration)
            generateToString(declaration)
            generateInternalCopy(declaration)
            generateOneOfCopy(declaration)

            declaration.nestedDeclarations.forEach { nested ->
                generateInternalMessage(nested)
            }

            generateCodecObject(declaration)
            generatePresenceClass(declaration)

            // required for decodeWith extension
            clazz(
                name = "",
                modifiers = "companion",
                annotations = listOf("@$INTERNAL_RPC_API_ANNO"),
                declarationType = CodeGenerator.DeclarationType.Object
            )
        }
    }

    private fun CodeGenerator.generateHashCode(declaration: MessageDeclaration) {
        val fields = declaration.actualFields
        function(
            name = "hashCode",
            modifiers = "override",
            returnType = "kotlin.Int",
        ) {
            addLine("checkRequiredFields()")
            when {
                fields.size == 1 -> {
                    val expr = fields[0].hashExprForHashCode()
                    addLine("return $expr")
                }

                fields.isNotEmpty() -> {
                    addLine("var result = ${fields.first().hashExprForHashCode()}")
                    fields.drop(1).forEach { f ->
                        addLine("result = 31 * result + ${f.hashExprForHashCode()}")
                    }
                    addLine("return result")
                }

                else -> {
                    addLine("return this::class.hashCode()")
                }
            }
        }
    }

    private fun FieldDeclaration.hashExprForHashCode(): String {
        return when (val t = type) {
            is FieldType.IntegralType -> {
                when (t) {
                    FieldType.IntegralType.BYTES -> {
                        if (nullable) "(${name}?.contentHashCode() ?: 0)" else "${name}.contentHashCode()"
                    }
                    else -> {
                        if (nullable) "(${name}?.hashCode() ?: 0)" else "${name}.hashCode()"
                    }
                }
            }

            is FieldType.OneOf -> {
                if (nullable) "(${name}?.oneOfHashCode() ?: 0)" else "${name}.oneOfHashCode() + "
            }

            is FieldType.Message, is FieldType.Enum, is FieldType.List, is FieldType.Map -> {
                if (nullable) "(${name}?.hashCode() ?: 0)" else "${name}.hashCode()"
            }
        }.let {
            if (presenceIdx != null) {
                "if (presenceMask[${presenceIdx}]) $it else 0"
            } else {
                it
            }
        }
    }

    private fun CodeGenerator.generateOneOfHashCode(declaration: MessageDeclaration) {
        declaration.oneOfDeclarations.forEach { oneOf ->
            function(
                name = "oneOfHashCode",
                returnType = "kotlin.Int",
                contextReceiver = oneOf.name.safeFullName(),
            ) {
                whenBlock(prefix = "val offset = ", condition = "this") {
                    oneOf.variants.forEachIndexed { index, variant ->
                        val variantName = "${oneOf.name.safeFullName()}.${variant.name}"
                        addLine("is $variantName -> $index")
                    }
                }
                addLine("return hashCode() + offset")
            }
        }
    }

    private fun CodeGenerator.generateEquals(declaration: MessageDeclaration) {
        val fields = declaration.actualFields
        function(
            name = "equals",
            modifiers = "override",
            args = "other: kotlin.Any?",
            returnType = "kotlin.Boolean",
        ) {
            addLine("checkRequiredFields()")
            addLine("if (this === other) return true")
            addLine("if (other == null || this::class != other::class) return false")
            addLine("other as ${declaration.internalClassName()}")
            addLine("other.checkRequiredFields()")
            if (declaration.presenceMaskSize != 0) {
                addLine("if (presenceMask != other.presenceMask) return false")
            }
            if (fields.isNotEmpty()) {
                fields.forEach { field ->
                    if (field.presenceIdx != null) {
                        fieldEqualsCheck(
                            presenceCheck = "presenceMask[${field.presenceIdx}] && ",
                            field = field,
                        )
                    } else {
                        fieldEqualsCheck(presenceCheck = "", field = field)
                    }
                }
            }
            addLine("return true")
        }
    }

    private fun CodeGenerator.fieldEqualsCheck(presenceCheck: String, field: FieldDeclaration) {
        when (val t = field.type) {
            is FieldType.IntegralType -> {
                if (t == FieldType.IntegralType.BYTES) {
                    if (field.nullable) {
                        addLine("if ($presenceCheck((${field.name} != null && (other.${field.name} == null || !${field.name}!!.contentEquals(other.${field.name}!!))) || ${field.name} == null)) return false")
                    } else {
                        addLine("if ($presenceCheck!${field.name}.contentEquals(other.${field.name})) return false")
                    }
                } else {
                    addLine("if ($presenceCheck${field.name} != other.${field.name}) return false")
                }
            }
            is FieldType.Message,
            is FieldType.Enum,
            is FieldType.OneOf,
            is FieldType.List,
            is FieldType.Map -> {
                addLine("if ($presenceCheck${field.name} != other.${field.name}) return false")
            }
        }
    }

    private fun CodeGenerator.generateToString(declaration: MessageDeclaration) {
        function(
            name = "toString",
            modifiers = "override",
            returnType = "kotlin.String",
        ) {
            addLine("return asString()")
        }

        function(
            name = "asString",
            args = "indent: kotlin.Int = 0",
            returnType = "kotlin.String",
        ) {
            addLine("checkRequiredFields()")
            addLine("val indentString = \" \".repeat(indent)")
            addLine("val nextIndentString = \" \".repeat(indent + ${config.indentSize})")
            scope("return buildString") {
                addLine("appendLine(\"${declaration.name}(\")")
                declaration.actualFields.forEach {
                    val suffix = when (it.type) {
                        FieldType.IntegralType.BYTES -> {
                            ".contentToString()"
                        }
                        is FieldType.Message -> {
                            ".asInternal().asString(indent = indent + ${config.indentSize})"
                        }
                        else -> {
                            ""
                        }
                    }

                    val valueBuilder: CodeGenerator.() -> Unit = {
                        addLine("appendLine(\"\${nextIndentString}${it.name}=\${${it.name}$suffix},\")")
                    }

                    if (it.presenceIdx != null) {
                        ifBranch(condition = "presenceMask[${it.presenceIdx}]", ifBlock = {
                            valueBuilder()
                        }) {
                            addLine("appendLine(\"\${nextIndentString}${it.name}=<unset>,\")")
                        }
                    } else {
                        valueBuilder()
                    }
                }
                addLine("append(\"\${indentString})\")")
            }
        }
    }

    private fun CodeGenerator.generatePublicCopy(declaration: MessageDeclaration) {
        if (!declaration.isUserFacing) {
            // e.g., internal map entries don't need a copy() method
            return
        }
        val demoField = declaration.actualFields.firstOrNull()?.name
        val invocation = if (demoField == null) "()" else """
            | {
            |    $demoField = ...    
            |}
        """.trimMargin()
        function(
            name = "copy",
            contextReceiver = declaration.name.safeFullName(),
            args = "body: ${declaration.internalClassFullName()}.() -> Unit = {}",
            returnType = declaration.name.safeFullName(),
            comment = Comment.leading("""
                |Copies the original message, including unknown fields.
                |```
                |val copy = original.copy$invocation
                |```
            """.trimMargin())
        ) {
            code("return this.asInternal().copyInternal(body)")
        }
    }

    private fun CodeGenerator.generateInternalCopy(declaration: MessageDeclaration) {
        if (!declaration.isUserFacing) {
            // e.g., internal map entries don't need a copy() method
            return
        }
        function(
            name = "copyInternal",
            annotations = listOf("@$INTERNAL_RPC_API_ANNO"),
            args = "body: ${declaration.internalClassName()}.() -> Unit",
            returnType = declaration.internalClassName(),
        ) {
            code("val copy = ${declaration.internalClassName()}()")
            for (field in declaration.actualFields) {
                // write each field to the new copy object
                if (field.presenceIdx != null) {
                    // if the field has presence, we need to check if it was set in the original object.
                    // if it was set, we copy it to the new object, otherwise we leave it unset.
                    ifBranch(condition = "presenceMask[${field.presenceIdx}]", ifBlock = {
                        code("copy.${field.name} = ${field.type.copyCall(field.name, field.nullable)}")
                    })
                } else {
                    // by default, we copy the field value
                    code("copy.${field.name} = ${field.type.copyCall(field.name, field.nullable)}")
                }
            }
            code("copy.apply(body)")
            code("return copy")
        }
    }

    private fun FieldType.copyCall(varName: String, nullable: Boolean): String {
        return when (this) {
            FieldType.IntegralType.BYTES -> {
                val optionalPrefix = if (nullable) "?" else ""
                "$varName$optionalPrefix.copyOf()"
            }
            is FieldType.IntegralType -> varName
            is FieldType.Enum -> varName
            is FieldType.List -> "$varName.map { ${value.copyCall("it", false)} }"
            is FieldType.Map -> "$varName.mapValues { ${entry.value.copyCall("it.value", false)} }"
            is FieldType.Message -> "$varName.copy()"
            is FieldType.OneOf -> "$varName?.oneOfCopy()"
        }
    }

    private fun CodeGenerator.generateOneOfCopy(declaration: MessageDeclaration) {
        declaration.oneOfDeclarations.forEach { oneOf ->
            val oneOfFullName = oneOf.name.safeFullName()
            function(
                name = "oneOfCopy",
                annotations = listOf("@$INTERNAL_RPC_API_ANNO"),
                returnType = oneOfFullName,
                contextReceiver = oneOfFullName,
            ) {
                // check if the type is copy by value (no need for deep copy)
                val copyByValue = { type: FieldType ->
                    (type is FieldType.IntegralType && type != FieldType.IntegralType.BYTES) || type is FieldType.Enum
                }

                // if all variants are integral or enum types, we can just return this directly.
                val fastPath = oneOf.variants.all { copyByValue(it.type) }
                if (fastPath) {
                    code("return this")
                } else {
                    // dispatch on all possible variants and copy its value
                    whenBlock(
                        prefix = "return",
                        condition = "this"
                    ) {
                        oneOf.variants.forEach { variant ->
                            val variantName = "$oneOfFullName.${variant.name}"
                            whenCase("is $variantName") {
                                if (copyByValue(variant.type)) {
                                    // no need to reconstruct a new object, we can just return this
                                    code("this")
                                } else {
                                    code("$variantName(${variant.type.copyCall("this.value", false)})")
                                }
                            }
                        }
                    }
                }

            }
        }
    }

    private fun CodeGenerator.generatePublicPresenceGetter(declaration: MessageDeclaration) {
        if (!declaration.isUserFacing) return
        if (!declaration.hasPresenceFields) return

        property(
            name = "presence",
            type = declaration.presenceClassFullName(),
            propertyInitializer = CodeGenerator.PropertyInitializer.GETTER,
            contextReceiver = declaration.name.safeFullName(),
            value = "this.asInternal()._presence",
        )
    }

    private fun CodeGenerator.generatePresenceClass(declaration: MessageDeclaration) {
        if (!declaration.isUserFacing) return
        if (!declaration.hasPresenceFields) return

        clazz(
            name = "Presence",
            constructorModifiers = "private",
            constructorArgs = listOf("val message: ${declaration.internalClassFullName()}" to null),
        ) {
            declaration.actualFields.forEach { field ->
                if (field.presenceIdx != null) {
                    property(
                        name = "has${field.name.capitalize()}",
                        type = "kotlin.Boolean",
                        propertyInitializer = CodeGenerator.PropertyInitializer.GETTER,
                        value = "message.presenceMask[${field.presenceIdx}]"
                    )
                }
            }
            companionObject {
                function(
                    name = "create",
                    annotations = listOf("@$INTERNAL_RPC_API_ANNO"),
                    args = "message: ${declaration.internalClassFullName()}",
                    returnType = declaration.presenceClassFullName(),
                ) {
                    code("return ${declaration.presenceClassFullName()}(message)")
                }
            }
        }
    }

    private fun CodeGenerator.generatePresenceIndicesObject(declaration: MessageDeclaration) {
        if (declaration.presenceMaskSize == 0) {
            return
        }

        clazz("PresenceIndices", modifiers = "private", declarationType = CodeGenerator.DeclarationType.Object) {
            val fieldDeclarations = declaration.actualFields.filter { it.presenceIdx != null }
            fieldDeclarations.forEachIndexed { i, field ->
                property(
                    field.name,
                    modifiers = "const",
                    value = field.presenceIdx.toString(),
                    type = "Int",
                    needsNewLineAfterDeclaration = i == fieldDeclarations.lastIndex,
                )
            }
        }
    }

    private fun CodeGenerator.generateBytesDefaultsObject(declaration: MessageDeclaration) {
        val fieldDeclarations = declaration.actualFields
            .filter {
                it.type == FieldType.IntegralType.BYTES &&
                        it.dec.hasDefaultValue() &&
                        !(it.dec.defaultValue as ByteString).isEmpty
            }

        if (fieldDeclarations.isEmpty()) {
            return
        }

        clazz("BytesDefaults", modifiers = "private", declarationType = CodeGenerator.DeclarationType.Object) {
            fieldDeclarations.forEachIndexed { i, field ->
                val value = if (field.dec.hasDefaultValue()) {
                    val stringValue = (field.dec.defaultValue as ByteString).toString(Charsets.UTF_8)
                    if (stringValue.isNotEmpty()) {
                        "\"${stringValue}\".encodeToByteArray()"
                    } else {
                        FieldType.IntegralType.BYTES.defaultValue
                    }
                } else {
                    FieldType.IntegralType.BYTES.defaultValue
                }

                property(
                    name = field.name,
                    value = value,
                    type = "ByteArray",
                    needsNewLineAfterDeclaration = i == fieldDeclarations.lastIndex,
                )
            }
        }
    }

    private fun CodeGenerator.generateCodecObject(declaration: MessageDeclaration) {
        if (!declaration.isUserFacing) return
        // the CODEC object is not necessary for groups, as they are inlined messages
        if (declaration.isGroup) return

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
                    code("msg.checkRequiredFields()")
                    code("return msg")
                }
            }
        }

        additionalInternalImports.add("kotlinx.rpc.protobuf.input.stream.asInputStream")
    }

    private fun CodeGenerator.generateMessageConstructor(declaration: MessageDeclaration) {
        if (!declaration.isUserFacing) return

        val demoField = declaration.actualFields.firstOrNull()?.name
        val invocation = if (demoField == null) "{ }" else """
            |{
            |    $demoField = ...    
            |}
        """.trimMargin()
        function(
            name = "invoke",
            modifiers = "operator",
            args = "body: ${declaration.internalClassFullName()}.() -> Unit",
            contextReceiver = "${declaration.name.safeFullName()}.Companion",
            returnType = declaration.name.safeFullName(),
            comment = Comment.leading("""
                |Constructs a new message.
                |```
                |val message = ${declaration.name.simpleName} $invocation
                |```
            """.trimMargin())
        ) {
            code("val msg = ${declaration.internalClassFullName()}().apply(body)")
            // check if the user set all required fields
            code("msg.checkRequiredFields()")
            code("return msg")
        }
    }

    private fun CodeGenerator.generateMessageDecoder(declaration: MessageDeclaration) {
        var args = "msg: ${declaration.internalClassFullName()}, decoder: $PB_PKG.WireDecoder"
        if (declaration.isGroup) {
            args += ", startGroup: $PB_PKG.KTag"
        }

        function(
            name = "decodeWith",
            args = args,
            annotations = listOf("@$INTERNAL_RPC_API_ANNO"),
            contextReceiver = "${declaration.internalClassFullName()}.Companion",
            returnType = "Unit",
        ) {
            whileBlock("true") {
                if (declaration.isGroup) {
                    code("val tag = decoder.readTag() ?: throw ProtobufDecodingException(\"Missing END_GROUP tag for field: \${startGroup.fieldNr}.\")")
                    ifBranch(condition = "tag.wireType == $PB_PKG.WireType.END_GROUP", ifBlock = {
                        ifBranch(condition = "tag.fieldNr != startGroup.fieldNr", ifBlock = {
                            code("throw ProtobufDecodingException(\"Wrong END_GROUP tag. Expected \${startGroup.fieldNr}, got \${tag.fieldNr}.\")")
                        })
                        code("return")
                    })
                } else {
                    code("val tag = decoder.readTag() ?: break // EOF, we read the whole message")
                }

                whenBlock {
                    declaration.actualFields.forEach { field -> readMatchCase(field) }
                    whenCase("else") {
                        if (!declaration.isGroup) {
                            // fail if we come across an END_GROUP in a normal message
                            ifBranch(condition = "tag.wireType == $PB_PKG.WireType.END_GROUP", ifBlock = {
                                code("throw $PB_PKG.ProtobufDecodingException(\"Unexpected END_GROUP tag.\")")
                            })
                        }
                        code("// we are currently just skipping unknown fields (KRPC-191)")
                        code("decoder.skipValue(tag)")
                    }
                }
            }

            // TODO: Make lists and maps immutable (KRPC-190)
        }
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
                whenCase("tag.fieldNr == ${field.number} && tag.wireType == $PB_PKG.WireType.${fieldType.wireType.name}") {
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
                val conversion = if (fieldType.value is FieldType.Enum) {
                    ".map { ${(fieldType.value as FieldType.Enum).dec.value.name.safeFullName()}.fromNumber(it) }"
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
                val fromNum = "${fieldType.dec.value.name.safeFullName()}.fromNumber"
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
                val msg = fieldType.dec.value
                val fullClassName = msg.internalClassFullName()
                if (msg.isGroup) {
                    code("$fullClassName.decodeWith($lvalue.asInternal(), decoder, tag)")
                } else {
                    code("decoder.readMessage($lvalue.asInternal(), $fullClassName::decodeWith)")
                }
            }

            is FieldType.Map -> {
                val entryClassName = fieldType.entry.dec.value.internalClassFullName()
                scope("with($entryClassName())") {
                    generateDecodeFieldValue(
                        fieldType = FieldType.Message(fieldType.entry.dec),
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

                val innerType = type.value
                encFunc = innerType.decodeEncodeFuncName()
                when {
                    isPacked && packedWithFixedSize ->
                        code("encoder.writePacked${encFunc!!}(fieldNr = $number, value = $packedValueVar)")

                    isPacked && !packedWithFixedSize ->
                        code(
                            "encoder.writePacked${encFunc!!}(fieldNr = $number, value = $packedValueVar, fieldSize = ${
                                type.valueSizeCall(valueVar, number, true)
                            })"
                        )

                    innerType is FieldType.Message -> scope("$valueVar.forEach") {
                        generateEncodeFieldValue("it", innerType, number, isPacked = false, packedWithFixedSize = false)
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
                            type = FieldType.Message(type.entry.dec),
                            number = number,
                            isPacked = false,
                            packedWithFixedSize = false,
                        )
                    }
                }
            }

            is FieldType.Message -> {
                val writeMethod = if (type.dec.value.isGroup) "writeGroupMessage" else "writeMessage"
                code("encoder.$writeMethod(fieldNr = ${number}, value = $valueVar.asInternal()) { encodeWith(it) }")
            }
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

        when (val fieldType = field.type) {
            is FieldType.List -> when {
                // packed fields also have the tag + len
                field.dec.isPacked -> code("__result += $valueSize.let { $tagSize + ${int32SizeCall("it")} + it }")
                else -> code("__result += $valueSize")
            }

            is FieldType.Message -> if (fieldType.dec.value.isGroup) {
                val groupTagSize = tagSizeCall(field.number, WireType.START_GROUP)
                // the group message size is the size of the message plus the start and end group tags
                code("__result += $valueSize.let { (2 * $groupTagSize) + it }")
            } else {
                code("__result += $valueSize.let { $tagSize + ${int32SizeCall("it")} + it }")
            }

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
        val entryClass = map.entry.dec.value.internalClassFullName()
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
            is FieldType.IntegralType -> {
                val defaultValue = safeDefaultValue()
                when {
                    fieldType == FieldType.IntegralType.STRING &&
                            defaultValue == FieldType.IntegralType.STRING.defaultValue -> "$name.isNotEmpty()"

                    fieldType == FieldType.IntegralType.STRING -> "!$name.contentEquals($defaultValue)"

                    fieldType == FieldType.IntegralType.BYTES &&
                            defaultValue == FieldType.IntegralType.BYTES.defaultValue -> "$name.isNotEmpty()"

                    fieldType == FieldType.IntegralType.BYTES -> "!$name.contentEquals($defaultValue)"

                    else -> "$name != $defaultValue"
                }
            }

            is FieldType.List, is FieldType.Map -> "$name.isNotEmpty()"

            is FieldType.Enum -> {
                "$name != ${safeDefaultValue()}"
            }

            is FieldType.Message -> error("Message fields should not be checked for default values.")
            is FieldType.OneOf -> "null"
        }
    }

    private fun FieldDeclaration.safeDefaultValue(): String {
        if (nullable) {
            return "null"
        }

        if (!dec.hasDefaultValue()) {
            return type.defaultValue ?: error("No default value for field $name")
        }

        val value = dec.defaultValue
        return when {
            value is String -> {
                "\"$value\""
            }

            value is ByteString -> {
                "BytesDefaults.$name"
            }

            value is Descriptors.EnumValueDescriptor -> {
                value.fqName().safeFullName()
            }

            value is Int && (type == FieldType.IntegralType.UINT32 || type == FieldType.IntegralType.FIXED32) -> {
                Integer.toUnsignedString(value) + "u"
            }

            value is Long && (type == FieldType.IntegralType.UINT64 || type == FieldType.IntegralType.FIXED64) -> {
                java.lang.Long.toUnsignedString(value) + "uL"
            }

            value is Float -> when {
                value.isNaN() -> "Float.NaN"
                value == Float.POSITIVE_INFINITY -> "Float.POSITIVE_INFINITY"
                value == Float.NEGATIVE_INFINITY -> "Float.NEGATIVE_INFINITY"
                else -> "Float.fromBits(0x%08X)".format(java.lang.Float.floatToRawIntBits(value))
            }

            value is Double -> when {
                value.isNaN() -> "Double.NaN"
                value == Double.POSITIVE_INFINITY -> "Double.POSITIVE_INFINITY"
                value == Double.NEGATIVE_INFINITY -> "Double.NEGATIVE_INFINITY"
                else -> "Double.fromBits(0x%016XL)".format(java.lang.Double.doubleToRawLongBits(value))
            }

            else -> {
                "${value}${type.scalarDefaultSuffix()}"
            }
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

        clazz(
            name = interfaceName,
            comment = declaration.doc,
            modifiers = "sealed",
            declarationType = CodeGenerator.DeclarationType.Interface
        ) {
            declaration.variants.forEach { variant ->
                clazz(
                    name = variant.name,
                    comment = variant.doc,
                    modifiers = "value",
                    constructorArgs = listOf("val value: ${variant.typeFqName()}"),
                    annotations = listOf("@JvmInline"),
                    superTypes = listOf(interfaceName),
                    deprecation = if (variant.deprecated) DeprecationLevel.WARNING else null,
                )

                additionalPublicImports.add("kotlin.jvm.JvmInline")
            }


        }
    }

    private fun CodeGenerator.generatePublicEnum(declaration: EnumDeclaration) {
        val className = declaration.name.simpleName
        val entriesSorted = declaration.originalEntries.sortedBy { it.dec.number }

        clazz(
            name = className,
            comment = declaration.doc,
            modifiers = "sealed",
            constructorArgs = listOf("open val number: Int"),
            deprecation = if (declaration.deprecated) DeprecationLevel.WARNING else null,
        ) {
            declaration.originalEntries.forEach { variant ->
                clazz(
                    name = variant.name.simpleName,
                    comment = variant.doc,
                    modifiers = "data",
                    declarationType = CodeGenerator.DeclarationType.Object,
                    superTypes = listOf("$className(number = ${variant.dec.number})"),
                    deprecation = if (variant.deprecated) DeprecationLevel.WARNING else null,
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
                        comment = alias.doc,
                        type = className,
                        propertyInitializer = CodeGenerator.PropertyInitializer.GETTER,
                        value = alias.original.name.simpleName,
                        deprecation = if (alias.deprecated) DeprecationLevel.WARNING else null,
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

private fun String.capitalize(): String = replaceFirstChar { it.uppercase() }