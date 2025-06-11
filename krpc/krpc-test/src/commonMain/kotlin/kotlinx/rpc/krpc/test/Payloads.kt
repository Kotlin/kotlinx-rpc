/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.test

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class PayloadWithStream(val payload: String, val stream: @Contextual Flow<String>)

fun payload(index: Int = 0): PayloadWithStream {
    return PayloadWithStream(
        "test$index",
        flow { emit("a$index"); emit("b$index"); emit("c$index"); }
    )
}

fun <T> plainFlow(count: Int = 5, get: (Int) -> T): Flow<T> {
    return flow { repeat(count) { emit(get(it)) } }
}
