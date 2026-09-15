/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protoc.gen.core.model

import com.google.protobuf.ByteString
import com.google.protobuf.Descriptors
import kotlinx.rpc.protoc.gen.core.Comment
import kotlinx.rpc.protoc.gen.core.FqNameTable
import kotlinx.rpc.protoc.gen.core.ScopedFormattedString
import kotlinx.rpc.protoc.gen.core.scoped

data class Model(
    val files: List<FileDeclaration>,
    val nameTable: FqNameTable,
)

data class FileDeclaration(
    val name: String,
    val packageName: FqName.Package,
    val dependencies: List<FileDeclaration>,
    val messageDeclarations: List<MessageDeclaration>,
    val enumDeclarations: List<EnumDeclaration>,
    val serviceDeclarations: List<ServiceDeclaration>,
    val extensions: List<FieldDeclaration>,
    val doc: List<Comment>,
    val dec: Descriptors.FileDescriptor,
    val deprecated: Boolean,

    // the object that stores internal extension descriptors for
    // extensions registered in this proto file
    val internalExtensionDescriptorObject: FqName.Declaration,
)

data class MessageDeclaration(
    val name: FqName,
    val presenceMaskSize: Int,
    // all singular/repeated/map fields in declaration order, including the members of every oneof
    val actualFields: List<FieldDeclaration>,
    val oneOfDeclarations: List<OneOfDeclaration>,
    val enumDeclarations: List<EnumDeclaration>,
    val nestedDeclarations: List<MessageDeclaration>,
    val extensions: List<FieldDeclaration>,
    val doc: Comment?,
    val dec: Descriptors.Descriptor,
    val deprecated: Boolean,
    val parent: Lazy<MessageDeclaration?>,
) {
    val isMapEntry = dec.options.mapEntry
    val isUserFacing = !isMapEntry

    val isGroup by lazy {
        val parent = dec.containingType ?: return@lazy false // top-level types can’t be groups
        parent.fields.any { f ->
            f.type == Descriptors.FieldDescriptor.Type.GROUP && f.messageType == dec
        }
    }

    val requiredFields by lazy { actualFields.filter { it.dec.isRequired } }

    val messageFields by lazy { actualFields.filter { it.type is FieldType.Message } }
    val listFields by lazy { actualFields.filter { it.type is FieldType.List } }
    val mapFields by lazy { actualFields.filter { it.type is FieldType.Map } }

    private var hasRequiredFieldsRecursivelyCheckStarted = false
    val hasRequiredFieldsRecursively: Boolean by lazy {
        if (hasRequiredFieldsRecursivelyCheckStarted) {
            false
        } else {
            hasRequiredFieldsRecursivelyCheckStarted = true
            requiredFields.isNotEmpty()
                || messageFields.any { it.type.hasRequiredFields }
                || listFields.any { (it.type as FieldType.List).value.hasRequiredFields }
                || mapFields.any { (it.type as FieldType.Map).entry.value.hasRequiredFields }
        }
    }

    val hasPresenceFields by lazy { actualFields.any { it.presenceIdx != null } }

    val presenceInterfaceName: FqName.Declaration by lazy {
        parent.value?.presenceInterfaceName?.nested(name.simpleName)
            ?: name.suffixed("Presence")
    }

    val companionName: FqName.Declaration by lazy { name.nested("Companion") }

    val internalClassName: FqName.Declaration by lazy {
        parent.value?.internalClassName?.nested(name.simpleName)?.suffixed("Internal")
            ?: name.suffixed("Internal")
    }

    val builderClassName: FqName.Declaration by lazy {
        name.nested("Builder")
    }

    val internalCompanionName: FqName.Declaration by lazy { internalClassName.nested("Companion") }
    val presenceIndicesName: FqName.Declaration by lazy { internalClassName.nested("PresenceIndices") }
    val defaultObjectRef: FqName.Declaration by lazy { internalClassName.nested("DEFAULT") }
    val marshallerObjectName: FqName.Declaration by lazy { internalClassName.nested("MARSHALLER") }
    val bytesDefaultsName: FqName.Declaration by lazy { internalClassName.nested("BytesDefaults") }

    fun hasPresenceFieldsRecursive(): Boolean {
        return hasPresenceFields || hasExtensionRange || nestedDeclarations.any { it.isUserFacing && it.hasPresenceFieldsRecursive() }
    }

    fun hasEnums(): Boolean {
        return enumDeclarations.isNotEmpty() || nestedDeclarations.any { it.hasEnums() }
    }

    fun nonDefaultByteFields(): List<FieldDeclaration> {
        return actualFields
            .filter {
                it.type == FieldType.IntegralType.BYTES &&
                    it.dec.hasDefaultValue() &&
                    !(it.dec.defaultValue as ByteString).isEmpty
            }
    }

    val extensionRanges: List<IntRange> by lazy {
        dec.toProto().extensionRangeList.map { it.start..it.end }
    }

    val hasExtensionRange: Boolean by lazy {
        extensionRanges.isNotEmpty()
    }

}

