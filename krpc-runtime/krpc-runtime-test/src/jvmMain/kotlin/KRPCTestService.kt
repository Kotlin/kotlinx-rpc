package org.jetbrains.krpc.test

import kotlinx.coroutines.flow.Flow
import org.jetbrains.krpc.RPC

interface KRPCTestService : RPC {
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

    suspend fun bidirectionalFlowOfPayloadWithPayload(payloadWithPayload: Flow<PayloadWithPayload>): Flow<PayloadWithPayload>

    suspend fun getNInts(n: Int): Flow<Int>
    suspend fun getNIntsBatched(n: Int): Flow<List<Int>>

    suspend fun bytes(byteArray: ByteArray)
    suspend fun nullableBytes(byteArray: ByteArray?)

    suspend fun throwsIllegalArgument(message: String)
    suspend fun throwsThrowable(message: String)
    suspend fun throwsUNSTOPPABLEThrowable(message: String)

    suspend fun nullableInt(v: Int?): Int?
    suspend fun nullableList(v: List<Int>?): List<Int>?
    suspend fun delayForever(): Flow<Boolean>

    suspend fun answerToAnything(arg: String): Int
}