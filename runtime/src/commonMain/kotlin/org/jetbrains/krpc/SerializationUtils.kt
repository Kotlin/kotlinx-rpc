package org.jetbrains.krpc

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer
import kotlinx.serialization.modules.SerializersModule
import kotlin.reflect.KClass
import kotlin.reflect.KType

@OptIn(ExperimentalSerializationApi::class)
public fun SerializersModule.buildContextual(type: KType): KSerializer<Any?> {
    val result = getContextual(
        type.classifier as? KClass<*> ?: error("Unknown type $type"),
        type.arguments.map { contextualForFlow(it.type ?: error("No type information for $type<$it>") ) }
    )
    return result as? KSerializer<Any?> ?: error("No serializer found for $type")
}


public fun SerializersModule.contextualForFlow(type: KType): KSerializer<Any?> = if (type.classifier == Flow::class) {
    buildContextual(type)
} else {
    serializer(type)
}