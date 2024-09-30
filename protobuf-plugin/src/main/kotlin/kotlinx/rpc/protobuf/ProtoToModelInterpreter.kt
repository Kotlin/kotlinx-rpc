/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf

import com.google.protobuf.DescriptorProtos
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto.Type
import com.google.protobuf.compiler.PluginProtos.CodeGeneratorRequest
import kotlinx.rpc.protobuf.model.*
import kotlinx.rpc.protobuf.model.FieldType.IntegralType
import org.slf4j.Logger
import kotlin.properties.Delegates

// todo parent types are broken a bit now
class ProtoToModelInterpreter(
    @Suppress("unused")
    private val logger: Logger,
) {
    private val fileDependencies = mutableMapOf<String, FileDeclaration>()

    fun interpretProtocRequest(message: CodeGeneratorRequest): Model {
        return Model(message.protoFileList.map { it.toModel() })
    }

    // package name of a currently parsed file
    private var packageName by Delegates.notNull<String>()

    private fun DescriptorProtos.FileDescriptorProto.toModel(): FileDeclaration {
        val dependencies = dependencyList.map { depFilename ->
            fileDependencies[depFilename]
                ?: error("Unknown dependency $depFilename for $name proto file, wrong topological order")
        }

        packageName = kotlinPackageName(`package`, options)

        return FileDeclaration(
            name = SimpleFqName(
                packageName = packageName,
                simpleName = kotlinFileName(name)
            ),
            dependencies = dependencies,
            messageDeclarations = messageTypeList.map { it.toModel() },
            enumDeclarations = enumTypeList.map { it.toModel() },
            serviceDeclarations = serviceList.map { it.toModel() },
            deprecated = options.deprecated,
            doc = null,
        ).also {
            fileDependencies[name] = it
        }
    }

    private fun kotlinFileName(originalName: String): String {
        return "${originalName.removeSuffix(".proto").fullProtoNameToKotlin(firstLetterUpper = true)}.kt"
    }

    private fun kotlinPackageName(originalPackage: String, options: DescriptorProtos.FileOptions): String {
        // todo check forbidden package names
        return originalPackage
    }

    private fun DescriptorProtos.DescriptorProto.toModel(): MessageDeclaration {
        val fields = fieldList.mapNotNull {
            val oneOfName = if (it.hasOneofIndex()) {
                oneofDeclList[it.oneofIndex].name
            } else {
                null
            }

            it.toModel(oneOfName)
        }

        return MessageDeclaration(
            name = name.fullProtoNameToKotlin(firstLetterUpper = true).toFqName(),
            actualFields = fields,
            oneOfDeclarations = oneofDeclList.mapIndexedNotNull { i, desc -> desc.toModel(i) },
            enumDeclarations = enumTypeList.map { it.toModel() },
            nestedDeclarations = nestedTypeList.map { it.toModel() },
            deprecated = options.deprecated,
            doc = null,
        )
    }

    private val oneOfFieldMembers = mutableMapOf<Int, MutableList<DescriptorProtos.FieldDescriptorProto>>()

    private fun DescriptorProtos.FieldDescriptorProto.toModel(oneOfName: String?): FieldDeclaration? {
        if (oneOfName != null) {
            val fieldType = when {
                // effectively optional
                // https://github.com/protocolbuffers/protobuf/blob/main/docs/implementing_proto3_presence.md#updating-a-code-generator
                oneOfName == "_$name" -> {
                    fieldType()
                }

                oneOfFieldMembers[oneofIndex] == null -> {
                    oneOfFieldMembers[oneofIndex] = mutableListOf<DescriptorProtos.FieldDescriptorProto>()
                        .also { list -> list.add(this) }

                    FieldType.Reference(oneOfName.fullProtoNameToKotlin(firstLetterUpper = true).toFqName())
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
            type = fieldType(),
            nullable = false,
            deprecated = options.deprecated,
            doc = null,
        )
    }

    private fun DescriptorProtos.FieldDescriptorProto.fieldType(): FieldType {
        return when {
            hasTypeName() -> {
                typeName
                    // from https://github.com/protocolbuffers/protobuf/blob/main/src/google/protobuf/descriptor.proto
                    // if the name starts with a '.', it is fully-qualified.
                    .substringAfter('.')
                    .fullProtoNameToKotlin(firstLetterUpper = true)
                    .toFqName()
                    .let { wrapWithLabel(it) }
            }

            else -> {
                primitiveType()
            }
        }
    }

    @Suppress("detekt.CyclomaticComplexMethod")
    private fun DescriptorProtos.FieldDescriptorProto.primitiveType(): FieldType {
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

    private fun DescriptorProtos.FieldDescriptorProto.wrapWithLabel(fqName: FqName): FieldType {
        return when (label) {
            DescriptorProtos.FieldDescriptorProto.Label.LABEL_REPEATED -> {
                FieldType.List(fqName)
            }
            // LABEL_OPTIONAL is not actually optional in proto3.
            // Actual optional is oneOf with one option and same name
            else -> {
                FieldType.Reference(fqName)
            }
        }
    }

    private fun DescriptorProtos.OneofDescriptorProto.toModel(index: Int): OneOfDeclaration? {
        val name = name.fullProtoNameToKotlin(firstLetterUpper = true).toFqName()

        val fields = oneOfFieldMembers[index] ?: return null
        return OneOfDeclaration(
            name = name,
            variants = fields.map { field ->
                FieldDeclaration(
                    name = field.name.fullProtoNameToKotlin(firstLetterUpper = true),
                    type = field.fieldType(),
                    nullable = false,
                    deprecated = field.options.deprecated,
                    doc = null,
                )
            }
        )
    }

    private fun DescriptorProtos.EnumDescriptorProto.toModel(): EnumDeclaration {
        val allowAlias = options.allowAlias
        val originalEntries = mutableMapOf<Int, EnumDeclaration.Entry>()
        val aliases = mutableListOf<EnumDeclaration.Alias>()

        valueList.forEach { enumEntry ->
            val original = originalEntries[enumEntry.number]
            if (original != null) {
                if (!allowAlias) {
                    error(
                        "Aliases are not allow for enum type $name: " +
                                "${enumEntry.number} of ${enumEntry.name} is already used by $original entry. " +
                                "Allow aliases via `allow_alias = true` option to avoid this error."
                    )
                }

                aliases.add(
                    EnumDeclaration.Alias(
                        name = enumEntry.name.toFqName(),
                        original = original,
                        deprecated = enumEntry.options.deprecated,
                        doc = null,
                    )
                )
            } else {
                originalEntries[enumEntry.number] = EnumDeclaration.Entry(
                    name = enumEntry.name.toFqName(),
                    deprecated = enumEntry.options.deprecated,
                    doc = null,
                )
            }
        }

        return EnumDeclaration(
            name = name.fullProtoNameToKotlin(firstLetterUpper = true).toFqName(),
            originalEntries = originalEntries.values.toList(),
            aliases = aliases,
            deprecated = options.deprecated,
            doc = null,
        )
    }

    private fun DescriptorProtos.ServiceDescriptorProto.toModel(): ServiceDeclaration {
        return ServiceDeclaration(
            name = name.fullProtoNameToKotlin(firstLetterUpper = true).toFqName(),
            methods = methodList.map { it.toModel() }
        )
    }

    private fun DescriptorProtos.MethodDescriptorProto.toModel(): MethodDeclaration {
        return MethodDeclaration(
            name = name.fullProtoNameToKotlin(firstLetterUpper = false).toFqName(),
            inputType = inputType
                .substringAfter('.') // see typeName resolution
                .fullProtoNameToKotlin(firstLetterUpper = true).toFqName(), // no resolution for now
            outputType = outputType
                .substringAfter('.') // see typeName resolution
                .fullProtoNameToKotlin(firstLetterUpper = true).toFqName(), // no resolution for now
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
            return "$packageName$delimiter${name.simpleProtoNameToKotlin(true)}"
        } else {
            simpleProtoNameToKotlin(firstLetterUpper)
        }
    }

    private val snakeRegExp = "_[a-z]".toRegex()

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

    private fun String.toFqName(parent: FqName? = null) = SimpleFqName(packageName, this, parent)
}
