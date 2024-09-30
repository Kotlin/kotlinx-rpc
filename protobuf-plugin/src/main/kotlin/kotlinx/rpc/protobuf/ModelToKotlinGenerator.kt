/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf

import kotlinx.rpc.protobuf.CodeGenerator.DeclarationType
import kotlinx.rpc.protobuf.model.*
import org.slf4j.Logger

class ModelToKotlinGenerator(
    private val model: Model,
    private val logger: Logger,
    private val codeGenerationParameters: CodeGenerationParameters,
) {
    fun generateKotlinFiles(): List<FileGenerator> {
        return model.files.map { it.generateKotlinFile() }
    }

    private fun FileDeclaration.generateKotlinFile(): FileGenerator {
        return file(codeGenerationParameters, logger = logger) {
            filename = name.simpleName
            packageName = name.packageName

            dependencies.forEach { dependency ->
                importPackage(dependency.name.packageName)
            }

            generateDeclaredEntities(this@generateKotlinFile)

            additionalImports.forEach {
                import(it)
            }
        }
    }

    private val additionalImports = mutableSetOf<String>()

    private fun CodeGenerator.generateDeclaredEntities(fileDeclaration: FileDeclaration) {
        fileDeclaration.messageDeclarations.forEach { generateMessage(it) }
        fileDeclaration.enumDeclarations.forEach { generateEnum(it) }
        fileDeclaration.serviceDeclarations.forEach { generateService(it) }
    }

    private fun CodeGenerator.generateMessage(declaration: MessageDeclaration) {
        val fields = declaration.actualFields.map { it.generateFieldDeclaration() to it.type.defaultValue }

        val isInterfaceMode = parameters.messageMode == RPCProtobufPlugin.MessageMode.Interface

        val (declarationType, modifiers) = when {
            isInterfaceMode -> {
                DeclarationType.Interface to ""
            }

            fields.isEmpty() -> {
                DeclarationType.Object to ""
            }

            else -> {
                DeclarationType.Class to "data"
            }
        }

        clazz(
            name = declaration.name.simpleName,
            modifiers = modifiers,
            constructorArgs = if (isInterfaceMode) emptyList() else fields.map { "val ${it.first}" to it.second },
            declarationType = declarationType,
        ) {
            if (isInterfaceMode) {
                fields.forEach {
                    code("val ${it.first}")
                    newLine()
                }
            }

            declaration.oneOfDeclarations.forEach { oneOf ->
                generateOneOf(oneOf)
            }

            declaration.nestedDeclarations.forEach { nested ->
                generateMessage(nested)
            }

            declaration.enumDeclarations.forEach { enum ->
                generateEnum(enum)
            }

            if (isInterfaceMode) {
                clazz("", modifiers = "companion", declarationType = DeclarationType.Object)
            }
        }

        if (isInterfaceMode) {
            clazz(
                name = "${declaration.name.simpleName}Builder",
                declarationType = DeclarationType.Class,
                superTypes = listOf(declaration.name.simpleName),
            ) {
                fields.forEach {
                    code("override var ${it.first} = ${it.second}")
                    newLine()
                }
            }

            function(
                name = "invoke",
                modifiers = "operator",
                args = "body: ${declaration.name.simpleName}.() -> Unit",
                contextReceiver = "${declaration.name.simpleName}.Companion",
                returnType = declaration.name.simpleName,
            ) {
                code("return ${declaration.name.simpleName}Builder().apply(body)")
            }
        }
    }

    private fun FieldDeclaration.generateFieldDeclaration(): String {
        return "${name}: ${typeFqName()}"
    }

    private fun FieldDeclaration.typeFqName(): String {
        return when (type) {
            is FieldType.Reference -> {
                type.value.simpleName
            }

            is FieldType.IntegralType -> {
                type.fqName.simpleName
            }

            is FieldType.List -> {
                "List<${type.valueName.simpleName}>"
            }

            is FieldType.Map -> {
                "Map<${type.keyName.simpleName}, ${type.valueName.simpleName}>"
            }
        }
    }

    private fun CodeGenerator.generateOneOf(declaration: OneOfDeclaration) {
        val interfaceName = declaration.name.simpleName

        clazz(declaration.name.simpleName, "sealed", declarationType = DeclarationType.Interface) {
            declaration.variants.forEach { variant ->
                clazz(
                    name = variant.name,
                    modifiers = "value",
                    constructorArgs = listOf("val value: ${variant.typeFqName()}"),
                    annotations = listOf("@JvmInline"),
                    superTypes = listOf(interfaceName),
                )

                additionalImports.add("kotlin.jvm.JvmInline")
            }
        }
    }

    private fun CodeGenerator.generateEnum(declaration: EnumDeclaration) {
        clazz(declaration.name.simpleName, "enum") {
            code(declaration.originalEntries.joinToString(", ", postfix = ";") { enumEntry ->
                enumEntry.name.simpleName
            })

            if (declaration.aliases.isNotEmpty()) {
                newLine()

                clazz("", modifiers = "companion", declarationType = DeclarationType.Object) {
                    declaration.aliases.forEach { alias: EnumDeclaration.Alias ->
                        code(
                            "val ${alias.name.simpleName}: ${declaration.name.simpleName} " +
                                    "= ${alias.original.name.simpleName}"
                        )
                    }
                }
            }
        }
    }

    private fun CodeGenerator.generateService(service: ServiceDeclaration) {
        clazz(service.name.simpleName, declarationType = DeclarationType.Interface) {
            service.methods.forEach { method ->
                // no streaming for now
                function(
                    name = method.name.simpleName,
                    modifiers = "suspend",
                    args = "input: ${method.inputType.simpleName}",
                    returnType = method.outputType.simpleName,
                )
            }
        }
    }
}
