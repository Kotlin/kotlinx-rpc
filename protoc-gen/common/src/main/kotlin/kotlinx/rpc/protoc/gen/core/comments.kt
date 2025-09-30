/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protoc.gen.core

import com.google.protobuf.Descriptors

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

class Comment(
    leadingDetached: List<String>,
    leading: List<String>,
    trailing: List<String>,
) {
    val leadingDetached: List<String> = leadingDetached.protoCommentsToKotlin()
    val leading: List<String> = leading.protoCommentsToKotlin()
    val trailing: List<String> = trailing.protoCommentsToKotlin()

    fun isEmpty(): Boolean {
        return leadingDetached.isEmpty() &&
                leading.isEmpty() &&
                trailing.isEmpty()
    }
}

fun Descriptors.FileDescriptor.extractComments(): Map<String, Comment> {
    return toProto().sourceCodeInfo.locationList.associate {
        val leading = it.leadingComments ?: ""
        val trailing = it.trailingComments ?: ""
        val detached = it.leadingDetachedCommentsList.toList()
        it.pathList.joinToString(".") to Comment(detached, listOf(leading), listOf(trailing))
    }
}

private fun List<String>.protoCommentsToKotlin(): List<String> {
    return flatMap { it.protoCommentToKotlin() }.dropLastWhile { it.isBlank() }
}

private fun String.protoCommentToKotlin(): List<String> {
    return split("\n", "\r\n").map { it.trimEnd().removePrefix(" ") }
}
