/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf

import org.slf4j.Logger
import org.slf4j.helpers.NOPLogger

open class CodeGenerator(
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
        CodeGenerator("$indent$ONE_INDENT", builder, logger).block()
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

        val nested = CodeGenerator("$indent$ONE_INDENT", logger = logger).apply(block)

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

    fun function(name: String, modifiers: String = "", args: String = "", block: CodeGenerator.() -> Unit) {
        val modifiersString = if (modifiers.isEmpty()) "" else "$modifiers "
        scope("${modifiersString}fun $name($args)") {
            block()
        }
    }

    enum class DeclarationType(val strValue: String) {
        Class("class"), Interface("interface"), Object("object");
    }

    fun clazz(
        name: String,
        modifiers: String = "",
        constructorArgs: List<String> = emptyList(),
        superTypes: List<String> = emptyList(),
        annotations: List<String> = emptyList(),
        declarationType: DeclarationType = DeclarationType.Class,
        block: (CodeGenerator.() -> Unit)? = null,
    ) {
        for (annotation in annotations) {
            addLine(annotation)
        }

        val modifiersString = if (modifiers.isEmpty()) "" else "$modifiers "

        val firstLine = "$modifiersString${declarationType.strValue} $name"
        addLine(firstLine)

        val shouldPutArgsOnNewLines =
            firstLine.length + constructorArgs.sumOf { it.length + 2 } + indent.length > 80

        when {
            shouldPutArgsOnNewLines && constructorArgs.isNotEmpty() -> {
                append("(")
                newLine()
                withNextIndent {
                    for (arg in constructorArgs) {
                        addLine("$arg,")
                    }
                }
                addLine(")")
            }

            constructorArgs.isNotEmpty() -> {
                append("(")
                append(constructorArgs.joinToString(", "))
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
    var filename: String? = null,
    var packageName: String? = null,
    logger: Logger = NOPLogger.NOP_LOGGER,
) : CodeGenerator("", logger = logger) {
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
    name: String? = null,
    packageName: String? = null,
    logger: Logger = NOPLogger.NOP_LOGGER,
    block: FileGenerator.() -> Unit,
): FileGenerator = FileGenerator(name, packageName, logger).apply(block)
