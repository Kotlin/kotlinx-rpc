/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protoc.gen.core

import com.google.protobuf.DescriptorProtos
import com.google.protobuf.Descriptors
import com.google.protobuf.compiler.PluginProtos.CodeGeneratorRequest
import kotlinx.rpc.protoc.gen.core.model.EnumDeclaration
import kotlinx.rpc.protoc.gen.core.model.FieldDeclaration
import kotlinx.rpc.protoc.gen.core.model.FieldType
import kotlinx.rpc.protoc.gen.core.model.FileDeclaration
import kotlinx.rpc.protoc.gen.core.model.FqName
import kotlinx.rpc.protoc.gen.core.model.MessageDeclaration
import kotlinx.rpc.protoc.gen.core.model.MethodDeclaration
import kotlinx.rpc.protoc.gen.core.model.Model
import kotlinx.rpc.protoc.gen.core.model.OneOfDeclaration
import kotlinx.rpc.protoc.gen.core.model.ServiceDeclaration
import kotlinx.rpc.protoc.gen.core.model.nested
import kotlin.Boolean
import kotlin.collections.plus
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

private val nameCache = mutableMapOf<Descriptors.GenericDescriptor, FqName>()
private val modelCache = mutableMapOf<Descriptors.GenericDescriptor, Any>()

/**
 * Converts a [CodeGeneratorRequest] into the protoc plugin [Model] of the protobuf.
 *
 * @return a [Model] instance containing a list of [FileDeclaration]s that represent
 *         the converted protobuf files.
 */
fun CodeGeneratorRequest.toModel(config: Config): Model {
    val protoFileMap = protoFileList.associateBy { it.name }
    val fileDescriptors = mutableMapOf<String, Descriptors.FileDescriptor>()

    val files = fileToGenerateList.map { protoFileMap[it]!! }
        .map { protoFile -> protoFile.toDescriptor(protoFileMap, fileDescriptors) }

    val nameTable = FqNameTable(config.platform)
    initNameTable(nameTable)

    return Model(files.map { it.toModel(nameTable) }, nameTable).also { model ->
        val typeNames = nameCache
            .filterKeys {
                it !is Descriptors.FieldDescriptor &&
                        it !is Descriptors.MethodDescriptor &&
                        it !is Descriptors.FileDescriptor
            }
            .values
            .filterIsInstance<FqName.Declaration>()

        model.nameTable.registerAll(typeNames)
    }
}

private fun initNameTable(nameTable: FqNameTable) {
    nameTable.registerAll(
        FqName.RpcClasses.InternalMessage,
        FqName.RpcClasses.ProtoDescriptor,
        FqName.RpcClasses.WireEncoder,
        FqName.RpcClasses.WireDecoder,
        FqName.RpcClasses.MsgFieldDelegate,
        FqName.RpcClasses.MessageCodec,
        FqName.RpcClasses.KTag,
        FqName.RpcClasses.ProtobufDecodingException,
        FqName.RpcClasses.ProtobufConfig,
        FqName.RpcClasses.CodecConfig,
        FqName.RpcClasses.WireSize,
        FqName.RpcClasses.WireType,
        FqName.RpcClasses.WireType_END_GROUP,
        FqName.RpcClasses.WireType_LENGTH_DELIMITED,
        FqName.RpcClasses.WireType_VARINT,
        FqName.RpcClasses.WireType_FIXED32,
        FqName.RpcClasses.WireType_START_GROUP,
        FqName.RpcClasses.WireType_FIXED64,

        FqName.Annotations.ExperimentalRpcApi,
        FqName.Annotations.InternalRpcApi,
        FqName.Annotations.Grpc,
        FqName.Annotations.GrpcMethod,
        FqName.Annotations.WithCodec,
        FqName.Annotations.GeneratedProtoMessage,

        FqName.KotlinLibs.Flow,
        FqName.KotlinLibs.Buffer,
        FqName.KotlinLibs.Source,
        FqName.KotlinLibs.JvmInline,
    )
}

