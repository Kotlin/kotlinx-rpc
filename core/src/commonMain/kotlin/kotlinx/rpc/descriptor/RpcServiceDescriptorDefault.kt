/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.descriptor

import kotlinx.rpc.annotations.Rpc
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlin.reflect.KType

@InternalRpcApi
public class RpcCallableDefault<@Rpc Service : Any>(
    override val name: String,
    override val returnType: RpcType,
    override val invokator: RpcInvokator<Service>,
    override val parameters: Array<out RpcParameter>,
) : RpcCallable<Service>

@InternalRpcApi
public class RpcParameterDefault(
    override val name: String,
    override val type: RpcType,
    override val isOptional: Boolean,
    override val annotations: List<Annotation>,
) : RpcParameter

@InternalRpcApi
public class RpcTypeDefault(
    override val kType: KType,
    override val annotations: List<Annotation>,
) : RpcType {
    override fun toString(): String {
        return kType.toString()
    }
}