val FieldType.hasRequiredFields: Boolean
    get() = this is FieldType.Message && dec.value.hasRequiredFieldsRecursively

data class EnumDeclaration(
    val name: FqName,
    val originalEntries: List<Entry>,
    val aliases: List<Alias>,
    val doc: Comment?,
    val dec: Descriptors.EnumDescriptor,
    val deprecated: Boolean,
) {
    val companionName: FqName.Declaration by lazy { name.nested("Companion") }
    val unrecognisedName: FqName.Declaration by lazy { name.nested("UNRECOGNIZED") }

    fun defaultEntry(): Entry {
        // In proto3 and editions:
        // The first value must be a zero value, so that we can use 0 as a numeric default value
        // In proto2:
        // We use the first value as the default value
        return originalEntries.first()
    }

    data class Entry(
        val name: FqName,
        val doc: Comment?,
        val dec: Descriptors.EnumValueDescriptor,
        val deprecated: Boolean,
    )

    data class Alias(
        val name: FqName,
        val original: Entry,
        val doc: Comment?,
        val dec: Descriptors.EnumValueDescriptor,
        val deprecated: Boolean,
    )
}

data class OneOfDeclaration(
    /** The Kotlin-safe name of the case extension property (escaped with backticks if it's a Kotlin keyword). */
    val name: String,
    /** The raw lower camel-cased (or proto) name. Used for derived names like `clear<OneOf>` and `when<OneOf>`. */
    val rawName: String,
    /** The top-level `<Message><OneOf>Case` enum class, nested message names flattened. */
    val caseTypeName: FqName.Declaration,
    val variants: List<FieldDeclaration>,
    val dec: Descriptors.OneofDescriptor,
    val doc: Comment?,
    val containingType: Lazy<MessageDeclaration>,
) {
    private val capitalizedRawName: String = rawName.replaceFirstChar { it.uppercase() }

    /** `clear<OneOf>` extension function on the builder. */
    val clearFunctionName: String = "clear$capitalizedRawName"

    /** `clear<OneOf>Internal` member of the internal class. */
    val internalClearFunctionName: String = "clear${capitalizedRawName}Internal"

    /** `when<OneOf>` extension function. */
    val whenFunctionName: String = "when$capitalizedRawName"

    /** `_<oneOf>Case` member of the internal class returning the active case. */
    val internalCaseGetterName: String = "_${rawName}Case"

    /** Members stored by reference (`string`, `bytes`, messages, groups). */
    val referenceVariants: List<FieldDeclaration> by lazy { variants.filter { it.type.isOneOfReferenceType } }

    /** Members stored in the numeric slot (numbers, bool, enums). */
    val numericVariants: List<FieldDeclaration> by lazy { variants.filter { !it.type.isOneOfReferenceType } }

    val hasReferenceSlot: Boolean get() = referenceVariants.isNotEmpty()
    val hasNumericSlot: Boolean get() = numericVariants.isNotEmpty()

    /** True if the numeric slot is a `Long`, false if it is an `Int`. */
    val numericSlotIs64Bit: Boolean by lazy { numericVariants.any { it.type.isOneOf64BitType } }

    val referenceSlotName: String = "_${rawName}Ref"
    val numericSlotName: String = "_${rawName}Num"

    private val memberEntryNames: Map<FieldDeclaration, String> by lazy {
        variants.associateWith { it.dec.name.uppercase() }
    }

    /** True if a member is literally named `not_set`, in which case the not-set entry is suffixed. */
    private val notSetCollides: Boolean by lazy { memberEntryNames.values.any { it == NOT_SET_ENTRY_NAME } }

    /** Case enum entry for a member: the proto member name upper-cased, snake case kept. */
    fun caseEntryName(variant: FieldDeclaration): String = memberEntryNames.getValue(variant)

    /** Case enum entry for "no member set". */
    val notSetEntryName: String by lazy { if (notSetCollides) "${NOT_SET_ENTRY_NAME}_" else NOT_SET_ENTRY_NAME }

    /** Name of the `notSet` lambda parameter of the `when<OneOf>` function. */
    val notSetParameterName: String by lazy {
        val base = "notSet"
        if (variants.any { it.rawName == base }) "${base}_" else base
    }

    companion object {
        const val NOT_SET_ENTRY_NAME = "NOT_SET"
    }
}