/**
 * Converts a [DescriptorProtos.FileDescriptorProto] instance to a [Descriptors.FileDescriptor],
 * resolving its dependencies.
 *
 * @param protoFileMap a map of file names to `FileDescriptorProto` objects which are available for resolution.
 * @param cache a mutable map that stores already resolved `FileDescriptor` objects by file name to prevent
 *  redundant computations.
 * @return the resolved `FileDescriptor` instance corresponding to this `FileDescriptorProto`.
 */
private fun DescriptorProtos.FileDescriptorProto.toDescriptor(
    protoFileMap: Map<String, DescriptorProtos.FileDescriptorProto>,
    cache: MutableMap<String, Descriptors.FileDescriptor>,
): Descriptors.FileDescriptor {
    if (cache.containsKey(name)) return cache[name]!!

    val dependencies = dependencyList.map { depName ->
        val depProto = protoFileMap[depName] ?: error("Missing dependency: $depName")
        depProto.toDescriptor(protoFileMap, cache)
    }.toTypedArray()

    val fileDescriptor = Descriptors.FileDescriptor.buildFrom(this, dependencies)
    cache[name] = fileDescriptor
    return fileDescriptor
}

/**
 * Returns the fully qualified name [FqName] of this descriptor, resolving it to the most specific
 * declaration or package name based on its type.
 *
 * Depending on the type of the descriptor, the fully qualified name is computed recursively,
 * using the containing type or file, and appropriately converting names.
 *
 * @return The fully qualified name represented as an instance of [FqName], specific to the descriptor's context.
 */
fun Descriptors.GenericDescriptor.fqName(): FqName {
    if (nameCache.containsKey(this)) {
        return nameCache[this]!!
    }

    val nameCapital = name.simpleProtoNameToKotlin(firstLetterUpper = true)
    val nameLower = name.simpleProtoNameToKotlin()

    val fqName = when (this) {
        is Descriptors.FileDescriptor -> FqName.Package.fromString(kotlinPackage())
        is Descriptors.Descriptor -> FqName.Declaration(nameCapital, containingType?.fqName() ?: file.fqName())
        is Descriptors.FieldDescriptor -> {
            val usedName = if (realContainingOneof != null) nameCapital else nameLower
            FqName.Declaration(usedName, containingType?.fqName() ?: file.fqName())
        }

        is Descriptors.OneofDescriptor -> FqName.Declaration(nameCapital, containingType?.fqName() ?: file.fqName())
        is Descriptors.EnumDescriptor -> FqName.Declaration(nameCapital, containingType?.fqName() ?: file.fqName())
        is Descriptors.EnumValueDescriptor -> FqName.Declaration(name, type.fqName())
        is Descriptors.ServiceDescriptor -> FqName.Declaration(nameCapital, file.fqName())
        is Descriptors.MethodDescriptor -> FqName.Declaration(nameLower, service?.fqName() ?: file.fqName())
        else -> error("Unknown generic descriptor: $this")
    }

    nameCache[this] = fqName

    return fqName
}

/**
 * Caches the `descriptor.toModel()` result in the [modelCache] to ensure that only a single object
 * per descriptor exists.
 */
private inline fun <D, reified T> D.cached(crossinline block: (D) -> T): T
        where D : Descriptors.GenericDescriptor, T : Any {
    if (modelCache.containsKey(this)) {
        return modelCache[this] as T
    }
    val declaration = block(this)
    modelCache[this] = declaration
    return declaration
}

