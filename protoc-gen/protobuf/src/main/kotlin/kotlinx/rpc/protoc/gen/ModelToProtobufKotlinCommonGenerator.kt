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
import kotlinx.rpc.protoc.gen.core.FileGenerator
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
import kotlinx.rpc.protoc.gen.core.model.hasRequiredFields
import kotlinx.rpc.protoc.gen.core.model.isOneOfReferenceType
import kotlinx.rpc.protoc.gen.core.model.nested
import kotlinx.rpc.protoc.gen.core.model.scalarDefaultSuffix
import kotlinx.rpc.protoc.gen.core.model.topLevelFP
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

    override fun FileGenerator.generatePublicDeclaredEntities(fileDeclaration: FileDeclaration) {
        fileSuppresses = listOf("ClassName")

        fileDeclaration.messageDeclarations.forEach { generatePublicMessage(it) }
        fileDeclaration.enumDeclarations.forEach { generatePublicEnum(it) }
    }

    override fun FileGenerator.generateExtensionEntities(fileDeclaration: FileDeclaration) {
        fileSuppresses = listOf("unused")

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

    override fun FileGenerator.generateInternalDeclaredEntities(fileDeclaration: FileDeclaration) {
        fileSuppresses = listOf("PropertyName", "CanBeVal", "ConstPropertyName", "LocalVariableName", "DuplicatedCode")

        generateInternalMessageEntities(fileDeclaration.messageDeclarations)

        val allEnums = fileDeclaration.enumDeclarations + fileDeclaration.messageDeclarations
            .flatMap {
                it.enumDeclarations + it.allNestedRecursively().flatMap(MessageDeclaration::enumDeclarations)
            }

        allEnums.forEach { enum ->
            generateInternalEnumConstructor(enum)
        }

        generateProtoExtensionsObject(fileDeclaration)
    }

    private fun CodeGenerator.generateInternalMessageEntities(messages: List<MessageDeclaration>) {
        messages.forEach { generateInternalMessage(it) }

        // emit all required functions in the outer scope
        val allMessages = messages + messages.flatMap(MessageDeclaration::allNestedRecursively)
        allMessages.forEach { message ->
            if (message.hasRequiredFieldsRecursively) {
                generateRequiredCheck(message)
            }
            generateMessageEncoder(message)
            generateMessageDecoder(message)
            generateInternalComputeSize(message)
            generateInternalCastExtension(message)
        }
    }

    private fun CodeGenerator.generateExtensionMessageEntities(messages: List<MessageDeclaration>) {
        val allMessages = messages + messages.flatMap(MessageDeclaration::allNestedRecursively)
        allMessages.forEach {
            generateMessageConstructor(it)
            generatePublicCopy(it)
            generatePublicPresenceGetter(it)
            if (config.generateOptionalFieldOrNullGetters) {
                // generates orNull getters for optional fields (including oneof members)
                generatePublicOrNullFieldGetters(it)
            }
            generateOneOfExtensions(it)
        }

        // the presence interfaces are not generated in the flattened list
        // as nested classes are generated as nested presence interfaces
        messages.forEach { generatePresenceInterface(it) }

        // the oneof case enums are top-level classes
        allMessages.forEach { message ->
            message.oneOfDeclarations.forEach { oneOf -> generateOneOfCaseEnum(message, oneOf) }
        }
    }

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

            declaration.nestedDeclarations.forEach { nested ->
                generatePublicMessage(nested)
            }

            declaration.enumDeclarations.forEach { enum ->
                generatePublicEnum(enum)
            }
        }
    }

    private fun CodeGenerator.generateInternalMessage(declaration: MessageDeclaration) {
        val superTypes = buildList {
            if (declaration.isUserFacing) {
                add(declaration.builderClassName.scoped())
            }
            add(
                "%T(fieldsWithPresence = ${declaration.presenceMaskSize})"
                    .scoped(FqName.RpcClasses.InternalMessage)
            )
        }

        clazz(
            name = declaration.internalClassName.simpleName,
            declarationType = CodeGenerator.DeclarationType.Class,
            superTypes = superTypes,
            annotations = listOf(FqName.Annotations.InternalRpcApi.scopedAnnotation()),
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

            declaration.oneOfDeclarations.forEach { oneOf ->
                generateOneOfInternalStorage(oneOf)
            }

            declaration.actualFields.forEachIndexed { i, field ->
                val oneOf = field.containingOneOf.value
                if (oneOf != null) {
                    generateInternalOneOfOptionDeclaration(i, field, oneOf, declaration)
                } else {
                    generateInternalFieldPropertyDeclaration(i, field, declaration)
                }
                generateInternalFieldClearFunction(field)
            }

            generateInternalPresenceObjectProperty(declaration)
            generateHashCode(declaration)
            generateEquals(declaration)
            generateToString(declaration)
            generateInternalCopy(declaration)

            declaration.nestedDeclarations.forEach { nested ->
                generateInternalMessage(nested)
            }

            generateMarshallerObject(declaration)
            generateDescriptorObject(declaration)
            generateCompanionObject(declaration)
        }
    }

    private fun CodeGenerator.generateInternalFieldPropertyDeclaration(
        index: Int,
        field: FieldDeclaration,
        msg: MessageDeclaration
    ) {
        val override = if (msg.isUserFacing) "override" else ""
        val fieldPresence = if (field.presenceIdx != null) {
            "(%T.${field.name})".scoped(msg.presenceIndicesName)
        } else {
            ScopedFormattedString.empty
        }

        val value = FqName.RpcClasses.MsgFieldDelegate
            .scoped()
            .merge(
                other = fieldPresence,
                another = field.safeDefaultValue(msg),
            ) { msgFieldDelegate, fieldPresence, fieldDefault ->
                "$msgFieldDelegate$fieldPresence { $fieldDefault }"
            }

        val delegateType = FqName.RpcClasses.MsgFieldDelegate.scoped()
            .merge(field.typeFqName()) { msgFieldDelegate, fieldFqName ->
                "$msgFieldDelegate<$fieldFqName>"
            }

        // create a delegate and store it using __<name>Delegate
        property(
            name = field.internalDelegateName,
            modifiers = "internal",
            type = delegateType,
            value = value,
            needsNewLineAfterDeclaration = false
        )

        property(
            name = field.name,
            modifiers = override,
            value = field.internalDelegateName.scoped(),
            isVar = true,
            type = field.typeFqName(),
            propertyInitializer = CodeGenerator.PropertyInitializer.DELEGATE,
            needsNewLineAfterDeclaration = index == msg.actualFields.lastIndex,
        )
    }

    /**
     * Generates the typed storage slots of a oneof on the internal class, the `_<oneOf>Case` getter that maps
     * the active presence bit to the case enum entry, and `clear<OneOf>Internal()` that resets the oneof.
     *
     * The active case is not stored separately: it is the member whose presence bit is set.
     * The members of one oneof have consecutive presence indices, so setting a member clears all
     * sibling bits with a single range operation, see `BitSet.setExclusive`.
     */
    private fun CodeGenerator.generateOneOfInternalStorage(oneOf: OneOfDeclaration) {
        if (oneOf.hasReferenceSlot) {
            property(
                name = oneOf.referenceSlotName,
                modifiers = "private",
                isVar = true,
                type = "%T?".scoped(FqName.Implicits.Any),
                value = "null".scoped(),
                needsNewLineAfterDeclaration = !oneOf.hasNumericSlot,
            )
        }

        if (oneOf.hasNumericSlot) {
            property(
                name = oneOf.numericSlotName,
                modifiers = "private",
                isVar = true,
                type = oneOf.numericSlotType(),
                value = oneOf.numericSlotZero().scoped(),
            )
        }

        // maps the set presence bit of the oneof range to the case enum entry
        property(
            name = oneOf.internalCaseGetterName,
            annotations = listOf(FqName.Annotations.InternalRpcApi.scopedAnnotation()),
            type = oneOf.caseTypeName.scoped(),
            propertyInitializer = CodeGenerator.PropertyInitializer.GETTER,
            value = "when".scoped(),
        ) {
            oneOf.variants.forEach { variant ->
                code(
                    variant.presenceIdxFieldName.merge(oneOf.caseTypeName.scoped()) { idx, caseType ->
                        "presenceMask[$idx] -> $caseType.${oneOf.caseEntryName(variant)}"
                    }
                )
            }
            code(oneOf.caseTypeName.scoped().wrapIn { "else -> $it.${oneOf.notSetEntryName}" })
        }

        function(
            name = oneOf.internalClearFunctionName,
            annotations = listOf(FqName.Annotations.InternalRpcApi.scopedAnnotation()),
            returnType = "".scoped(),
        ) {
            code(
                oneOf.variants.first().presenceIdxFieldName
                    .merge(oneOf.variants.last().presenceIdxFieldName) { first, last ->
                        "presenceMask.clearRange($first, $last)"
                    }
            )
            if (oneOf.hasReferenceSlot) {
                code("${oneOf.referenceSlotName} = null".scoped())
            }
            if (oneOf.hasNumericSlot) {
                code("${oneOf.numericSlotName} = ${oneOf.numericSlotZero()}".scoped())
            }
        }
    }

    /**
     * Generates a oneof member as a flat property backed by the oneof slots:
     * the getter returns the proto default unless the member is the active case,
     * the setter marks the member as the active case and stores the value in its slot.
     */
    private fun CodeGenerator.generateInternalOneOfOptionDeclaration(
        index: Int,
        field: FieldDeclaration,
        oneOf: OneOfDeclaration,
        msg: MessageDeclaration,
    ) {
        val override = if (msg.isUserFacing) "override" else ""

        val getter = field.presenceIdxFieldName.merge(
            other = oneOf.slotReadExpr(field),
            another = field.safeDefaultValue(msg),
        ) { idx, read, default ->
            "if (presenceMask[$idx]) $read else $default"
        }

        val setExclusive = field.presenceIdxFieldName.merge(
            other = oneOf.variants.first().presenceIdxFieldName,
            another = oneOf.variants.last().presenceIdxFieldName,
        ) { idx, first, last ->
            "presenceMask.setExclusive($idx, $first, $last)"
        }

        val resetOtherSlot = when {
            field.type.isOneOfReferenceType && oneOf.hasNumericSlot ->
                "; ${oneOf.numericSlotName} = ${oneOf.numericSlotZero()}"

            !field.type.isOneOfReferenceType && oneOf.hasReferenceSlot ->
                "; ${oneOf.referenceSlotName} = null"

            else -> ""
        }

        val setter = setExclusive.wrapIn { setExclusive ->
            "$setExclusive; ${oneOf.slotWriteExpr(field)}$resetOtherSlot"
        }

        property(
            name = field.name,
            modifiers = override,
            isVar = true,
            type = field.typeFqName(),
            propertyInitializer = CodeGenerator.PropertyInitializer.GETTER,
            value = getter,
            setter = setter,
            deprecation = if (field.deprecated) DeprecationLevel.WARNING else null,
            needsNewLineAfterDeclaration = index == msg.actualFields.lastIndex,
        )
    }

    private fun OneOfDeclaration.numericSlotType(): ScopedFormattedString =
        if (numericSlotIs64Bit) FqName.Implicits.Long.scoped() else FqName.Implicits.Int.scoped()

    private fun OneOfDeclaration.numericSlotZero(): String = if (numericSlotIs64Bit) "0L" else "0"

    private fun OneOfDeclaration.numericSlotOne(): String = if (numericSlotIs64Bit) "1L" else "1"

    /**
     * Expression reading the value of [field] from its slot, converted back to the member type.
     * Floats and doubles are stored as raw bits, enums as their number, bools as 0/1.
     */
    private fun OneOfDeclaration.slotReadExpr(field: FieldDeclaration): ScopedFormattedString {
        val num = numericSlotName
        val toInt = if (numericSlotIs64Bit) ".toInt()" else ""

        return when (val type = field.type) {
            is FieldType.Message -> "($referenceSlotName as %T)".scoped(type.dec.value.name)
            is FieldType.Enum -> "%T.%F($num$toInt)".scoped(
                type.dec.value.name,
                type.dec.value.name.topLevelFP("fromNumber"),
            )

            FieldType.IntegralType.STRING -> "($referenceSlotName as %T)".scoped(FqName.Implicits.String)
            FieldType.IntegralType.BYTES -> "($referenceSlotName as %T)".scoped(FqName.KotlinLibs.ByteString)
            FieldType.IntegralType.BOOL -> "($num != ${numericSlotZero()})".scoped()
            FieldType.IntegralType.FLOAT -> "%T.fromBits($num$toInt)".scoped(FqName.Implicits.Float)
            FieldType.IntegralType.DOUBLE -> "%T.fromBits($num)".scoped(FqName.Implicits.Double)

            FieldType.IntegralType.INT32,
            FieldType.IntegralType.SINT32,
            FieldType.IntegralType.SFIXED32,
                -> "$num$toInt".scoped()

            FieldType.IntegralType.UINT32,
            FieldType.IntegralType.FIXED32,
                -> "$num.toUInt()".scoped()

            FieldType.IntegralType.INT64,
            FieldType.IntegralType.SINT64,
            FieldType.IntegralType.SFIXED64,
                -> num.scoped()

            FieldType.IntegralType.UINT64,
            FieldType.IntegralType.FIXED64,
                -> "$num.toULong()".scoped()

            is FieldType.List, is FieldType.Map -> error("Oneof members cannot be repeated: ${field.name}")
        }
    }

    /**
     * Statement storing the setter `value` of [field] into its slot.
     */
    private fun OneOfDeclaration.slotWriteExpr(field: FieldDeclaration): String {
        val num = numericSlotName
        val toLong = if (numericSlotIs64Bit) ".toLong()" else ""

        return when (field.type) {
            is FieldType.Message,
            FieldType.IntegralType.STRING,
            FieldType.IntegralType.BYTES,
                -> "$referenceSlotName = value"

            is FieldType.Enum -> "$num = value.number$toLong"
            FieldType.IntegralType.BOOL -> "$num = if (value) ${numericSlotOne()} else ${numericSlotZero()}"
            FieldType.IntegralType.FLOAT -> "$num = value.toRawBits()$toLong"
            FieldType.IntegralType.DOUBLE -> "$num = value.toRawBits()"

            FieldType.IntegralType.INT32,
            FieldType.IntegralType.SINT32,
            FieldType.IntegralType.SFIXED32,
                -> "$num = value$toLong"

            FieldType.IntegralType.UINT32,
            FieldType.IntegralType.FIXED32,
                -> if (numericSlotIs64Bit) "$num = value.toLong()" else "$num = value.toInt()"

            FieldType.IntegralType.INT64,
            FieldType.IntegralType.SINT64,
            FieldType.IntegralType.SFIXED64,
                -> "$num = value"

            FieldType.IntegralType.UINT64,
            FieldType.IntegralType.FIXED64,
                -> "$num = value.toLong()"

            is FieldType.List, is FieldType.Map -> error("Oneof members cannot be repeated: ${field.name}")
        }
    }

    /**
     * Generates the clear<Field>() functions that are defined by the compiler plugin generated
     * Builder interface for fields that have a presence Idx (and therefore are presence tracked).
     *
     * The function access uses the delegate `clearField()` method to unset the value and clear the
     * presence bit in the presence mask of the internal message.
     */
    private fun CodeGenerator.generateInternalFieldClearFunction(field: FieldDeclaration) {
        // if the field must always be present, we don't have a clear function
        if (field.presenceIdx == null || field.isPartOfMapEntry) return

        val oneOf = field.containingOneOf.value

        function(
            name = "clear${field.rawName.capitalize()}",
            modifiers = "override",
            returnType = "".scoped(),
        ) {
            if (oneOf != null) {
                // clearing a oneof member only has an effect if it is the active case
                code(field.presenceIdxFieldName.wrapIn { "if (presenceMask[$it]) ${oneOf.internalClearFunctionName}()" })
            } else {
                code("${field.internalDelegateName}.clearField(this)".scoped())
            }
        }
    }

    private fun CodeGenerator.generateCompanionObject(declaration: MessageDeclaration) {
        clazz(
            name = "",
            modifiers = "companion",
            annotations = listOf(FqName.Annotations.InternalRpcApi.scopedAnnotation()),
            declarationType = CodeGenerator.DeclarationType.Object
        ) {
            generateMessageDefault(declaration)
        }
    }

    private fun CodeGenerator.generateHashCode(declaration: MessageDeclaration) {
        val fields = declaration.actualFields
        function(
            name = "hashCode",
            modifiers = "override",
            returnType = FqName.Implicits.Int.scoped(),
        ) {
            if (declaration.hasRequiredFieldsRecursively) {
                code("%F()".scoped(declaration.name.topLevelFP("checkRequiredFields")))
            }

            var isFirst = true
            fun prefix(): String = if (isFirst) "var result =" else "result = 31 * result +"

            val emittedOneOfs = mutableSetOf<OneOfDeclaration>()
            fields.forEach { field ->
                val oneOf = field.containingOneOf.value
                if (oneOf == null) {
                    code(field.hashExprForHashCode().wrapIn { "${prefix()} $it" })
                } else if (emittedOneOfs.add(oneOf)) {
                    // a oneof contributes `fieldNumber * 31 + valueHash` of the active member, or 0 if not set
                    whenBlock(prefix = prefix().scoped()) {
                        oneOf.variants.forEach { variant ->
                            code(
                                variant.presenceIdxFieldName.merge(variant.rawHashExpr()) { idx, hash ->
                                    "presenceMask[$idx] -> ${variant.number} * 31 + $hash"
                                }
                            )
                        }
                        code("else -> 0".scoped())
                    }
                } else {
                    return@forEach
                }
                isFirst = false
            }

            if (isFirst) {
                code("var result = this::class.hashCode()".scoped())
            }

            if (declaration.hasExtensionRange) {
                code("result = 31 * result + extensionsHashCode()".scoped())
            }

            code("return result".scoped())
        }
    }

    /**
     * Hash of the field value without any presence check. Floats and doubles hash their bits.
     */
    private fun FieldDeclaration.rawHashExpr(): ScopedFormattedString {
        val thisField = "this.$name"
        return when (type) {
            FieldType.IntegralType.FLOAT, FieldType.IntegralType.DOUBLE -> "$thisField.toBits().hashCode()"
            else -> "$thisField.hashCode()"
        }.scoped()
    }

    private fun FieldDeclaration.hashExprForHashCode(): ScopedFormattedString {
        val hashCodeExpr = rawHashExpr()
        return if (presenceIdx != null) {
            presenceIdxFieldName.merge(hashCodeExpr) { index, hash -> "if (presenceMask[$index]) $hash else 0" }
        } else {
            hashCodeExpr
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
            if (declaration.hasRequiredFieldsRecursively) {
                code("%F()".scoped(declaration.name.topLevelFP("checkRequiredFields")))
            }
            code("if (this === other) return true".scoped())
            code("if (other == null || this::class != other::class) return false".scoped())
            code("other as %T".scoped(declaration.internalClassName))
            if (declaration.hasRequiredFieldsRecursively) {
                code("other.%F()".scoped(declaration.name.topLevelFP("checkRequiredFields")))
            }
            var generatedFinalReturn = false

            if (declaration.presenceMaskSize != 0) {
                val useFinalReturn = fields.isEmpty() && !declaration.hasExtensionRange
                generatedFinalReturn = useFinalReturn

                if (useFinalReturn) {
                    code("return presenceMask == other.presenceMask".scoped())
                } else {
                    code("if (presenceMask != other.presenceMask) return false".scoped())
                }
            }

            if (fields.isNotEmpty()) {
                fields.forEachIndexed { i, field ->
                    val useFinalReturn = i == fields.lastIndex && !declaration.hasExtensionRange
                    generatedFinalReturn = useFinalReturn

                    if (field.presenceIdx != null) {
                        fieldEqualsCheck(
                            presenceCheck = if (useFinalReturn) {
                                field.presenceIdxFieldName.wrapIn { "!presenceMask[$it] || " }
                            } else {
                                field.presenceIdxFieldName.wrapIn { "presenceMask[$it] && " }
                            },
                            field = field,
                            useFinalReturn = useFinalReturn,
                        )
                    } else {
                        fieldEqualsCheck(
                            presenceCheck = "".scoped(),
                            field = field,
                            useFinalReturn = useFinalReturn,
                        )
                    }
                }
            }

            if (declaration.hasExtensionRange) {
                generatedFinalReturn = true

                code("return extensionsEqual(other)".scoped())
            }

            if (!generatedFinalReturn) {
                code("return true".scoped())
            }
        }
    }

    private fun CodeGenerator.fieldEqualsCheck(
        presenceCheck: ScopedFormattedString,
        field: FieldDeclaration,
        useFinalReturn: Boolean,
    ) {
        val default: CodeGenerator.() -> Unit = {
            if (useFinalReturn) {
                code(
                    presenceCheck.wrapIn { presenceCheck ->
                        "return ${presenceCheck}this.${field.name} == other.${field.name}"
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

        when (val t = field.type) {
            is FieldType.IntegralType -> {
                when (t) {
                    FieldType.IntegralType.FLOAT -> {
                        if (useFinalReturn) {
                            code(
                                presenceCheck.wrapIn { presenceCheck ->
                                    "return ${presenceCheck}this.${field.name}.toBits() == other.${field.name}.toBits()"
                                }
                            )
                        } else {
                            code(
                                presenceCheck.wrapIn { presenceCheck ->
                                    "if (${presenceCheck}this.${field.name}.toBits() != other.${field.name}.toBits()) return false"
                                }
                            )
                        }
                    }

                    FieldType.IntegralType.DOUBLE -> {
                        if (useFinalReturn) {
                            code(
                                presenceCheck.wrapIn { presenceCheck ->
                                    "return ${presenceCheck}this.${field.name}.toBits() == other.${field.name}.toBits()"
                                }
                            )
                        } else {
                            code(
                                presenceCheck.wrapIn { presenceCheck ->
                                    "if (${presenceCheck}this.${field.name}.toBits() != other.${field.name}.toBits()) return false"
                                }
                            )
                        }
                    }

                    else -> {
                        default()
                    }
                }
            }

            is FieldType.Message,
            is FieldType.Enum,
            is FieldType.List,
            is FieldType.Map,
                -> {
                default()
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
            code("val indentString = \" \".repeat(indent)".scoped())
            if (declaration.hasExtensionRange || declaration.actualFields.isNotEmpty()) {
                code("val nextIndentString = \" \".repeat(indent + ${config.indentSize})".scoped())
            }

            code("val builder = StringBuilder()".scoped())
            code("builder.appendLine(\"%T(\")".scoped(declaration.name))
            declaration.actualFields.forEach { field ->
                val suffix = when (val type = field.type) {
                    FieldType.IntegralType.BYTES -> {
                        ".%F()".scoped(FqName.TopLevelFP.protoToString)
                    }

                    is FieldType.Message -> {
                        ".asInternal().asString(indent = indent + ${config.indentSize})"
                            .scoped(type.dec.value.name.topLevelFP("asInternal"))
                    }

                    else -> {
                        ScopedFormattedString.empty
                    }
                }

                val valueBuilder: CodeGenerator.() -> Unit = {
                    val line = suffix.wrapIn { suffix ->
                        $$"builder.appendLine(\"${nextIndentString}$${field.name}=${this.$${field.name}$$suffix},\")"
                    }

                    code(line)
                }

                if (field.isPartOfOneof) {
                    // only the active member of a oneof is printed
                    ifBranch(condition = field.presenceIdxFieldName.wrapIn { "presenceMask[$it]" }, ifBlock = {
                        valueBuilder()
                    })
                } else if (field.presenceIdx != null) {
                    ifBranch(condition = field.presenceIdxFieldName.wrapIn { "presenceMask[$it]" }, ifBlock = {
                        valueBuilder()
                    }) {
                        code($$"builder.appendLine(\"${nextIndentString}$${field.name}=<unset>,\")".scoped())
                    }
                } else {
                    valueBuilder()
                }
            }
            if (declaration.hasExtensionRange) {
                code("builder.appendExtensions(nextIndentString)".scoped())
            }
            code($$"builder.append(\"${indentString})\")".scoped())
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
            code(
                "return this.%F().copyInternal(body)"
                    .scoped(declaration.name.topLevelFP("asInternal"))
            )
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
                    ifBranch(condition = field.presenceIdxFieldName.wrapIn { "presenceMask[$it]" }, ifBlock = {
                        code(
                            field.type.copyCall(field.name.scoped()).wrapIn { copyCall ->
                                "copy.${field.name} = this.$copyCall"
                            }
                        )
                    })
                } else {
                    // by default, we copy the field value
                    code(
                        field.type.copyCall(field.name.scoped()).wrapIn { copyCall ->
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

    private fun FieldType.copyCall(varName: ScopedFormattedString): ScopedFormattedString {
        return when (this) {
            is FieldType.IntegralType -> varName
            is FieldType.Enum -> varName
            is FieldType.List -> varName.merge(value.copyCall("it".scoped())) { varName, copyCall ->
                "$varName.map { $copyCall }"
            }

            is FieldType.Map -> varName.merge(entry.value.copyCall("it.value".scoped())) { varName, copyCall ->
                "$varName.mapValues { $copyCall }"
            }

            is FieldType.Message -> {
                val copy = dec.value.name.topLevelFP("copy")
                varName.merge(copy.scoped()) { varName, copy -> "$varName.$copy()" }
            }
        }
    }

    private fun CodeGenerator.generatePresenceInterface(declaration: MessageDeclaration) {
        if (!declaration.isUserFacing) return
        // we must generate the interface if any sub message contains presence fields.
        // this is because nested messages are inner-interfaces of the outer one.
        if (!(declaration.hasPresenceFieldsRecursive())) return

        val comment = if (declaration.hasPresenceFields) {
            val nameRef = declaration.name.scoped()
            Comment(
                leadingDetached = emptyList(),
                leading = listOf(
                    nameRef.wrapIn { "Interface providing field-presence information for [$it] messages." },
                    nameRef.wrapIn { "Retrieve it via the [$it.presence] extension property." },
                ),
                trailing = emptyList(),
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
            value = "this.%F()._presence".scoped(declaration.name.topLevelFP("asInternal")),
            comment = Comment.leading(
                declaration.name.scoped().wrapIn {
                    "Returns the field-presence view for this [$it] instance."
                }
            )
        )
    }

    private fun CodeGenerator.generatePublicOrNullFieldGetters(declaration: MessageDeclaration) {
        declaration.actualFields
            .filter { it.hasOrNullGetter }
            .forEach { field ->
                property(
                    name = "${field.rawName}OrNull",
                    type = field.typeFqName().wrapIn { "$it?" },
                    propertyInitializer = CodeGenerator.PropertyInitializer.GETTER,
                    contextReceiver = declaration.name.scoped(),
                    value = "if (this.presence.${field.presenceGetterName}) this.${field.name} else null".scoped(),
                    comment = Comment.leading(
                        """
                            Returns the value of the `${field.rawName}` field if present, otherwise null.
                        """.trimIndent()
                    )
                )
            }
    }

    private fun CodeGenerator.generateProtoExtensionProperty(declaration: FieldDeclaration) {
        val name = declaration.name
        val extendee = declaration.containingType.value
        val descriptorRef = requireNotNull(declaration.extensionDescriptorName) {
            "Missing extension descriptor name for ${declaration.name}"
        }
        val asInternal = extendee.name.topLevelFP("asInternal")

        val value = "%F().getExtensionValue(%T) ?: %T.defaultValue.value"
            .scoped(asInternal, descriptorRef, descriptorRef)

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
            name = declaration.presenceGetterName,
            contextReceiver = extendee.presenceInterfaceName.scoped(),
            type = FqName.Implicits.Boolean.scoped(),
            propertyInitializer = CodeGenerator.PropertyInitializer.GETTER,
            value = "(this as %T).hasExtension(%T)".scoped(
                FqName.RpcClasses.InternalPresenceObject,
                descriptorRef
            )
        )

        // val MyMessage.Builder.myExtensionField: Boolean
        //      get()      = // ...
        //      set(value) = // ...
        property(
            name = name,
            contextReceiver = extendee.builderClassName.scoped(),
            type = declaration.typeFqName(),
            propertyInitializer = CodeGenerator.PropertyInitializer.GETTER,
            isVar = true,
            value = value,
            setter = "%F().setExtensionValue(%T, value)".scoped(asInternal, descriptorRef)
        )

        // fun MyMessage.Builder.clearMyExtensionField { /* ... */ }
        function(
            name = declaration.clearFunctionName,
            contextReceiver = extendee.builderClassName.scoped(),
            returnType = "".scoped()
        ) {
            code("%F().setExtensionValue(%T, null)".scoped(asInternal, descriptorRef))
        }
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
            value = "object : %T, %T".scoped(
                declaration.presenceInterfaceName,
                FqName.RpcClasses.InternalPresenceObject
            ),
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
                        name = field.presenceGetterName,
                        modifiers = "override",
                        type = FqName.Implicits.Boolean.scoped(),
                        propertyInitializer = CodeGenerator.PropertyInitializer.GETTER,
                        value = field.presenceIdxFieldName.wrapIn { "presenceMask[$it]" },
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
            modifiers = "internal",
            declarationType = CodeGenerator.DeclarationType.Object,
            annotations = listOf(FqName.Annotations.InternalRpcApi.scopedAnnotation()),
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
                        "%T($hexBytes)".scoped(FqName.KotlinLibs.ByteString)
                    } else {
                        FieldType.IntegralType.BYTES.defaultValue
                    }
                } else {
                    FieldType.IntegralType.BYTES.defaultValue
                }

                property(
                    name = field.name,
                    value = value,
                    type = FqName.KotlinLibs.ByteString.scoped(),
                    needsNewLineAfterDeclaration = i == fieldDeclarations.lastIndex,
                )
            }
        }
    }

    private fun CodeGenerator.generateMessageDefault(declaration: MessageDeclaration) {
        if (!declaration.isUserFacing) return

        property(
            name = declaration.defaultObjectRef.simpleName,
            type = declaration.name.scoped(),
            propertyInitializer = CodeGenerator.PropertyInitializer.DELEGATE,
            value = "lazy { %T() }".scoped(declaration.internalClassName),
        )
    }

    private fun CodeGenerator.generateMarshallerObject(declaration: MessageDeclaration) {
        if (!declaration.isUserFacing) return

        clazz(
            name = declaration.marshallerObjectName.simpleName,
            annotations = listOf(FqName.Annotations.InternalRpcApi.scopedAnnotation()),
            declarationType = CodeGenerator.DeclarationType.Object,
            superTypes = listOf("%T<%T>".scoped(FqName.RpcClasses.GrpcMarshaller, declaration.name)),
        ) {
            function(
                name = "encode",
                modifiers = "override",
                args = "value: %T, config: %T?".scoped(declaration.name, FqName.RpcClasses.GrpcMarshallerConfig),
                returnType = FqName.KotlinLibs.Source.scoped(),
            ) {
                code("val buffer = %T()".scoped(FqName.KotlinLibs.Buffer))
                code("val encoder = %T(buffer)".scoped(FqName.RpcClasses.WireEncoder))
                code("val internalMsg = value.%F()".scoped(declaration.name.topLevelFP("asInternal")))
                scope(FqName.TopLevelFP.checkForPlatformEncodeException.scoped(), nlAfterClosed = false) {
                    code(
                        "internalMsg.%F(encoder, config as? %T)"
                            .scoped(declaration.name.topLevelFP("encodeWith"), FqName.RpcClasses.ProtoConfig)
                    )
                }
                code("encoder.flush()".scoped())
                code("return buffer".scoped())
            }

            function(
                name = "decode",
                modifiers = "override",
                args = "source: %T, config: %T?".scoped(
                    FqName.KotlinLibs.Source,
                    FqName.RpcClasses.GrpcMarshallerConfig
                ),
                returnType = declaration.name.scoped(),
            ) {
                scope("%T(source).use".scoped(FqName.RpcClasses.WireDecoder)) {
                    code(
                        "(config as? %T)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }"
                            .scoped(FqName.RpcClasses.ProtoConfig)
                    )
                    code("val msg = %T()".scoped(declaration.internalClassName))
                    scope(FqName.TopLevelFP.checkForPlatformDecodeException.scoped(), nlAfterClosed = false) {
                        // if the message declaration is a group, we must pass null as the
                        // startGroup tag to indicate that this message is decoded as standalone (like a normal message)
                        val groupExtraArg = if (declaration.isGroup) ", null" else ""
                        code(
                            "%T.%F(msg, it, config as? %T$groupExtraArg)".scoped(
                                declaration.internalClassName,
                                declaration.name.topLevelFP("decodeWith"),
                                FqName.RpcClasses.ProtoConfig,
                            )
                        )
                    }
                    if (declaration.hasRequiredFieldsRecursively) {
                        code("msg.%F()".scoped(declaration.name.topLevelFP("checkRequiredFields")))
                    }
                    code("return msg".scoped())
                }
            }
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
            val suffix = if (declaration.hasRequiredFieldsRecursively) {
                ".apply(%T::%F)".scoped(
                    declaration.internalClassName,
                    declaration.name.topLevelFP("checkRequiredFields")
                )
            } else {
                ScopedFormattedString.empty
            }

            code(
                "return %T().apply(body)"
                    .scoped(declaration.internalClassName)
                    .merge(suffix) { result, suffix ->
                        "$result$suffix"
                    }
            )
        }
    }

    private fun CodeGenerator.generateMessageDecoder(declaration: MessageDeclaration) {
        var args = "msg: %T, decoder: %T, config: %T?"
            .scoped(declaration.internalClassName, FqName.RpcClasses.WireDecoder, FqName.RpcClasses.ProtoConfig)

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
            if (declaration.hasExtensionRange) {
                code(
                    "val knownExtensions = config?.extensionRegistry?.getAllExtensionsForMessage(%T::class) ?: emptyMap()"
                        .scoped(declaration.name)
                )
            }
            whileBlock("true".scoped()) {
                if (declaration.isGroup) {
                    scope("val tag = decoder.readTag() ?: run".scoped()) {
                        // if the startGroup tag is set, we decode the message as an inline group message,
                        // so in case of the tag being null, we know that the payload is malformed
                        scope("startGroup?.let".scoped()) {
                            code(
                                $$"throw %T(\"Missing END_GROUP tag for field: ${startGroup.fieldNr}.\")"
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
                                $$"throw %T(\"Wrong END_GROUP tag. Expected ${startGroup?.fieldNr}, got ${tag.fieldNr}.\")"
                                    .scoped(FqName.RpcClasses.ProtobufDecodingException)
                            )
                        })
                        code("return".scoped())
                    })
                } else {
                    code("val tag = decoder.readTag() ?: break // EOF, we read the whole message".scoped())
                }

                whenBlock("tag.fieldNr".scoped()) {
                    declaration.actualFields.forEach { field -> readMatchCase(field) }
                    whenCase("else".scoped()) {
                        // check if it is an extension
                        if (declaration.hasExtensionRange) {
                            code("val extension = knownExtensions[tag.fieldNr] as? %T".scoped(FqName.RpcClasses.InternalExtensionDescriptor))
                            ifBranch(
                                condition = "extension != null && tag.wireType in extension.acceptedWireTypes".scoped(),
                                ifBlock = {
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

            if (!declaration.isGroup) {
                // we must flush the encoder and "delete" it
                code("msg._unknownFieldsEncoder?.flush()".scoped())
                code("msg._unknownFieldsEncoder = null".scoped())
            }

            // TODO: Make lists and maps immutable (KRPC-190)
        }
    }

    private fun CodeGenerator.readMatchCase(field: FieldDeclaration) {
        val lvalue = "msg.${field.name}".scoped()

        fun CodeGenerator.repeatedDecodeTarget(): ScopedFormattedString {
            code(
                "val target = msg.${field.internalDelegateName}.getOrCreate(msg) { mutableListOf() } as %T"
                    .scoped(FqName.Implicits.MutableList)
            )
            return "target".scoped()
        }

        fun CodeGenerator.mapDecodeTarget(): ScopedFormattedString {
            code(
                "val target = msg.${field.internalDelegateName}.getOrCreate(msg) { mutableMapOf() } as %T"
                    .scoped(FqName.Implicits.MutableMap)
            )
            return "target".scoped()
        }

        fun CodeGenerator.messageDecodeTarget(message: MessageDeclaration): ScopedFormattedString {
            if (field.isPartOfOneof) {
                // merge into the existing message if this member is the active case,
                // otherwise a new instance becomes the active case (last field wins across cases)
                code(
                    field.presenceIdxFieldName.merge(
                        other = message.name.topLevelFP("asInternal").scoped(),
                        another = message.internalClassName.scoped(),
                    ) { idx, asInternal, internalClassName ->
                        "val target = if (msg.presenceMask[$idx]) msg.${field.name}.$asInternal() else $internalClassName().also { msg.${field.name} = it }"
                    }
                )
                return "target".scoped()
            }

            code(
                "val target = msg.${field.internalDelegateName}.getOrCreate(msg) { %T() }"
                    .scoped(message.internalClassName)
            )
            return "target".scoped()
        }

        when (val fieldType = field.type) {
            is FieldType.IntegralType -> whenCase(
                "${field.number} if tag.wireType == %T"
                    .scoped(FqName.RpcClasses.WireType.nested(fieldType.wireType.name))
            ) {
                generateDecodeFieldValue(fieldType, lvalue)
            }

            is FieldType.List -> {
                // Protocol buffer parsers must be able
                // to parse repeated fields that were compiled as packed as if they were not packed,
                // and vice versa.
                if (fieldType.value.isPackable) {
                    whenCase(
                        "${field.number} if tag.wireType == %T"
                            .scoped(FqName.RpcClasses.WireType_LENGTH_DELIMITED)
                    ) {
                        val target = repeatedDecodeTarget()
                        generateDecodeFieldValue(fieldType, target, isPacked = true)
                    }
                }

                whenCase(
                    "${field.number} if tag.wireType == %T"
                        .scoped(FqName.RpcClasses.WireType.nested(fieldType.value.wireType.name))
                ) {
                    val target = repeatedDecodeTarget()
                    generateDecodeFieldValue(fieldType, target, isPacked = false)
                }
            }

            is FieldType.Enum -> whenCase(
                "${field.number} if tag.wireType == %T"
                    .scoped(FqName.RpcClasses.WireType_VARINT)
            ) {
                generateDecodeFieldValue(fieldType, lvalue)
            }

            is FieldType.Message -> {
                whenCase(
                    "${field.number} if tag.wireType == %T"
                        .scoped(FqName.RpcClasses.WireType.nested(fieldType.wireType.name))
                ) {
                    val target = messageDecodeTarget(fieldType.dec.value)
                    generateDecodeFieldValue(fieldType, target)
                }
            }

            is FieldType.Map -> whenCase(
                "${field.number} if tag.wireType == %T"
                    .scoped(FqName.RpcClasses.WireType_LENGTH_DELIMITED)
            ) {
                val target = mapDecodeTarget()
                generateDecodeFieldValue(fieldType, target)
            }
        }
    }

    private fun CodeGenerator.generateDecodeFieldValue(
        fieldType: FieldType,
        lvalue: ScopedFormattedString,
        isPacked: Boolean = false,
    ) {
        fun emitAssignment(raw: ScopedFormattedString) {
            code(lvalue.merge(raw) { target, value -> "$target = $value" })
        }

        when (fieldType) {
            is FieldType.IntegralType -> {
                val raw = "decoder.read${fieldType.decodeEncodeFuncName()}()".scoped()
                emitAssignment(raw)
            }

            is FieldType.List -> if (isPacked) {
                val elementType = fieldType.value
                val conversion = if (elementType is FieldType.Enum) {
                    ".map { %T.%F(it) }".scoped(elementType.dec.value.name, elementType.dec.value.name.topLevelFP("fromNumber"))
                } else {
                    "".scoped()
                }

                // Note that although there’s usually no reason
                // to encode more than one key-value pair for a packed repeated field,
                // parsers must be prepared to accept multiple key-value pairs.
                // In this case, the payloads should be concatenated.
                code(
                    lvalue.merge(conversion) { target, packedConversion ->
                        "$target.addAll(decoder.readPacked${elementType.decodeEncodeFuncName()}()$packedConversion)"
                    }
                )
            } else {
                when (val elemType = fieldType.value) {
                    is FieldType.Message -> {
                        code("val elem = %T()".scoped(elemType.dec.value.internalClassName))
                        generateDecodeFieldValue(fieldType.value, "elem".scoped())
                    }

                    else -> generateDecodeFieldValue(fieldType.value, "val elem".scoped())
                }
                code(lvalue.wrapIn { target -> "$target.add(elem)" })
            }

            is FieldType.Enum -> {
                val raw = "%T.%F(decoder.read${fieldType.decodeEncodeFuncName()}())"
                    .scoped(fieldType.dec.value.name, fieldType.dec.value.name.topLevelFP("fromNumber"))

                emitAssignment(raw)
            }

            is FieldType.Message -> {
                val msg = fieldType.dec.value
                val asInternal: ScopedFormattedString = msg.name.topLevelFP("asInternal").scoped()
                val decodeWith: ScopedFormattedString = msg.name.topLevelFP("decodeWith").scoped()

                if (msg.isGroup) {
                    code(
                        lvalue.merge(
                            other = asInternal,
                            another = msg.internalClassName.scoped(),
                            anotherOne = decodeWith,
                        ) { target, asInternal, internalClassName, decodeWith ->
                            "decoder.readGroup($target.$asInternal()) { msg, decoder -> $internalClassName.$decodeWith(msg, decoder, config, tag) }"
                        }
                    )
                } else {
                    code(
                        lvalue.merge(
                            other = asInternal,
                            another = msg.internalClassName.scoped(),
                            anotherOne = decodeWith,
                        ) { target, asInternal, internalClassName, decodeWith ->
                            "decoder.readMessage($target.$asInternal()) { msg, decoder -> $internalClassName.$decodeWith(msg, decoder, config) }"
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
                    )
                    code(lvalue.wrapIn { target -> "$target[key] = value" })
                }
            }
        }
    }

    private fun CodeGenerator.generateMessageEncoder(declaration: MessageDeclaration) = function(
        name = "encodeWith",
        annotations = listOf(FqName.Annotations.InternalRpcApi.scopedAnnotation()),
        args = "encoder: %T, config: %T?".scoped(FqName.RpcClasses.WireEncoder, FqName.RpcClasses.ProtoConfig),
        contextReceiver = declaration.internalClassName.scoped(),
        returnType = FqName.Implicits.Unit.scoped(),
    ) {
        if (declaration.actualFields.isEmpty()) {
            code("// no fields to encode".scoped())
        } else {
            declaration.actualFields.forEach { field ->
                if (field.presenceIdx != null) {
                    ifBranch(condition = field.presenceIdxFieldName.wrapIn { "presenceMask[$it]" }, ifBlock = {
                        generateEncodeFieldValue(field, "this.${field.name}".scoped())
                    })
                } else {
                    ifBranch(condition = field.notDefaultCheck(declaration), ifBlock = {
                        generateEncodeFieldValue(field, "this.${field.name}".scoped())
                    })
                }
            }
        }

        // encode all extension values
        scope("_extensions.forEach".scoped(), paramDecl = "(key, value) ->".scoped()) {
            scope("value.descriptor.let".scoped(), paramDecl = "descriptor ->".scoped()) {
                code("descriptor.encode(encoder, key, descriptor.valueType.%F(value.value), config)".scoped(FqName.TopLevelFP.cast))
            }
        }

        // encode all unknown fields
        code("encoder.writeRawBytes(_unknownFields)".scoped())

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
                val asInternal = type.dec.value.name.topLevelFP("asInternal")
                val encodeWith = type.dec.value.name.topLevelFP("encodeWith")
                code(
                    valueVar.merge(asInternal.scoped(), encodeWith.scoped()) { valueVar, asInternal, encodeWith ->
                        "encoder.$writeMethod(fieldNr = $number, value = $valueVar.$asInternal()) { encoder -> $encodeWith(encoder, config) }"
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
        declaration.requiredFields.forEach { field ->
            ifBranch(condition = field.presenceIdxFieldName.wrapIn { "!presenceMask[$it]" }, ifBlock = {
                code(
                    "throw %T.missingRequiredField(\"${declaration.name.simpleName}\", \"${field.name}\")"
                        .scoped(FqName.RpcClasses.ProtobufException)
                )
            })
        }

        // check submessages
        declaration.messageFields.forEach { field ->
            if (field.type.hasRequiredFields) {
                ifBranch(condition = "presenceMask[${field.presenceIdx}]".scoped(), ifBlock = {
                    val fieldMessageName = (field.type as FieldType.Message).dec.value.name
                    code(
                        "this.${field.name}.%F().%F()"
                            .scoped(
                                fieldMessageName.topLevelFP("asInternal"),
                                fieldMessageName.topLevelFP("checkRequiredFields"),
                            )
                    )
                })
            }
        }

        // check submessages in lists
        declaration.listFields.forEach { field ->
            val listType = field.type as FieldType.List
            if (listType.value !is FieldType.Message) return@forEach

            if (listType.value.hasRequiredFields) {
                scope("this.${field.name}.forEach".scoped()) {
                    val listTypeName = (listType.value as FieldType.Message).dec.value.name
                    code(
                        "it.%F().%F()"
                            .scoped(
                                listTypeName.topLevelFP("asInternal"),
                                listTypeName.topLevelFP("checkRequiredFields"),
                            )
                    )
                }
            }
        }

        // check submessage in maps
        declaration.mapFields.forEach { field ->
            val mapType = field.type as FieldType.Map
            // we only have to check the value, as the key cannot be a message
            if (mapType.entry.value !is FieldType.Message) return@forEach

            if (mapType.entry.value.hasRequiredFields) {
                scope("this.${field.name}.values.forEach".scoped()) {
                    val mapEntryTypeName = (mapType.entry.value as FieldType.Message).dec.value.name

                    code(
                        "it.%F().%F()"
                            .scoped(
                                mapEntryTypeName.topLevelFP("asInternal"),
                                mapEntryTypeName.topLevelFP("checkRequiredFields"),
                            )
                    )
                }
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
                if (field.presenceIdx == null) {
                    scope(field.notDefaultCheck(declaration).wrapIn { "if ($it)" }) {
                        generateFieldComputeSizeCall(field, fieldName.scoped(), noResultUpdate = false)
                    }
                } else {
                    scope(field.presenceIdxFieldName.wrapIn { "if (presenceMask[$it])" }) {
                        generateFieldComputeSizeCall(field, fieldName.scoped(), noResultUpdate = false)
                    }
                }
            }

            if (declaration.hasExtensionRange) {
                code("__result += extensionsSize()".scoped())
            }

            code("__result += _unknownFields.size.toInt()".scoped())

            code("return __result".scoped())
        }
    }

    private fun CodeGenerator.generateInternalCastExtension(declaration: MessageDeclaration) {
        val ctxReceiver = if (declaration.isUserFacing) declaration.name else declaration.internalClassName

        // we generate the asInternal extension even for non-user-facing message classes (map entry)
        // to avoid edge-cases when generating other code that uses the asInternal() extension.
        function(
            name = "asInternal",
            annotations = listOf(FqName.Annotations.InternalRpcApi.scopedAnnotation()),
            contextReceiver = ctxReceiver.scoped(),
            returnType = declaration.internalClassName.scoped(),
        ) {
            if (!declaration.isUserFacing) {
                code("return this".scoped())
            } else {
                code(
                    $$"return this as? %T ?: error(\"Message ${this::class.simpleName} is a non-internal message type.\")"
                        .scoped(declaration.internalClassName)
                )
            }
        }
    }

    private fun CodeGenerator.generateFieldComputeSizeCall(
        field: FieldDeclaration,
        variable: ScopedFormattedString,
        noResultUpdate: Boolean,
    ) {
        val resultPrefix = if (noResultUpdate) "" else "__result += "
        val valueSize by lazy { field.type.valueSizeCall(variable, field.number, field.dec.isPacked) }
        val tagSize = tagSizeCall(field.number, field.type.wireType)

        when (val fieldType = field.type) {
            is FieldType.List -> when {
                // packed fields also have the tag + len
                field.dec.isPacked -> {
                    code(
                        valueSize.merge(tagSize, int32SizeCall("it".scoped())) { valueSize, tagSize, int32SizeCall ->
                            "$resultPrefix$valueSize.let { $tagSize + $int32SizeCall + it }"
                        }
                    )
                }

                else -> {
                    code(valueSize.wrapIn { valueSize -> "$resultPrefix$valueSize" })
                }
            }

            is FieldType.Message -> if (fieldType.dec.value.isGroup) {
                val groupTagSize = tagSizeCall(field.number, WireType.START_GROUP)
                // the group message size is the size of the message plus the start and end group tags
                code(
                    valueSize.merge(groupTagSize) { valueSize, groupTagSize ->
                        "$resultPrefix$valueSize.let { (2 * $groupTagSize) + it }"
                    }
                )
            } else {
                code(
                    valueSize.merge(tagSize, int32SizeCall("it".scoped())) { valueSize, tagSize, int32SizeCall ->
                        "$resultPrefix$valueSize.let { $tagSize + $int32SizeCall + it }"
                    }
                )
            }

            FieldType.IntegralType.STRING, FieldType.IntegralType.BYTES -> {
                code(
                    valueSize.merge(tagSize, int32SizeCall("it".scoped())) { valueSize, tagSize, int32SizeCall ->
                        "$resultPrefix$valueSize.let { $tagSize + $int32SizeCall + it }"
                    }
                )
            }

            is FieldType.Map -> {
                scope(variable.wrapIn { "$resultPrefix$it.entries.sumOf" }, paramDecl = "kEntry ->".scoped()) {
                    generateMapConstruction(field.type as FieldType.Map, "kEntry.key".scoped(), "kEntry.value".scoped())
                    code(
                        tagSize.merge(int32SizeCall("it".scoped())) { tagSize, int32SizeCall ->
                            "._size.let { $tagSize + $int32SizeCall + it }"
                        }
                    )
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
                        "$resultPrefix$tagSize + $valueSize"
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
        val sizeFuncFqName = sizeFuncName?.let { FqName.RpcClasses.WireSize.topLevelFP(it) }

        val convertedVariable = if (isPacked && this is FieldType.List && value is FieldType.Enum) {
            variable.wrapIn { variable -> "$variable.map { it.number }" }
        } else {
            variable
        }

        fun sizeFunc() = FqName.RpcClasses.WireSize.scoped()
            .merge(sizeFuncFqName!!.scoped(), convertedVariable) { wireSize, sizeFuncName, convertedVariable ->
                "$wireSize.$sizeFuncName($convertedVariable)"
            }

        return when (this) {
            is FieldType.IntegralType -> sizeFunc()

            is FieldType.List -> when {
                isPacked -> sizeFunc()

                else -> {
                    // calculate the size of the values within the list.
                    val valueSize = value.valueSizeCall("element".scoped(), number)
                    val tagSize = tagSizeCall(number, value.wireType)

                    val elementSize = when (value.wireType) {
                        WireType.LENGTH_DELIMITED -> {
                            valueSize.merge(
                                other = tagSize,
                                another = int32SizeCall("it".scoped()),
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
                        "$variable.sumOf { element -> $elementSize }"
                    }
                }
            }

            is FieldType.Enum -> {
                FqName.RpcClasses.WireSize.scoped()
                    .merge(sizeFuncFqName!!.scoped(), variable) { wireSize, sizeFuncName, variable ->
                        "$wireSize.$sizeFuncName($variable.number)"
                    }
            }

            is FieldType.Message -> variable.merge(
                dec.value.name.topLevelFP("asInternal").scoped()
            ) { variable, asInternal ->
                "$variable.$asInternal()._size"
            }

            is FieldType.Map -> error("Map fields have no direct valueSizeCall")
        }
    }

    private fun tagSizeCall(number: Int, wireType: WireType): ScopedFormattedString {
        return "%T.%F($number, %T)".scoped(
            FqName.RpcClasses.WireSize,
            FqName.RpcClasses.WireSize.topLevelFP("tag"),
            FqName.RpcClasses.WireType.nested(wireType.name)
        )
    }

    @Suppress("SameParameterValue")
    private fun int32SizeCall(number: ScopedFormattedString): ScopedFormattedString {
        return FqName.RpcClasses.WireSize.scoped()
            .merge(
                other = FqName.RpcClasses.WireSize.topLevelFP("int32").scoped(),
                another = number,
            ) { wireSize, int32, number ->
                "$wireSize.$int32($number)"
            }
    }

    private fun FieldDeclaration.notDefaultCheck(parent: MessageDeclaration): ScopedFormattedString {
        return when (val fieldType = type) {
            is FieldType.IntegralType -> {
                val defaultValue = safeDefaultValue(parent)
                when (fieldType) {
                    FieldType.IntegralType.STRING if defaultValue == FieldType.IntegralType.STRING.defaultValue -> {
                        "this.$name.isNotEmpty()".scoped()
                    }

                    FieldType.IntegralType.STRING -> defaultValue.wrapIn { defaultValue ->
                        "!this.$name.contentEquals($defaultValue)"
                    }

                    FieldType.IntegralType.BYTES if defaultValue == FieldType.IntegralType.BYTES.defaultValue -> {
                        "this.$name.%F()".scoped(FqName.TopLevelFP.byteStringIsNotEmpty)
                    }

                    FieldType.IntegralType.BOOL -> {
                        if (defaultValue == FieldType.IntegralType.BOOL.defaultValue) { // "false"
                            "this.$name".scoped()
                        } else {
                            "!this.$name".scoped()
                        }
                    }

                    else -> defaultValue.wrapIn { defaultValue -> "this.$name != $defaultValue" }
                }
            }

            is FieldType.List, is FieldType.Map -> "this.$name.isNotEmpty()".scoped()

            is FieldType.Enum -> {
                safeDefaultValue(parent).wrapIn { safeDefaultValue -> "this.$name != $safeDefaultValue" }
            }

            is FieldType.Message -> error("Message fields should not be checked for default values.")
        }
    }

    private fun FieldDeclaration.safeDefaultValue(parent: MessageDeclaration): ScopedFormattedString {
        if (!dec.hasDefaultValue()) {
            return type.defaultValue
        }

        return when (val value = dec.defaultValue) {
            is String -> {
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

            // an explicit empty default is the type default; BytesDefaults only holds non-empty defaults
            is ByteString if value.isEmpty -> FieldType.IntegralType.BYTES.defaultValue

            is ByteString -> {
                "%T.$name".scoped(parent.bytesDefaultsName)
            }

            is Descriptors.EnumValueDescriptor -> {
                value.fqName().scoped()
            }

            is Int if (type == FieldType.IntegralType.UINT32 || type == FieldType.IntegralType.FIXED32) -> {
                (Integer.toUnsignedString(value) + "u").scoped()
            }

            is Long if (type == FieldType.IntegralType.UINT64 || type == FieldType.IntegralType.FIXED64) -> {
                (java.lang.Long.toUnsignedString(value) + "uL").scoped()
            }

            is Float -> when {
                value.isNaN() -> "%T.NaN".scoped(FqName.Implicits.Float)
                value == Float.POSITIVE_INFINITY -> "%T.POSITIVE_INFINITY".scoped(FqName.Implicits.Float)
                value == Float.NEGATIVE_INFINITY -> "%T.NEGATIVE_INFINITY".scoped(FqName.Implicits.Float)
                else -> FqName.Implicits.Float.scoped().wrapIn { float ->
                    val bits = java.lang.Float.floatToRawIntBits(value)
                    val toInt = if (bits < 0) ".toInt()" else ""
                    // using string concat, because otherwise, .format will fuck around with %T
                    float.toString() + ".fromBits(0x%08X$toInt)".format(bits)
                }
            }

            is Double -> when {
                value.isNaN() -> "%T.NaN".scoped(FqName.Implicits.Double)
                value == Double.POSITIVE_INFINITY -> "%T.POSITIVE_INFINITY".scoped(FqName.Implicits.Double)
                value == Double.NEGATIVE_INFINITY -> "%T.NEGATIVE_INFINITY".scoped(FqName.Implicits.Double)
                else -> FqName.Implicits.Double.scoped().wrapIn { double ->
                    // otherwise, `.format` will fuck around with %T
                    double.toString() + ".fromBits(0x%016XL)".format(java.lang.Double.doubleToRawLongBits(value))
                }
            }

            // Long.MIN_VALUE and Int.MIN_VALUE can't be expressed as literals directly
            // because the compiler parses the minus sign and the number separately.
            is Long if value == Long.MIN_VALUE -> "%T.MIN_VALUE".scoped(FqName.Implicits.Long)
            is Int if value == Int.MIN_VALUE -> "%T.MIN_VALUE".scoped(FqName.Implicits.Int)
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
        is FieldType.Message -> null
    }

    /**
     * Generates the top-level `<Message><OneOf>Case` enum class with one entry per member (declaration order)
     * and a final not-set entry.
     */
    private fun CodeGenerator.generateOneOfCaseEnum(declaration: MessageDeclaration, oneOf: OneOfDeclaration) {
        if (!declaration.isUserFacing) return

        val nameRef = declaration.name.scoped()
        val comment = Comment(
            leadingDetached = emptyList(),
            leading = listOfNotNull(
                nameRef.wrapIn { "Cases of the `${oneOf.dec.name}` oneof of [$it]." },
                nameRef.wrapIn { "Retrieve the active case via the [$it.${oneOf.name}] extension property." },
            ) + (oneOf.doc?.takeIf { !it.isEmpty() }?.let { doc ->
                buildList {
                    addAll(doc.leadingDetached)
                    addAll(doc.leading)
                    addAll(doc.trailing)
                }
            } ?: emptyList()),
            trailing = emptyList(),
        )

        clazz(
            name = oneOf.caseTypeName.simpleName,
            comment = comment,
            modifiers = "enum",
            declarationType = CodeGenerator.DeclarationType.Class,
        ) {
            oneOf.variants.forEach { variant ->
                appendComment(variant.doc)
                code("${oneOf.caseEntryName(variant)},".scoped())
            }
            code("${oneOf.notSetEntryName},".scoped())
        }
    }

    /**
     * Generates the oneof extensions: the `<oneOf>` case property, `Builder.clear<OneOf>()`
     * and, if enabled, the exhaustive `when<OneOf>` dispatch function.
     */
    private fun CodeGenerator.generateOneOfExtensions(declaration: MessageDeclaration) {
        if (!declaration.isUserFacing) return

        val asInternal = declaration.name.topLevelFP("asInternal")

        declaration.oneOfDeclarations.forEach { oneOf ->
            val caseType = oneOf.caseTypeName.scoped()

            // val Event.payload: EventPayloadCase get() = this.asInternal()._payloadCase
            property(
                name = oneOf.name,
                contextReceiver = declaration.name.scoped(),
                type = caseType,
                propertyInitializer = CodeGenerator.PropertyInitializer.GETTER,
                value = "this.%F().${oneOf.internalCaseGetterName}".scoped(asInternal),
                comment = Comment.leading(
                    caseType.wrapIn { "The active case of the `${oneOf.dec.name}` oneof, or [$it.${oneOf.notSetEntryName}]." }
                ),
            )

            // fun Event.Builder.clearPayload()
            function(
                name = oneOf.clearFunctionName,
                contextReceiver = declaration.builderClassName.scoped(),
                returnType = "".scoped(),
                comment = Comment.leading("Clears the `${oneOf.dec.name}` oneof regardless of its active case."),
            ) {
                code("this.%F().${oneOf.internalClearFunctionName}()".scoped(asInternal))
            }

            if (config.generateOneOfWhenFunctions) {
                generateOneOfWhenFunction(declaration, oneOf)
            }
        }
    }

    /**
     * ```kotlin
     * inline fun <R> Event.whenPayload(text: (String) -> R, ..., notSet: () -> R): R = when (payload) { ... }
     * ```
     * The function is inline, so no lambda objects are created and primitive parameters are not boxed.
     */
    private fun CodeGenerator.generateOneOfWhenFunction(declaration: MessageDeclaration, oneOf: OneOfDeclaration) {
        val caseType = oneOf.caseTypeName.scoped()

        val args = oneOf.variants
            .map { variant ->
                variant.typeFqName().wrapIn { type -> "${variant.name}: ($type) -> R" }
            }
            .plus("${oneOf.notSetParameterName}: () -> R".scoped())

        function(
            name = oneOf.whenFunctionName,
            modifiers = "inline",
            typeParameters = "R".scoped(),
            contextReceiver = declaration.name.scoped(),
            args = args,
            returnType = "R".scoped(),
            comment = Comment.leading(
                "Exhaustive, typed dispatch on the active case of the `${oneOf.dec.name}` oneof."
            ),
        ) {
            whenBlock(prefix = "return".scoped(), condition = "this.${oneOf.name}".scoped()) {
                oneOf.variants.forEach { variant ->
                    code(
                        caseType.wrapIn {
                            "$it.${oneOf.caseEntryName(variant)} -> ${variant.name}(this.${variant.name})"
                        }
                    )
                }
                code(caseType.wrapIn { "$it.${oneOf.notSetEntryName} -> ${oneOf.notSetParameterName}()" })
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

            // TODO KRPC-550: Avoid enum unrecognised name conflict
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
            type = "%T<%T,".scoped(
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
                "decodeValue" to "{ %T.%F(it) }".scoped(enumType, enumType.topLevelFP("fromNumber")),
                "defaultValue" to "{ %T }".scoped(defaultValue),
            ),
        )
    }

    private fun CodeGenerator.generateMessageExtensionDescriptor(
        field: FieldDeclaration,
        type: FieldType.Message,
    ) {
        val message = type.dec.value
        val asInternal = message.name.topLevelFP("asInternal")
        val encodeWith = message.name.topLevelFP("encodeWith")
        val decodeWith = message.name.topLevelFP("decodeWith")

        if (message.isGroup) {
            functionCall(
                function = "%T.group".scoped(FqName.RpcClasses.InternalExtensionDescriptor),
                namedArgs = field.baseExtensionDescriptorArgs() + listOf(
                    "valueType" to "%T::class".scoped(message.name),
                    "default" to "{ %T }".scoped(message.defaultObjectRef),
                    "asInternal" to "{ it.%F() }".scoped(asInternal),
                    "encodeWith" to "{ value, encoder, config -> value.%F().%F(encoder, config) }".scoped(
                        asInternal,
                        encodeWith
                    ),
                    "decodeWith" to "{ value, decoder, config, startGroup -> %T.%F(value.%F(), decoder, config, startGroup) }".scoped(
                        message.internalClassName,
                        decodeWith,
                        asInternal,
                    ),
                ),
            )
        } else {
            functionCall(
                function = "%T.message".scoped(FqName.RpcClasses.InternalExtensionDescriptor),
                namedArgs = field.baseExtensionDescriptorArgs() + listOf(
                    "valueType" to "%T::class".scoped(message.name),
                    "default" to "{ %T }".scoped(message.defaultObjectRef),
                    "asInternal" to "{ it.%F() }".scoped(asInternal),
                    "encodeWith" to "{ value, encoder, config -> value.%F().%F(encoder, config) }".scoped(
                        asInternal,
                        encodeWith
                    ),
                    "decodeWith" to "{ value, decoder, config -> %T.%F(value.%F(), decoder, config) }".scoped(
                        message.internalClassName,
                        decodeWith,
                        asInternal,
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

// we use double "__" to avoid conflicts with internal names that start with "_"
private val FieldDeclaration.internalDelegateName: String get() = "__${rawName}Delegate"

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

private fun String.capitalize(): String = replaceFirstChar { it.uppercase() }
