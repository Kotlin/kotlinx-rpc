/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf

import com.google.protobuf.DescriptorProtos
import com.google.protobuf.Descriptors
import com.google.protobuf.compiler.PluginProtos.CodeGeneratorRequest
import kotlinx.rpc.protobuf.model.*

private val modelCache = mutableMapOf<Descriptors.GenericDescriptor, Any>()

/**
 * Converts a [CodeGeneratorRequest] into the protoc plugin [Model] of the protobuf.
 *
 * @return a [Model] instance containing a list of [FileDeclaration]s that represent
 *         the converted protobuf files.
 */
fun CodeGeneratorRequest.toModel(): Model {
    val protoFileMap = protoFileList.associateBy { it.name }
    val fileDescriptors = mutableMapOf<String, Descriptors.FileDescriptor>()

    val files = fileToGenerateList.map { protoFileMap[it]!! }
        .map { protoFile -> protoFile.toDescriptor(protoFileMap, fileDescriptors) }

    return Model(
        files = files.map { it.toModel() }
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
    cache: MutableMap<String, Descriptors.FileDescriptor>
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
 * @return The fully qualified name represented as an instance of FqName, specific to the descriptor's context.
 */
private fun Descriptors.GenericDescriptor.fqName(): FqName {
    val nameCapital = name.simpleProtoNameToKotlin(firstLetterUpper = true)
    val nameLower = name.simpleProtoNameToKotlin()
    return when (this) {
        is Descriptors.FileDescriptor -> FqName.Package.fromString(`package`)
        is Descriptors.Descriptor -> FqName.Declaration(nameCapital, containingType?.fqName() ?: file.fqName())
        is Descriptors.FieldDescriptor -> {
            val usedName = if (realContainingOneof != null) nameCapital else nameLower
            FqName.Declaration(usedName, containingType?.fqName() ?: file.fqName())
        }

        is Descriptors.OneofDescriptor -> FqName.Declaration(nameCapital, containingType?.fqName() ?: file.fqName())
        is Descriptors.EnumDescriptor -> FqName.Declaration(nameCapital, containingType?.fqName() ?: file.fqName())
        is Descriptors.EnumValueDescriptor -> FqName.Declaration(name, type.fqName())
        is Descriptors.ServiceDescriptor -> FqName.Declaration(nameCapital, file?.fqName() ?: file.fqName())
        is Descriptors.MethodDescriptor -> FqName.Declaration(nameLower, service?.fqName() ?: file.fqName())
        else -> error("Unknown generic descriptor: $this")
    }
}

/**
 * Caches the `descriptor.toModel()` result in the [modelCache] to ensure that only a single object
 * per descriptor exists.
 */
private inline fun <D, reified T> D.cached(block: (D) -> T): T
        where D : Descriptors.GenericDescriptor, T : Any {
    if (modelCache.containsKey(this)) {
        return modelCache[this] as T
    }
    val declaration = block(this)
    modelCache[this] = declaration
    return declaration
}

private fun Descriptors.FileDescriptor.toModel(): FileDeclaration = cached {
    return FileDeclaration(
        name = kotlinFileName(),
        packageName = FqName.Package.fromString(`package`),
        dependencies = dependencies.map { it.toModel() },
        messageDeclarations = messageTypes.map { it.toModel() },
        enumDeclarations = enumTypes.map { it.toModel() },
        serviceDeclarations = services.map { it.toModel() },
        doc = null,
        dec = this,
    )
}

private fun Descriptors.Descriptor.toModel(): MessageDeclaration = cached {
    var currPresenceIdx = 0
    var regularFields = fields
        // only fields that are not part of a oneOf declaration
        .filter { field -> field.realContainingOneof == null }
        .map {
            val presenceIdx = if (it.hasPresence()) currPresenceIdx++ else null
            it.toModel(presenceIdx = presenceIdx)
        }
    val oneOfs = oneofs.filter { it.fields[0].realContainingOneof != null }.map { it.toModel() }

    regularFields = regularFields + oneOfs.map {
        FieldDeclaration(
            // TODO: Proper handling of this field name
            it.name.simpleName.decapitalize(),
            FieldType.OneOf(it),
            doc = null,
            dec = it.variants.first().dec,
        )
    }

    return MessageDeclaration(
        name = fqName(),
        presenceMaskSize = currPresenceIdx,
        actualFields = regularFields,
        // get all oneof declarations that are not created from an optional in proto3 https://github.com/googleapis/api-linter/issues/1323
        oneOfDeclarations = oneOfs,
        enumDeclarations = enumTypes.map { it.toModel() },
        nestedDeclarations = nestedTypes.map { it.toModel() },
        doc = null,
        dec = this,
    )
}

private fun Descriptors.FieldDescriptor.toModel(presenceIdx: Int? = null): FieldDeclaration = cached {
    toProto().hasProto3Optional()
    return FieldDeclaration(
        name = fqName().simpleName,
        type = modelType(),
        presenceIdx = presenceIdx,
        doc = null,
        dec = this,
    )
}


private fun Descriptors.OneofDescriptor.toModel(): OneOfDeclaration = cached {
    return OneOfDeclaration(
        name = fqName(),
        variants = fields.map { it.toModel() },
        dec = this,
    )
}

private fun Descriptors.EnumDescriptor.toModel(): EnumDeclaration = cached {
    val entriesMap = mutableMapOf<Int, EnumDeclaration.Entry>()
    val aliases = mutableListOf<EnumDeclaration.Alias>()

    values.forEach { value ->
        if (entriesMap.containsKey(value.number)) {
            val original = entriesMap.getValue(value.number)
            aliases.add(value.toAliasModel(original))
        } else {
            entriesMap[value.number] = value.toModel()
        }
    }

    if (!options.allowAlias && aliases.isNotEmpty()) {
        error("Enum ${fullName} has aliases: ${aliases.joinToString { it.name.simpleName }}")
    }

    return EnumDeclaration(
        name = fqName(),
        originalEntries = entriesMap.values.toList(),
        aliases = aliases,
        doc = null,
        dec = this,
    )
}

private fun Descriptors.EnumValueDescriptor.toModel(): EnumDeclaration.Entry = cached {
    return EnumDeclaration.Entry(
        name = fqName(),
        doc = null,
        dec = this,
    )
}

// no caching, as it would conflict with .toModel
private fun Descriptors.EnumValueDescriptor.toAliasModel(original: EnumDeclaration.Entry): EnumDeclaration.Alias {
    return EnumDeclaration.Alias(
        name = fqName(),
        original = original,
        doc = null,
        dec = this,
    )
}

private fun Descriptors.ServiceDescriptor.toModel(): ServiceDeclaration = cached {
    return ServiceDeclaration(
        name = fqName(),
        methods = methods.map { it.toModel() },
        dec = this,
    )
}

private fun Descriptors.MethodDescriptor.toModel(): MethodDeclaration = cached {
    return MethodDeclaration(
        name = name,
        inputType = inputType.toModel(),
        outputType = outputType.toModel(),
        dec = this,
    )
}

//// Type Conversion Extension ////

private fun Descriptors.FieldDescriptor.modelType(): FieldType {
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
        Descriptors.FieldDescriptor.Type.ENUM -> FieldType.Enum(enumType.toModel())
        Descriptors.FieldDescriptor.Type.MESSAGE -> FieldType.Reference(messageType!!.toModel())
        Descriptors.FieldDescriptor.Type.GROUP -> error("GROUP type is unsupported")
    }

    if (isMapField) {
        val keyType = messageType.findFieldByName("key").modelType()
        val valType = messageType.findFieldByName("value").modelType()
        val mapEntry = FieldType.Map.Entry(keyType, valType)
        return FieldType.Map(lazy { mapEntry })
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

private fun Descriptors.FileDescriptor.protoFileNameToKotlinName(): String {
    return name.removeSuffix(".proto").fullProtoNameToKotlin(firstLetterUpper = true)
}


private fun String.fullProtoNameToKotlin(firstLetterUpper: Boolean = false): String {
    val lastDelimiterIndex = indexOfLast { it == '.' || it == '/' }
    return if (lastDelimiterIndex != -1) {
        val packageName = substring(0, lastDelimiterIndex)
        val name = substring(lastDelimiterIndex + 1)
        val delimiter = this[lastDelimiterIndex]
        return "$packageName$delimiter${name.simpleProtoNameToKotlin(firstLetterUpper = true)}"
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

