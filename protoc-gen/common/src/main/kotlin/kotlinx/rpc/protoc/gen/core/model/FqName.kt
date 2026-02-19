/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protoc.gen.core.model

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

    object Implicits {
        val Unit = fq("kotlin", "Unit")
        val Any = fq("kotlin", "Any")
        val String = fq("kotlin", "String")
        val Int = fq("kotlin", "Int")
        val Float = fq("kotlin", "Float")
        val Float_Nan = Float.nested("Nan")
        val Double = fq("kotlin", "Double")
        val Double_Nan = Double.nested("Nan")
        val Boolean = fq("kotlin", "Boolean")
        val ByteArray = fq("kotlin", "ByteArray")
        val List = fq("kotlin.collections", "List")
        val MutableList = fq("kotlin.collections", "MutableList")
        val Map = fq("kotlin.collections", "Map")
        val MutableMap = fq("kotlin.collections", "MutableMap")
        val Deprecated = fq("kotlin", "Deprecated")
        val DeprecationLevel = fq("kotlin", "DeprecationLevel")
        val OptIn = fq("kotlin", "OptIn")
    }

    object Annotations {
        val ExperimentalRpcApi = fqDec("kotlinx.rpc.internal.utils", "ExperimentalRpcApi")
        val InternalRpcApi = fqDec("kotlinx.rpc.internal.utils", "InternalRpcApi")
        val Grpc = fqDec("kotlinx.rpc.grpc.annotations", "Grpc")
        val GrpcMethod = fqDec("kotlinx.rpc.grpc.annotations", "Grpc.Method")
        val WithCodec = fqDec("kotlinx.rpc.grpc.codec", "WithCodec")
        val GeneratedProtoMessage = fqDec("kotlinx.rpc.protobuf", "GeneratedProtoMessage")
    }

    @Suppress("unused")
    object RpcClasses {
        val InternalMessage = fqDec("kotlinx.rpc.protobuf.internal", "InternalMessage")
        val ProtoDescriptor = fqDec("kotlinx.rpc.protobuf.internal", "ProtoDescriptor")
        val WireEncoder = fqDec("kotlinx.rpc.protobuf.internal", "WireEncoder")
        val WireDecoder = fqDec("kotlinx.rpc.protobuf.internal", "WireDecoder")
        val MsgFieldDelegate = fqDec("kotlinx.rpc.protobuf.internal", "MsgFieldDelegate")
        val MessageCodec = fqDec("kotlinx.rpc.grpc.codec", "MessageCodec")
        val KTag = fqDec("kotlinx.rpc.protobuf.internal", "KTag")
        val ProtobufDecodingException = fqDec("kotlinx.rpc.protobuf.internal", "ProtobufDecodingException")
        val CodecConfig = fqDec("kotlinx.rpc.grpc.codec", "CodecConfig")
        val ProtobufConfig = fqDec("kotlinx.rpc.protobuf", "ProtobufConfig")
        val WireSize = fqDec("kotlinx.rpc.protobuf.internal", "WireSize")
        val WireType = fqDec("kotlinx.rpc.protobuf.internal", "WireType")
        val WireType_END_GROUP = WireType.nested("END_GROUP")
        val WireType_LENGTH_DELIMITED = WireType.nested("LENGTH_DELIMITED")
        val WireType_VARINT = WireType.nested("VARINT")
        val WireType_FIXED32 = WireType.nested("FIXED32")
        val WireType_START_GROUP = WireType.nested("START_GROUP")
        val WireType_FIXED64 = WireType.nested("FIXED64")
    }

    object KotlinLibs {
        val Buffer = fqDec("kotlinx.io", "Buffer")
        val Source = fqDec("kotlinx.io", "Source")
        val Flow = fqDec("kotlinx.coroutines.flow", "Flow")
        val JvmInline = fqDec("kotlin.jvm", "JvmInline")
    }
}

fun FqName.nested(name: String): FqName.Declaration = FqName.Declaration(name, this)

fun FqName.suffixed(suffix: String): FqName.Declaration = FqName.Declaration("$simpleName$suffix", parent)

fun FqName.fullName(classSuffix: String = ""): String {
    val parentName = parent
    val name = if (this is FqName.Declaration) "$simpleName$classSuffix" else simpleName
    return when {
        parentName == this -> name
        else -> {
            val fullParentName = parentName.fullName(classSuffix)
            if (fullParentName.isEmpty()) {
                name
            } else {
                "$fullParentName.$name"
            }
        }
    }
}

fun FqName.packageName(): FqName.Package {
    return when (this) {
        is FqName.Package -> this
        is FqName.Declaration -> parent.packageName()
    }
}

fun FqName.Package.importPath(entity: String): String =
    if (this == FqName.Package.Root) entity else "${fullName()}.$entity"

internal fun FqName.fullNestedNameAsList(): List<String> {
    return when (val parentName = parent) {
        is FqName.Package -> listOf(simpleName)
        this -> listOf(simpleName)
        else -> {
            val fullParentName = parentName.fullNestedNameAsList()
            if (fullParentName.isEmpty()) {
                listOf(simpleName)
            } else {
                fullParentName + simpleName
            }
        }
    }
}

internal fun String.asParentsAndSimpleName(): Pair<List<String>, String> =
    split(".").takeIf { it.size > 1 }?.run { dropLast(1) to last() }
        ?: (emptyList<String>() to this)

fun fq(packages: String, classes: String): FqName {
    return classFq(classes.split(".").filter { it.isNotBlank() }, packages)
}

fun fqDec(packages: String, classes: String): FqName.Declaration {
    require(classes.isNotBlank()) { "Classes name must not be blank" }

    return fq(packages, classes) as FqName.Declaration
}

private fun classFq(parts: List<String>, packages: String): FqName {
    if (parts.isEmpty()) {
        return FqName.Package.fromString(packages)
    }

    return FqName.Declaration(parts.last(), classFq(parts.dropLast(1), packages))
}
