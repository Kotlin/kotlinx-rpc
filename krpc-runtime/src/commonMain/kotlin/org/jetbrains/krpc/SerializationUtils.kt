package org.jetbrains.krpc

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer
import kotlinx.serialization.modules.SerializersModule
import kotlin.reflect.KClass
import kotlin.reflect.KType

@OptIn(ExperimentalSerializationApi::class)
internal fun SerializersModule.buildContextual(type: KType): KSerializer<Any?> {
    val result = getContextual(
        type.classifier as? KClass<*> ?: error("Unknown type $type"),
        type.arguments.map { contextualForFlow(it.type ?: error("No type information for $type<$it>") ) }
    )
    @Suppress("UNCHECKED_CAST")
    return result as? KSerializer<Any?> ?: error("No serializer found for $type")
}


fun SerializersModule.contextualForFlow(type: KType): KSerializer<Any?> {
    return when (type.classifier) {
        Flow::class, SharedFlow::class, StateFlow::class -> buildContextual(type)
        else -> serializer(type)
    }
}
