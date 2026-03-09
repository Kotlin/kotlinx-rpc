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
import kotlinx.rpc.protoc.gen.core.GeneratedMetadata
import kotlinx.rpc.protoc.gen.core.NameConflictCollector
import kotlinx.rpc.protoc.gen.core.ScopedFormattedString
import kotlinx.rpc.protoc.gen.core.fqName
import kotlinx.rpc.protoc.gen.core.joinToScopedString
import kotlinx.rpc.protoc.gen.core.merge
import kotlinx.rpc.protoc.gen.core.model.EnumDeclaration
import kotlinx.rpc.protoc.gen.core.model.FieldDeclaration
import kotlinx.rpc.protoc.gen.core.model.FieldType
import kotlinx.rpc.protoc.gen.core.model.FileDeclaration
import kotlinx.rpc.protoc.gen.core.model.FqName
import kotlinx.rpc.protoc.gen.core.model.MessageDeclaration
import kotlinx.rpc.protoc.gen.core.model.Model
import kotlinx.rpc.protoc.gen.core.model.OneOfDeclaration
import kotlinx.rpc.protoc.gen.core.model.WireType
import kotlinx.rpc.protoc.gen.core.model.fullName
import kotlinx.rpc.protoc.gen.core.model.importPath
import kotlinx.rpc.protoc.gen.core.model.nested
import kotlinx.rpc.protoc.gen.core.model.packageName
import kotlinx.rpc.protoc.gen.core.model.scalarDefaultSuffix
import kotlinx.rpc.protoc.gen.core.scoped
import kotlinx.rpc.protoc.gen.core.scopedAnnotation
import kotlinx.rpc.protoc.gen.core.wrapIn

