@file:Suppress("detekt.MatchingDeclarationName")

/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.descriptor

import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.serialization.KSerializer
import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * TODO KRPC-178 General Approach for custom Service Descriptors;
 *  Move to a custom descriptor later
 */
@InternalRpcApi
public class RpcTypeKrpc(
    override val kType: KType,
    override val annotations: List<Annotation>,
    /**
     * Contains serializer instances from [kotlinx.serialization.Serializable.with] parameters from [annotations],
     * mapped by their [KClass].
     */
    public val serializers: Map<KClass<KSerializer<Any?>>, KSerializer<Any?>>,
) : RpcType {
    override fun toString(): String {
        return kType.toString()
    }
}