private fun Descriptors.FileDescriptor.toModel(nameTable: FqNameTable): FileDeclaration = cached {
    val comments = Comments(extractComments(), ObjectPath.empty)

    FileDeclaration(
        name = kotlinFileName(),
        packageName = FqName.Package.fromString(kotlinPackage()),
        dependencies = dependencies.map { it.toModel(nameTable) },
        messageDeclarations = messageTypes.map {
            it.toModel(
                comments = comments + Paths.messageCommentPath + it.index,
                nameTable = nameTable,
            )
        },
        enumDeclarations = enumTypes.map { it.toModel(comments + Paths.enumCommentPath + it.index, nameTable) },
        serviceDeclarations = services.map { it.toModel(comments + Paths.serviceCommentPath + it.index, nameTable) },
        doc = listOfNotNull(
            (comments + Paths.syntaxCommentPath).get(),
            (comments + Paths.editionsCommentPath).get(),
            (comments + Paths.packageCommentPath).get()
        ),
        deprecated = options.deprecated,
        dec = this,
    )
}

private fun Descriptors.Descriptor.toModel(comments: Comments?, nameTable: FqNameTable): MessageDeclaration = cached {
    ensureCommentsPresent(fqName(), comments)

    var currPresenceIdx = 0
    var regularFields = fields
        // only fields that are not part of a oneOf declaration
        .filter { field -> field.realContainingOneof == null }
        .map {
            val presenceIdx = if (it.hasPresence()) currPresenceIdx++ else null
            it.toModel(comments + Paths.messageFieldCommentPath + it.index, nameTable, presenceIdx = presenceIdx)
        }

    val oneOfs = oneofs
        .filter { it.fields[0].realContainingOneof != null }
        .map { it.toModel(comments, nameTable) }

    regularFields = regularFields + oneOfs.map {
        FieldDeclaration(
            // TODO: Proper handling of this field name
            name = it.name.simpleName.decapitalize(),
            type = FieldType.OneOf(it),
            doc = it.doc,
            dec = it.variants.first().dec,
            deprecated = options.deprecated,
        )
    }

    MessageDeclaration(
        name = fqName(),
        presenceMaskSize = currPresenceIdx,
        actualFields = regularFields,
        // get all oneof declarations that are not created from an optional in proto3 https://github.com/googleapis/api-linter/issues/1323
        oneOfDeclarations = oneOfs,
        enumDeclarations = enumTypes.map { it.toModel(comments + Paths.messageEnumCommentPath + it.index, nameTable) },
        nestedDeclarations = nestedTypes.map {
            it.toModel(
                comments = comments + Paths.messageMessageCommentPath + it.index,
                nameTable = nameTable,
            )
        },
        doc = comments.get(),
        dec = this,
        deprecated = options.deprecated,
        // getting the (already cached) parent declaration.
        // it must be lazy as it is only available after full model conversion.
        parent = lazy { containingType?.toModel(null, nameTable) },
    ).also { declaration ->
        nameTable.register { declaration.companionName }
        nameTable.register { declaration.internalClassName }
        nameTable.register { declaration.internalCompanionName }

        if (declaration.isUserFacing && declaration.hasPresenceFieldsRecursive()) {
            nameTable.register { declaration.presenceInterfaceName }
        }
        if (declaration.presenceMaskSize != 0) {
            nameTable.register { declaration.presenceIndicesName }
        }

        if (declaration.isUserFacing && !declaration.isGroup) {
            nameTable.register { declaration.codecObjectName }
        }

        if (declaration.nonDefaultByteFields().isNotEmpty()) {
            nameTable.register { declaration.bytesDefaultsName }
        }
    }
}

@OptIn(ExperimentalContracts::class)
private fun ensureCommentsPresent(declaration: FqName, comments: Comments?) {
    contract {
        returns() implies (comments != null)
    }

    requireNotNull(comments) {
        "Comments are missing for message declaration: $declaration. " +
                "This likely because the lazy declaration value was access before the whole model was composed. " +
                "Check that you don't access lazy values during resolution."
    }
}