data class FieldDeclaration(
    /** The Kotlin-safe name (escaped with backticks if it's a Kotlin keyword). Used in generated code. */
    val name: String,
    /** The raw proto-derived name (unescaped). Used for derived names like `hasXxx` and display. */
    val rawName: String,
    val type: FieldType,
    val doc: Comment?,
    val dec: Descriptors.FieldDescriptor,
    val deprecated: Boolean,
    // defines the index in the presenceMask of the Message.
    // this cannot be the number, as only fields with hasPresence == true are part of the presenceMask
    val presenceIdx: Int? = null,
    // the message of the field (also for extension fields)
    val containingType: Lazy<MessageDeclaration>,
    // fully-qualified symbol of the generated internal extension descriptor property
    // (null for non-extension fields)
    val extensionDescriptorName: FqName.Declaration? = null,
    // the oneof this field is a member of (null for regular fields and synthetic proto3 optional oneofs)
    val containingOneOf: Lazy<OneOfDeclaration?> = lazyOf(null),
) {
    val packedFixedSize by lazy { type.wireType == WireType.FIXED64 || type.wireType == WireType.FIXED32 }

    val isPartOfOneof: Boolean = dec.realContainingOneof != null

    val isPartOfMapEntry = dec.containingType.options.mapEntry

    val number: Int = dec.number

    // all fields are non-nullable (KRPC-262), including oneof members which return the proto default
    // when they are not the active case.
    val nullable: Boolean = false

    // if the field may have an `orNull` extension getter
    val hasOrNullGetter: Boolean =
        dec.hasPresence()
            && !dec.isRequired
            && !dec.isRepeated    // repeated fields cannot be nullable (just empty)
            && !isPartOfMapEntry  // map entry fields cannot be null

    val presenceGetterName = "has${rawName.replaceFirstChar { it.uppercase() }}"
    val clearFunctionName = "clear${rawName.replaceFirstChar { it.uppercase() }}"

    val presenceIdxFieldName: ScopedFormattedString by lazy {
        check(presenceIdx != null) {
            "Internal error: presence index is null for field $name in ${containingType.value.name}"
        }

        "%T.$name".scoped(containingType.value.presenceIndicesName)
    }
}

data class ServiceDeclaration(
    val name: FqName,
    val methods: List<MethodDeclaration>,
    val dec: Descriptors.ServiceDescriptor,
    val doc: Comment?,
    val deprecated: Boolean,
)

data class MethodDeclaration(
    val name: String,
    val inputType: Lazy<MessageDeclaration>,
    val outputType: Lazy<MessageDeclaration>,
    val dec: Descriptors.MethodDescriptor,
    val doc: Comment?,
    val deprecated: Boolean,
)
