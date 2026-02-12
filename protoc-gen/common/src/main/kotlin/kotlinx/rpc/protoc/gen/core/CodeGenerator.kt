/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("DuplicatedCode")

package kotlinx.rpc.protoc.gen.core

import kotlinx.rpc.protoc.gen.core.model.FqName

@DslMarker
annotation class CodegenDsl

@CodegenDsl
open class CodeGenerator(
    private val indent: String,
    private val builder: StringBuilder = StringBuilder(),
    val config: Config,
    protected val nameTable: ScopedFqNameTable,
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

    fun addLine(value: String? = null) {
        _append("$indent${value ?: ""}", newLineIfAbsent = true)
    }

    fun newLine() {
        _append(newLineBefore = true)
    }

    fun blankLine() {
        _append(newLineBefore = true, newLineAfter = true)
    }

    private fun withNextIndent(block: CodeGenerator.() -> Unit) {
        CodeGenerator("$indent$ONE_INDENT", builder, config, nameTable).block()
    }

    fun scope(
        prefix: String,
        suffix: String = "",
        nlAfterClosed: Boolean = true,
        openingBracket: Boolean = true,
        paramDecl: String = "",
        scopeNestedClassName: String? = null,
        block: (CodeGenerator.() -> Unit)? = null,
    ) {
        addLine(prefix)
        scopeWithSuffix(suffix, openingBracket, nlAfterClosed, paramDecl, scopeNestedClassName, block)
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
            scopeNestedClassName = null,
            block = ifBlock,
        )
        scopeWithSuffix(scopeNestedClassName = null, block = elseBlock)
    }

    fun whileBlock(
        condition: String,
        block: (CodeGenerator.() -> Unit),
    ) {
        scope("while ($condition)", scopeNestedClassName = null, block = block)
    }

    fun whenBlock(
        condition: String? = null,
        prefix: String = "",
        block: (CodeGenerator.() -> Unit),
    ) {
        val pre = if (prefix.isNotEmpty()) prefix.trim() + " " else ""
        val cond = condition?.let { " ($it)" } ?: ""
        scope("${pre}when$cond", scopeNestedClassName = null, block = block)
    }

    fun whenCase(
        condition: String,
        block: (CodeGenerator.() -> Unit),
    ) {
        scope("$condition ->", block = block, scopeNestedClassName = null, nlAfterClosed = false)
    }

    private fun scopeWithSuffix(
        suffix: String = "",
        openingBracket: Boolean = true,
        nlAfterClosed: Boolean = true,
        paramDeclaration: String = "",
        scopeNestedClassName: String?,
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
            config = config,
            nameTable = scopeNestedClassName?.let { nameTable.nested(it) } ?: nameTable
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
        comment: Comment? = null,
        modifiers: String = "",
        contextReceiver: String = "",
        annotations: List<String> = emptyList(),
        deprecation: DeprecationLevel? = null,
        type: String,
        propertyInitializer: PropertyInitializer = PropertyInitializer.PLAIN,
        value: String = "",
        isVar: Boolean = false,
        needsNewLineAfterDeclaration: Boolean = true,
        block: (CodeGenerator.() -> Unit)? = null,
    ) {
        appendComment(comment)
        for (annotation in annotations) {
            addLine(annotation)
        }
        addDeprecation(deprecation)

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
            prefix = "${modifiersString}$varString $contextString$name$typeString$initializer$value",
            scopeNestedClassName = null,
            block = block,
        )

        this.needsNewLineAfterDeclaration = needsNewLineAfterDeclaration
    }

    enum class PropertyInitializer {
        GETTER, DELEGATE, PLAIN;
    }

    fun function(
        name: String,
        comment: Comment? = null,
        modifiers: String = "",
        typeParameters: String = "",
        args: String = "",
        contextReceiver: String = "",
        annotations: List<String> = emptyList(),
        deprecation: DeprecationLevel? = null,
        returnType: String,
        block: (CodeGenerator.() -> Unit)? = null,
    ) {
        appendComment(comment)
        for (annotation in annotations) {
            addLine(annotation)
        }
        addDeprecation(deprecation)

        val modifiersString = (if (modifiers.isEmpty()) "" else "$modifiers ").withVisibility()
        val contextString = if (contextReceiver.isEmpty()) "" else "$contextReceiver."
        val returnTypeString = if (returnType.isEmpty() || returnType == "Unit") "" else ": $returnType"
        val typeParametersString = if (typeParameters.isEmpty()) "" else " <$typeParameters>"
        scope(
            prefix = "${modifiersString}fun$typeParametersString $contextString$name($args)$returnTypeString",
            scopeNestedClassName = null,
            block = block,
        )
    }

    enum class DeclarationType(val strValue: String) {
        Class("class"), Interface("interface"), Object("object");
    }

    @JvmName("clazz_no_constructorArgs")
    fun clazz(
        name: String,
        comment: Comment? = null,
        modifiers: String = "",
        superTypes: List<String> = emptyList(),
        annotations: List<String> = emptyList(),
        deprecation: DeprecationLevel? = null,
        declarationType: DeclarationType = DeclarationType.Class,
        block: (CodeGenerator.() -> Unit)? = null,
    ) {
        clazz(
            name = name,
            comment = comment,
            modifiers = modifiers,
            constructorArgs = emptyList<String>(),
            superTypes = superTypes,
            annotations = annotations,
            deprecation = deprecation,
            declarationType = declarationType,
            block = block,
        )
    }

    @JvmName("clazz_constructorArgs_no_default")
    fun clazz(
        name: String,
        comment: Comment? = null,
        modifiers: String = "",
        constructorArgs: List<String> = emptyList(),
        superTypes: List<String> = emptyList(),
        annotations: List<String> = emptyList(),
        deprecation: DeprecationLevel? = null,
        declarationType: DeclarationType = DeclarationType.Class,
        block: (CodeGenerator.() -> Unit)? = null,
    ) {
        clazz(
            name = name,
            comment = comment,
            modifiers = modifiers,
            constructorArgs = constructorArgs.map { it to null },
            superTypes = superTypes,
            annotations = annotations,
            deprecation = deprecation,
            declarationType = declarationType,
            block = block,
        )
    }

    fun clazz(
        name: String,
        comment: Comment? = null,
        modifiers: String = "",
        constructorModifiers: String = "",
        constructorArgs: List<Pair<String, String?>> = emptyList(),
        superTypes: List<String> = emptyList(),
        annotations: List<String> = emptyList(),
        deprecation: DeprecationLevel? = null,
        declarationType: DeclarationType = DeclarationType.Class,
        block: (CodeGenerator.() -> Unit)? = null,
    ) {
        require(name.isNotBlank() || (modifiers.contains("companion") && declarationType == DeclarationType.Object)) {
            "Class name cannot be blank unless it's a companion object"
        }

        appendComment(comment)
        for (annotation in annotations) {
            addLine(annotation)
        }
        addDeprecation(deprecation)

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
                !config.explicitApiModeEnabled -> ""

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

        scopeWithSuffix(scopeNestedClassName = name.takeIf { it.isNotBlank() } ?: "Companion", block = block)
    }

    open fun build(): String {
        if (result == null) {
            result = builder.toString()
        }

        return result!!
    }

    fun String.withVisibility(): String {
        return if (config.explicitApiModeEnabled) {
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

    fun appendComments(comments: List<Comment>) {
        val filteredComments = comments.filter { !it.isEmpty() }

        filteredComments.forEachIndexed { index, comment ->
            appendComment(comment, first = index == 0, final = index == filteredComments.lastIndex)
        }
    }

    fun appendComment(comment: Comment?, first: Boolean = true, final: Boolean = true) {
        if (!config.generateComments || comment == null || comment.isEmpty()) {
            return
        }

        val leadingDetached = comment.leadingDetached
        val leading = comment.leading
        val trailing = comment.trailing

        if (first) {
            addLine("/**")
        } else {
            addLine("* ")
        }

        leadingDetached.forEach {
            addLine("* $it")
        }

        if (leadingDetached.isNotEmpty() && (leading.isNotEmpty() || trailing.isNotEmpty())) {
            addLine("* ")
        }
        leading.forEach {
            addLine("* $it")
        }

        if ((leadingDetached.isNotEmpty() || leading.isNotEmpty()) && trailing.isNotEmpty()) {
            addLine("* ")
        }
        trailing.forEach {
            addLine("* $it")
        }

        if (final) {
            addLine("*/")
        }
    }

    private fun addDeprecation(deprecation: DeprecationLevel?) {
        if (deprecation != null) {
            val value = if (deprecation != DeprecationLevel.WARNING) {
                "@Deprecated(\"This declaration is deprecated in .proto file\", DeprecationLevel.$deprecation)"
            } else {
                "@Deprecated(\"This declaration is deprecated in .proto file\")"
            }
            addLine(value)
        }
    }

    @Suppress("PrivatePropertyName")
    private val ONE_INDENT = " ".repeat(config.indentSize)
}

@CodegenDsl
class FileGenerator(
    val packageName: FqName.Package,
    rawNameTable: FqNameTable,
    var filename: String? = null,
    var packagePath: String? = packageName.toString(),
    var comments: List<Comment> = emptyList(),
    var fileOptIns: List<String> = emptyList(),
    config: Config,
) : CodeGenerator("", config = config, nameTable = rawNameTable.scoped(packageName)) {
    private val stringPackageName = packageName.toString()
    private val imports = mutableListOf<String>()

    fun importPackage(name: String) {
        if (name != stringPackageName && name.isNotBlank()) {
            imports.add("$name.*")
        }
    }

    fun import(name: String) {
        imports.add(name)
    }

    override fun build(): String {
        val builder = CodeGenerator(
            indent = "",
            config = config,
            nameTable = nameTable,
        ).apply {
            val sortedImports = this@FileGenerator.imports.toSortedSet()

            if (config.generateFileLevelComments) {
                appendComments(this@FileGenerator.comments)
            }

            if (config.generateComments && config.generateFileLevelComments && this@FileGenerator.comments.any { !it.isEmpty() }) {
                blankLine()
            }

            if (this@FileGenerator.fileOptIns.isNotEmpty()) {
                addLine("@file:OptIn(${this@FileGenerator.fileOptIns.joinToString(", ")})")
            }

            val packageName = this@FileGenerator.stringPackageName
            if (packageName.isNotEmpty()) {
                addLine("package $packageName")
            }

            if (this@FileGenerator.fileOptIns.isNotEmpty() || packageName.isNotEmpty()) {
                blankLine()
            }

            for (import in sortedImports) {
                addLine("import $import")
            }

            if (this@FileGenerator.imports.isNotEmpty()) {
                blankLine()
            }
        }

        return builder.build() + super.build()
    }
}

fun file(
    config: Config,
    packageName: FqName.Package,
    nameTable: FqNameTable,
    name: String? = null,
    block: FileGenerator.() -> Unit,
): FileGenerator = FileGenerator(
    filename = name,
    packageName = packageName,
    fileOptIns = emptyList(),
    rawNameTable = nameTable,
    config = config,
).apply(block)
