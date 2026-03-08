/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("DuplicatedCode")

package kotlinx.rpc.protoc.gen.core

import kotlinx.rpc.protoc.gen.core.model.FqName
import kotlinx.rpc.protoc.gen.core.model.fullName

@DslMarker
annotation class CodegenDsl

@CodegenDsl
open class CodeGenerator(
    private val indent: String,
    private val builder: StringBuilder = StringBuilder(),
    val config: Config,
    protected val nameTable: ScopedFqNameTable,
) {
    // test only
    @Suppress("PropertyName")
    val __imports: Set<String> get() = nameTable.requiredImports

    private var isEmpty: Boolean = true
    private var result: String? = null
    private var lastIsDeclaration: Boolean = false
    private var needsNewLineAfterDeclaration: Boolean = true

    context(selectedNameTable: ScopedFqNameTable)
    @Suppress("FunctionName")
    private fun _append(
        value: ScopedFormattedString? = null,
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
            builder.append(value.resolve(selectedNameTable))
        }
        if (newLineAfter) {
            builder.appendLine()
        }

        isEmpty = false
        needsNewLineAfterDeclaration = true
    }

    /**
     * Use [code] for public API
     */
    context(selectedNameTable: ScopedFqNameTable)
    private fun append(value: ScopedFormattedString) {
        _append(value)
    }

    /**
     * Use [code] for public API
     */
    context(selectedNameTable: ScopedFqNameTable)
    private fun addLine(value: ScopedFormattedString) {
        _append(value.wrapIn { "$indent$it" }, newLineIfAbsent = true)
    }

    private fun selectNames(
        selectedNameTable: ScopedFqNameTable? = null,
        block: context(ScopedFqNameTable) () -> Unit,
    ) {
        with(selectedNameTable ?: nameTable, block)
    }

    fun newLine() {
        selectNames {
            _append(newLineBefore = true)
        }
    }

    fun blankLine() {
        selectNames {
            _append(newLineBefore = true, newLineAfter = true)
        }
    }

    private fun withNextIndent(block: CodeGenerator.() -> Unit) {
        CodeGenerator("$indent$ONE_INDENT", builder, config, nameTable).block()
    }

    fun scope(
        prefix: ScopedFormattedString,
        suffix: ScopedFormattedString = ScopedFormattedString.empty,
        nlAfterClosed: Boolean = true,
        openingBracket: Boolean = true,
        paramDecl: ScopedFormattedString = ScopedFormattedString.empty,
        scopeNestedClassName: String? = null,
        block: (CodeGenerator.() -> Unit)? = null,
    ) {
        val nestedNameTable = scopeNestedClassName?.let { nameTable.nested(it) } ?: nameTable

        selectNames(nestedNameTable) {
            addLine(prefix)
        }

        scopeWithSuffix(
            suffix = suffix,
            openingBracket = openingBracket,
            nlAfterClosed = nlAfterClosed,
            paramDeclaration = paramDecl,
            nestedNameTable = nestedNameTable,
            block = block,
        )
    }

    fun ifBranch(
        prefix: ScopedFormattedString = ScopedFormattedString.empty,
        condition: ScopedFormattedString,
        ifBlock: (CodeGenerator.() -> Unit),
        elseBlock: (CodeGenerator.() -> Unit)? = null,
    ) {
        scope(
            prefix = prefix.merge(condition) { prefix, condition -> "${prefix}if ($condition)" },
            nlAfterClosed = false,
            suffix = if (elseBlock != null) " else".scoped() else ScopedFormattedString.empty,
            block = ifBlock,
        )
        scopeWithSuffix(block = elseBlock)
    }

    fun whileBlock(
        condition: ScopedFormattedString,
        block: (CodeGenerator.() -> Unit),
    ) {
        scope(condition.wrapIn { "while ($it)" }, scopeNestedClassName = null, block = block)
    }

    fun whenBlock(
        condition: ScopedFormattedString? = null,
        prefix: ScopedFormattedString = ScopedFormattedString.empty,
        block: (CodeGenerator.() -> Unit),
    ) {
        val pre = if (prefix.value.isNotEmpty()) {
            prefix.wrapIn { it.trim() + " " }
        } else {
            ScopedFormattedString.empty
        }

        val cond = condition?.let { condition.wrapIn { " ($it)" } } ?: ScopedFormattedString.empty
        scope(pre.merge(cond) { pre, cond -> "${pre}when$cond" }, scopeNestedClassName = null, block = block)
    }

    fun whenCase(
        condition: ScopedFormattedString,
        block: (CodeGenerator.() -> Unit),
    ) {
        scope(condition.wrapIn { "$it ->" }, block = block, scopeNestedClassName = null, nlAfterClosed = false)
    }

    private fun scopeWithSuffix(
        suffix: ScopedFormattedString = ScopedFormattedString.empty,
        openingBracket: Boolean = true,
        nlAfterClosed: Boolean = true,
        paramDeclaration: ScopedFormattedString = ScopedFormattedString.empty,
        nestedNameTable: ScopedFqNameTable? = null,
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
            nameTable = nestedNameTable ?: nameTable,
        ).apply(block)

        if (nested.isEmpty) {
            newLine()
            lastIsDeclaration = true
            return
        }

        if (openingBracket) {
            selectNames(nestedNameTable) {
                append(paramDeclaration.wrapInIfNotBlankOr(" {") { " { $it" })
            }
        }

        newLine()
        selectNames {
            append(nested.build().trimEnd().scoped())
        }

        selectNames(nestedNameTable) {
            addLine(suffix.wrapIn { "}$it" })
        }

        if (nlAfterClosed) {
            newLine()
        }

        lastIsDeclaration = nlAfterClosed
    }

    fun code(code: ScopedFormattedString) {
        if (code.value.contains('\n')) {
            error("Code block should not contain newlines. Use multiple invocations of `code` instead.")
        }

        selectNames {
            addLine(code)
        }
    }

    fun functionCall(
        function: ScopedFormattedString,
        namedArgs: List<Pair<String, ScopedFormattedString>> = emptyList(),
    ) {
        if (namedArgs.isEmpty()) {
            code(function.wrapIn { "$it()" })
            return
        }

        selectNames {
            addLine(function.wrapIn { "$it(" })
        }

        withNextIndent {
            for ((name, value) in namedArgs) {
                selectNames {
                    addLine(value.wrapIn { "$name = $it," })
                }
            }
        }

        selectNames {
            addLine(")".scoped())
        }
    }

    @JvmName("functionCallBlockArgs")
    fun functionCall(
        function: ScopedFormattedString,
        namedArgBlocks: List<Pair<String, CodeGenerator.() -> Unit>>,
    ) {
        if (namedArgBlocks.isEmpty()) {
            code(function.wrapIn { "$it()" })
            return
        }

        selectNames {
            addLine(function.wrapIn { "$it(" })
        }

        withNextIndent {
            for ((name, valueBuilder) in namedArgBlocks) {
                selectNames {
                    addLine("$name =".scoped())
                }

                val nested = CodeGenerator(
                    indent = "$indent$ONE_INDENT",
                    config = config,
                    nameTable = nameTable,
                ).apply(valueBuilder)

                if (!nested.isEmpty) {
                    newLine()
                    selectNames {
                        append((nested.build().trimEnd() + ",").scoped())
                    }
                }
            }
        }

        selectNames {
            addLine(")".scoped())
        }
    }

    fun property(
        name: String,
        comment: Comment? = null,
        modifiers: String = "",
        contextReceiver: ScopedFormattedString = ScopedFormattedString.empty,
        annotations: List<ScopedFormattedString> = emptyList(),
        deprecation: DeprecationLevel? = null,
        type: ScopedFormattedString,
        propertyInitializer: PropertyInitializer = PropertyInitializer.PLAIN,
        value: ScopedFormattedString = ScopedFormattedString.empty,
        setter: ScopedFormattedString = ScopedFormattedString.empty,
        isVar: Boolean = false,
        needsNewLineAfterDeclaration: Boolean = true,
        block: (CodeGenerator.() -> Unit)? = null,
    ) {
        require((isVar && propertyInitializer == PropertyInitializer.GETTER) == (setter != ScopedFormattedString.empty)) {
            "If property is mutable, with a PropertyInitializer.GETTER, it should have a setter as well"
        }

        appendComment(comment)
        for (annotation in annotations) {
            selectNames {
                addLine(annotation)
            }
        }
        addDeprecation(deprecation)

        val modifiersString = (if (modifiers.isEmpty()) "" else "$modifiers ").withVisibility()
        val contextString = contextReceiver.wrapInIfNotBlankOr { "$it." }
        val typeString = type.wrapInIfNotBlankOr { ": $it" }
        val initializer = when (propertyInitializer) {
            PropertyInitializer.GETTER -> " get() = "
            PropertyInitializer.DELEGATE -> " by "
            PropertyInitializer.PLAIN -> " = "
        }.takeIf { value.value.isNotEmpty() } ?: ""
        val varString = if (isVar) "var" else "val"

        if (setter.value.isEmpty()) {
            scope(
                prefix = contextString.merge(typeString, value) { contextString, typeString, value ->
                    "${modifiersString}$varString $contextString$name$typeString$initializer$value"
                },
                scopeNestedClassName = null,
                block = block,
            )
        } else {
            selectNames {
                addLine(contextString.merge(typeString) { contextString, typeString ->
                    "${modifiersString}$varString $contextString$name$typeString"
                })
                withNextIndent {
                    addLine(value.wrapIn { "get() = $it" })
                    addLine(setter.wrapIn { "set(value) { $it }" })
                    newLine()
                    newLine()
                }
            }
        }

        this.needsNewLineAfterDeclaration = needsNewLineAfterDeclaration
    }

    fun property(
        name: String,
        comment: Comment? = null,
        modifiers: String = "",
        contextReceiver: ScopedFormattedString = ScopedFormattedString.empty,
        annotations: List<ScopedFormattedString> = emptyList(),
        deprecation: DeprecationLevel? = null,
        type: ScopedFormattedString,
        valueOnNewLine: Boolean = false,
        needsNewLineAfterDeclaration: Boolean = true,
        initializer: CodeGenerator.() -> Unit,
    ) {
        appendComment(comment)
        for (annotation in annotations) {
            selectNames {
                addLine(annotation)
            }
        }
        addDeprecation(deprecation)

        val modifiersString = (if (modifiers.isEmpty()) "" else "$modifiers ").withVisibility()
        val contextString = contextReceiver.wrapInIfNotBlankOr { "$it." }
        val typeString = type.wrapInIfNotBlankOr { ": $it" }

        selectNames {
            if (valueOnNewLine) {
                newLine()
            }
            addLine(contextString.merge(typeString) { contextString, typeString ->
                "${modifiersString}val $contextString$name$typeString = "
            })
        }

        val nested = CodeGenerator(
            indent = "$indent$ONE_INDENT",
            config = config,
            nameTable = nameTable,
        ).apply(initializer)

        if (!nested.isEmpty) {
            var valueString = nested.build().trimEnd()
            if (!valueOnNewLine) {
                valueString = valueString.trimStart()
            }
            selectNames {
                append(valueString.scoped())
            }
        }

        newLine()
        lastIsDeclaration = true

        this.needsNewLineAfterDeclaration = needsNewLineAfterDeclaration
    }

    enum class PropertyInitializer {
        GETTER, DELEGATE, PLAIN;
    }

    fun function(
        name: String,
        comment: Comment? = null,
        modifiers: String = "",
        typeParameters: ScopedFormattedString = ScopedFormattedString.empty,
        args: ScopedFormattedString = ScopedFormattedString.empty,
        contextReceiver: ScopedFormattedString = ScopedFormattedString.empty,
        annotations: List<ScopedFormattedString> = emptyList(),
        deprecation: DeprecationLevel? = null,
        returnType: ScopedFormattedString,
        block: (CodeGenerator.() -> Unit)? = null,
    ) {
        appendComment(comment)
        for (annotation in annotations) {
            selectNames {
                addLine(annotation)
            }
        }
        addDeprecation(deprecation)

        val modifiersString = (if (modifiers.isEmpty()) "" else "$modifiers ").withVisibility()
        val contextString = contextReceiver.wrapInIfNotBlankOr { "$it." }
        val returnTypeString = if (
            returnType == ScopedFormattedString.empty ||
            (returnType.args.size == 1 && (returnType.args[0] as? FqName) == FqName.Implicits.Unit)
        ) {
            ScopedFormattedString.empty
        } else {
            returnType.wrapIn { ": $it" }
        }
        val typeParametersString = typeParameters.wrapInIfNotBlankOr { " <$it>" }
        scope(
            prefix = typeParametersString.merge(
                other = contextString,
                another = args,
                anotherOne = returnTypeString,
            ) { typeParametersString, contextString, args, returnTypeString ->
                "${modifiersString}fun$typeParametersString $contextString$name($args)$returnTypeString"
            },
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
        superTypes: List<ScopedFormattedString> = emptyList(),
        annotations: List<ScopedFormattedString> = emptyList(),
        deprecation: DeprecationLevel? = null,
        declarationType: DeclarationType = DeclarationType.Class,
        block: (CodeGenerator.() -> Unit)? = null,
    ) {
        clazz(
            name = name,
            comment = comment,
            modifiers = modifiers,
            constructorArgs = emptyList<ScopedFormattedString>(),
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
        constructorArgs: List<ScopedFormattedString> = emptyList(),
        superTypes: List<ScopedFormattedString> = emptyList(),
        annotations: List<ScopedFormattedString> = emptyList(),
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
        constructorArgs: List<Pair<ScopedFormattedString, ScopedFormattedString?>> = emptyList(),
        superTypes: List<ScopedFormattedString> = emptyList(),
        annotations: List<ScopedFormattedString> = emptyList(),
        deprecation: DeprecationLevel? = null,
        declarationType: DeclarationType = DeclarationType.Class,
        block: (CodeGenerator.() -> Unit)? = null,
    ) {
        require(name.isNotBlank() || (modifiers.contains("companion") && declarationType == DeclarationType.Object)) {
            "Class name cannot be blank unless it's a companion object"
        }

        val nestedNameTable = nameTable.nested(name.takeIf { it.isNotBlank() } ?: "Companion")

        appendComment(comment)
        for (annotation in annotations) {
            selectNames(nestedNameTable) {
                addLine(annotation)
            }
        }
        addDeprecation(deprecation)

        val modifiersString = (if (modifiers.isEmpty()) "" else "$modifiers ").withVisibility()

        val firstLine = "$modifiersString${declarationType.strValue}${if (name.isNotEmpty()) " " else ""}$name"

        selectNames(nestedNameTable) {
            addLine(firstLine.scoped())
        }

        val shouldPutArgsOnNewLines =
            firstLine.length + constructorArgs.sumOf {
                it.first.value.length + (it.second?.value?.length?.plus(3) ?: 0) + 2
            } + indent.length > 80

        val constructorArgsTransformed = constructorArgs.map { (arg, default) ->
            val defaultString = default?.wrapIn { " = $it" } ?: ScopedFormattedString.empty
            val modifierString = when {
                !config.explicitApiModeEnabled -> ""

                arg.value.contains("val") || arg.value.contains("var") -> when {
                    modifiers.contains("internal") ||
                        modifiers.contains("private") ||
                        arg.value.contains("private ") ||
                        arg.value.contains("protected ") ||
                        arg.value.contains("internal ") ||
                        arg.value.contains("public ") ||
                        arg.value.contains("override ") -> ""

                    else -> "public "
                }

                else -> ""
            }

            arg.merge(defaultString) { arg, defaultString -> "$modifierString$arg$defaultString" }
        }

        val constructorModifiersTransformed = if (constructorModifiers.isEmpty()) "" else
            " ${constructorModifiers.trim()} constructor "

        selectNames(nestedNameTable) {
            when {
                shouldPutArgsOnNewLines && constructorArgsTransformed.isNotEmpty() -> {
                    append(constructorModifiersTransformed.scoped())
                    append("(".scoped())
                    newLine()
                    withNextIndent {
                        for (arg in constructorArgsTransformed) {
                            addLine(arg.wrapIn { "$it," })
                        }
                    }
                    addLine(")".scoped())
                }

                constructorArgsTransformed.isNotEmpty() -> {
                    append(constructorModifiersTransformed.scoped())
                    append("(".scoped())
                    append(constructorArgsTransformed.joinToScopedString(", "))
                    append(")".scoped())
                }

                constructorModifiersTransformed.isNotEmpty() -> {
                    append("$constructorModifiersTransformed()".scoped())
                }
            }
        }

        val superString = superTypes
            .takeIf { it.isNotEmpty() }
            ?.joinToScopedString(", ")
            ?.wrapIn { ": $it" }
            ?: ScopedFormattedString.empty

        selectNames(nestedNameTable) {
            append(superString)
        }

        scopeWithSuffix(
            nestedNameTable = nestedNameTable,
            block = block,
        )
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

    private fun String.escapeComment(): ScopedFormattedString {
        return replace("%", "%%").scoped()
    }

    fun appendComment(comment: Comment?, first: Boolean = true, final: Boolean = true) = selectNames {
        if (!config.generateComments || comment == null || comment.isEmpty()) {
            return@selectNames
        }

        val leadingDetached = comment.leadingDetached
        val leading = comment.leading
        val trailing = comment.trailing

        if (first) {
            addLine("/**".escapeComment())
        } else {
            addLine("* ".escapeComment())
        }

        leadingDetached.forEach {
            addLine("* $it".escapeComment())
        }

        if (leadingDetached.isNotEmpty() && (leading.isNotEmpty() || trailing.isNotEmpty())) {
            addLine("* ".escapeComment())
        }
        leading.forEach {
            addLine("* $it".escapeComment())
        }

        if ((leadingDetached.isNotEmpty() || leading.isNotEmpty()) && trailing.isNotEmpty()) {
            addLine("* ".escapeComment())
        }
        trailing.forEach {
            addLine("* $it".escapeComment())
        }

        if (final) {
            addLine("*/".escapeComment())
        }
    }

    private fun addDeprecation(deprecation: DeprecationLevel?) = selectNames {
        if (deprecation != null) {
            val value = if (deprecation != DeprecationLevel.WARNING) {
                "@%T(\"This declaration is deprecated in .proto file\", %T.$deprecation)"
                    .scoped(FqName.Implicits.Deprecated, FqName.Implicits.DeprecationLevel)
            } else {
                "@%T(\"This declaration is deprecated in .proto file\")"
                    .scoped(FqName.Implicits.Deprecated)
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
    var fileOptIns: List<ScopedFormattedString> = emptyList(),
    val imports: MutableSet<String> = mutableSetOf(),
    config: Config,
) : CodeGenerator("", config = config, nameTable = rawNameTable.scoped(packageName, imports)) {
    private val stringPackageName = packageName.toString()

    fun importPackage(name: String) {
        if (name != stringPackageName && name.isNotBlank()) {
            imports.add("$name.*")
        }
    }

    fun import(name: String) {
        imports.add(name)
    }

    private val conditionalImports = mutableMapOf<String, Set<String>>()
    fun importConditionally(imports: Map<FqName, Set<String>>) {
        conditionalImports += imports.mapKeys { it.key.fullName() }
    }

    override fun build(): String {
        // before builder to calculate imports
        val body = super.build()

        val builder = CodeGenerator(
            indent = "",
            config = config,
            nameTable = nameTable,
        ).apply {
            if (config.generateFileLevelComments) {
                appendComments(this@FileGenerator.comments)
            }

            if (config.generateComments && config.generateFileLevelComments && this@FileGenerator.comments.any { !it.isEmpty() }) {
                blankLine()
            }

            if (this@FileGenerator.fileOptIns.isNotEmpty()) {
                val optIn = "@file:%T".scoped(FqName.Implicits.OptIn)
                code(optIn.merge(this@FileGenerator.fileOptIns.joinToScopedString(", ")) { optIn, it ->
                    "$optIn($it)"
                })
            }

            val packageName = this@FileGenerator.stringPackageName
            if (packageName.isNotEmpty()) {
                code("package $packageName".scoped())
            }

            if (this@FileGenerator.fileOptIns.isNotEmpty() || packageName.isNotEmpty()) {
                blankLine()
            }

            val conditionalImports = this@FileGenerator.conditionalImports
            val regularImports = this@FileGenerator.imports

            conditionalImports.forEach { (fqName, imports) ->
                if (regularImports.contains(fqName)) {
                    regularImports += imports
                }
            }

            val sortedImports = regularImports.toSortedSet()

            for (import in sortedImports) {
                code("import $import".scoped())
            }

            if (this@FileGenerator.imports.isNotEmpty()) {
                blankLine()
            }
        }

        return builder.build() + body
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
