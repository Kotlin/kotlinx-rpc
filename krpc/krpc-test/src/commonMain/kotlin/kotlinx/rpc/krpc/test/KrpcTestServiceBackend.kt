/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.test

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.TestScope
import kotlinx.serialization.Serializable
import kotlin.coroutines.resumeWithException
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class KrpcTestServiceBackend : KrpcTestService {
    override fun nonSuspendFlow(): Flow<Int> {
        return flow {
            repeat(10) {
                delay(100)
                emit(it)
            }
        }
    }

    override fun nonSuspendFlowErrorOnEmit(): Flow<Int> {
        return flow {
            error("nonSuspendFlowErrorOnEmit")
        }
    }

    override fun nonSuspendFlowErrorOnReturn(): Flow<Int> {
        error("nonSuspendFlowErrorOnReturn")
    }

    override fun nonSuspendBidirectional(flow: Flow<Int>): Flow<Int> {
        return flow.map { it * 2 }
    }

    override fun nonSuspendBidirectionalPayload(payloadWithStream: PayloadWithStream): Flow<Int> {
        return payloadWithStream.stream.map { it.length }
    }

    @Suppress("detekt.EmptyFunctionBlock")
    override suspend fun empty() {}

    override suspend fun returnType(): String {
        return "test"
    }

    override suspend fun simpleWithParams(name: String): String {
        return name.reversed()
    }

    override suspend fun genericReturnType(): List<String> {
        return listOf("hello", "world")
    }

    override suspend fun doubleGenericReturnType(): List<List<String>> {
        return listOf(listOf("1", "2"), listOf("a", "b"))
    }

    override suspend fun paramsSingle(arg1: String) {
        assertEquals("test", arg1)
    }

    override suspend fun paramsDouble(arg1: String, arg2: String) {
        assertEquals("test", arg1)
        assertEquals("test2", arg2)
    }

    override suspend fun varargParams(arg1: String, vararg arg2: String) {
        assertEquals("test", arg1)
        assertEquals("test2", arg2[0])
        assertEquals("test3", arg2[1])
        assertEquals(emptyList(), arg2.drop(2))
    }

    override suspend fun genericParams(arg1: List<String>) {
        assertEquals(listOf("test", "test2", "test3"), arg1)
    }

    override suspend fun doubleGenericParams(arg1: List<List<String>>) {
        assertContentEquals(listOf(listOf("test", "test2", "test3")), arg1)
    }

    @Suppress("detekt.MaxLineLength")
    override suspend fun mapParams(arg1: Map<String, Map<Int, List<String>>>) {
        assertEquals(arg1.size, 1)
        assertContentEquals(arg1.keys, listOf("key"))
        assertEquals(arg1["key"]?.size, 1)
        assertContentEquals(arg1["key"]?.keys, listOf(1))
        assertContentEquals(arg1["key"]?.get(1), listOf("test", "test2", "test3"))
    }

    override suspend fun customType(arg1: TestClass): TestClass {
        return arg1
    }

    override suspend fun nullable(arg1: String?): TestClass? {
        return if (arg1 == null) null else TestClass()
    }

    override suspend fun variance(
        arg2: TestList<in TestClass>,
        arg3: TestList2<*>
    ): TestList<out TestClass> {
        assertEquals(arg2.value, 42)
        assertEquals(arg3.value, 42)
        return TestList(3)
    }

    override suspend fun nonSerializableClass(localDate: LocalDate): LocalDate {
        return LocalDate(localDate.year, localDate.month, localDate.day + 1)
    }

    override suspend fun nonSerializableClassWithSerializer(localDateTime: LocalDateTime): LocalDateTime {
        return LocalDateTime(
            date = LocalDate(
                year = localDateTime.date.year,
                month = localDateTime.date.month,
                day = localDateTime.date.day + 1,
            ),
            time = localDateTime.time,
        )
    }

    override suspend fun incomingStreamSyncCollect(arg1: Flow<String>): Int {
        return arg1.count()
    }

    override suspend fun incomingStreamSyncCollectMultiple(
        arg1: Flow<String>,
        arg2: Flow<String>,
        arg3: Flow<String>,
    ): Int {
        return arg1.count() + arg2.count() + arg3.count()
    }

    override fun outgoingStream(): Flow<String> {
        return flow { emit("a"); emit("b"); emit("c") }
    }

    override fun bidirectionalStream(arg1: Flow<String>): Flow<String> {
        return arg1.map { it.reversed() }
    }

    override fun echoStream(arg1: Flow<Int>): Flow<Int> = flow {
        arg1.collect {
            emit(it)
        }
    }

    override suspend fun streamInDataClass(payloadWithStream: PayloadWithStream): Int {
        return payloadWithStream.payload.length + payloadWithStream.stream.count()
    }

    override fun getNInts(n: Int): Flow<Int> {
        return flow {
            for (it in 1..n) {
                emit(it)
            }
        }
    }

    override fun getNIntsBatched(n: Int): Flow<List<Int>> {
        return flow {
            for (it in (1..n).chunked(1000)) {
                emit(it)
            }
        }
    }

    @Suppress("detekt.EmptyFunctionBlock")
    override suspend fun bytes(byteArray: ByteArray) { }

    @Suppress("detekt.EmptyFunctionBlock")
    override suspend fun nullableBytes(byteArray: ByteArray?) { }

    @Suppress("detekt.TooGenericExceptionThrown")
    override suspend fun throwsIllegalArgument(message: String) {
        throw IllegalArgumentException(message)
    }

    @Serializable
    class SerializableTestException(
        override val message: String?,
        override val cause: SerializableTestException? = null,
    ) : Exception()

    override suspend fun throwsSerializableWithMessageAndCause(message: String) {
        throw SerializableTestException(message, SerializableTestException("cause: $message"))
    }

    @Suppress("detekt.TooGenericExceptionThrown")
    override suspend fun throwsThrowable(message: String) {
        throw Throwable(message)
    }

    override suspend fun throwsUNSTOPPABLEThrowable(message: String) {
        suspendCancellableCoroutine<Unit> { continuation ->
            runThreadIfPossible {
                continuation.resumeWithException(Throwable(message))
            }
        }
    }

    override suspend fun nullableInt(v: Int?): Int? = v
    override suspend fun nullableList(v: List<Int>?): List<Int>? = v

    override fun delayForever(): Flow<Boolean> = flow {
        emit(true)
        delay(Int.MAX_VALUE.toLong())
    }

    override suspend fun answerToAnything(arg: String): Int {
        return 42
    }

    private suspend fun doWork(): String {
        delay(1)
        return "qwerty"
    }

    override suspend fun krpc173() {
        doWork()
    }

    override fun unitFlow(): Flow<Unit> {
        return flow {
            emit(Unit)
        }
    }
}

internal expect fun runThreadIfPossible(runner: () -> Unit)

internal expect fun TestScope.debugCoroutines()
