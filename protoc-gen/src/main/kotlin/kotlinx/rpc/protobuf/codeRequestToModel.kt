/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf

import com.google.protobuf.DescriptorProtos
import com.google.protobuf.Descriptors
import com.google.protobuf.compiler.PluginProtos.CodeGeneratorRequest
import kotlinx.rpc.protobuf.model.*

private val modelCache = mutableMapOf<Descriptors.GenericDescriptor, Any>()

fun CodeGeneratorRequest.toCommonModel(): Model {
    val protoFileMap = protoFileList.associateBy { it.name }
    val fileDescriptors = mutableMapOf<String, Descriptors.FileDescriptor>()

    val files = protoFileList.map { protoFile -> protoFile.toDescriptor(protoFileMap, fileDescriptors) }

    return Model(
        files = files.map { it.toCommonModel() }
    )
}


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

private inline fun <D, reified T> D.cached(block: (D) -> T): T
        where D : Descriptors.GenericDescriptor, T : Any {
    if (modelCache.containsKey(this)) {
        return modelCache[this] as T
    }
    val declaration = block(this)
    modelCache[this] = declaration
    return declaration
}

private fun Descriptors.FileDescriptor.toCommonModel(): FileDeclaration = cached {
    return FileDeclaration(
        name = kotlinFileName(),
        packageName = FqName.Package.fromString(`package`),
        dependencies = dependencies.map { it.toCommonModel() },
        messageDeclarations = messageTypes.map { it.toCommonModel() },
        enumDeclarations = enumTypes.map { it.toCommonModel() },
        serviceDeclarations = services.map { it.toCommonModel() },
        deprecated = options.deprecated,
        doc = null,
    )
}

private fun Descriptors.Descriptor.toCommonModel(): MessageDeclaration = cached {
    val regularFields = fields.filter { field -> field.realContainingOneof == null }.map { it.toCommonModel() }

    return MessageDeclaration(
        outerClassName = fqName(),
        name = fqName(),
        actualFields = regularFields,
        // get all oneof declarations that are not created from an optional in proto3 https://github.com/googleapis/api-linter/issues/1323
        oneOfDeclarations = oneofs.filter { it.fields[0].realContainingOneof != null }.map { it.toCommonModel() },
        enumDeclarations = enumTypes.map { it.toCommonModel() },
        nestedDeclarations = nestedTypes.map { it.toCommonModel() },
        deprecated = options.deprecated,
        doc = null,
    )
}

private fun Descriptors.FieldDescriptor.toCommonModel(): FieldDeclaration = cached {
    toProto().hasProto3Optional()
    return FieldDeclaration(
        name = fqName().simpleName,
        number = number,
        type = modelType(),
        nullable = isNullable(),
        deprecated = options.deprecated,
        doc = null,
        proto = toProto(),
    )
}

private fun Descriptors.FieldDescriptor.isNullable(): Boolean {
    // aligns with edition settings and backward compatibility with proto2 and proto3
    return hasPresence() && !isRequired && !hasDefaultValue()
}

private fun Descriptors.OneofDescriptor.toCommonModel(): OneOfDeclaration = cached {
    return OneOfDeclaration(
        name = fqName(),
        variants = fields.map { it.toCommonModel() },
        descriptor = this
    )
}

private fun Descriptors.EnumDescriptor.toCommonModel(): EnumDeclaration = cached {
    val entriesMap = mutableMapOf<Int, EnumDeclaration.Entry>()
    val aliases = mutableListOf<EnumDeclaration.Alias>()

    values.forEach { value ->
        if (entriesMap.containsKey(value.number)) {
            val original = entriesMap.getValue(value.number)
            aliases.add(value.toAliasModel(original))
        } else {
            entriesMap[value.number] = value.toCommonModel()
        }
    }

    if (!options.allowAlias && aliases.isNotEmpty()) {
        error("Enum ${fullName} has aliases: ${aliases.joinToString { it.name.simpleName }}")
    }

    return EnumDeclaration(
        outerClassName = fqName(),
        name = fqName(),
        originalEntries = entriesMap.values.toList(),
        aliases = aliases,
        deprecated = options.deprecated,
        doc = null
    )
}

private fun Descriptors.EnumValueDescriptor.toCommonModel(): EnumDeclaration.Entry = cached {
    return EnumDeclaration.Entry(
        name = fqName(),
        deprecated = options.deprecated,
        doc = null,
    )
}

// no caching, as it would conflict with .toModel
private fun Descriptors.EnumValueDescriptor.toAliasModel(original: EnumDeclaration.Entry): EnumDeclaration.Alias {
    return EnumDeclaration.Alias(
        name = fqName(),
        original = original,
        deprecated = options.deprecated,
        doc = null,
    )
}

private fun Descriptors.ServiceDescriptor.toCommonModel(): ServiceDeclaration = cached {
    return ServiceDeclaration(
        name = fqName(),
        methods = methods.map { it.toCommonModel() }
    )
}

private fun Descriptors.MethodDescriptor.toCommonModel(): MethodDeclaration = cached {
    return MethodDeclaration(
        name = name,
        clientStreaming = isClientStreaming,
        serverStreaming = isServerStreaming,
        inputType = lazy { inputType.toCommonModel() },
        outputType = lazy { outputType.toCommonModel() }
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
        Descriptors.FieldDescriptor.Type.ENUM -> FieldType.Reference(lazy { enumType!!.toCommonModel().name })
        Descriptors.FieldDescriptor.Type.MESSAGE -> FieldType.Reference(lazy { messageType!!.toCommonModel().name })
        Descriptors.FieldDescriptor.Type.GROUP -> error("GROUP type is unsupported")
    }

    if (isRepeated) {
        return FieldType.List(baseType)
    }

    // TODO: Handle map type

    return baseType
}

//// GenericDescriptor Extensions ////

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

        is Descriptors.EnumValueDescriptor -> FqName.Declaration(name, type.fqName())
        is Descriptors.OneofDescriptor -> FqName.Declaration(nameCapital, containingType?.fqName() ?: file.fqName())
        is Descriptors.ServiceDescriptor -> FqName.Declaration(nameCapital, file?.fqName() ?: file.fqName())
        is Descriptors.MethodDescriptor -> FqName.Declaration(nameLower, service?.fqName() ?: file.fqName())
        else -> error("Unknown generic descriptor: $this")
    }
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

