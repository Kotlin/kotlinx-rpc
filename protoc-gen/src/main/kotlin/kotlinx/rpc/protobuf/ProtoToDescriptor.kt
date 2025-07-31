/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf

import com.google.protobuf.DescriptorProtos
import com.google.protobuf.Descriptors
import kotlinx.rpc.protobuf.model.FqName
import kotlinx.rpc.protobuf.model.NameResolver

private fun DescriptorProtos.FileDescriptorProto.toDescriptor(
    protoFileMap: Map<String, DescriptorProtos.FileDescriptorProto>,
    cache: MutableMap<String, Descriptors.FileDescriptor>
): Descriptors.FileDescriptor {
    if (cache.containsKey(name)) return cache[name]!!

    val dependencies = dependencyList.map { depName ->
        val depProto = protoFileMap[depName] ?: error("Missing dependency: $depName")
        depProto.toDescriptor(protoFileMap, cache)
    }.toTypedArray()

    val fileDescriptor = Descriptors.FileDescriptor.buildFrom(this, dependencies)
    cache[name] = fileDescriptor
    return fileDescriptor
}

//// GenericDescriptor Extensions ////

fun Descriptors.GenericDescriptor.fqName(): FqName {
    val nameCapital = name.simpleProtoNameToKotlin(firstLetterUpper = true)
    val nameLower = name.simpleProtoNameToKotlin()
    return when (this) {
        is Descriptors.FileDescriptor -> FqName.Package.fromString(`package`)
        is Descriptors.Descriptor -> FqName.Declaration(nameCapital, containingType?.fqName() ?: file.fqName())
        is Descriptors.FieldDescriptor -> {
            val usedName = if (realContainingOneof != null) nameCapital else nameLower
            FqName.Declaration(usedName, containingType?.fqName() ?: file.fqName())
        }

        is Descriptors.EnumValueDescriptor -> FqName.Declaration(name, type.fqName())
        is Descriptors.OneofDescriptor -> FqName.Declaration(nameCapital, containingType?.fqName() ?: file.fqName())
        is Descriptors.ServiceDescriptor -> FqName.Declaration(nameCapital, file?.fqName() ?: file.fqName())
        is Descriptors.MethodDescriptor -> FqName.Declaration(nameLower, service?.fqName() ?: file.fqName())
        else -> error("Unknown generic descriptor: $this")
    }
}

//// Descriptor Extensions ////

fun Descriptors.Descriptor.deprecated(): Boolean {
    return options.deprecated
}

fun Descriptors.FieldDescriptor.test() {
    this
}

//// Utility Extensions ////

private val snakeRegExp = "(_[a-z]|-[a-z])".toRegex()

private fun String.snakeToCamelCase(): String {
    return replace(snakeRegExp) { it.value.last().uppercase() }
}

private fun String.simpleProtoNameToKotlin(firstLetterUpper: Boolean = false): String {
    return snakeToCamelCase().run {
        if (firstLetterUpper) {
            replaceFirstChar { it.uppercase() }
        } else {
            this
        }
    }
}

private fun NameResolver.declarationFqName(simpleName: String, parent: FqName): FqName.Declaration {
    return FqName.Declaration(simpleName, parent).also { add(it) }
}