private fun Descriptors.FieldDescriptor.toModel(
    comments: Comments,
    nameTable: FqNameTable,
    presenceIdx: Int? = null,
): FieldDeclaration =
    cached {
        FieldDeclaration(
            name = fqName().simpleName,
            type = modelType(nameTable),
            presenceIdx = presenceIdx,
            doc = comments.get(),
            dec = this,
            deprecated = options.deprecated,
        )
    }

private fun Descriptors.OneofDescriptor.toModel(
    parentComments: Comments,
    nameTable: FqNameTable,
): OneOfDeclaration = cached {
    OneOfDeclaration(
        name = fqName(),
        variants = fields.map { it.toModel(parentComments + Paths.messageFieldCommentPath + it.index, nameTable) },
        doc = (parentComments + Paths.messageOneOfCommentPath + index).get(),
        dec = this,
    ).also { declaration ->
        declaration.variants.forEach { variant ->
            nameTable.register { declaration.name.nested(variant.name) }
        }
    }
}

private fun Descriptors.EnumDescriptor.toModel(comments: Comments?, nameTable: FqNameTable): EnumDeclaration = cached {
    ensureCommentsPresent(fqName(), comments)

    val entriesMap = mutableMapOf<Int, EnumDeclaration.Entry>()
    val aliases = mutableListOf<EnumDeclaration.Alias>()

    values.forEach { value ->
        if (entriesMap.containsKey(value.number)) {
            val original = entriesMap.getValue(value.number)
            aliases.add(value.toAliasModel(comments + Paths.enumValueCommentPath + value.index, original))
        } else {
            entriesMap[value.number] = value.toModel(comments + Paths.enumValueCommentPath + value.index)
        }
    }

    if (!options.allowAlias && aliases.isNotEmpty()) {
        error("Enum $fullName can't have aliases in current proto config: ${aliases.joinToString { it.name.simpleName }}")
    }

    EnumDeclaration(
        name = fqName(),
        originalEntries = entriesMap.values.toList(),
        aliases = aliases,
        doc = comments.get(),
        dec = this,
        deprecated = options.deprecated,
    ).also { declaration ->
        nameTable.register { declaration.companionName }
        nameTable.register { declaration.unrecognisedName }
    }
}

private fun Descriptors.EnumValueDescriptor.toModel(comments: Comments): EnumDeclaration.Entry = cached {
    EnumDeclaration.Entry(
        name = fqName(),
        doc = comments.get(),
        dec = this,
        deprecated = options.deprecated,
    )
}

// no caching, as it would conflict with .toModel
private fun Descriptors.EnumValueDescriptor.toAliasModel(
    enumComments: Comments,
    original: EnumDeclaration.Entry,
): EnumDeclaration.Alias = cached {
    EnumDeclaration.Alias(
        name = fqName(),
        original = original,
        doc = enumComments.get(),
        dec = this,
        deprecated = options.deprecated,
    )
}

private fun Descriptors.ServiceDescriptor.toModel(comments: Comments, nameTable: FqNameTable): ServiceDeclaration =
    cached {
        ServiceDeclaration(
            name = fqName(),
            methods = methods.map { it.toModel(comments + Paths.serviceMethodCommentPath + it.index, nameTable) },
            dec = this,
            doc = comments.get(),
            deprecated = options.deprecated,
        )
    }

private fun Descriptors.MethodDescriptor.toModel(comments: Comments, nameTable: FqNameTable): MethodDeclaration =
    cached {
        MethodDeclaration(
            name = name,
            inputType = lazy { inputType.toModel(null, nameTable) },
            outputType = lazy { outputType.toModel(null, nameTable) },
            dec = this,
            doc = comments.get(),
            deprecated = options.deprecated,
        )
    }

//// Type Conversion Extension ////

