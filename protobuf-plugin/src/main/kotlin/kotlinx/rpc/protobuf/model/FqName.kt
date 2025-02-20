/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.model

sealed interface FqName {
    val simpleName: String
    val parent: FqName

    data class Declaration(
        override val simpleName: String,
        override val parent: FqName,
    ) : FqName {
        override fun toString(): String {
            return fullName()
        }
    }

    class Package private constructor(
        override val simpleName: String,
        parent: Package? = null,
    ) : FqName {
        override val parent: FqName = parent ?: this

        override fun equals(other: Any?): Boolean {
            return other is Package && other.simpleName == simpleName && (simpleName == "" || other.parent == parent)
        }

        override fun hashCode(): Int {
            if (simpleName.isEmpty()) {
                return simpleName.hashCode()
            }

            return parent.hashCode() * 31 + simpleName.hashCode()
        }

        override fun toString(): String {
            return fullName()
        }

        companion object {
            val Root = Package("")

            fun fromString(name: String): Package {
                return if (name.isEmpty()) {
                    Root
                } else {
                    Package(name.substringAfterLast(".", name), fromString(name.substringBeforeLast('.', "")))
                }
            }
        }
    }
}

internal fun FqName.fullName(): String {
    val parentName = parent
    return when {
        parentName == this -> simpleName
        else -> {
            val fullParentName = parentName.fullName()
            if (fullParentName.isEmpty()) {
                simpleName
            } else {
                "$fullParentName.$simpleName"
            }
        }
    }
}

internal fun String.asParentsAndSimpleName(): Pair<List<String>, String> =
    split(".").takeIf { it.size > 1 }?.run { dropLast(1) to last() }
        ?: (emptyList<String>() to this)