class ModelToProtobufKotlinCommonGenerator(
    config: Config,
    generatedMetadata: GeneratedMetadata,
    model: Model,
    conflictCollector: NameConflictCollector = NameConflictCollector(),
) : AModelToKotlinCommonGenerator(config, generatedMetadata, model, conflictCollector) {
    override val publicGeneratedNames: Set<String> = setOf(
        // Nested types generated per message
        "Builder", "Companion", "MARSHALLER", "DESCRIPTOR", "PresenceIndices", "BytesDefaults",
        // Internal properties that cannot be mangled (referenced by fixed names across generated files)
        "_size", "_unknownFields", "_presence", "presenceMask", "_extensions", "_owner",
        // Public extension functions
        "copy", "invoke", "presence",
    )

    override val FileDeclaration.hasPublicGeneratedContent: Boolean
        get() = enumDeclarations.isNotEmpty() || messageDeclarations.isNotEmpty() || hasExtensionGroupMessages()
    override val FileDeclaration.hasExtensionGeneratedContent: Boolean
        get() = hasPublicGeneratedContent || allExtensions().isNotEmpty()
    override val FileDeclaration.hasInternalGeneratedContent: Boolean
        get() = hasPublicGeneratedContent || allExtensions().isNotEmpty()

    override fun CodeGenerator.generatePublicDeclaredEntities(fileDeclaration: FileDeclaration) {
        fileDeclaration.messageDeclarations.forEach { generatePublicMessage(it) }
        fileDeclaration.enumDeclarations.forEach { generatePublicEnum(it) }
    }

    override fun CodeGenerator.generateExtensionEntities(fileDeclaration: FileDeclaration) {
        generateExtensionMessageEntities(fileDeclaration.messageDeclarations)

        // Keep file-level extensions in top-level scope.
        fileDeclaration.extensions.forEach { extension ->
            generateProtoExtensionProperty(extension)
        }

        // Message-scoped extensions are emitted in namespace objects.
        fileDeclaration.messageDeclarations.forEach { message ->
            generateMessageExtensionNamespace(message)
        }
    }

    override fun CodeGenerator.generateInternalDeclaredEntities(fileDeclaration: FileDeclaration) {
        generateInternalMessageEntities(fileDeclaration.messageDeclarations)

        val allEnums =
            fileDeclaration.enumDeclarations + fileDeclaration.messageDeclarations
                .flatMap { it.enumDeclarations + it.allNestedRecursively().flatMap(MessageDeclaration::enumDeclarations) }
        allEnums.forEach { enum ->
            generateInternalEnumConstructor(enum)
        }

        generateProtoExtensionsObject(fileDeclaration)
    }

    private fun CodeGenerator.generateInternalMessageEntities(messages: List<MessageDeclaration>) {
        messages.forEach { generateInternalMessage(it) }

        // emit all required functions in the outer scope
        val allMessages = messages + messages.flatMap(MessageDeclaration::allNestedRecursively)
        allMessages.forEach {
            // add all required imports of submessage fields
            // (e.g. asInternal() from messages in different packages)
            it.addNecessaryInternalImports()

            generateRequiredCheck(it)
            generateMessageEncoder(it)
            generateMessageDecoder(it)
            generateInternalComputeSize(it)
            generateInternalCastExtension(it)
        }
    }

    private fun CodeGenerator.generateExtensionMessageEntities(message: List<MessageDeclaration>) {
        val allMessages = message + message.flatMap(MessageDeclaration::allNestedRecursively)
        allMessages.forEach {
            generateMessageConstructor(it)
            generatePublicCopy(it)
            generatePublicPresenceGetter(it)
        }

        // the presence interfaces are not generated in the flattened list
        // as nested classes are generated as nested presence interfaces
        message.forEach { generatePresenceInterface(it) }
    }

    @Suppress("detekt.CyclomaticComplexMethod")
    private fun CodeGenerator.generatePublicMessage(declaration: MessageDeclaration) {
        if (!declaration.isUserFacing) return

        val annotations = listOf(
            FqName.Annotations.GeneratedProtoMessage.scopedAnnotation()
        )

        generatedMetadata.protoNamesList.add(declaration.name)

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
        }
    }

    @Suppress("detekt.CyclomaticComplexMethod")
    private fun CodeGenerator.generateInternalMessage(declaration: MessageDeclaration) {
        val superTypes = buildList {
            if (declaration.isUserFacing) {
                add(declaration.builderClassName.scoped())
            }
            add("%T(fieldsWithPresence = ${declaration.presenceMaskSize})"
                .scoped(FqName.RpcClasses.InternalMessage))
        }

        clazz(
            name = declaration.internalClassName.simpleName,
            declarationType = CodeGenerator.DeclarationType.Class,
            superTypes = superTypes,
        ) {
            generatePresenceIndicesObject(declaration)
            generateBytesDefaultsObject(declaration)

            property(
                name = "_size",
                modifiers = "override",
                annotations = listOf(FqName.Annotations.InternalRpcApi.scopedAnnotation()),
                type = FqName.Implicits.Int.scoped(),
                propertyInitializer = CodeGenerator.PropertyInitializer.DELEGATE,
                value = "lazy { computeSize() }".scoped(),
            )

            // unknown fields are currently not stored in a map like structure, but directly in binary form
            // within a buffer.
            // creating a Buffer object is inexpensive as there is no dynamic allocation involved.
            property(
                name = "_unknownFields",
                modifiers = "override",
                annotations = listOf(FqName.Annotations.InternalRpcApi.scopedAnnotation()),
                type = FqName.KotlinLibs.Buffer.scoped(),
                value = "%T()".scoped(FqName.KotlinLibs.Buffer),
            )

            // by setting the encoder to null, we avoid unnecessary creation of the WireEncoder
            // if there is no unknown field. by checking if it is null, we can also check if there
            // was any unknown field after decoding (for flushing).
            property(
                name = "_unknownFieldsEncoder",
                modifiers = "internal",
                isVar = true,
                annotations = listOf(FqName.Annotations.InternalRpcApi.scopedAnnotation()),
                type = "%T?".scoped(FqName.RpcClasses.WireEncoder),
                value = "null".scoped(),
            )

            val override = if (declaration.isUserFacing) "override" else ""
            declaration.actualFields.forEachIndexed { i, field ->
                val value = when {
                    field.nullable && field.presenceIdx == null -> {
                        "null".scoped()
                    }

                    field.type is FieldType.Message -> {
                        "%T(%T.${field.name}) { %T() }".scoped(
                            FqName.RpcClasses.MsgFieldDelegate,
                            declaration.presenceIndicesName,
                            (field.type as FieldType.Message).dec.value.internalClassName,
                        )
                    }

                    else -> {
                        val fieldPresence = if (field.presenceIdx != null) {
                            "(%T.${field.name})".scoped(declaration.presenceIndicesName)
                        } else {
                            ScopedFormattedString.empty
                        }

                        FqName.RpcClasses.MsgFieldDelegate
                            .scoped()
                            .merge(
                                other = fieldPresence,
                                another = field.safeDefaultValue(declaration),
                            ) { msgFieldDelegate, fieldPresence, fieldDefault ->
                                "$msgFieldDelegate$fieldPresence { $fieldDefault }"
                            }
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

            generateInternalPresenceObjectProperty(declaration)
            generateHashCode(declaration)
            generateOneOfHashCode(declaration)
            generateEquals(declaration)
            generateToString(declaration)
            generateInternalCopy(declaration)
            generateOneOfCopy(declaration)

            declaration.nestedDeclarations.forEach { nested ->
                generateInternalMessage(nested)
            }

            generateMarshallerObject(declaration)
            generateDescriptorObject(declaration)
            generateCompanionObject(declaration)
        }
    }

    /**
     * Adds necessary imports required for this message declaration.
     * E.g., the `asInternal()` and `copy()` functions of messages in other packages.
     */
    private fun MessageDeclaration.addNecessaryInternalImports() {
        val pkg = name.packageName()
        actualFields.mapNotNull { (it.type as? FieldType.Message)?.dec?.value }.forEach { subMsg ->
            val subMsgPkg = subMsg.name.packageName()
            // if the subMsg is part of some other package and not a well-known type,
            // we import all necessary functions
            if (subMsgPkg != pkg && subMsgPkg.fullName() != "com.google.protobuf.kotlin") {
                internalImports.add(subMsgPkg.importPath("asInternal"))
                internalImports.add(subMsgPkg.importPath("copy"))
                internalImports.add(subMsgPkg.importPath("checkRequiredFields"))
                internalImports.add(subMsgPkg.importPath("decodeWith"))
            }
        }
    }

    private fun CodeGenerator.generateCompanionObject(declaration: MessageDeclaration) {
        clazz(
            name = "",
            modifiers = "companion",
            annotations = listOf(FqName.Annotations.InternalRpcApi.scopedAnnotation()),
            declarationType = CodeGenerator.DeclarationType.Object
        )
    }

    private fun CodeGenerator.generateHashCode(declaration: MessageDeclaration) {
        val fields = declaration.actualFields
        function(
            name = "hashCode",
            modifiers = "override",
            returnType = FqName.Implicits.Int.scoped(),
        ) {
            code("checkRequiredFields()".scoped())

            when {
                fields.size == 1 -> {
                    code(fields[0].hashExprForHashCode().wrapIn { "var result = $it" })
                }

                fields.isNotEmpty() -> {
                    code(fields.first().hashExprForHashCode().wrapIn { "var result = $it" })
                    fields.drop(1).forEach { f ->
                        code(f.hashExprForHashCode().wrapIn { "result = 31 * result + $it" })
                    }
                }

                else -> {
                    code("var result = this::class.hashCode()".scoped())
                }
            }

            if (declaration.hasExtensionRange) {
                code("result = 31 * result + extensionsHashCode()".scoped())
            }

            code("return result".scoped())
        }
    }

    private fun FieldDeclaration.hashExprForHashCode(): ScopedFormattedString {
        return when (val t = type) {
            is FieldType.IntegralType -> {
                when (t) {
                    FieldType.IntegralType.BYTES -> {
                        if (nullable) "(${name}?.contentHashCode() ?: 0)" else "${name}.contentHashCode()"
                    }

                    FieldType.IntegralType.FLOAT -> {
                        if (nullable) "(${name}?.toBits()?.hashCode() ?: 0)" else "${name}.toBits().hashCode()"
                    }

                    FieldType.IntegralType.DOUBLE -> {
                        if (nullable) "(${name}?.toBits()?.hashCode() ?: 0)" else "${name}.toBits().hashCode()"
                    }

                    else -> {
                        if (nullable) "(${name}?.hashCode() ?: 0)" else "${name}.hashCode()"
                    }
                }
            }

            is FieldType.OneOf -> {
                if (nullable) "(${name}?.oneOfHashCode() ?: 0)" else "${name}.oneOfHashCode() + "
            }

            is FieldType.List -> {
                if (t.value == FieldType.IntegralType.BYTES) {
                    "${name}.fold(1) { acc, b -> 31 * acc + b.contentHashCode() }"
                } else {
                    if (nullable) "(${name}?.hashCode() ?: 0)" else "${name}.hashCode()"
                }
            }

            is FieldType.Message, is FieldType.Enum, is FieldType.Map -> {
                if (nullable) "(${name}?.hashCode() ?: 0)" else "${name}.hashCode()"
            }
        }.let {
            if (presenceIdx != null) {
                "if (presenceMask[${presenceIdx}]) $it else 0"
            } else {
                it
            }
        }.scoped()
    }

    private fun CodeGenerator.generateOneOfHashCode(declaration: MessageDeclaration) {
        declaration.oneOfDeclarations.forEach { oneOf ->
            val hasBytesVariant = oneOf.variants.any { it.type == FieldType.IntegralType.BYTES }
            val hasFloatVariant = oneOf.variants.any {
                it.type == FieldType.IntegralType.FLOAT || it.type == FieldType.IntegralType.DOUBLE
            }
            function(
                name = "oneOfHashCode",
                returnType = FqName.Implicits.Int.scoped(),
                contextReceiver = oneOf.name.scoped(),
            ) {
                if (hasBytesVariant || hasFloatVariant) {
                    // Generate a when expression that handles ByteArray/Float/Double variants specially
                    whenBlock(prefix = "return ".scoped(), condition = "this".scoped()) {
                        oneOf.variants.forEachIndexed { index, variant ->
                            val variantName = "%T.${variant.name}".scoped(oneOf.name)
                            val hashExpr = when (variant.type) {
                                FieldType.IntegralType.BYTES -> "value.contentHashCode()"
                                FieldType.IntegralType.FLOAT -> "value.toBits().hashCode()"
                                FieldType.IntegralType.DOUBLE -> "value.toBits().hashCode()"
                                else -> "hashCode()"
                            }
                            code(variantName.wrapIn { "is $it -> $hashExpr + $index" })
                        }
                    }
                } else {
                    whenBlock(prefix = "val offset = ".scoped(), condition = "this".scoped()) {
                        oneOf.variants.forEachIndexed { index, variant ->
                            val variantName = "%T.${variant.name}".scoped(oneOf.name)
                            code(variantName.wrapIn { "is $it -> $index" })
                        }
                    }
                    code("return hashCode() + offset".scoped())
                }
            }

            if (hasBytesVariant || hasFloatVariant) {
                // Generate a custom oneOfEquals for byte/float/double-aware comparison
                function(
                    name = "oneOfEquals",
                    args = "a: %T?, b: %T?".scoped(oneOf.name, oneOf.name),
                    returnType = FqName.Implicits.Boolean.scoped(),
                ) {
                    code("if (a === b) return true".scoped())
                    code("if (a == null || b == null) return false".scoped())
                    code("if (a::class != b::class) return false".scoped())
                    whenBlock(prefix = "return ".scoped(), condition = "a".scoped()) {
                        oneOf.variants.forEach { variant ->
                            val variantFqName = oneOf.name.nested(variant.name)
                            when (variant.type) {
                                FieldType.IntegralType.BYTES ->
                                    code("is %T -> a.value.contentEquals((b as %T).value)".scoped(variantFqName, variantFqName))
                                FieldType.IntegralType.FLOAT ->
                                    code("is %T -> a.value.toBits() == (b as %T).value.toBits()".scoped(variantFqName, variantFqName))
                                FieldType.IntegralType.DOUBLE ->
                                    code("is %T -> a.value.toBits() == (b as %T).value.toBits()".scoped(variantFqName, variantFqName))
                                else ->
                                    code("is %T -> a == b".scoped(variantFqName))
                            }
                        }
                    }
                }
            }
        }
    }

    private fun CodeGenerator.generateEquals(declaration: MessageDeclaration) {
        val fields = declaration.actualFields
        function(
            name = "equals",
            modifiers = "override",
            args = "other: %T?".scoped(FqName.Implicits.Any),
            returnType = FqName.Implicits.Boolean.scoped(),
        ) {
            code("checkRequiredFields()".scoped())
            code("if (this === other) return true".scoped())
            code("if (other == null || this::class != other::class) return false".scoped())
            code("other as %T".scoped(declaration.internalClassName))
            code("other.checkRequiredFields()".scoped())
            if (declaration.presenceMaskSize != 0) {
                code("if (presenceMask != other.presenceMask) return false".scoped())
            }
            if (fields.isNotEmpty()) {
                fields.forEach { field ->
                    if (field.presenceIdx != null) {
                        fieldEqualsCheck(
                            presenceCheck = "presenceMask[${field.presenceIdx}] && ".scoped(),
                            field = field,
                        )
                    } else {
                        fieldEqualsCheck(presenceCheck = "".scoped(), field = field)
                    }
                }
            }

            if (declaration.hasExtensionRange) {
                code("if (!extensionsEqual(other)) return false".scoped())
            }

            code("return true".scoped())
        }
    }

    private fun CodeGenerator.fieldEqualsCheck(
        presenceCheck: ScopedFormattedString,
        field: FieldDeclaration,
    ) {
        when (val t = field.type) {
            is FieldType.IntegralType -> {
                when (t) {
                    FieldType.IntegralType.BYTES -> {
                        if (field.nullable) {
                            code(
                                presenceCheck.wrapIn { presenceCheck ->
                                    "if ($presenceCheck((this.${field.name} != null && (other.${field.name} == null || !this.${field.name}!!.contentEquals(other.${field.name}!!))) || this.${field.name} == null)) return false"
                                }
                            )
                        } else {
                            code(
                                presenceCheck.wrapIn { presenceCheck ->
                                    "if ($presenceCheck!this.${field.name}.contentEquals(other.${field.name})) return false"
                                }
                            )
                        }
                    }

                    FieldType.IntegralType.FLOAT -> {
                        code(
                            presenceCheck.wrapIn { presenceCheck ->
                                "if (${presenceCheck}this.${field.name}${if (field.nullable) "?" else ""}.toBits() != other.${field.name}${if (field.nullable) "?" else ""}.toBits()) return false"
                            }
                        )
                    }

                    FieldType.IntegralType.DOUBLE -> {
                        code(
                            presenceCheck.wrapIn { presenceCheck ->
                                "if (${presenceCheck}this.${field.name}${if (field.nullable) "?" else ""}.toBits() != other.${field.name}${if (field.nullable) "?" else ""}.toBits()) return false"
                            }
                        )
                    }

                    else -> {
                        code(
                            presenceCheck.wrapIn { presenceCheck ->
                                "if (${presenceCheck}this.${field.name} != other.${field.name}) return false"
                            }
                        )
                    }
                }
            }

            is FieldType.List -> {
                if (t.value == FieldType.IntegralType.BYTES) {
                    // List<ByteArray> needs element-wise contentEquals
                    code(
                        presenceCheck.wrapIn { presenceCheck ->
                            "if (${presenceCheck}(this.${field.name}.size != other.${field.name}.size || !this.${field.name}.zip(other.${field.name}).all { (a, b) -> a.contentEquals(b) })) return false"
                        }
                    )
                } else {
                    code(
                        presenceCheck.wrapIn { presenceCheck ->
                            "if (${presenceCheck}this.${field.name} != other.${field.name}) return false"
                        }
                    )
                }
            }

            is FieldType.OneOf -> {
                val hasBytesOrFloatVariant = t.dec.variants.any {
                    it.type == FieldType.IntegralType.BYTES ||
                        it.type == FieldType.IntegralType.FLOAT ||
                        it.type == FieldType.IntegralType.DOUBLE
                }
                if (hasBytesOrFloatVariant) {
                    code(
                        presenceCheck.wrapIn { presenceCheck ->
                            "if (${presenceCheck}!oneOfEquals(this.${field.name}, other.${field.name})) return false"
                        }
                    )
                } else {
                    code(
                        presenceCheck.wrapIn { presenceCheck ->
                            "if (${presenceCheck}this.${field.name} != other.${field.name}) return false"
                        }
                    )
                }
            }

            is FieldType.Message,
            is FieldType.Enum,
            is FieldType.Map,
                -> {
                code(
                    presenceCheck.wrapIn { presenceCheck ->
                        "if (${presenceCheck}this.${field.name} != other.${field.name}) return false"
                    }
                )
            }
        }
    }

    private fun CodeGenerator.generateToString(declaration: MessageDeclaration) {
        function(
            name = "toString",
            modifiers = "override",
            returnType = FqName.Implicits.String.scoped(),
        ) {
            code("return asString()".scoped())
        }

        function(
            name = "asString",
            args = "indent: %T = 0".scoped(FqName.Implicits.Int),
            returnType = FqName.Implicits.String.scoped(),
        ) {
            code("checkRequiredFields()".scoped())
            code("val indentString = \" \".repeat(indent)".scoped())
            code("val nextIndentString = \" \".repeat(indent + ${config.indentSize})".scoped())

            code("val builder = StringBuilder()".scoped())
            code("builder.appendLine(\"%T(\")".scoped(declaration.name))
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
                    code("builder.appendLine(\"\${nextIndentString}${it.name}=\${this.${it.name}$suffix},\")".scoped())
                }

                if (it.presenceIdx != null) {
                    ifBranch(condition = "presenceMask[${it.presenceIdx}]".scoped(), ifBlock = {
                        valueBuilder()
                    }) {
                        code("builder.appendLine(\"\${nextIndentString}${it.name}=<unset>,\")".scoped())
                    }
                } else {
                    valueBuilder()
                }
            }
            if (declaration.hasExtensionRange) {
                code("builder.appendExtensions(nextIndentString)".scoped())
            }
            code("builder.append(\"\${indentString})\")".scoped())
            code("return builder.toString()".scoped())
        }
    }

    private fun CodeGenerator.generatePublicCopy(declaration: MessageDeclaration) {
        if (!declaration.isUserFacing) {
            // e.g. internal map entries don't need a copy() method
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
            contextReceiver = declaration.name.scoped(),
            args = "body: %T.() -> %T = {}".scoped(declaration.builderClassName, FqName.Implicits.Unit),
            returnType = declaration.name.scoped(),
            comment = Comment.leading(
                """
                    |Copies the original message, including unknown fields.
                    |```
                    |val copy = original.copy$invocation
                    |```
                """.trimMargin()
            )
        ) {
            code("return this.asInternal().copyInternal(body)".scoped())
        }
    }

    private fun CodeGenerator.generateInternalCopy(declaration: MessageDeclaration) {
        // the copyInternal method that override the abstract definition in
        // InternalMessage
        function(
            name = "copyInternal",
            modifiers = "override",
            returnType = declaration.internalClassName.scoped(),
        ) {
            if (declaration.isUserFacing) {
                code("return copyInternal { }".scoped())
            } else {
                // this is a map-entry, so copy on this internal message (solely used for encoding and decoding)
                // is never going to be copied, as it is never part of an actual message.
                // this is because during decoding, a Proto map entry (this message) is transformed into
                // a Kotlin map entry.
                code("return this".scoped())
            }
        }

        if (!declaration.isUserFacing) {
            // e.g. internal map entries don't need a copy() method
            return
        }

        function(
            name = "copyInternal",
            annotations = listOf(FqName.Annotations.InternalRpcApi.scopedAnnotation()),
            args = "body: %T.() -> %T".scoped(declaration.internalClassName, FqName.Implicits.Unit),
            returnType = declaration.internalClassName.scoped(),
        ) {
            code("val copy = %T()".scoped(declaration.internalClassName))
            for (field in declaration.actualFields) {
                // write each field to the new copy object
                if (field.presenceIdx != null) {
                    // if the field has presence, we need to check if it was set in the original object.
                    // if it was set, we copy it to the new object, otherwise we leave it unset.
                    ifBranch(condition = "presenceMask[${field.presenceIdx}]".scoped(), ifBlock = {
                        code(
                            field.type.copyCall(field.name.scoped(), field.nullable).wrapIn { copyCall ->
                                "copy.${field.name} = this.$copyCall"
                            }
                        )
                    })
                } else {
                    // by default, we copy the field value
                    code(
                        field.type.copyCall(field.name.scoped(), field.nullable).wrapIn { copyCall ->
                            "copy.${field.name} = this.$copyCall"
                        }
                    )
                }
            }

            if (declaration.hasExtensionRange) {
                code("copy.copyExtensionsFrom(this)".scoped())
            }

            code("copy.apply(body)".scoped())
            code("this._unknownFields.copyTo(copy._unknownFields)".scoped())
            code("return copy".scoped())
        }
    }

    private fun FieldType.copyCall(varName: ScopedFormattedString, nullable: Boolean): ScopedFormattedString {
        return when (this) {
            FieldType.IntegralType.BYTES -> {
                val optionalPrefix = if (nullable) "?" else ""
                varName.wrapIn { varName -> "$varName$optionalPrefix.copyOf()" }
            }

            is FieldType.IntegralType -> varName
            is FieldType.Enum -> varName
            is FieldType.List -> varName.merge(value.copyCall("it".scoped(), false)) { varName, copyCall ->
                "$varName.map { $copyCall }"
            }

            is FieldType.Map -> varName.merge(entry.value.copyCall("it.value".scoped(), false)) { varName, copyCall ->
                "$varName.mapValues { $copyCall }"
            }

            is FieldType.Message -> varName.wrapIn { varName -> "$varName.copy()" }
            is FieldType.OneOf -> varName.wrapIn { varName -> "$varName?.oneOfCopy()" }
        }
    }

    private fun CodeGenerator.generateOneOfCopy(declaration: MessageDeclaration) {
        declaration.oneOfDeclarations.forEach { oneOf ->
            function(
                name = "oneOfCopy",
                annotations = listOf(FqName.Annotations.InternalRpcApi.scopedAnnotation()),
                returnType = oneOf.name.scoped(),
                contextReceiver = oneOf.name.scoped(),
            ) {
                // check if the type is copy by value (no need for deep copy)
                val copyByValue = { type: FieldType ->
                    (type is FieldType.IntegralType && type != FieldType.IntegralType.BYTES) || type is FieldType.Enum
                }

                // if all variants are integral or enum types, we can just return this directly.
                val fastPath = oneOf.variants.all { copyByValue(it.type) }
                if (fastPath) {
                    code("return this".scoped())
                } else {
                    // dispatch on all possible variants and copy its value
                    whenBlock(
                        prefix = "return".scoped(),
                        condition = "this".scoped(),
                    ) {
                        oneOf.variants.forEach { variant ->
                            val variantName = oneOf.name.nested(variant.name).scoped()
                            whenCase(variantName.wrapIn { "is $it" }) {
                                if (copyByValue(variant.type)) {
                                    // no need to reconstruct a new object, we can just return this
                                    code("this".scoped())
                                } else {
                                    code(
                                        variantName.merge(
                                            other = variant.type.copyCall(
                                                varName = "this.value".scoped(),
                                                nullable = false,
                                            ),
                                        ) { variantName, copyCall ->
                                            "$variantName($copyCall)"
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun CodeGenerator.generatePresenceInterface(declaration: MessageDeclaration) {
        if (!declaration.isUserFacing) return
        // we must generate the interface if any sub message contains presence fields.
        // this is because nested messages are inner-interfaces of the outer one.
        if (!(declaration.hasPresenceFieldsRecursive())) return

        val comment = if (declaration.hasPresenceFields) {
            // TODO KRPC-252 protoc-gen: Support type resolution in comments
            Comment.leading(
                """
                    Interface providing field-presence information for [${declaration.name.fullName()}] messages.
                    Retrieve it via the [${declaration.name.fullName()}.presence] extension property.
                """.trimIndent()
            )
        } else {
            null
        }

        clazz(
            name = declaration.presenceInterfaceName.simpleName,
            declarationType = CodeGenerator.DeclarationType.Interface,
            comment = comment,
        ) {
            declaration.actualFields.forEach { field ->
                if (field.presenceIdx != null) {
                    property(
                        name = "has${field.rawName.capitalize()}",
                        type = FqName.Implicits.Boolean.scoped(),
                    )
                }
            }

            declaration.nestedDeclarations.forEach {
                generatePresenceInterface(it)
            }
        }
    }

    private fun CodeGenerator.generatePublicPresenceGetter(declaration: MessageDeclaration) {
        if (!declaration.isUserFacing) return
        if (!declaration.hasPresenceFields && !declaration.hasExtensionRange) return

        property(
            name = "presence",
            type = declaration.presenceInterfaceName.scoped(),
            propertyInitializer = CodeGenerator.PropertyInitializer.GETTER,
            contextReceiver = declaration.name.scoped(),
            value = "this.asInternal()._presence".scoped(),
            // TODO KRPC-252 protoc-gen: Support type resolution in comments
            comment = Comment.leading(
                """
                    Returns the field-presence view for this [${declaration.name.fullName()}] instance.
                """.trimIndent()
            )
        )
    }

    private fun CodeGenerator.generateProtoExtensionProperty(declaration: FieldDeclaration) {
        val name = declaration.name
        val extendee = declaration.containingType.value
        val descriptorRef = requireNotNull(declaration.extensionDescriptorName) {
            "Missing extension descriptor name for ${declaration.name}"
        }

        var value = "asInternal().getExtensionValue(%T)".scoped(descriptorRef)
        if (!declaration.nullable) {
            // if the field is non-nullable, e.g. a message, we fallback to the defined default value
            val default = "%T.defaultValue.value".scoped(descriptorRef)
            value = value
                .merge(default) { value, default ->
                    "$value ?: $default"
                }
        }

        // val MyMessage.myExtensionField: FieldType? get() =
        //  asInternal().getExtensionValue(MyProtoFileKtExtensions.myExtensionField)
        property(
            name = name,
            contextReceiver = extendee.name.scoped(),
            type = declaration.typeFqName(),
            propertyInitializer = CodeGenerator.PropertyInitializer.GETTER,
            value = value,
        )

        // val MyMessage.Companion.myExtensionField: ProtoExtensionDescriptor<MyMessage, FieldType> get() =
        //  MyProtoFileKtExtensions.myExtensionField
        property(
            name = name,
            contextReceiver = "%T.Companion".scoped(extendee.name),
            type = "%T<%T".scoped(
                FqName.RpcClasses.ProtoExtensionDescriptor,
                extendee.name,
            ).merge(declaration.typeFqNameNonNullable()) { first, second ->
                "$first, $second>"
            },
            propertyInitializer = CodeGenerator.PropertyInitializer.GETTER,
            value = descriptorRef.scoped()
        )

        // val MyMessage.hasMyExtensionField: Boolean get() =
        //  (this as InternalPresenceObject).hasExtension(MyProtoFileKtExtensions.int32)
        property(
            name = "has" + name.capitalize(),
            contextReceiver = extendee.presenceInterfaceName.scoped(),
            type = FqName.Implicits.Boolean.scoped(),
            propertyInitializer = CodeGenerator.PropertyInitializer.GETTER,
            value = "(this as %T).hasExtension(%T)".scoped(
                FqName.RpcClasses.InternalPresenceObject,
                descriptorRef
            )
        )

        property(
            name = name,
            contextReceiver = extendee.builderClassName.scoped(),
            type = declaration.typeFqName(),
            propertyInitializer = CodeGenerator.PropertyInitializer.GETTER,
            isVar = true,
            value = value,
            setter = "asInternal().setExtensionValue(%T, value)".scoped(descriptorRef)
        )
    }

    private fun CodeGenerator.generateMessageExtensionNamespace(
        declaration: MessageDeclaration,
    ) {
        val hasOwnExtensions = declaration.extensions.isNotEmpty()
        val nestedWithExtensions = declaration.nestedDeclarations.filter { it.hasExtensionsRecursively() }
        if (!hasOwnExtensions && nestedWithExtensions.isEmpty()) return

        clazz(
            name = declaration.name.simpleName + "Extensions",
            declarationType = CodeGenerator.DeclarationType.Object,
        ) {
            declaration.extensions.forEach { extension ->
                generateProtoExtensionProperty(extension)
            }

            nestedWithExtensions.forEach { nested ->
                generateMessageExtensionNamespace(nested)
            }
        }
    }

    private fun CodeGenerator.generateInternalPresenceObjectProperty(declaration: MessageDeclaration) {
        if (!declaration.isUserFacing) return
        if (!declaration.hasPresenceFields && !declaration.hasExtensionRange) return

        // this property is required to implement the InternalPresenceObject interface
        // for every possible case: if we have a nested message type with the same name
        // like MyClass1Internal.StringInternal.StringInternal, we cannot use "this@StringInternal"
        // as it is ambiguous.
        // to workaround this, we add the "_owner" property to the internal message, that can be
        // used by the presence object to reference its message.
        this@generateInternalPresenceObjectProperty.property(
            name = "_owner",
            modifiers = "private",
            type = declaration.internalClassName.scoped(),
            value = "this".scoped(),
        )

        this@generateInternalPresenceObjectProperty.property(
            name = "_presence",
            annotations = listOf(FqName.Annotations.InternalRpcApi.scopedAnnotation()),
            type = declaration.presenceInterfaceName.scoped(),
            value = "object : %T, %T".scoped(declaration.presenceInterfaceName, FqName.RpcClasses.InternalPresenceObject),
        ) {
            property(
                name = "_message",
                modifiers = "override",
                type = declaration.internalClassName.scoped(),
                propertyInitializer = CodeGenerator.PropertyInitializer.GETTER,
                value = "_owner".scoped(),
            )

            declaration.actualFields.forEach { field ->
                if (field.presenceIdx != null) {
                    property(
                        name = "has${field.rawName.capitalize()}",
                        modifiers = "override",
                        type = FqName.Implicits.Boolean.scoped(),
                        propertyInitializer = CodeGenerator.PropertyInitializer.GETTER,
                        value = "presenceMask[${field.presenceIdx}]".scoped(),
                    )
                }
            }
        }
    }

    private fun CodeGenerator.generatePresenceIndicesObject(declaration: MessageDeclaration) {
        if (declaration.presenceMaskSize == 0) {
            return
        }

        clazz(
            name = declaration.presenceIndicesName.simpleName,
            modifiers = "private",
            declarationType = CodeGenerator.DeclarationType.Object,
        ) {
            val fieldDeclarations = declaration.actualFields.filter { it.presenceIdx != null }
            fieldDeclarations.forEachIndexed { i, field ->
                property(
                    name = field.name,
                    modifiers = "const",
                    value = field.presenceIdx.toString().scoped(),
                    type = FqName.Implicits.Int.scoped(),
                    needsNewLineAfterDeclaration = i == fieldDeclarations.lastIndex,
                )
            }
        }
    }

    private fun CodeGenerator.generateBytesDefaultsObject(declaration: MessageDeclaration) {
        val fieldDeclarations = declaration.nonDefaultByteFields()

        if (fieldDeclarations.isEmpty()) {
            return
        }

        clazz(
            name = declaration.bytesDefaultsName.simpleName,
            modifiers = "private",
            declarationType = CodeGenerator.DeclarationType.Object
        ) {
            fieldDeclarations.forEachIndexed { i, field ->
                val value = if (field.dec.hasDefaultValue()) {
                    val bytes = field.dec.defaultValue as ByteString
                    if (bytes.size() > 0) {
                        val hexBytes = (0 until bytes.size()).joinToString(", ") { idx ->
                            val b = bytes.byteAt(idx).toInt() and 0xFF
                            if (b > 0x7F) "0x${"%02X".format(b)}.toByte()" else "0x%02X".format(b)
                        }
                        "byteArrayOf($hexBytes)".scoped()
                    } else {
                        FieldType.IntegralType.BYTES.defaultValue
                    }
                } else {
                    FieldType.IntegralType.BYTES.defaultValue
                }

                property(
                    name = field.name,
                    value = value,
                    type = FqName.Implicits.ByteArray.scoped(),
                    needsNewLineAfterDeclaration = i == fieldDeclarations.lastIndex,
                )
            }
        }
    }

    private fun CodeGenerator.generateMarshallerObject(declaration: MessageDeclaration) {
        if (!declaration.isUserFacing) return

        clazz(
            name = declaration.marshallerObjectName.simpleName,
            annotations = listOf(FqName.Annotations.InternalRpcApi.scopedAnnotation()),
            declarationType = CodeGenerator.DeclarationType.Object,
            superTypes = listOf("%T<%T>".scoped(FqName.RpcClasses.MessageMarshaller, declaration.name)),
        ) {
            function(
                name = "encode",
                modifiers = "override",
                args = "value: %T, config: %T?".scoped(declaration.name, FqName.RpcClasses.MarshallerConfig),
                returnType = FqName.KotlinLibs.Source.scoped(),
            ) {
                code("val buffer = %T()".scoped(FqName.KotlinLibs.Buffer))
                code("val encoder = %T(buffer)".scoped(FqName.RpcClasses.WireEncoder))
                code("val internalMsg = value.asInternal()".scoped())
                scope("checkForPlatformEncodeException".scoped(), nlAfterClosed = false) {
                    code("internalMsg.encodeWith(encoder, config as? %T)".scoped(FqName.RpcClasses.ProtobufConfig))
                }
                code("encoder.flush()".scoped())
                code("internalMsg._unknownFields.copyTo(buffer)".scoped())
                code("return buffer".scoped())
            }

            function(
                name = "decode",
                modifiers = "override",
                args = "source: %T, config: %T?".scoped(FqName.KotlinLibs.Source, FqName.RpcClasses.MarshallerConfig),
                returnType = declaration.name.scoped(),
            ) {
                scope("%T(source).use".scoped(FqName.RpcClasses.WireDecoder)) {
                    code(
                        "(config as? %T)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }"
                            .scoped(FqName.RpcClasses.ProtobufConfig)
                    )
                    code("val msg = %T()".scoped(declaration.internalClassName))
                    scope("checkForPlatformDecodeException".scoped(), nlAfterClosed = false) {
                        // if the message declaration is a group, we must pass null as the
                        // startGroup tag to indicate that this message is decoded as standalone (like a normal message)
                        val groupExtraArg = if (declaration.isGroup) ", null" else ""
                        code(
                            "%T.decodeWith(msg, it, config as? %T$groupExtraArg)".scoped(
                                declaration.internalClassName,
                                FqName.RpcClasses.ProtobufConfig,
                            )
                        )
                    }
                    code("msg.checkRequiredFields()".scoped())
                    code("msg._unknownFieldsEncoder?.flush()".scoped())
                    code("return msg".scoped())
                }
            }

            internalImports.add("kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException")
            internalImports.add("kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException")
        }
    }

    private fun CodeGenerator.generateDescriptorObject(declaration: MessageDeclaration) {
        if (!declaration.isUserFacing) return

        clazz(
            name = "DESCRIPTOR",
            annotations = listOf(FqName.Annotations.InternalRpcApi.scopedAnnotation()),
            declarationType = CodeGenerator.DeclarationType.Object,
            superTypes = listOf("%T<%T>".scoped(FqName.RpcClasses.ProtoDescriptor, declaration.name)),
        ) {
            property(
                name = "fullName",
                modifiers = "override",
                type = FqName.Implicits.String.scoped(),
                value = "\"${declaration.dec.fullName}\"".scoped()
            )
        }
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
            args = "body: %T.() -> %T".scoped(declaration.builderClassName, FqName.Implicits.Unit),
            contextReceiver = declaration.companionName.scoped(),
            returnType = declaration.name.scoped(),
            comment = Comment.leading(
                """
                |Constructs a new message.
                |```
                |val message = ${declaration.name.simpleName} $invocation
                |```
            """.trimMargin()
            )
        ) {
            code("val msg = %T().apply(body)".scoped(declaration.internalClassName))
            // check if the user set all required fields
            code("msg.checkRequiredFields()".scoped())
            code("return msg".scoped())
        }
    }

    private fun CodeGenerator.generateMessageDecoder(declaration: MessageDeclaration) {
        var args = "msg: %T, decoder: %T, config: %T?"
            .scoped(declaration.internalClassName, FqName.RpcClasses.WireDecoder, FqName.RpcClasses.ProtobufConfig)

        if (declaration.isGroup) {
            // if the message is a group message, the decoder accepts an optional startGroup tag, which indicates
            // that the decoding of the message must end with an END_GROUP tag of the same fieldNr.
            // if the startGroup tag is null, we treat it like a normal message.
            // the argument is not default null, to avoid that we forget to set it when changing the generator.
            args = args.merge(FqName.RpcClasses.KTag.scoped()) { start, kTag ->
                "$start, startGroup: $kTag?"
            }
        }

        function(
            name = "decodeWith",
            args = args,
            annotations = listOf(FqName.Annotations.InternalRpcApi.scopedAnnotation()),
            contextReceiver = declaration.internalCompanionName.scoped(),
            returnType = FqName.Implicits.Unit.scoped(),
        ) {
            if (!declaration.isGroup && declaration.isUserFacing) {
                code("val knownExtensions = config?.extensionRegistry?.getAllExtensionsForMessage(%T::class) ?: emptyMap()"
                    .scoped(declaration.name))
            }
            whileBlock("true".scoped()) {
                if (declaration.isGroup) {
                    scope("val tag = decoder.readTag() ?: run".scoped()) {
                        // if the startGroup tag is set, we decode the message as an inline group message,
                        // so in case of the tag being null, we know that the payload is malformed
                        scope("startGroup?.let".scoped()) {
                            code(
                                "throw %T(\"Missing END_GROUP tag for field: \${startGroup.fieldNr}.\")"
                                    .scoped(FqName.RpcClasses.ProtobufDecodingException)
                            )
                        }
                        // if the startGroup is null, we decode the message like a normal non-group message,
                        // so we stop when the tag is null
                        code("return".scoped())
                    }
                    ifBranch(condition = "tag.wireType == %T".scoped(FqName.RpcClasses.WireType_END_GROUP), ifBlock = {
                        ifBranch(condition = "tag.fieldNr != startGroup?.fieldNr".scoped(), ifBlock = {
                            code(
                                "throw %T(\"Wrong END_GROUP tag. Expected \${startGroup?.fieldNr}, got \${tag.fieldNr}.\")"
                                    .scoped(FqName.RpcClasses.ProtobufDecodingException)
                            )
                        })
                        code("return".scoped())
                    })
                } else {
                    code("val tag = decoder.readTag() ?: break // EOF, we read the whole message".scoped())
                }

                whenBlock {
                    declaration.actualFields.forEach { field -> readMatchCase(field) }
                    whenCase("else".scoped()) {
                        // check if it is an extension
                        if (declaration.extensionRanges.isNotEmpty()) {
                            code("val extension = knownExtensions[tag.fieldNr] as? %T".scoped(FqName.RpcClasses.InternalExtensionDescriptor))
                            ifBranch(condition = "extension != null && tag.wireType in extension.acceptedWireTypes".scoped(), ifBlock = {
                                code("val currentExtension = msg._extensions[tag.fieldNr]?.takeIf { it.descriptor == extension }?.value".scoped())
                                code(
                                    "val decodedExtension = if (extension.isPacked && tag.wireType == %T) extension.decodePacked!!(currentExtension, decoder, config) else extension.decode(currentExtension, decoder, config)"
                                        .scoped(FqName.RpcClasses.WireType_LENGTH_DELIMITED)
                                )
                                code("msg._extensions[tag.fieldNr] = %T(decodedExtension, extension)".scoped(FqName.RpcClasses.ExtensionValue))
                                code("continue // with next tag".scoped())
                            })
                        }
                        if (!declaration.isGroup) {
                            // fail if we come across an END_GROUP in a normal message
                            ifBranch(
                                condition = "tag.wireType == %T".scoped(FqName.RpcClasses.WireType_END_GROUP),
                                ifBlock = {
                                    code(
                                        "throw %T(\"Unexpected END_GROUP tag.\")"
                                            .scoped(FqName.RpcClasses.ProtobufDecodingException)
                                    )
                                },
                            )
                        }

                        // handle unknown fields
                        ifBranch(condition = "config?.discardUnknownFields ?: false".scoped(), ifBlock = {
                            // discard unknown fields if set in the config
                            code("decoder.skipUnknownField(tag)".scoped())
                        }, elseBlock = {
                            // we the config is not set, or discardUnknownFields is set to false,
                            // we will read and store all unknown fields
                            ifBranch(condition = "msg._unknownFieldsEncoder == null".scoped(), ifBlock = {
                                code(
                                    "msg._unknownFieldsEncoder = %T(msg._unknownFields)"
                                        .scoped(FqName.RpcClasses.WireEncoder)
                                )
                            })
                            code("decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)".scoped())
                        })
                    }
                }
            }

            // TODO: Make lists and maps immutable (KRPC-190)
        }
    }

    private fun CodeGenerator.readMatchCase(
        field: FieldDeclaration,
        lvalue: ScopedFormattedString = "msg.${field.name}".scoped(),
        wrapperCtor: (ScopedFormattedString) -> ScopedFormattedString = { it },
        beforeValueDecoding: CodeGenerator.() -> Unit = {},
    ) {
        when (val fieldType = field.type) {
            is FieldType.IntegralType -> whenCase(
                "tag.fieldNr == ${field.number} && tag.wireType == %T"
                    .scoped(FqName.RpcClasses.WireType.nested(fieldType.wireType.name))
            ) {
                beforeValueDecoding()
                generateDecodeFieldValue(fieldType, lvalue, wrapperCtor = wrapperCtor)
            }

            is FieldType.List -> {
                // Protocol buffer parsers must be able
                // to parse repeated fields that were compiled as packed as if they were not packed,
                // and vice versa.
                if (fieldType.value.isPackable) {
                    whenCase(
                        "tag.fieldNr == ${field.number} && tag.wireType == %T"
                            .scoped(FqName.RpcClasses.WireType_LENGTH_DELIMITED)
                    ) {
                        beforeValueDecoding()
                        generateDecodeFieldValue(fieldType, lvalue, isPacked = true, wrapperCtor = wrapperCtor)
                    }
                }

                whenCase(
                    "tag.fieldNr == ${field.number} && tag.wireType == %T"
                        .scoped(FqName.RpcClasses.WireType.nested(fieldType.value.wireType.name))
                ) {
                    beforeValueDecoding()
                    generateDecodeFieldValue(fieldType, lvalue, isPacked = false, wrapperCtor = wrapperCtor)
                }
            }

            is FieldType.Enum -> whenCase(
                "tag.fieldNr == ${field.number} && tag.wireType == %T"
                    .scoped(FqName.RpcClasses.WireType_VARINT)
            ) {
                beforeValueDecoding()
                generateDecodeFieldValue(fieldType, lvalue, wrapperCtor = wrapperCtor)
            }

            is FieldType.OneOf -> {
                fieldType.dec.variants.forEach { variant ->
                    val variantName = fieldType.dec.name.nested(variant.name)

                    if (variant.type is FieldType.Message) {
                        // in case of a message, we must construct an empty message before reading the message
                        val message = variant.type as FieldType.Message
                        readMatchCase(
                            field = variant,
                            lvalue = "field.value".scoped(),
                            beforeValueDecoding = {
                                beforeValueDecoding()

                                scope(
                                    lvalue.merge(
                                        other = variantName.scoped(),
                                        another = variantName.scoped(),
                                        anotherOne = message.dec.value.internalClassName.scoped(),
                                    ) { lvalue, variantName1, variantName2, internalClassName ->
                                        "val field = ($lvalue as? $variantName1) ?: $variantName2($internalClassName()).also"
                                    }
                                ) {
                                    // write the constructed oneof variant to the field
                                    code(lvalue.wrapIn { lvalue -> "$lvalue = it" })
                                }
                            },
                        )
                    } else {
                        readMatchCase(
                            field = variant,
                            lvalue = lvalue,
                            wrapperCtor = { raw ->
                                variantName.scoped().merge(raw) { variantName, raw ->
                                    "$variantName($raw)"
                                }
                            },
                            beforeValueDecoding = beforeValueDecoding,
                        )
                    }
                }
            }

            is FieldType.Message -> {
                whenCase(
                    "tag.fieldNr == ${field.number} && tag.wireType == %T"
                        .scoped(FqName.RpcClasses.WireType.nested(fieldType.wireType.name))
                ) {
                    if (field.presenceIdx != null) {
                        // check if the current sub message object was already set, if not, set a new one
                        // to set the field's presence tracker to true
                        ifBranch(condition = "!msg.presenceMask[${field.presenceIdx}]".scoped(), ifBlock = {
                            code(
                                lvalue.merge(fieldType.dec.value.internalClassName.scoped()) { lvalue, internalClassName ->
                                    "$lvalue = $internalClassName()"
                                }
                            )
                        })
                    }
                    beforeValueDecoding()
                    generateDecodeFieldValue(fieldType, lvalue, wrapperCtor = wrapperCtor)
                }
            }

            is FieldType.Map -> whenCase(
                "tag.fieldNr == ${field.number} && tag.wireType == %T"
                    .scoped(FqName.RpcClasses.WireType_LENGTH_DELIMITED)
            ) {
                beforeValueDecoding()
                generateDecodeFieldValue(fieldType, lvalue, wrapperCtor = wrapperCtor)
            }
        }
    }

    private fun CodeGenerator.generateDecodeFieldValue(
        fieldType: FieldType,
        lvalue: ScopedFormattedString,
        isPacked: Boolean = false,
        wrapperCtor: (ScopedFormattedString) -> ScopedFormattedString = { it },
    ) {
        when (fieldType) {
            is FieldType.IntegralType -> {
                val raw = "decoder.read${fieldType.decodeEncodeFuncName()}()".scoped()
                code(lvalue.merge(wrapperCtor(raw)) { lvalue, ctor -> "$lvalue = $ctor" })
            }

            is FieldType.List -> if (isPacked) {
                val fieldType = fieldType.value
                val conversion = if (fieldType is FieldType.Enum) {
                    ".map { %T.fromNumber(it) }".scoped(fieldType.dec.value.name)
                } else {
                    "".scoped()
                }

                // Note that although there’s usually no reason
                // to encode more than one key-value pair for a packed repeated field,
                // parsers must be prepared to accept multiple key-value pairs.
                // In this case, the payloads should be concatenated.
                code(
                    lvalue.merge(conversion) { lvalue, conversion ->
                        "$lvalue += decoder.readPacked${fieldType.decodeEncodeFuncName()}()$conversion"
                    }
                )
            } else {
                when (val elemType = fieldType.value) {
                    is FieldType.Message -> {
                        code("val elem = %T()".scoped(elemType.dec.value.internalClassName))
                        generateDecodeFieldValue(fieldType.value, "elem".scoped(), wrapperCtor = wrapperCtor)
                    }

                    else -> generateDecodeFieldValue(fieldType.value, "val elem".scoped(), wrapperCtor = wrapperCtor)
                }
                code(
                    lvalue.merge(FqName.Implicits.MutableList.scoped()) { lvalue, mutableList ->
                        "($lvalue as $mutableList).add(elem)"
                    }
                )
            }

            is FieldType.Enum -> {
                val raw = "%T.fromNumber(decoder.read${fieldType.decodeEncodeFuncName()}())"
                    .scoped(fieldType.dec.value.name)

                code(
                    lvalue.merge(wrapperCtor(raw)) { lvalue, ctor -> "$lvalue = $ctor" }
                )
            }

            is FieldType.OneOf -> {
                fieldType.dec.variants.forEach { variant ->
                    val variantName = fieldType.dec.name.nested(variant.name).scoped()
                    readMatchCase(
                        field = variant,
                        lvalue = lvalue,
                        wrapperCtor = { raw ->
                            variantName.merge(raw) { variantName, raw -> "$variantName($raw)" }
                        }
                    )
                }
            }

            is FieldType.Message -> {
                val msg = fieldType.dec.value
                if (msg.isGroup) {
                    code(
                        lvalue.merge(msg.internalClassName.scoped()) { lvalue, internalClassName ->
                            "decoder.readGroup($lvalue.asInternal()) { msg, decoder -> $internalClassName.decodeWith(msg, decoder, config, tag) }"
                        }
                    )
                } else {
                    code(
                        lvalue.merge(msg.internalClassName.scoped()) { lvalue, internalClassName ->
                            "decoder.readMessage($lvalue.asInternal(), { msg, decoder -> $internalClassName.decodeWith(msg, decoder, config) })"
                        }
                    )
                }
            }

            is FieldType.Map -> {
                scope("with(%T())".scoped(fieldType.entry.dec.value.internalClassName)) {
                    generateDecodeFieldValue(
                        fieldType = FieldType.Message(fieldType.entry.dec),
                        lvalue = "this".scoped(),
                        isPacked = false,
                        wrapperCtor = wrapperCtor
                    )
                    code(
                        lvalue.merge(FqName.Implicits.MutableMap.scoped()) { lvalue, mutableMap ->
                            "($lvalue as $mutableMap)[key] = value"
                        }
                    )
                }
            }
        }
    }

    private fun CodeGenerator.generateMessageEncoder(declaration: MessageDeclaration) = function(
        name = "encodeWith",
        annotations = listOf(FqName.Annotations.InternalRpcApi.scopedAnnotation()),
        args = "encoder: %T, config: %T?".scoped(FqName.RpcClasses.WireEncoder, FqName.RpcClasses.ProtobufConfig),
        contextReceiver = declaration.internalClassName.scoped(),
        returnType = FqName.Implicits.Unit.scoped(),
    ) {
        if (declaration.actualFields.isEmpty()) {
            code("// no fields to encode".scoped())
            return@function
        }

        declaration.actualFields.forEach { field ->
            if (field.nullable) {
                scope("this.${field.name}?.also".scoped()) {
                    generateEncodeFieldValue(field, "it".scoped())
                }
            } else if (field.dec.hasPresence()) {
                ifBranch(condition = "presenceMask[${field.presenceIdx}]".scoped(), ifBlock = {
                    generateEncodeFieldValue(field, "this.${field.name}".scoped())
                })
            } else {
                ifBranch(condition = field.notDefaultCheck(declaration), ifBlock = {
                    generateEncodeFieldValue(field, "this.${field.name}".scoped())
                })
            }
        }

        // encode all extension values
        scope("_extensions.forEach".scoped(), paramDecl = "(key, value) ->".scoped()) {
            scope("value.descriptor.let".scoped(), paramDecl = "descriptor ->".scoped()) {
                internalImports.add("kotlin.reflect.cast")
                code("descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)".scoped())
            }
        }

    }

    private fun CodeGenerator.generateEncodeFieldValue(
        field: FieldDeclaration,
        valueVar: ScopedFormattedString,
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
        valueVar: ScopedFormattedString,
        type: FieldType,
        number: Int,
        isPacked: Boolean,
        packedWithFixedSize: Boolean,
    ) {
        var encFunc = type.decodeEncodeFuncName()
        when (type) {
            is FieldType.IntegralType -> code(
                valueVar.wrapIn { valueVar -> "encoder.write${encFunc!!}(fieldNr = $number, value = $valueVar)" }
            )

            is FieldType.List -> {
                val packedValueVar = if (type.value is FieldType.Enum) {
                    valueVar.wrapIn { valueVar -> "$valueVar.map { it.number }" }
                } else {
                    valueVar
                }

                val innerType = type.value
                encFunc = innerType.decodeEncodeFuncName()
                when {
                    isPacked && packedWithFixedSize -> {
                        code(
                            packedValueVar.wrapIn { packedValueVar ->
                                "encoder.writePacked${encFunc!!}(fieldNr = $number, value = $packedValueVar)"
                            }
                        )
                    }

                    isPacked && !packedWithFixedSize -> {
                        code(
                            packedValueVar.merge(
                                other = type.valueSizeCall(
                                    variable = valueVar,
                                    number = number,
                                    isPacked = true,
                                ),
                            ) { packedValueVar, valueSizeCall ->
                                "encoder.writePacked${encFunc!!}(fieldNr = $number, value = $packedValueVar, fieldSize = $valueSizeCall)"
                            }
                        )
                    }

                    innerType is FieldType.Message -> scope(valueVar.wrapIn { valueVar -> "$valueVar.forEach" }) {
                        generateEncodeFieldValue(
                            "it".scoped(),
                            innerType,
                            number,
                            isPacked = false,
                            packedWithFixedSize = false
                        )
                    }

                    else -> {
                        require(encFunc != null) { "No encode function for list type: $type" }
                        scope(valueVar.wrapIn { valueVar -> "$valueVar.forEach" }) {
                            val enumSuffix = if (type.value is FieldType.Enum) ".number" else ""
                            code("encoder.write${encFunc}($number, it$enumSuffix)".scoped())
                        }
                    }
                }
            }

            is FieldType.Enum -> {
                code(
                    valueVar.wrapIn { valueVar ->
                        "encoder.write${encFunc!!}(fieldNr = $number, value = ${valueVar}.number)"
                    }
                )
            }

            is FieldType.OneOf -> whenBlock(valueVar.wrapIn { valueVar -> "val value = $valueVar" }) {
                type.dec.variants.forEach { variant ->
                    whenCase("is %T".scoped(type.dec.name.nested(variant.name))) {
                        generateEncodeFieldValue(variant, "value.value".scoped())
                    }
                }
            }

            is FieldType.Map -> {
                scope(valueVar.wrapIn { valueVar -> "$valueVar.forEach" }, paramDecl = "kEntry ->".scoped()) {
                    generateMapConstruction(type, "kEntry.key".scoped(), "kEntry.value".scoped())
                    scope(".also".scoped(), paramDecl = "entry ->".scoped()) {
                        generateEncodeFieldValue(
                            valueVar = "entry".scoped(),
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
                code(
                    valueVar.wrapIn { valueVar ->
                        "encoder.$writeMethod(fieldNr = ${number}, value = $valueVar.asInternal()) { encodeWith(it, config) }"
                    }
                )
            }
        }
    }

    private fun CodeGenerator.generateInternalEnumConstructor(enum: EnumDeclaration) {
        function(
            name = "fromNumber",
            args = "number: %T".scoped(FqName.Implicits.Int),
            annotations = listOf(FqName.Annotations.InternalRpcApi.scopedAnnotation()),
            contextReceiver = enum.companionName.scoped(),
            returnType = enum.name.scoped(),
        ) {
            whenBlock(prefix = "return".scoped(), condition = "number".scoped()) {
                enum.originalEntries.forEach { entry ->
                    whenCase("${entry.dec.number}".scoped()) {
                        code(entry.name.scoped())
                    }
                }
                whenCase("else".scoped()) {
                    code("%T(number)".scoped(enum.unrecognisedName))
                }
            }
        }
    }

    /**
     * Generates a function to check for the presence of all required fields in a message declaration.
     */
    private fun CodeGenerator.generateRequiredCheck(declaration: MessageDeclaration) = function(
        name = "checkRequiredFields",
        annotations = listOf(FqName.Annotations.InternalRpcApi.scopedAnnotation()),
        contextReceiver = declaration.internalClassName.scoped(),
        returnType = FqName.Implicits.Unit.scoped(),
    ) {
        val requiredFields = declaration.actualFields.filter { it.dec.isRequired }

        if (requiredFields.isEmpty()) {
            code("// no required fields to check".scoped())
        }

        requiredFields.forEach { field ->
            ifBranch(condition = "!presenceMask[${field.presenceIdx}]".scoped(), ifBlock = {
                code(
                    "throw %T.missingRequiredField(\"${declaration.name.simpleName}\", \"${field.name}\")"
                        .scoped(FqName.RpcClasses.ProtobufDecodingException)
                )
            })
        }

        // check submessages
        declaration.actualFields.filter { it.type is FieldType.Message }.forEach { field ->
            ifBranch(condition = "presenceMask[${field.presenceIdx}]".scoped(), ifBlock = {
                code("this.${field.name}.asInternal().checkRequiredFields()".scoped())
            })
        }

        // check submessages in oneofs
        declaration.actualFields.filter { it.type is FieldType.OneOf }.forEach { field ->
            val oneOfType = field.type as FieldType.OneOf
            val messageVariants = oneOfType.dec.variants.filter { it.type is FieldType.Message }
            if (messageVariants.isEmpty()) return@forEach

            scope("this.${field.name}?.also".scoped()) {
                whenBlock {
                    messageVariants.forEach { variant ->
                        val variantClassName = (field.type as FieldType.OneOf).dec.name.nested(variant.name)

                        whenCase("it is %T".scoped(variantClassName)) {
                            code("it.value.asInternal().checkRequiredFields()".scoped())
                        }
                    }
                }
            }
        }

        // check submessages in lists
        declaration.actualFields.filter { it.type is FieldType.List }.forEach { field ->
            val listType = field.type as FieldType.List
            if (listType.value !is FieldType.Message) return@forEach

            scope("this.${field.name}.forEach".scoped()) {
                code("it.asInternal().checkRequiredFields()".scoped())
            }
        }

        // check submessage in maps
        declaration.actualFields.filter { it.type is FieldType.Map }.forEach { field ->
            val mapType = field.type as FieldType.Map
            // we only have to check the value, as the key cannot be a message
            if (mapType.entry.value !is FieldType.Message) return@forEach

            scope("this.${field.name}.values.forEach".scoped()) {
                code("it.asInternal().checkRequiredFields()".scoped())
            }
        }
    }

    private fun CodeGenerator.generateInternalComputeSize(declaration: MessageDeclaration) {
        function(
            name = "computeSize",
            modifiers = "private",
            contextReceiver = declaration.internalClassName.scoped(),
            returnType = FqName.Implicits.Int.scoped(),
        ) {
            code("var __result = 0".scoped())
            declaration.actualFields.forEach { field ->
                val fieldName = "this.${field.name}"
                if (field.nullable) {
                    scope("$fieldName?.also".scoped()) {
                        generateFieldComputeSizeCall(field, "it".scoped())
                    }
                } else if (!field.dec.hasPresence()) {
                    scope(field.notDefaultCheck(declaration).wrapIn { "if ($it)" }) {
                        generateFieldComputeSizeCall(field, fieldName.scoped())
                    }
                } else {
                    scope("if (presenceMask[${field.presenceIdx}])".scoped()) {
                        generateFieldComputeSizeCall(field, fieldName.scoped())
                    }
                }
            }

            if (declaration.hasExtensionRange) {
                code("__result += extensionsSize()".scoped())
            }

            code("return __result".scoped())
        }
    }

    private fun CodeGenerator.generateInternalCastExtension(declaration: MessageDeclaration) {
        val ctxReceiver = if (declaration.isUserFacing) declaration.name else declaration.internalClassName

        // we generate the asInternal extension even for non-user-facing message classes (map entry)
        // to avoid edge-cases when generating other code that uses the asInternal() extension.
        function(
            "asInternal",
            annotations = listOf(FqName.Annotations.InternalRpcApi.scopedAnnotation()),
            contextReceiver = ctxReceiver.scoped(),
            returnType = declaration.internalClassName.scoped(),
        ) {
            if (!declaration.isUserFacing) {
                code("return this".scoped())
            } else {
                code(
                    "return this as? %T ?: error(\"Message \${this::class.simpleName} is a non-internal message type.\")"
                        .scoped(declaration.internalClassName)
                )
            }
        }
    }

    private fun CodeGenerator.generateFieldComputeSizeCall(
        field: FieldDeclaration,
        variable: ScopedFormattedString,
    ) {
        val valueSize by lazy { field.type.valueSizeCall(variable, field.number, field.dec.isPacked) }
        val tagSize = tagSizeCall(field.number, field.type.wireType)

        when (val fieldType = field.type) {
            is FieldType.List -> when {
                // packed fields also have the tag + len
                field.dec.isPacked -> {
                    code(
                        valueSize.merge(tagSize, int32SizeCall("it".scoped())) { valueSize, tagSize, int32SizeCall ->
                            "__result += $valueSize.let { $tagSize + $int32SizeCall + it }"
                        }
                    )
                }

                else -> {
                    code(valueSize.wrapIn { valueSize -> "__result += $valueSize" })
                }
            }

            is FieldType.Message -> if (fieldType.dec.value.isGroup) {
                val groupTagSize = tagSizeCall(field.number, WireType.START_GROUP)
                // the group message size is the size of the message plus the start and end group tags
                code(
                    valueSize.merge(groupTagSize) { valueSize, groupTagSize ->
                        "__result += $valueSize.let { (2 * $groupTagSize) + it }"
                    }
                )
            } else {
                code(
                    valueSize.merge(tagSize, int32SizeCall("it".scoped())) { valueSize, tagSize, int32SizeCall ->
                        "__result += $valueSize.let { $tagSize + $int32SizeCall + it }"
                    }
                )
            }

            FieldType.IntegralType.STRING, FieldType.IntegralType.BYTES -> {
                code(
                    valueSize.merge(tagSize, int32SizeCall("it".scoped())) { valueSize, tagSize, int32SizeCall ->
                        "__result += $valueSize.let { $tagSize + $int32SizeCall + it }"
                    }
                )
            }

            is FieldType.Map -> {
                scope("__result += ${field.name}.entries.sumOf".scoped(), paramDecl = "kEntry ->".scoped()) {
                    generateMapConstruction(field.type as FieldType.Map, "kEntry.key".scoped(), "kEntry.value".scoped())
                    code(
                        tagSize.merge(int32SizeCall("it".scoped())) { tagSize, int32SizeCall ->
                            "._size.let { $tagSize + $int32SizeCall + it }"
                        }
                    )
                }
            }

            is FieldType.OneOf -> whenBlock(variable.wrapIn { "val value = $it" }) {
                (field.type as FieldType.OneOf).dec.variants.forEach { variant ->
                    val variantName = (field.type as FieldType.OneOf).dec.name.nested(variant.name)
                    whenCase("is %T".scoped(variantName)) {
                        generateFieldComputeSizeCall(variant, "value.value".scoped())
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
                -> {
                code(
                    tagSize.merge(valueSize) { tagSize, valueSize ->
                        "__result += ($tagSize + $valueSize)"
                    }
                )
            }
        }
    }

    private fun CodeGenerator.generateMapConstruction(
        map: FieldType.Map,
        keyVar: ScopedFormattedString,
        valueVar: ScopedFormattedString,
    ) {
        scope("%T().apply".scoped(map.entry.dec.value.internalClassName), nlAfterClosed = false) {
            code(keyVar.wrapIn { "key = $it" })
            code(valueVar.wrapIn { "value = $it" })
        }
    }

    private fun FieldType.valueSizeCall(
        variable: ScopedFormattedString,
        number: Int,
        isPacked: Boolean = false,
    ): ScopedFormattedString {
        val sizeFuncName = decodeEncodeFuncName()?.replaceFirstChar { it.lowercase() }

        val convertedVariable = if (isPacked && this is FieldType.List && value is FieldType.Enum) {
            variable.wrapIn { variable -> "$variable.map { it.number }" }
        } else {
            variable
        }

        val sizeFunc = FqName.RpcClasses.WireSize.scoped().merge(convertedVariable) { wireSize, convertedVariable ->
            "$wireSize.$sizeFuncName($convertedVariable)"
        }

        return when (this) {
            is FieldType.IntegralType -> sizeFunc.also {
                referenceExtension(FqName.RpcClasses.WireSize, sizeFuncName!!)
            }

            is FieldType.List -> when {
                isPacked -> sizeFunc.also {
                    referenceExtension(FqName.RpcClasses.WireSize, sizeFuncName!!)
                }

                else -> {
                    // calculate the size of the values within the list.
                    val valueSize = value.valueSizeCall("it".scoped(), number)
                    val tagSize = tagSizeCall(number, value.wireType)

                    val elementSize = when (value.wireType) {
                        WireType.LENGTH_DELIMITED -> {
                            valueSize.merge(
                                tagSize,
                                int32SizeCall("it".scoped())
                            ) { valueSize, tagSize, int32SizeCall ->
                                "$valueSize.let { $tagSize + $int32SizeCall + it }"
                            }
                        }

                        WireType.START_GROUP -> {
                            tagSize.merge(
                                other = valueSize,
                                another = tagSizeCall(number, WireType.END_GROUP),
                            ) { tagSize, valueSize, tagSizeCall ->
                                "$tagSize + $valueSize + $tagSizeCall"
                            }
                        }

                        else -> {
                            tagSize.merge(valueSize) { tagSize, valueSize ->
                                "$tagSize + $valueSize"
                            }
                        }
                    }

                    variable.merge(elementSize) { variable, elementSize ->
                        "$variable.sumOf { $elementSize }"
                    }
                }
            }

            is FieldType.Enum -> {
                FqName.RpcClasses.WireSize.scoped().merge(variable) { wireSize, variable ->
                    "$wireSize.$sizeFuncName($variable.number)"
                }.also {
                    referenceExtension(FqName.RpcClasses.WireSize, sizeFuncName!!)
                }
            }

            is FieldType.Message -> variable.wrapIn { variable -> "$variable.asInternal()._size" }
            is FieldType.Map -> error("Map fields have no direct valueSizeCall")
            is FieldType.OneOf -> error("OneOf fields have no direct valueSizeCall")
        }
    }

    private fun tagSizeCall(number: Int, wireType: WireType): ScopedFormattedString {
        return "%T.tag($number, %T)".scoped(
            FqName.RpcClasses.WireSize,
            FqName.RpcClasses.WireType.nested(wireType.name)
        ).also {
            referenceExtension(FqName.RpcClasses.WireSize, "tag")
        }
    }

    @Suppress("SameParameterValue")
    private fun int32SizeCall(number: ScopedFormattedString): ScopedFormattedString {
        return FqName.RpcClasses.WireSize.scoped().merge(number) { wireSize, number ->
            "$wireSize.int32($number)"
        }.also {
            referenceExtension(FqName.RpcClasses.WireSize, "int32")
        }
    }

    private fun FieldDeclaration.notDefaultCheck(parent: MessageDeclaration): ScopedFormattedString {
        return when (val fieldType = type) {
            is FieldType.IntegralType -> {
                val defaultValue = safeDefaultValue(parent)
                when {
                    fieldType == FieldType.IntegralType.STRING &&
                        defaultValue == FieldType.IntegralType.STRING.defaultValue -> "this.$name.isNotEmpty()".scoped()

                    fieldType == FieldType.IntegralType.STRING -> defaultValue.wrapIn { defaultValue ->
                        "!this.$name.contentEquals($defaultValue)"
                    }

                    fieldType == FieldType.IntegralType.BYTES &&
                        defaultValue == FieldType.IntegralType.BYTES.defaultValue -> "this.$name.isNotEmpty()".scoped()

                    fieldType == FieldType.IntegralType.BYTES -> {
                        defaultValue.wrapIn { defaultValue -> "!this.$name.contentEquals($defaultValue)" }
                    }

                    else -> defaultValue.wrapIn { defaultValue -> "this.$name != $defaultValue" }
                }
            }

            is FieldType.List, is FieldType.Map -> "this.$name.isNotEmpty()".scoped()

            is FieldType.Enum -> {
                safeDefaultValue(parent).wrapIn { safeDefaultValue -> "this.$name != $safeDefaultValue" }
            }

            is FieldType.Message -> error("Message fields should not be checked for default values.")
            is FieldType.OneOf -> "null".scoped()
        }
    }

    private fun FieldDeclaration.safeDefaultValue(parent: MessageDeclaration): ScopedFormattedString {
        if (nullable) {
            return "null".scoped()
        }

        if (!dec.hasDefaultValue()) {
            return type.defaultValue ?: error("No default value for field $name")
        }

        val value = dec.defaultValue
        return when {
            value is String -> {
                val escaped = value
                    .replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("$", "\\$")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t")
                    .replace("\u0000", "\\u0000")
                "\"$escaped\"".scoped()
            }

            value is ByteString -> {
                "%T.$name".scoped(parent.bytesDefaultsName)
            }

            value is Descriptors.EnumValueDescriptor -> {
                value.fqName().scoped()
            }

            value is Int && (type == FieldType.IntegralType.UINT32 || type == FieldType.IntegralType.FIXED32) -> {
                (Integer.toUnsignedString(value) + "u").scoped()
            }

            value is Long && (type == FieldType.IntegralType.UINT64 || type == FieldType.IntegralType.FIXED64) -> {
                (java.lang.Long.toUnsignedString(value) + "uL").scoped()
            }

            value is Float -> when {
                value.isNaN() -> "%T.NaN".scoped(FqName.Implicits.Float)
                value == Float.POSITIVE_INFINITY -> "%T.POSITIVE_INFINITY".scoped(FqName.Implicits.Float)
                value == Float.NEGATIVE_INFINITY -> "%T.NEGATIVE_INFINITY".scoped(FqName.Implicits.Float)
                else -> FqName.Implicits.Float.scoped().wrapIn { float ->
                    val bits = java.lang.Float.floatToRawIntBits(value)
                    // otherwise, .format will fuck around with %T
                    float + ".fromBits(0x%08X.toInt())".format(bits.toLong() and 0xFFFFFFFFL)
                }
            }

            value is Double -> when {
                value.isNaN() -> "%T.NaN".scoped(FqName.Implicits.Double)
                value == Double.POSITIVE_INFINITY -> "%T.POSITIVE_INFINITY".scoped(FqName.Implicits.Double)
                value == Double.NEGATIVE_INFINITY -> "%T.NEGATIVE_INFINITY".scoped(FqName.Implicits.Double)
                else -> FqName.Implicits.Double.scoped().wrapIn { double ->
                    // otherwise, `.format` will fuck around with %T
                    double + ".fromBits(0x%016XL)".format(java.lang.Double.doubleToRawLongBits(value))
                }
            }

            // Long.MIN_VALUE and Int.MIN_VALUE can't be expressed as literals directly
            // because the compiler parses the minus sign and the number separately.
            value is Long && value == Long.MIN_VALUE -> "%T.MIN_VALUE".scoped(FqName.Implicits.Long)
            value is Int && value == Int.MIN_VALUE -> "%T.MIN_VALUE".scoped(FqName.Implicits.Int)

            else -> {
                "${value}${type.scalarDefaultSuffix()}".scoped()
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
        clazz(
            name = declaration.name.simpleName,
            comment = declaration.doc,
            modifiers = "sealed",
            declarationType = CodeGenerator.DeclarationType.Interface,
        ) {
            declaration.variants.forEach { variant ->
                clazz(
                    name = variant.name,
                    comment = variant.doc,
                    modifiers = "value",
                    constructorArgs = listOf(variant.typeFqName().wrapIn { "val value: $it" }),
                    annotations = listOf(FqName.KotlinLibs.JvmInline.scopedAnnotation()),
                    superTypes = listOf(declaration.name.scoped()),
                    deprecation = if (variant.deprecated) DeprecationLevel.WARNING else null,
                )
            }
        }
    }

    private fun CodeGenerator.generatePublicEnum(declaration: EnumDeclaration) {
        val entriesSorted = declaration.originalEntries.sortedBy { it.dec.number }

        clazz(
            name = declaration.name.simpleName,
            comment = declaration.doc,
            modifiers = "sealed",
            constructorArgs = listOf("open val number: %T".scoped(FqName.Implicits.Int)),
            deprecation = if (declaration.deprecated) DeprecationLevel.WARNING else null,
        ) {
            declaration.originalEntries.forEach { variant ->
                clazz(
                    name = variant.name.simpleName,
                    comment = variant.doc,
                    modifiers = "data",
                    declarationType = CodeGenerator.DeclarationType.Object,
                    superTypes = listOf("%T(number = ${variant.dec.number})".scoped(declaration.name)),
                    deprecation = if (variant.deprecated) DeprecationLevel.WARNING else null,
                )
            }

            // TODO: Avoid name conflict
            clazz(
                modifiers = "data",
                name = declaration.unrecognisedName.simpleName,
                constructorArgs = listOf("override val number: %T".scoped(FqName.Implicits.Int)),
                superTypes = listOf("%T(number)".scoped(declaration.name)),
            )

            newLine()

            clazz("", modifiers = "companion", declarationType = CodeGenerator.DeclarationType.Object) {
                declaration.aliases.forEach { alias: EnumDeclaration.Alias ->
                    property(
                        name = alias.name.simpleName,
                        comment = alias.doc,
                        type = declaration.name.scoped(),
                        propertyInitializer = CodeGenerator.PropertyInitializer.GETTER,
                        value = alias.original.name.scoped(),
                        deprecation = if (alias.deprecated) DeprecationLevel.WARNING else null,
                    )
                }

                val entryNamesSorted = entriesSorted.map { it.name.scoped() }.joinToScopedString(", ")
                property(
                    name = "entries",
                    type = "%T<%T>".scoped(FqName.Implicits.List, declaration.name),
                    propertyInitializer = CodeGenerator.PropertyInitializer.DELEGATE,
                    value = entryNamesSorted.wrapIn { entryNamesSorted -> "lazy { listOf($entryNamesSorted) }" },
                )
            }
        }
    }

    private fun CodeGenerator.generateProtoExtensionsObject(fileDeclaration: FileDeclaration) {
        val extensions = fileDeclaration.allExtensions()
        if (extensions.isEmpty()) return

        clazz(
            name = fileDeclaration.internalExtensionDescriptorObject.simpleName,
            annotations = listOf(FqName.Annotations.InternalRpcApi.scopedAnnotation()),
            declarationType = CodeGenerator.DeclarationType.Object,
        ) {
            fileDeclaration.extensions.forEach { extension ->
                emitExtensionDescriptorProperty(extension)
            }

            fileDeclaration.messageDeclarations.forEach { message ->
                emitMessageScopedExtensionDescriptors(message)
            }
        }
    }

    private fun CodeGenerator.emitMessageScopedExtensionDescriptors(message: MessageDeclaration) {
        val nestedWithExtensions = message.nestedDeclarations.filter { it.hasExtensionsRecursively() }
        if (message.extensions.isEmpty() && nestedWithExtensions.isEmpty()) return

        clazz(
            name = message.name.simpleName,
            declarationType = CodeGenerator.DeclarationType.Object,
        ) {
            message.extensions.forEach { extension ->
                emitExtensionDescriptorProperty(extension)
            }

            nestedWithExtensions.forEach { nested ->
                emitMessageScopedExtensionDescriptors(nested)
            }
        }
    }

    private fun CodeGenerator.emitExtensionDescriptorProperty(extension: FieldDeclaration) {
        val descriptorName = requireNotNull(extension.extensionDescriptorName) {
            "Missing extension descriptor name for ${extension.name}"
        }.simpleName
        val messageName = extension.containingType.value.name
        val typeNonNull = extension.typeFqNameNonNullable()
        property(
            name = descriptorName,
            type = "%T<%T, ".scoped(
                FqName.RpcClasses.InternalExtensionDescriptor,
                messageName,
            ).merge(typeNonNull) { first, typeNonNull -> "$first $typeNonNull>" },
            valueOnNewLine = true
        ) {
            generateExtensionDescriptor(extension)
        }
    }

    private fun CodeGenerator.generateExtensionDescriptor(field: FieldDeclaration) {
        when (val type = field.type) {
            is FieldType.IntegralType -> generateScalarExtensionDescriptor(field, type)
            is FieldType.Enum -> generateEnumExtensionDescriptor(field, type)
            is FieldType.Message -> generateMessageExtensionDescriptor(field, type)
            is FieldType.List -> generateRepeatedExtensionDescriptor(field, type)
            is FieldType.Map -> error("Extensions can't be of type Map")
            is FieldType.OneOf -> error("Extensions can't be of type OneOf")
        }
    }

    private fun CodeGenerator.generateScalarExtensionDescriptor(
        field: FieldDeclaration,
        type: FieldType.IntegralType,
    ) {
        functionCall(
            function = "%T.${type.name.lowercase()}".scoped(FqName.RpcClasses.InternalExtensionDescriptor),
            namedArgs = field.baseExtensionDescriptorArgs(),
        )
    }

    private fun CodeGenerator.generateEnumExtensionDescriptor(
        field: FieldDeclaration,
        type: FieldType.Enum,
    ) {
        val enumDeclaration = type.dec.value
        val enumType = enumDeclaration.name
        val defaultValue = enumDeclaration.extensionDefaultValue(field)
        functionCall(
            function = "%T.enum".scoped(FqName.RpcClasses.InternalExtensionDescriptor),
            namedArgs = field.baseExtensionDescriptorArgs() + listOf(
                "valueType" to "%T::class".scoped(enumType),
                "encodeValue" to "{ it.number }".scoped(),
                "decodeValue" to "{ %T.fromNumber(it) }".scoped(enumType),
                "defaultValue" to "{ %T }".scoped(defaultValue),
            ),
        )
    }

    private fun CodeGenerator.generateMessageExtensionDescriptor(
        field: FieldDeclaration,
        type: FieldType.Message,
    ) {
        val message = type.dec.value
        if (message.isGroup) {
            functionCall(
                function = "%T.group".scoped(FqName.RpcClasses.InternalExtensionDescriptor),
                namedArgs = field.baseExtensionDescriptorArgs() + listOf(
                    "valueType" to "%T::class".scoped(message.name),
                    "default" to "{ %T() }".scoped(message.internalClassName),
                    "asInternal" to "{ it.asInternal() }".scoped(),
                    "encodeWith" to "{ value, encoder, config -> value.asInternal().encodeWith(encoder, config) }".scoped(),
                    "decodeWith" to "{ value, decoder, config, startGroup -> %T.decodeWith(value.asInternal(), decoder, config, startGroup) }".scoped(
                        message.internalClassName,
                    ),
                ),
            )
        } else {
            functionCall(
                function = "%T.message".scoped(FqName.RpcClasses.InternalExtensionDescriptor),
                namedArgs = field.baseExtensionDescriptorArgs() + listOf(
                    "valueType" to "%T::class".scoped(message.name),
                    "default" to "{ %T() }".scoped(message.internalClassName),
                    "asInternal" to "{ it.asInternal() }".scoped(),
                    "encodeWith" to "{ value, encoder, config -> value.asInternal().encodeWith(encoder, config) }".scoped(),
                    "decodeWith" to "{ value, decoder, config -> %T.decodeWith(value.asInternal(), decoder, config) }".scoped(
                        message.internalClassName,
                    ),
                ),
            )
        }
    }

    private fun CodeGenerator.generateRepeatedExtensionDescriptor(
        field: FieldDeclaration,
        type: FieldType.List,
    ) {
        val function = when {
            field.dec.isPacked && type.value.isPackable -> "%T.packedRepeated".scoped(FqName.RpcClasses.InternalExtensionDescriptor)
            field.dec.isPacked -> error("Packed extensions are not supported for ${field.name}: ${type.value}")
            else -> "%T.repeated".scoped(FqName.RpcClasses.InternalExtensionDescriptor)
        }

        functionCall(
            function = function,
            namedArgBlocks = listOf(
                "elementDescriptor" to {
                    generateNonRepeatedExtensionDescriptor(field, type.value)
                },
            ),
        )
    }

    private fun CodeGenerator.generateNonRepeatedExtensionDescriptor(
        field: FieldDeclaration,
        type: FieldType,
    ) {
        when (type) {
            is FieldType.IntegralType -> generateScalarExtensionDescriptor(field, type)
            is FieldType.Enum -> generateEnumExtensionDescriptor(field, type)
            is FieldType.Message -> generateMessageExtensionDescriptor(field, type)
            is FieldType.List -> error("Nested repeated extensions are not supported: ${field.name}")
            is FieldType.Map -> error("Map extensions are not supported: ${field.name}")
            is FieldType.OneOf -> error("OneOf extensions are not supported: ${field.name}")
        }
    }
}

private fun FieldDeclaration.baseExtensionDescriptorArgs(): List<Pair<String, ScopedFormattedString>> {
    return listOf(
        "fieldNumber" to number.toString().scoped(),
        "name" to "\"$name\"".scoped(),
        "extendee" to "%T::class".scoped(containingType.value.name),
    )
}

private fun EnumDeclaration.generatedValueName(descriptor: Descriptors.EnumValueDescriptor): FqName {
    originalEntries.firstOrNull { it.dec == descriptor }?.let { return it.name }
    aliases.firstOrNull { it.dec == descriptor }?.let { return it.name }
    error("Missing generated enum value for ${descriptor.fullName}")
}

private fun EnumDeclaration.extensionDefaultValue(field: FieldDeclaration): FqName {
    val descriptorDefault = field.dec.defaultValue as? Descriptors.EnumValueDescriptor
    return if (descriptorDefault != null) {
        generatedValueName(descriptorDefault)
    } else {
        defaultEntry().name
    }
}

private fun MessageDeclaration.allNestedRecursively(): List<MessageDeclaration> =
    nestedDeclarations + nestedDeclarations.flatMap(MessageDeclaration::allNestedRecursively)

private fun MessageDeclaration.hasExtensionsRecursively(): Boolean =
    extensions.isNotEmpty() || nestedDeclarations.any { it.hasExtensionsRecursively() }

private fun FileDeclaration.allExtensions(): List<FieldDeclaration> =
    extensions + allNestedExtensions()

private fun FileDeclaration.allNestedExtensions(): List<FieldDeclaration> =
    (messageDeclarations + messageDeclarations.flatMap(MessageDeclaration::allNestedRecursively))
        .flatMap { it.extensions }

/**
 * Indicates whether this file contains extension declarations that reference group messages.
 *
 * This is used to keep generation enabled even when the file has no regular message declarations,
 * but still requires generated types because extensions involve groups.
 */
private fun FileDeclaration.hasExtensionGroupMessages(): Boolean =
    allExtensions().any { field ->
        field.containingType.value.isGroup || ((field.type as? FieldType.Message)?.dec?.value?.isGroup == true)
    }

private fun FileDeclaration.asDeclName() = name.replace(".", "")
private fun FqName.asDeclName() = toString().replace(".", "")

private fun String.capitalize(): String = replaceFirstChar { it.uppercase() }
