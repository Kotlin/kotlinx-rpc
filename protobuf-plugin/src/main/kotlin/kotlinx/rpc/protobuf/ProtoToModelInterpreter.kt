/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("detekt.all")

package kotlinx.rpc.protobuf

import com.google.protobuf.DescriptorProtos
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto.Type
import com.google.protobuf.compiler.PluginProtos.CodeGeneratorRequest
import kotlinx.rpc.protobuf.model.*
import kotlinx.rpc.protobuf.model.FieldType.IntegralType
import org.slf4j.Logger
import kotlin.properties.Delegates

private const val ENUM_UNRECOGNIZED = "UNRECOGNIZED"

class ProtoToModelInterpreter(
    @Suppress("unused")
    private val logger: Logger,
) {
    private val fileDependencies = mutableMapOf<String, FileDeclaration>()
    private val messages = mutableMapOf<FqName, MessageDeclaration>()
    private val rootResolver = NameResolver.create(logger)

    fun interpretProtocRequest(message: CodeGeneratorRequest): Model {
        return Model(message.protoFileList.map { it.toModel() })
    }

    // package name of a currently parsed file
    private var packageName: FqName.Package by Delegates.notNull()

    private fun DescriptorProtos.FileDescriptorProto.toModel(): FileDeclaration {
        val dependencies = dependencyList.map { depFilename ->
            fileDependencies[depFilename]
                ?: error("Unknown dependency $depFilename for $name proto file, wrong topological order")
        }

        packageName = FqName.Package.fromString(kotlinPackageName(`package`, options))

        val resolver = rootResolver
            .withScope(packageName)
            .withImports(dependencies.map { it.packageName })

        val outerClass = outerClassFq()

        return FileDeclaration(
            name = kotlinFileName(),
            packageName = packageName,
            dependencies = dependencies,
            messageDeclarations = messageTypeList.mapNotNull { it.toModel(resolver, outerClass, parent = null) },
            enumDeclarations = enumTypeList.map { it.toModel(resolver, outerClass, packageName) },
            serviceDeclarations = serviceList.map { it.toModel(resolver) },
            deprecated = options.deprecated,
            doc = null,
        ).also {
            // TODO KRPC-144: check uniqueness
            fileDependencies[name] = it
        }
    }

    private fun DescriptorProtos.FileDescriptorProto.outerClassFq(): FqName {
        val filename = protoFileNameToKotlinName()

        val messageClash = messageTypeList.any { it.name.fullProtoNameToKotlin(firstLetterUpper = true) == filename }
        val enumClash = enumTypeList.any { it.name.fullProtoNameToKotlin(firstLetterUpper = true) == filename }
        val serviceClash = serviceList.any { it.name.fullProtoNameToKotlin(firstLetterUpper = true) == filename }

        val suffix = if (messageClash || enumClash || serviceClash) {
            "OuterClass"
        } else {
            ""
        }

        val simpleName = "${filename}$suffix"
        return FqName.Declaration(simpleName, packageName)
    }

    private fun DescriptorProtos.FileDescriptorProto.kotlinFileName(): String {
        return "${protoFileNameToKotlinName()}.kt"
    }

    private fun DescriptorProtos.FileDescriptorProto.protoFileNameToKotlinName(): String {
        return name.removeSuffix(".proto").fullProtoNameToKotlin(firstLetterUpper = true)
    }

    private fun kotlinPackageName(originalPackage: String, options: DescriptorProtos.FileOptions): String {
        // todo check forbidden package names
        return originalPackage
    }

    private val mapEntries = mutableMapOf<FqName, Lazy<FieldType.Map.Entry>>()

    private fun DescriptorProtos.DescriptorProto.toModel(
        parentResolver: NameResolver,
        outerClass: FqName,
        parent: FqName?,
    ): MessageDeclaration? {
        val simpleName = name.fullProtoNameToKotlin(firstLetterUpper = true)
        val fqName = parentResolver.declarationFqName(simpleName, parent ?: packageName)
        val resolver = parentResolver.withScope(fqName)

        if (options.mapEntry) {
            mapEntries[fqName] = resolveMapEntry(resolver)
            return null
        }

        // resolve before fields
        val nestedDeclarations = nestedTypeList.mapNotNull { it.toModel(resolver, outerClass, fqName) }
        val fields = fieldList.mapNotNull {
            val oneOfName = if (it.hasOneofIndex()) {
                oneofDeclList[it.oneofIndex].name
            } else {
                null
            }

            it.toModel(oneOfName, resolver)
        }

        return MessageDeclaration(
            outerClassName = outerClass,
            name = fqName,
            actualFields = fields,
            oneOfDeclarations = oneofDeclList.mapIndexedNotNull { i, desc -> desc.toModel(i, resolver, fqName) },
            enumDeclarations = enumTypeList.map { it.toModel(resolver, outerClass, fqName) },
            nestedDeclarations = nestedDeclarations,
            deprecated = options.deprecated,
            doc = null,
        ).apply {
            messages[name] = this
        }
    }

    private fun DescriptorProtos.DescriptorProto.resolveMapEntry(resolver: NameResolver): Lazy<FieldType.Map.Entry> =
        lazy {
            val keyType = fieldList[0].toModel(null, resolver) ?: error("Key type is null")
            val valueType = fieldList[1].toModel(null, resolver) ?: error("Value type is null")

            FieldType.Map.Entry(keyType.type, valueType.type)
        }

    private val oneOfFieldMembers = mutableMapOf<Int, MutableList<DescriptorProtos.FieldDescriptorProto>>()

    private fun DescriptorProtos.FieldDescriptorProto.toModel(
        oneOfName: String?,
        resolver: NameResolver,
    ): FieldDeclaration? {
        if (oneOfName != null) {
            val fieldType = when {
                // effectively optional
                // https://github.com/protocolbuffers/protobuf/blob/main/docs/implementing_proto3_presence.md#updating-a-code-generator
                oneOfName == "_$name" -> {
                    fieldType(resolver)
                }

                oneOfFieldMembers[oneofIndex] == null -> {
                    oneOfFieldMembers[oneofIndex] = mutableListOf<DescriptorProtos.FieldDescriptorProto>()
                        .also { list -> list.add(this) }

                    val name = oneOfName.fullProtoNameToKotlin(firstLetterUpper = true)
                    FieldType.OneOf(lazy { resolver.resolve(name) }, oneofIndex)
                }

                else -> {
                    oneOfFieldMembers[oneofIndex]!!.add(this)
                    null
                }
            } ?: return null

            return FieldDeclaration(
                name = oneOfName.removePrefix("_").fullProtoNameToKotlin(),
                type = fieldType,
                nullable = true,
                deprecated = options.deprecated,
                doc = null,
            )
        }

        return FieldDeclaration(
            name = name.fullProtoNameToKotlin(),
            type = fieldType(resolver),
            nullable = proto3Optional,
            deprecated = options.deprecated,
            doc = null,
        )
    }

    private fun DescriptorProtos.FieldDescriptorProto.fieldType(resolver: NameResolver): FieldType {
        return when {
            // from https://github.com/protocolbuffers/protobuf/blob/5fc0e22b9f912c2aa94a34502887c3719e805834/src/google/protobuf/descriptor.proto#L294
            // if the name starts with a '.', it is fully-qualified.
            hasTypeName() && typeName.startsWith(".") -> {
                typeName
                    .substringAfter('.')
                    .fullProtoNameToKotlin(firstLetterUpper = true)
                    //  KRPC-144 Import types
                    .let { resolvedType(it, resolver) }
            }

            hasTypeName() -> {
                typeName
                    .fullProtoNameToKotlin(firstLetterUpper = true)
                    //  KRPC-144 Import types: we assume local types now
                    .let { resolvedType(it, resolver) }
            }

            else -> {
                wrapWithLabel(primitiveType())
            }
        }
    }

    @Suppress("detekt.CyclomaticComplexMethod")
    private fun DescriptorProtos.FieldDescriptorProto.primitiveType(): IntegralType {
        return when (type) {
            Type.TYPE_STRING -> IntegralType.STRING
            Type.TYPE_BYTES -> IntegralType.BYTES
            Type.TYPE_BOOL -> IntegralType.BOOL
            Type.TYPE_FLOAT -> IntegralType.FLOAT
            Type.TYPE_DOUBLE -> IntegralType.DOUBLE
            Type.TYPE_INT32 -> IntegralType.INT32
            Type.TYPE_INT64 -> IntegralType.INT64
            Type.TYPE_UINT32 -> IntegralType.UINT32
            Type.TYPE_UINT64 -> IntegralType.UINT64
            Type.TYPE_FIXED32 -> IntegralType.FIXED32
            Type.TYPE_FIXED64 -> IntegralType.FIXED64
            Type.TYPE_SINT32 -> IntegralType.SINT32
            Type.TYPE_SINT64 -> IntegralType.SINT64
            Type.TYPE_SFIXED32 -> IntegralType.SFIXED32
            Type.TYPE_SFIXED64 -> IntegralType.SFIXED64

            Type.TYPE_ENUM, Type.TYPE_MESSAGE, Type.TYPE_GROUP, null ->
                error("Expected to find primitive type, instead got $type with name '$typeName'")
        }
    }

    private fun DescriptorProtos.FieldDescriptorProto.resolvedType(name: String, resolver: NameResolver): FieldType {
        val entry = mapEntries[resolver.resolveOrNull(name)]

        if (entry != null) {
            return FieldType.Map(entry)
        }

        val fieldType = FieldType.Reference(lazy { resolver.resolve(name) })

        return wrapWithLabel(fieldType)
    }

    private fun DescriptorProtos.FieldDescriptorProto.wrapWithLabel(fieldType: FieldType): FieldType {
        return when (label) {
            DescriptorProtos.FieldDescriptorProto.Label.LABEL_REPEATED -> {
                FieldType.List(fieldType)
            }
            // LABEL_OPTIONAL is not actually optional in proto3.
            // Actual optional is oneOf with one option and same name
            else -> {
                fieldType
            }
        }
    }

    private fun DescriptorProtos.OneofDescriptorProto.toModel(
        index: Int,
        resolver: NameResolver,
        parent: FqName,
    ): OneOfDeclaration? {
        val name = name.fullProtoNameToKotlin(firstLetterUpper = true)
        val fqName = resolver.declarationFqName(name, parent)

        val fields = oneOfFieldMembers[index] ?: return null

        val fieldResolver = resolver.withScope(fqName)
        return OneOfDeclaration(
            name = fqName,
            variants = fields.map { field ->
                FieldDeclaration(
                    name = field.name.fullProtoNameToKotlin(firstLetterUpper = true),
                    type = field.fieldType(fieldResolver),
                    nullable = false,
                    deprecated = field.options.deprecated,
                    doc = null,
                )
            }
        )
    }

    private fun DescriptorProtos.EnumDescriptorProto.toModel(
        resolver: NameResolver,
        outerClassName: FqName,
        parent: FqName,
    ): EnumDeclaration {
        val allowAlias = options.allowAlias
        val originalEntries = mutableMapOf<Int, EnumDeclaration.Entry>()
        val aliases = mutableListOf<EnumDeclaration.Alias>()

        val enumName = resolver.declarationFqName(name.fullProtoNameToKotlin(firstLetterUpper = true), parent)

        valueList.forEach { enumEntry ->
            val original = originalEntries[enumEntry.number]
            if (original != null) {
                if (!allowAlias) {
                    error(
                        "Aliases are not allowed for enum type $name: " +
                                "${enumEntry.number} of ${enumEntry.name} is already used by $original entry. " +
                                "Allow aliases via `allow_alias = true` option to avoid this error."
                    )
                }

                aliases.add(
                    EnumDeclaration.Alias(
                        name = resolver.declarationFqName(enumEntry.name, enumName),
                        original = original,
                        deprecated = enumEntry.options.deprecated,
                        doc = null,
                    )
                )
            } else {
                originalEntries[enumEntry.number] = EnumDeclaration.Entry(
                    name = resolver.declarationFqName(enumEntry.name, enumName),
                    deprecated = enumEntry.options.deprecated,
                    doc = null,
                )
            }
        }

        originalEntries[-1] = EnumDeclaration.Entry(
            name = resolver.declarationFqName(ENUM_UNRECOGNIZED, enumName),
            deprecated = false,
            doc = null,
        )

        return EnumDeclaration(
            name = enumName,
            outerClassName = outerClassName,
            originalEntries = originalEntries.values.toList(),
            aliases = aliases,
            deprecated = options.deprecated,
            doc = null,
        )
    }

    private fun DescriptorProtos.ServiceDescriptorProto.toModel(resolver: NameResolver): ServiceDeclaration {
        return ServiceDeclaration(
            name = resolver.declarationFqName(name.fullProtoNameToKotlin(firstLetterUpper = true), packageName),
            methods = methodList.map { it.toModel(resolver) }
        )
    }

    private fun DescriptorProtos.MethodDescriptorProto.toModel(resolver: NameResolver): MethodDeclaration {
        return MethodDeclaration(
            name = name.fullProtoNameToKotlin(firstLetterUpper = false),
            inputType = inputType
                .substringAfter('.') // see typeName resolution
                .fullProtoNameToKotlin(firstLetterUpper = true)
                .let {
                    lazy {
                        messages[resolver.resolve(it)]
                            ?: error("Unknown message type $it, available: ${messages.keys.joinToString(",")}")
                    }
                },
            outputType = outputType
                .substringAfter('.') // see typeName resolution
                .fullProtoNameToKotlin(firstLetterUpper = true)
                .let {
                    lazy {
                        messages[resolver.resolve(it)]
                            ?: error("Unknown message type $it, available: ${messages.keys.joinToString(",")}")
                    }
                },
            clientStreaming = clientStreaming,
            serverStreaming = serverStreaming,
        )
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

    private fun NameResolver.declarationFqName(simpleName: String, parent: FqName): FqName.Declaration {
        return FqName.Declaration(simpleName, parent).also { add(it) }
    }
}
