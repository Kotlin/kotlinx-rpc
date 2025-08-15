/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protoc.gen.core

import org.slf4j.Logger
import org.slf4j.helpers.NOPLogger

open class CodeGenerator(
    private val indent: String,
    private val builder: StringBuilder = StringBuilder(),
    protected val logger: Logger = NOPLogger.NOP_LOGGER,
    protected val explicitApiModeEnabled: Boolean,
) {
    private var isEmpty: Boolean = true
    private var result: String? = null
    private var lastIsDeclaration: Boolean = false
    private var needsNewLineAfterDeclaration: Boolean = true

    @Suppress("FunctionName")
    private fun _append(
        value: String? = null,
        newLineBefore: Boolean = false,
        newLineAfter: Boolean = false,
        newLineIfAbsent: Boolean = false,
    ) {
        var addedNewLineBefore = false

        if (lastIsDeclaration) {
            if (needsNewLineAfterDeclaration) {
                builder.appendLine()
            }

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
        needsNewLineAfterDeclaration = true
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
        CodeGenerator("$indent$ONE_INDENT", builder, logger, explicitApiModeEnabled).block()
    }

    fun scope(
        prefix: String,
        suffix: String = "",
        nlAfterClosed: Boolean = true,
        openingBracket: Boolean = true,
        paramDecl: String = "",
        block: (CodeGenerator.() -> Unit)? = null,
    ) {
        addLine(prefix)
        scopeWithSuffix(suffix, openingBracket, nlAfterClosed, paramDecl, block)
    }

    fun ifBranch(
        prefix: String = "",
        condition: String,
        ifBlock: (CodeGenerator.() -> Unit),
        elseBlock: (CodeGenerator.() -> Unit)? = null,
    ) {
        scope(
            "${prefix}if ($condition)",
            nlAfterClosed = false,
            suffix = if (elseBlock != null) " else" else "",
            block = ifBlock
        )
        scopeWithSuffix(block = elseBlock)
    }

    fun whileBlock(
        condition: String,
        block: (CodeGenerator.() -> Unit),
    ) {
        scope("while ($condition)", block = block)
    }

    fun whenBlock(
        condition: String? = null,
        prefix: String = "",
        block: (CodeGenerator.() -> Unit),
    ) {
        val pre = if (prefix.isNotEmpty()) prefix.trim() + " " else ""
        val cond = condition?.let { " ($it)" } ?: ""
        scope("${pre}when$cond", block = block)
    }

    fun whenCase(
        condition: String,
        block: (CodeGenerator.() -> Unit),
    ) {
        scope("$condition ->", block = block)
    }

    private fun scopeWithSuffix(
        suffix: String = "",
        openingBracket: Boolean = true,
        nlAfterClosed: Boolean = true,
        paramDeclaration: String = "",
        block: (CodeGenerator.() -> Unit)? = null,
    ) {
        if (block == null) {
            if (nlAfterClosed) {
                newLine()
            }
            lastIsDeclaration = nlAfterClosed
            return
        }

        val nested = CodeGenerator(
            indent = "$indent$ONE_INDENT",
            logger = logger,
            explicitApiModeEnabled = explicitApiModeEnabled,
        ).apply(block)

        if (nested.isEmpty) {
            newLine()
            lastIsDeclaration = true
            return
        }

        if (openingBracket) {
            append(" { $paramDeclaration")
        }
        newLine()
        append(nested.build().trimEnd())
        addLine("}$suffix")
        if (nlAfterClosed) {
            newLine()
        }

        lastIsDeclaration = nlAfterClosed
    }

    fun code(code: String) {
        code.lines().forEach { addLine(it) }
    }

    fun property(
        name: String,
        modifiers: String = "",
        contextReceiver: String = "",
        annotations: List<String> = emptyList(),
        type: String,
        propertyInitializer: PropertyInitializer = PropertyInitializer.PLAIN,
        value: String = "",
        isVar: Boolean = false,
        needsNewLineAfterDeclaration: Boolean = true,
        block: (CodeGenerator.() -> Unit)? = null,
    ) {
        for (annotation in annotations) {
            addLine(annotation)
        }

        val modifiersString = (if (modifiers.isEmpty()) "" else "$modifiers ").withVisibility()
        val contextString = if (contextReceiver.isEmpty()) "" else "$contextReceiver."
        val typeString = if (type.isEmpty()) "" else ": $type"
        val initializer = when (propertyInitializer) {
            PropertyInitializer.GETTER -> " get() = "
            PropertyInitializer.DELEGATE -> " by "
            PropertyInitializer.PLAIN -> " = "
        }.takeIf { value.isNotEmpty() } ?: ""
        val varString = if (isVar) "var" else "val"

        scope(
            "${modifiersString}$varString $contextString$name$typeString$initializer$value",
            block = block,
        )

        this.needsNewLineAfterDeclaration = needsNewLineAfterDeclaration
    }

    enum class PropertyInitializer {
        GETTER, DELEGATE, PLAIN;
    }

    fun function(
        name: String,
        modifiers: String = "",
        typeParameters: String = "",
        args: String = "",
        contextReceiver: String = "",
        annotations: List<String> = emptyList(),
        returnType: String,
        block: (CodeGenerator.() -> Unit)? = null,
    ) {
        for (annotation in annotations) {
            addLine(annotation)
        }
        val modifiersString = (if (modifiers.isEmpty()) "" else "$modifiers ").withVisibility()
        val contextString = if (contextReceiver.isEmpty()) "" else "$contextReceiver."
        val returnTypeString = if (returnType.isEmpty() || returnType == "Unit") "" else ": $returnType"
        val typeParametersString = if (typeParameters.isEmpty()) "" else " <$typeParameters>"
        scope("${modifiersString}fun$typeParametersString $contextString$name($args)$returnTypeString", block = block)
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
        constructorModifiers: String = "",
        constructorArgs: List<Pair<String, String?>> = emptyList(),
        superTypes: List<String> = emptyList(),
        annotations: List<String> = emptyList(),
        declarationType: DeclarationType = DeclarationType.Class,
        block: (CodeGenerator.() -> Unit)? = null,
    ) {
        for (annotation in annotations) {
            addLine(annotation)
        }

        val modifiersString = (if (modifiers.isEmpty()) "" else "$modifiers ").withVisibility()

        val firstLine = "$modifiersString${declarationType.strValue}${if (name.isNotEmpty()) " " else ""}$name"
        addLine(firstLine)

        val shouldPutArgsOnNewLines =
            firstLine.length + constructorArgs.sumOf {
                it.first.length + (it.second?.length?.plus(3) ?: 0) + 2
            } + indent.length > 80

        val constructorArgsTransformed = constructorArgs.map { (arg, default) ->
            val defaultString = default?.let { " = $it" } ?: ""
            val modifierString = when {
                !explicitApiModeEnabled -> ""

                arg.contains("val") || arg.contains("var") -> when {
                    modifiers.contains("internal") ||
                            modifiers.contains("private") ||
                            arg.contains("private ") ||
                            arg.contains("protected ") ||
                            arg.contains("internal ") ||
                            arg.contains("public ") ||
                            arg.contains("override ") -> ""

                    else -> "public "
                }

                else -> ""
            }

            "$modifierString$arg$defaultString"
        }

        val constructorModifiersTransformed = if (constructorModifiers.isEmpty()) "" else
            " ${constructorModifiers.trim()} constructor "

        when {
            shouldPutArgsOnNewLines && constructorArgsTransformed.isNotEmpty() -> {
                append(constructorModifiersTransformed)
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
                append(constructorModifiersTransformed)
                append("(")
                append(constructorArgsTransformed.joinToString(", "))
                append(")")
            }

            constructorModifiersTransformed.isNotEmpty() -> {
                append("$constructorModifiersTransformed()")
            }
        }

        val superString = superTypes
            .takeIf { it.isNotEmpty() }
            ?.joinToString(", ")
            ?.let { ": $it" }
            ?: ""

        append(superString)

        scopeWithSuffix(block = block)
    }

    open fun build(): String {
        if (result == null) {
            result = builder.toString()
        }

        return result!!
    }

    fun String.withVisibility(): String {
        return if (explicitApiModeEnabled) {
            when {
                contains("public") -> this
                contains("protected") -> this
                contains("private") -> this
                contains("internal") -> this
                else -> "public $this"
            }
        } else {
            this
        }
    }

    companion object {
        private const val ONE_INDENT = "    "
    }
}

class FileGenerator(
    var filename: String? = null,
    var packageName: String? = null,
    var packagePath: String? = packageName,
    var fileOptIns: List<String> = emptyList(),
    logger: Logger = NOPLogger.NOP_LOGGER,
    explicitApiModeEnabled: Boolean,
) : CodeGenerator("", logger = logger, explicitApiModeEnabled = explicitApiModeEnabled) {
    private val imports = mutableListOf<String>()

    fun importPackage(name: String) {
        if (name != packageName && name.isNotBlank()) {
            imports.add("$name.*")
        }
    }

    fun import(name: String) {
        imports.add(name)
    }

    override fun build(): String {
        val sortedImports = imports.toSortedSet()
        val prefix = buildString {
            if (fileOptIns.isNotEmpty()) {
                appendLine("@file:OptIn(${fileOptIns.joinToString(", ")})")
                newLine()
            }

            val packageName = packageName
            if (packageName != null && packageName.isNotEmpty()) {
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
    explicitApiModeEnabled: Boolean,
    block: FileGenerator.() -> Unit,
): FileGenerator = FileGenerator(
    filename = name,
    packageName = packageName,
    packagePath = packageName,
    fileOptIns = emptyList(),
    logger = logger,
    explicitApiModeEnabled = explicitApiModeEnabled,
).apply(block)
