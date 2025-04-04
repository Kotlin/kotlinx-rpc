/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.test.api

import kotlinx.rpc.internal.rpcInternalKClass
import kotlinx.rpc.internal.rpcInternalQualifiedClassName
import kotlinx.rpc.krpc.test.api.util.GoldUtils.NewLine
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.reflect.*
import kotlin.reflect.full.createType
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.memberProperties

inline fun <reified RootType> checkProtocolApi(): ApiTestContext {
    return ApiTestContext().apply { traverseClasses(typeOf<RootType>()) }
}

@PublishedApi
internal fun ApiTestContext.traverseClasses(rootType: KType) {
    traverseClasses(rootType.rpcInternalKClass<Any>(), rootType)
}

private fun ApiTestContext.traverseClasses(root: KClass<*>, rootType: KType) {
    if (root.isSampled()) {
        return
    } else {
        root.markSampled()
    }

    when {
        root.isSealed -> checkSealed(root)
        else -> checkType(root, rootType)
    }
}

private fun ApiTestContext.checkSealed(clazz: KClass<*>) {
    val children = clazz.sealedSubclasses.map { child ->
        traverseClasses(child, child.createType())

        lines(
            child.serialName().prop(prefix = null),
            child.declaredName()?.prop(),
        )
    }

    val props = serialProperties(clazz)

    compareAndDump(clazz, lines(clazz.serialAndDeclaredName(), list = props + "" + children))
}

private fun ApiTestContext.checkType(kClass: KClass<*>, type: KType) {
    val name = kClass.rpcInternalQualifiedClassName
    when {
        name in stdLibTypes -> {} // ignore

        name in wellKnownGenericTypes -> {
            traverseClasses(type.arguments[0].type!!)
        }

        name == "kotlin.collections.Map" -> {
            traverseClasses(type.arguments[0].type!!)
            traverseClasses(type.arguments[1].type!!)
        }

        kClass.isSubclassOf(Enum::class) -> {
            @Suppress("UNCHECKED_CAST")
            checkEnum(kClass as KClass<Enum<*>>)
        }

        type.arguments.isNotEmpty() -> {
            fails.add("Custom protocol classes can not have type arguments: $name")
        }

        !name.startsWith("kotlinx.rpc") -> {
            fails.add("All protocol classes must be located in 'kotlinx.rpc' package or its descendant: $name")
        }

        !kClass.isData -> {
            fails.add("All protocol classes must be data classes: $name")
        }

        kClass.findAnnotation<Serializable>() == null -> {
            fails.add("All protocol classes must be annotated with @Serializable: $name")
        }

        else -> {
            compareAndDump(kClass, lines(kClass.serialAndDeclaredName(), list = serialProperties(kClass)))
        }
    }
}

private fun ApiTestContext.checkEnum(kClass: KClass<Enum<*>>) {
    val enumValues: List<String> = kClass.java.enumConstants.map { entry ->
        val enumField = kClass.java.getField(entry.name)
        val serialValue = enumField.annotations.filterIsInstance<SerialName>().singleOrNull()?.value

        lines(
            (serialValue ?: entry.name).prop(prefix = null),
            serialValue?.let { "Declared name: ${entry.name}" }?.prop(),
        )
    }

    compareAndDump(kClass, lines(kClass.serialAndDeclaredName(), list = enumValues))
}

private fun ApiTestContext.serialProperties(kClass: KClass<*>): List<String> {
    return kClass.memberProperties.map { serialProperty(it) }
}

private fun ApiTestContext.serialProperty(property: KProperty<*>): String {
    traverseClasses(property.returnType)
    val type = property.returnType
    val kClass = type.rpcInternalKClass<Any>()
    val nullable = if (type.isMarkedNullable) "Nullable" else null

    return lines(
        "${property.serialName()}: ${kClass.serialName()}".prop(),
        nullable?.prop(level = 2),
        property.declaredName()?.prop(level = 2),
        kClass.declaredName()?.prop(level = 2),
    )
}

private fun KClass<*>.serialAndDeclaredName(): String {
    return "${serialName()}${declaredName()?.let { " [$it]" } ?: ""}"
}

private fun KClass<*>.serialName(): String {
    return serialName(rpcInternalQualifiedClassName)
}

private fun KProperty<*>.serialName(): String {
    return serialName(name)
}

private fun KAnnotatedElement.serialName(original: String): String {
    return findAnnotation<SerialName>()?.value ?: original
}

private fun KClass<*>.declaredName(): String? {
    return declaredName(rpcInternalQualifiedClassName)
}

private fun KProperty<*>.declaredName(): String? {
    return declaredName(name)
}

private fun KAnnotatedElement.declaredName(original: String): String? {
    return findAnnotation<SerialName>()?.value?.let { "Declared name: $original" }
}

private fun String.prop(level: Int = 1, prefix: String? = "- "): String {
    return if (prefix != null) {
        "${"  ".repeat(level)}$prefix$this"
    } else {
        "${"  ".repeat(level)}$this"
    }
}

private fun lines(
    vararg line: String?,
    list: List<String> = emptyList(),
) = (line.filterNotNull() + list).joinToString(NewLine)

private val stdLibTypes = setOf(
    "kotlin.String",
    "kotlin.Unit",
    "kotlin.Int",
    "kotlin.Boolean",
    "kotlin.Long",
    "kotlin.Double",
    "kotlin.IntArray",
    "kotlin.ByteArray",
)

private val wellKnownGenericTypes = setOf(
    "kotlin.Array",
    "kotlin.collections.Collection",
    "kotlin.collections.List",
    "kotlin.collections.Set",
    "kotlinx.coroutines.flow.Flow",
)
