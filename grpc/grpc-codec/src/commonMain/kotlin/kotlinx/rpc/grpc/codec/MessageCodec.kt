/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.codec

import kotlinx.io.Source
import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlin.reflect.KType

@ExperimentalRpcApi
public fun interface MessageCodecResolver {
    public fun resolve(kType: KType): MessageCodec<*>
}

@ExperimentalRpcApi
public object EmptyMessageCodecResolver : MessageCodecResolver {
    override fun resolve(kType: KType): MessageCodec<*> {
        error("No codec found for type $kType")
    }
}

@ExperimentalRpcApi
public interface MessageCodec<T> {
    public fun encode(value: T): Source
    public fun decode(stream: Source): T
}
