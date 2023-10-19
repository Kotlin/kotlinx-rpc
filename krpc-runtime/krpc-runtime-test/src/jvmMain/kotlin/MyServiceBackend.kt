package org.jetbrains.krpc.test

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resumeWithException

@OptIn(ExperimentalCoroutinesApi::class)
class MyServiceBackend : MyService {
    override val coroutineContext: CoroutineContext = Job()

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

    override suspend fun incomingStreamSyncCollect(arg1: Flow<String>): Int {
        return arg1.count()
    }

    @OptIn(DelicateCoroutinesApi::class)
    override suspend fun incomingStreamAsyncCollect(arg1: Flow<String>): Int {
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

    override suspend fun bidirectionalFlowOfPayloadWithPayload(payloadWithPayload: Flow<PayloadWithPayload>): Flow<PayloadWithPayload> {
         return payloadWithPayload
    }

    override suspend fun getNInts(n: Int): Flow<Int> {
        return flow {
            (1..n).forEach {
                emit(it)
            }
        }
    }

    override suspend fun getNIntsBatched(n: Int): Flow<List<Int>> {
        return flow {
            (1..n).chunked(1000).forEach {
                emit(it)
            }
        }
    }

    override suspend fun bytes(byteArray: ByteArray) {
    }

    override suspend fun nullableBytes(byteArray: ByteArray?) {
    }

    override suspend fun throwsIllegalArgument(message: String) {
        throw IllegalArgumentException(message)
    }

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

}