private fun Descriptors.FieldDescriptor.modelType(nameTable: FqNameTable): FieldType {
    val baseType = when (type) {
        Descriptors.FieldDescriptor.Type.DOUBLE -> FieldType.IntegralType.DOUBLE
        Descriptors.FieldDescriptor.Type.FLOAT -> FieldType.IntegralType.FLOAT
        Descriptors.FieldDescriptor.Type.INT64 -> FieldType.IntegralType.INT64
        Descriptors.FieldDescriptor.Type.UINT64 -> FieldType.IntegralType.UINT64
        Descriptors.FieldDescriptor.Type.INT32 -> FieldType.IntegralType.INT32
        Descriptors.FieldDescriptor.Type.FIXED64 -> FieldType.IntegralType.FIXED64
        Descriptors.FieldDescriptor.Type.FIXED32 -> FieldType.IntegralType.FIXED32
        Descriptors.FieldDescriptor.Type.BOOL -> FieldType.IntegralType.BOOL
        Descriptors.FieldDescriptor.Type.STRING -> FieldType.IntegralType.STRING
        Descriptors.FieldDescriptor.Type.BYTES -> FieldType.IntegralType.BYTES
        Descriptors.FieldDescriptor.Type.UINT32 -> FieldType.IntegralType.UINT32
        Descriptors.FieldDescriptor.Type.SFIXED32 -> FieldType.IntegralType.SFIXED32
        Descriptors.FieldDescriptor.Type.SFIXED64 -> FieldType.IntegralType.SFIXED64
        Descriptors.FieldDescriptor.Type.SINT32 -> FieldType.IntegralType.SINT32
        Descriptors.FieldDescriptor.Type.SINT64 -> FieldType.IntegralType.SINT64
        Descriptors.FieldDescriptor.Type.ENUM -> FieldType.Enum(lazy { enumType.toModel(null, nameTable) })
        Descriptors.FieldDescriptor.Type.MESSAGE -> FieldType.Message(lazy { messageType!!.toModel(null, nameTable) })
        Descriptors.FieldDescriptor.Type.GROUP -> FieldType.Message(lazy { messageType!!.toModel(null, nameTable) })
    }

    if (isMapField) {
        val keyType = messageType.findFieldByName("key").modelType(nameTable)
        val valType = messageType.findFieldByName("value").modelType(nameTable)
        val mapEntryDec = lazy { messageType.toModel(null, nameTable) }
        val mapEntry = FieldType.Map.Entry(mapEntryDec, keyType, valType)
        return FieldType.Map(mapEntry)
    }

    if (isRepeated) {
        return FieldType.List(baseType)
    }

    return baseType
}

//// Utility Extensions ////

private fun Descriptors.FileDescriptor.kotlinFileName(): String {
    return "${protoFileNameToKotlinName()}.kt"
}

private fun Descriptors.FileDescriptor.kotlinPackage(): String {
    val requestedPackage = if (options.hasJavaPackage()) {
        options.javaPackage
    } else {
        `package`
    }

    if (requestedPackage == "com.google.protobuf") {
        return "com.google.protobuf.kotlin"
    }

    return requestedPackage
}

private fun Descriptors.FileDescriptor.protoFileNameToKotlinName(): String {
    return name.removeSuffix(".proto").fullProtoNameToKotlin(firstLetterUpper = true)
}

private fun String.fullProtoNameToKotlin(firstLetterUpper: Boolean = false): String {
    val lastDelimiterIndex = indexOfLast { it == '.' || it == '/' }
    return if (lastDelimiterIndex != -1) {
        val name = substring(lastDelimiterIndex + 1)
        name.simpleProtoNameToKotlin(firstLetterUpper = true)
    } else {
        simpleProtoNameToKotlin(firstLetterUpper)
    }
}

private val snakeRegExp = "(_[a-z]|-[a-z])".toRegex()

private fun String.snakeToCamelCase(): String {
    return replace(snakeRegExp) { it.value.last().uppercase() }
}

private fun String.simpleProtoNameToKotlin(firstLetterUpper: Boolean = false): String {
    return snakeToCamelCase().run {
        if (firstLetterUpper) {
            replaceFirstChar { it.uppercase() }
        } else {
            this
        }
    }
}
