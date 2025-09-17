/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.test

import kotlinx.coroutines.flow.Flow
import kotlinx.rpc.annotations.Rpc
import kotlinx.serialization.Serializable

data class LocalDate(
    val year: Int,
    val month: Int,
    val day: Int,
) {
    override fun toString(): String {
        return "$year-$month-$day"
    }

    companion object {
        fun parse(str: String): LocalDate = str.split('-').let {
            LocalDate(it[0].toInt(), it[1].toInt(), it[2].toInt())
        }
    }
}

data class LocalDateTime(
    val date: LocalDate,
    val time: String,
) {
    override fun toString(): String {
        return "$date $time"
    }

    companion object {
        fun parse(str: String): LocalDateTime = str.split(' ').let {
            LocalDateTime(LocalDate.parse(it[0]), it[1])
        }
    }
}

@Suppress("detekt.TooManyFunctions")
@Rpc
interface KrpcTestService {
    fun nonSuspendFlow(): Flow<Int>
    fun nonSuspendFlowErrorOnEmit(): Flow<Int>
    fun nonSuspendFlowErrorOnReturn(): Flow<Int>
    fun nonSuspendBidirectional(flow: Flow<Int>): Flow<Int>
    fun nonSuspendBidirectionalPayload(payloadWithStream: PayloadWithStream): Flow<Int>

    fun slowConsumer(): Flow<Int>
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
    suspend fun nullableParam(arg1: String?): String
    suspend fun nullableReturn(returnNull: Boolean): TestClass?
    suspend fun variance(arg2: TestList<in TestClass>, arg3: TestList2<TestClass>): TestList<out TestClass>?
    suspend fun collectOnce(flow: Flow<String>)
    suspend fun returnTestClassThatThrowsWhileDeserialization(value: Int): TestClassThatThrowsWhileDeserialization

    suspend fun nonSerializableClass(localDate: LocalDate): LocalDate
    suspend fun nonSerializableClassWithSerializer(
        localDateTime: @Serializable(LocalDateTimeSerializer::class) LocalDateTime,
    ): @Serializable(LocalDateTimeSerializer::class) LocalDateTime
    suspend fun nullableNonSerializableClass(localDate: LocalDate?): LocalDate?

    suspend fun incomingStreamSyncCollect(arg1: Flow<String>): Int
    suspend fun incomingStreamSyncCollectMultiple(arg1: Flow<String>, arg2: Flow<String>, arg3: Flow<String>): Int
    fun outgoingStream(): Flow<String>
    fun bidirectionalStream(arg1: Flow<String>): Flow<String>
    fun echoStream(arg1: Flow<Int>): Flow<Int>

    suspend fun streamInDataClass(payloadWithStream: PayloadWithStream): Int

    fun getNInts(n: Int): Flow<Int>
    fun getNIntsBatched(n: Int): Flow<List<Int>>

    suspend fun bytes(byteArray: ByteArray)
    suspend fun nullableBytes(byteArray: ByteArray?)

    suspend fun throwsIllegalArgument(message: String)
    suspend fun throwsSerializableWithMessageAndCause(message: String)
    suspend fun throwsThrowable(message: String)
    suspend fun throwsUNSTOPPABLEThrowable(message: String)

    suspend fun nullableInt(v: Int?): Int?
    suspend fun nullableList(v: List<Int>?): List<Int>?
    suspend fun nullableEnum(enum: TestEnum?): TestEnum?
    fun delayForever(): Flow<Boolean>

    suspend fun answerToAnything(arg: String): Int

    suspend fun krpc173()

    fun unitFlow(): Flow<Unit>

    @Serializable
    enum class TestEnum {
        ENUM_VALUE_1, ENUM_VALUE_2
    }
}
