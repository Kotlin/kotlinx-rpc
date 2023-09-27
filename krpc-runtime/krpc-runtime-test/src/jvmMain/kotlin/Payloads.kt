package org.jetbrains.krpc.test

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class PayloadWithStream(val payload: String, val stream: @Contextual Flow<String>)

@Serializable
data class PayloadWithPayload(val payload: String, val flow: @Contextual Flow<PayloadWithStream>) {
    suspend fun collectAndPrint() {
        flow.collect {
            it.stream.collect { println("item $it") }
        }
    }
}

fun payload(index: Int = 0): PayloadWithStream {
    return PayloadWithStream(
        "test$index",
        flow { emit("a$index"); emit("b$index"); emit("c$index"); }
    )
}

fun payloadWithPayload(index: Int = 0): PayloadWithPayload {
    return PayloadWithPayload("test$index", payloadStream(index))
}

fun payloadStream(count: Int = 10): Flow<PayloadWithStream> {
    return flow {
        repeat(count) {
            emit(payload(it))
        }
    }
}

fun payloadWithPayloadStream(count: Int = 10): Flow<PayloadWithPayload> {
    return flow {
        repeat(count) {
            emit(payloadWithPayload(it))
        }
    }
}
