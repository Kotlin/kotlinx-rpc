/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protoc.gen.core

import com.google.protobuf.Descriptors
import kotlinx.rpc.protoc.gen.core.model.FqName

@Suppress("ConstPropertyName")
object Paths {
    // tag numbers in FileDescriptorProto
    const val packageCommentPath = 2
    const val messageCommentPath = 4
    const val enumCommentPath = 5
    const val serviceCommentPath = 6
    const val extensionCommentPath = 7
    const val syntaxCommentPath = 12
    const val editionsCommentPath = 14

    // tag numbers in DescriptorProto
    const val messageFieldCommentPath = 2 // field
    const val messageMessageCommentPath = 3 // nested_type
    const val messageEnumCommentPath = 4 // enum_type
    const val messageExtensionCommentPath = 6 // extension
    const val messageOneOfCommentPath = 8 // oneof_decl

    // tag numbers in EnumDescriptorProto
    const val enumValueCommentPath = 2 // value

    // tag numbers in ServiceDescriptorProto
    const val serviceMethodCommentPath = 2
}

class ObjectPath(
    val path: List<Int>,
) {
    override fun toString(): String {
        return path.joinToString(".")
    }

    operator fun plus(pathElement: Int): ObjectPath {
        return ObjectPath(path + pathElement)
    }

    companion object {
        val empty = ObjectPath(emptyList())
    }
}

fun Map<String, Comment>.at(path: Int): Comment? {
    return this[path.toString()]
}

fun Map<String, Comment>.at(path: ObjectPath): Comment? {
    return this[path.toString()]
}

class Comments(
    val comments: Map<String, Comment>,
    val parent: ObjectPath,
) {
    fun get(): Comment? {
        return comments.at(parent)
    }
}

operator fun Comments.plus(path: Int): Comments {
    return this.run { Comments(comments, parent + path) }
}

/**
 * A processed proto comment ready for KDoc output.
 *
 * Values must already have `%` characters escaped as `%%` (unless they are `%T` placeholders).
 * Use [fromProto] to construct from raw proto strings, or [leading] for programmatic comments.
 */
class Comment(
    val leadingDetached: List<ScopedFormattedString>,
    val leading: List<ScopedFormattedString>,
    val trailing: List<ScopedFormattedString>,
) {
    fun isEmpty(): Boolean {
        return leadingDetached.isEmpty() &&
                leading.isEmpty() &&
                trailing.isEmpty()
    }

    companion object {
        fun fromProto(
            leadingDetached: List<String>,
            leading: List<String>,
            trailing: List<String>,
            resolver: ProtoTypeResolver? = null,
        ): Comment = Comment(
            leadingDetached.protoCommentsToKotlin(resolver),
            leading.protoCommentsToKotlin(resolver),
            trailing.protoCommentsToKotlin(resolver),
        )

        fun leading(comment: String): Comment = fromProto(emptyList(), listOf(comment), emptyList())

        fun leading(comment: ScopedFormattedString): Comment = Comment(
            emptyList(),
            listOf(comment),
            emptyList(),
        )
    }
}


fun Descriptors.FileDescriptor.extractComments(resolver: ProtoTypeResolver? = null): Map<String, Comment> {
    return toProto().sourceCodeInfo.locationList.associate {
        val leading = it.leadingComments ?: ""
        val trailing = it.trailingComments ?: ""
        val detached = it.leadingDetachedCommentsList.toList()
        it.pathList.joinToString(".") to Comment.fromProto(detached, listOf(leading), listOf(trailing), resolver)
    }
}

/**
 * Resolves proto type names to Kotlin [FqName]s in comment text.
 *
 * Supports:
 * - Fully qualified proto names: `[google.protobuf.Timestamp]` or `[google.protobuf.Timestamp][]`
 * - Names relative to the current proto package: `[NestedMessage]`
 *
 * Unresolvable bracket references are left as-is (with trailing `[]` removed if present).
 */
class ProtoTypeResolver(
    private val fullNameMap: Map<String, FqName>,
    private val currentPackage: String,
) {
    fun resolve(name: String): FqName? {
        // Try exact match (fully qualified proto name)
        fullNameMap[name]?.let { return it }

        // Try relative to current package
        if (currentPackage.isNotEmpty()) {
            fullNameMap["$currentPackage.$name"]?.let { return it }
        }

        return null
    }
}

private val typeRefPattern = Regex("""\[([a-zA-Z_][\w.]*)\](\[])?""")

private fun List<String>.protoCommentsToKotlin(
    resolver: ProtoTypeResolver? = null,
): List<ScopedFormattedString> {
    return flatMap { it.protoCommentToKotlin(resolver) }.dropLastWhile { it.value.isBlank() }
}

private fun String.protoCommentToKotlin(
    resolver: ProtoTypeResolver? = null,
): List<ScopedFormattedString> {
    return split("\n", "\r\n").map {
        val cleaned = it.trimEnd().removePrefix(" ")
            .replace("*/", "&#42;/")
            .replace("/*", "/&#42;")
        cleaned.resolveTypeReferences(resolver)
    }
}

private fun String.resolveTypeReferences(resolver: ProtoTypeResolver?): ScopedFormattedString {
    if (resolver == null) return replace("%", "%%").scoped()

    val args = mutableListOf<FqName>()
    val result = StringBuilder()
    var lastIndex = 0

    typeRefPattern.findAll(this).forEach { match ->
        val protoName = match.groupValues[1]
        val fqName = resolver.resolve(protoName)

        // Append text before this match (with % escaping)
        result.append(substring(lastIndex, match.range.first).replace("%", "%%"))

        if (fqName != null) {
            args.add(fqName)
            result.append("[%T]")
        } else {
            // Leave as-is but drop trailing [] (proto cross-reference convention)
            val originalText = if (match.groupValues[2].isNotEmpty()) {
                "[${match.groupValues[1]}]"
            } else {
                match.value
            }
            result.append(originalText.replace("%", "%%"))
        }

        lastIndex = match.range.last + 1
    }

    // Append remaining text
    result.append(substring(lastIndex).replace("%", "%%"))

    return ScopedFormattedString(result.toString(), args)
}
