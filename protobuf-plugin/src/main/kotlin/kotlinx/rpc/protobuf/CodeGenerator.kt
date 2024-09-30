/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf

import org.slf4j.Logger
import org.slf4j.helpers.NOPLogger

data class CodeGenerationParameters(
    val messageMode: RPCProtobufPlugin.MessageMode,
)

open class CodeGenerator(
    val parameters: CodeGenerationParameters,
    private val indent: String,
    private val builder: StringBuilder = StringBuilder(),
    private val logger: Logger = NOPLogger.NOP_LOGGER,
) {
    private var isEmpty: Boolean = true
    private var result: String? = null
    private var lastIsDeclaration: Boolean = false

    @Suppress("FunctionName")
    private fun _append(
        value: String? = null,
        newLineBefore: Boolean = false,
        newLineAfter: Boolean = false,
        newLineIfAbsent: Boolean = false,
    ) {
        var addedNewLineBefore = false

        if (lastIsDeclaration) {
            builder.appendLine()
            lastIsDeclaration = false
            addedNewLineBefore = true
        } else if (newLineIfAbsent) {
            val last = builder.lastOrNull()
            if (last != null && last != '\n') {
                builder.appendLine()
                addedNewLineBefore = true
            }
        }

        if (!addedNewLineBefore && newLineBefore) {
            builder.appendLine()
        }
        if (value != null) {
            builder.append(value)
        }
        if (newLineAfter) {
            builder.appendLine()
        }

        isEmpty = false
    }

    private fun append(value: String) {
        _append(value)
    }

    private fun addLine(value: String? = null) {
        _append("$indent${value ?: ""}", newLineIfAbsent = true)
    }

    fun newLine() {
        _append(newLineBefore = true)
    }

    private fun withNextIndent(block: CodeGenerator.() -> Unit) {
        CodeGenerator(parameters, "$indent$ONE_INDENT", builder, logger).block()
    }

    private fun scope(prefix: String, block: (CodeGenerator.() -> Unit)? = null) {
        addLine(prefix)
        scope(block)
    }

    private fun scope(block: (CodeGenerator.() -> Unit)? = null) {
        if (block == null) {
            newLine()
            lastIsDeclaration = true
            return
        }

        val nested = CodeGenerator(parameters, "$indent$ONE_INDENT", logger = logger).apply(block)

        if (nested.isEmpty) {
            newLine()
            lastIsDeclaration = true
            return
        }

        append(" {")
        newLine()
        append(nested.build().trimEnd())
        addLine("}")
        newLine()
        lastIsDeclaration = true
    }

    fun code(code: String) {
        code.lines().forEach { addLine(it) }
    }

    fun function(
        name: String,
        modifiers: String = "",
        args: String = "",
        contextReceiver: String = "",
        returnType: String = "",
        block: (CodeGenerator.() -> Unit)? = null,
    ) {
        val modifiersString = if (modifiers.isEmpty()) "" else "$modifiers "
        val contextString = if (contextReceiver.isEmpty()) "" else "$contextReceiver."
        val returnTypeString = if (returnType.isEmpty()) "" else ": $returnType"
        scope("${modifiersString}fun $contextString$name($args)$returnTypeString", block)
    }

    enum class DeclarationType(val strValue: String) {
        Class("class"), Interface("interface"), Object("object");
    }

    @JvmName("clazz_no_constructorArgs")
    fun clazz(
        name: String,
        modifiers: String = "",
        superTypes: List<String> = emptyList(),
        annotations: List<String> = emptyList(),
        declarationType: DeclarationType = DeclarationType.Class,
        block: (CodeGenerator.() -> Unit)? = null,
    ) {
        clazz(
            name = name,
            modifiers = modifiers,
            constructorArgs = emptyList<String>(),
            superTypes = superTypes,
            annotations = annotations,
            declarationType = declarationType,
            block = block,
        )
    }

    @JvmName("clazz_constructorArgs_no_default")
    fun clazz(
        name: String,
        modifiers: String = "",
        constructorArgs: List<String> = emptyList(),
        superTypes: List<String> = emptyList(),
        annotations: List<String> = emptyList(),
        declarationType: DeclarationType = DeclarationType.Class,
        block: (CodeGenerator.() -> Unit)? = null,
    ) {
        clazz(
            name = name,
            modifiers = modifiers,
            constructorArgs = constructorArgs.map { it to null },
            superTypes = superTypes,
            annotations = annotations,
            declarationType = declarationType,
            block = block,
        )
    }

    fun clazz(
        name: String,
        modifiers: String = "",
        constructorArgs: List<Pair<String, String?>> = emptyList(),
        superTypes: List<String> = emptyList(),
        annotations: List<String> = emptyList(),
        declarationType: DeclarationType = DeclarationType.Class,
        block: (CodeGenerator.() -> Unit)? = null,
    ) {
        for (annotation in annotations) {
            addLine(annotation)
        }

        val modifiersString = if (modifiers.isEmpty()) "" else "$modifiers "

        val firstLine = "$modifiersString${declarationType.strValue}${if (name.isNotEmpty()) " " else ""}$name"
        addLine(firstLine)

        val shouldPutArgsOnNewLines =
            firstLine.length + constructorArgs.sumOf {
                it.first.length + (it.second?.length?.plus(3) ?: 0) + 2
            } + indent.length > 80

        val constructorArgsTransformed = constructorArgs.map { (arg, default) ->
            val defaultString = default?.let { " = $it" } ?: ""
            "$arg$defaultString"
        }

        when {
            shouldPutArgsOnNewLines && constructorArgsTransformed.isNotEmpty() -> {
                append("(")
                newLine()
                withNextIndent {
                    for (arg in constructorArgsTransformed) {
                        addLine("$arg,")
                    }
                }
                addLine(")")
            }

            constructorArgsTransformed.isNotEmpty() -> {
                append("(")
                append(constructorArgsTransformed.joinToString(", "))
                append(")")
            }
        }

        val superString = superTypes
            .takeIf { it.isNotEmpty() }
            ?.joinToString(", ")
            ?.let { ": $it" }
            ?: ""

        append(superString)

        scope(block)
    }

    open fun build(): String {
        if (result == null) {
            result = builder.toString()
        }

        return result!!
    }

    companion object {
        private const val ONE_INDENT = "    "
    }
}

class FileGenerator(
    codeGenerationParameters: CodeGenerationParameters,
    var filename: String? = null,
    var packageName: String? = null,
    logger: Logger = NOPLogger.NOP_LOGGER,
) : CodeGenerator(codeGenerationParameters, "", logger = logger) {
    private val imports = mutableListOf<String>()

    fun importPackage(name: String) {
        if (name != packageName) {
            imports.add("$name.*")
        }
    }

    fun import(name: String) {
        imports.add(name)
    }

    override fun build(): String {
        val sortedImports = imports.toSortedSet()
        val prefix = buildString {
            if (packageName != null) {
                appendLine("package $packageName")
            }

            appendLine()

            for (import in sortedImports) {
                appendLine("import $import")
            }

            if (imports.isNotEmpty()) {
                appendLine()
            }
        }

        return prefix + super.build()
    }
}

fun file(
    codeGenerationParameters: CodeGenerationParameters,
    name: String? = null,
    packageName: String? = null,
    logger: Logger = NOPLogger.NOP_LOGGER,
    block: FileGenerator.() -> Unit,
): FileGenerator = FileGenerator(codeGenerationParameters, name, packageName, logger).apply(block)
