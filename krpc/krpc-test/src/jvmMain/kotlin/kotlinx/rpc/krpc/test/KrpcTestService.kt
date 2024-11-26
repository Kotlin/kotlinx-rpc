/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.test

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.rpc.RemoteService
import kotlinx.rpc.annotations.Rpc
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalDateTime

@Suppress("detekt.TooManyFunctions")
@Rpc
interface KrpcTestService : RemoteService {
    suspend fun empty()
    suspend fun returnType(): String
    suspend fun simpleWithParams(name: String): String
    suspend fun genericReturnType(): List<String>
    suspend fun doubleGenericReturnType(): List<List<String>>
    suspend fun paramsSingle(arg1: String)
    suspend fun paramsDouble(arg1: String, arg2: String)
    suspend fun varargParams(arg1: String, vararg arg2: String)
    suspend fun genericParams(arg1: List<String>)
    suspend fun doubleGenericParams(arg1: List<List<String>>)
    suspend fun mapParams(arg1: Map<String, Map<Int, List<String>>>)
    suspend fun customType(arg1: TestClass): TestClass
    suspend fun nullable(arg1: String?): TestClass?
    suspend fun variance(arg2: TestList<in TestClass>, arg3: TestList2<*>): TestList<out TestClass>?

    suspend fun nonSerializableClass(localDate: @Contextual LocalDate): LocalDate
    suspend fun nonSerializableClassWithSerializer(
        localDateTime: @Serializable(LocalDateTimeSerializer::class) LocalDateTime,
    ): String

    suspend fun incomingStreamSyncCollect(arg1: Flow<String>): Int
    suspend fun incomingStreamAsyncCollect(arg1: Flow<String>): Int
    suspend fun outgoingStream(): Flow<String>
    suspend fun bidirectionalStream(arg1: Flow<String>): Flow<String>
    suspend fun echoStream(arg1: Flow<Int>): Flow<Int>

    suspend fun streamInDataClass(payloadWithStream: PayloadWithStream): Int
    suspend fun streamInStream(payloadWithStream: Flow<PayloadWithStream>): Int

    suspend fun streamOutDataClass(): PayloadWithStream
    suspend fun streamOfStreamsInReturn(): Flow<Flow<String>>
    suspend fun streamOfPayloadsInReturn(): Flow<PayloadWithStream>

    suspend fun streamInDataClassWithStream(payloadWithPayload: PayloadWithPayload): Int
    suspend fun streamInStreamWithStream(payloadWithPayload: Flow<PayloadWithPayload>): Int
    suspend fun returnPayloadWithPayload(): PayloadWithPayload
    suspend fun returnFlowPayloadWithPayload(): Flow<PayloadWithPayload>

    suspend fun bidirectionalFlowOfPayloadWithPayload(
        payloadWithPayload: Flow<PayloadWithPayload>
    ): Flow<PayloadWithPayload>

    suspend fun getNInts(n: Int): Flow<Int>
    suspend fun getNIntsBatched(n: Int): Flow<List<Int>>

    suspend fun bytes(byteArray: ByteArray)
    suspend fun nullableBytes(byteArray: ByteArray?)

    suspend fun throwsIllegalArgument(message: String)
    suspend fun throwsSerializableWithMessageAndCause(message: String)
    suspend fun throwsThrowable(message: String)
    suspend fun throwsUNSTOPPABLEThrowable(message: String)

    suspend fun nullableInt(v: Int?): Int?
    suspend fun nullableList(v: List<Int>?): List<Int>?
    suspend fun delayForever(): Flow<Boolean>

    suspend fun answerToAnything(arg: String): Int

    val plainFlowOfInts : Flow<Int>

    val plainFlowOfFlowsOfInts : Flow<Flow<Int>>

    val plainFlowOfFlowsOfFlowsOfInts : Flow<Flow<Flow<Int>>>

    val sharedFlowOfInts : SharedFlow<Int>

    val sharedFlowOfFlowsOfInts : SharedFlow<SharedFlow<Int>>

    val sharedFlowOfFlowsOfFlowsOfInts : SharedFlow<SharedFlow<SharedFlow<Int>>>

    val stateFlowOfInts : StateFlow<Int>

    val stateFlowOfFlowsOfInts : StateFlow<StateFlow<Int>>

    val stateFlowOfFlowsOfFlowsOfInts : StateFlow<StateFlow<StateFlow<Int>>>

    suspend fun emitNextForStateFlowOfInts(value: Int)

    suspend fun emitNextForStateFlowOfFlowsOfInts(value: Int)

    suspend fun emitNextForStateFlowOfFlowsOfFlowsOfInts(value: Int)

    suspend fun sharedFlowInFunction(sharedFlow: SharedFlow<Int>): StateFlow<Int>

    suspend fun stateFlowInFunction(stateFlow: StateFlow<Int>): StateFlow<Int>
}
