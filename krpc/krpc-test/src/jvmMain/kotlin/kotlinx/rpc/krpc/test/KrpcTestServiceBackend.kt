/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.test

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resumeWithException
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class KrpcTestServiceBackend(override val coroutineContext: CoroutineContext) : KrpcTestService {
    companion object {
        const val SHARED_FLOW_REPLAY = 5
    }

    override suspend fun empty() {
        println("empty")
    }

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
        println("SINGLE: $arg1")
    }

    override suspend fun paramsDouble(arg1: String, arg2: String) {
        println("double $arg1 $arg2")
    }

    override suspend fun varargParams(arg1: String, vararg arg2: String) {
        println("vararg $arg1 ${arg2.joinToString()}")
    }

    override suspend fun genericParams(arg1: List<String>) {
        println("Received list: ${arg1.joinToString()}")
    }

    override suspend fun doubleGenericParams(arg1: List<List<String>>) {
        println("Received list of lists: ${arg1.joinToString { it.joinToString() }} }}")
    }

    @Suppress("detekt.MaxLineLength")
    override suspend fun mapParams(arg1: Map<String, Map<Int, List<String>>>) {
        println("Received map: ${arg1.entries.joinToString { "${it.key} -> ${it.value.entries.joinToString { (key, value) -> "$key -> ${value.joinToString()}" }}" }}")
    }

    override suspend fun customType(arg1: TestClass): TestClass {
        return arg1
    }

    override suspend fun nullable(arg1: String?): TestClass? {
        println("nullable $arg1")
        return if (arg1 == null) null else TestClass()
    }

    override suspend fun variance(
        arg2: TestList<in TestClass>,
        arg3: TestList2<*>
    ): TestList<out TestClass> {
        println("variance: $arg2 $arg3")
        return TestList(3)
    }

    override suspend fun nonSerializableClass(localDate: LocalDate): LocalDate {
        return localDate.plusDays(1)
    }

    override suspend fun nonSerializableClassWithSerializer(localDateTime: LocalDateTime): String {
        return localDateTime.plusDays(1).format(DateTimeFormatter.ISO_DATE_TIME)
    }

    override suspend fun incomingStreamSyncCollect(arg1: Flow<String>): Int {
        return arg1.count()
    }

    @OptIn(DelicateCoroutinesApi::class)
    override suspend fun incomingStreamAsyncCollect(arg1: Flow<String>): Int {
        @Suppress("detekt.GlobalCoroutineUsage")
        GlobalScope.launch {
            arg1.collect { println("incomingStreamAsyncCollect item $it") }
        }
        return 5
    }

    override suspend fun outgoingStream(): Flow<String> {
        return flow { emit("a"); emit("b"); emit("c") }
    }

    override suspend fun bidirectionalStream(arg1: Flow<String>): Flow<String> {
        return arg1.map { it.reversed() }
    }

    override suspend fun echoStream(arg1: Flow<Int>): Flow<Int> = flow {
        arg1.collect {
            emit(it)
        }
    }

    override suspend fun streamInDataClass(payloadWithStream: PayloadWithStream): Int {
        return payloadWithStream.payload.length + payloadWithStream.stream.count()
    }

    // necessary for older Kotlin versions
    @Suppress("UnnecessaryOptInAnnotation")
    @OptIn(FlowPreview::class)
    override suspend fun streamInStream(payloadWithStream: Flow<PayloadWithStream>): Int {
        return payloadWithStream.flatMapConcat { it.stream }.count()
    }

    override suspend fun streamOutDataClass(): PayloadWithStream {
        return payload()
    }

    override suspend fun streamOfStreamsInReturn(): Flow<Flow<String>> {
        return flow {
            emit(flow { emit("a"); emit("b"); emit("c") })
            emit(flow { emit("1"); emit("2"); emit("3") })
        }
    }

    override suspend fun streamOfPayloadsInReturn(): Flow<PayloadWithStream> {
        return payloadStream()
    }

    override suspend fun streamInDataClassWithStream(payloadWithPayload: PayloadWithPayload): Int {
        payloadWithPayload.collectAndPrint()
        return 5
    }

    override suspend fun streamInStreamWithStream(payloadWithPayload: Flow<PayloadWithPayload>): Int {
        payloadWithPayload.collect {
            it.collectAndPrint()
        }
        return 5
    }

    override suspend fun returnPayloadWithPayload(): PayloadWithPayload {
        return payloadWithPayload()
    }

    override suspend fun returnFlowPayloadWithPayload(): Flow<PayloadWithPayload> {
        return payloadWithPayloadStream()
    }

    override suspend fun bidirectionalFlowOfPayloadWithPayload(
        payloadWithPayload: Flow<PayloadWithPayload>
    ): Flow<PayloadWithPayload> {
        return payloadWithPayload
    }

    override suspend fun getNInts(n: Int): Flow<Int> {
        return flow {
            for (it in 1..n) {
                emit(it)
            }
        }
    }

    override suspend fun getNIntsBatched(n: Int): Flow<List<Int>> {
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
            Thread {
                continuation.resumeWithException(Throwable(message))
            }.start()
        }
    }

    override suspend fun nullableInt(v: Int?): Int? = v
    override suspend fun nullableList(v: List<Int>?): List<Int>? = v

    override suspend fun delayForever(): Flow<Boolean> = flow {
        emit(true)
        delay(Int.MAX_VALUE.toLong())
    }

    override suspend fun answerToAnything(arg: String): Int {
        println("Return 42")
        return 42
    }

    override val plainFlowOfInts: Flow<Int> = plainFlow { it }

    override val plainFlowOfFlowsOfInts: Flow<Flow<Int>> = plainFlow { plainFlow { i -> i } }

    override val plainFlowOfFlowsOfFlowsOfInts: Flow<Flow<Flow<Int>>> = plainFlow { plainFlow { plainFlow { i -> i } } }

    override val sharedFlowOfInts: SharedFlow<Int> =
        sharedFlowOfT { it }

    override val sharedFlowOfFlowsOfInts: SharedFlow<SharedFlow<Int>> =
        sharedFlowOfT { sharedFlowOfT { it } }

    override val sharedFlowOfFlowsOfFlowsOfInts: SharedFlow<SharedFlow<SharedFlow<Int>>> =
        sharedFlowOfT { sharedFlowOfT { sharedFlowOfT { it } } }

    override val stateFlowOfInts: MutableStateFlow<Int> =
        stateFlowOfT { it }

    override val stateFlowOfFlowsOfInts: MutableStateFlow<MutableStateFlow<Int>> =
        stateFlowOfT { stateFlowOfT { it } }

    override val stateFlowOfFlowsOfFlowsOfInts: MutableStateFlow<MutableStateFlow<MutableStateFlow<Int>>> =
        stateFlowOfT { stateFlowOfT { stateFlowOfT { it } } }

    override suspend fun emitNextForStateFlowOfInts(value: Int) {
        stateFlowOfInts.emit(value)
    }

    override suspend fun emitNextForStateFlowOfFlowsOfInts(value: Int) {
        stateFlowOfFlowsOfInts.value.emit(value)
        stateFlowOfFlowsOfInts.emit(MutableStateFlow(value))
    }

    override suspend fun emitNextForStateFlowOfFlowsOfFlowsOfInts(value: Int) {
        stateFlowOfFlowsOfFlowsOfInts.value.value.emit(value)
        stateFlowOfFlowsOfFlowsOfInts.value.emit(MutableStateFlow(value))
        stateFlowOfFlowsOfFlowsOfInts.emit(MutableStateFlow(MutableStateFlow(value)))
    }

    override suspend fun sharedFlowInFunction(sharedFlow: SharedFlow<Int>): StateFlow<Int> {
        val state = MutableStateFlow(-1)

        launch {
            assertEquals(listOf(0, 1, 2, 3, 4), sharedFlow.take(5).toList())
            println("hello 1")
            state.emit(1)
            println("hello 2")
        }

        return state
    }

    override suspend fun stateFlowInFunction(stateFlow: StateFlow<Int>): StateFlow<Int> {
        val state = MutableStateFlow(-1)
        assertEquals(-1, stateFlow.value)

        launch {
            assertEquals(42, stateFlow.first { it == 42 })
            state.emit(1)
        }

        return state
    }
}
