/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("FunctionName")

package kotlinx.rpc.krpc.test

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.test.runTest
import kotlinx.rpc.awaitFieldInitialization
import kotlinx.rpc.krpc.KrpcTransport
import kotlinx.rpc.krpc.rpcClientConfig
import kotlinx.rpc.krpc.rpcServerConfig
import kotlinx.rpc.krpc.serialization.KrpcSerialFormatConfiguration
import kotlinx.rpc.krpc.server.KrpcServer
import kotlinx.rpc.krpc.streamScoped
import kotlinx.rpc.registerService
import kotlinx.rpc.withService
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.SerializersModule
import kotlin.coroutines.cancellation.CancellationException
import kotlin.test.*

internal object LocalDateSerializer : KSerializer<LocalDate> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LocalDate", PrimitiveKind.STRING)

    override fun serialize(
        encoder: Encoder,
        value: LocalDate,
    ) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): LocalDate {
        return LocalDate.parse(decoder.decodeString())
    }
}

internal object LocalDateTimeSerializer : KSerializer<LocalDateTime> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LocalDateTime", PrimitiveKind.STRING)

    override fun serialize(
        encoder: Encoder,
        value: LocalDateTime,
    ) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): LocalDateTime {
        return LocalDateTime.parse(decoder.decodeString())
    }
}

abstract class KrpcTransportTestBase {
    protected val serializersModule = SerializersModule {
        contextual(LocalDate::class) { LocalDateSerializer }
    }

    protected abstract val serializationConfig: KrpcSerialFormatConfiguration.() -> Unit

    private val serverConfig by lazy {
        rpcServerConfig {
            sharedFlowParameters {
                replay = KrpcTestServiceBackend.SHARED_FLOW_REPLAY
            }

            serialization {
                serializationConfig()
            }
        }
    }

    private val clientConfig by lazy {
        rpcClientConfig {
            sharedFlowParameters {
                replay = KrpcTestServiceBackend.SHARED_FLOW_REPLAY
            }

            serialization {
                serializationConfig()
            }
        }
    }

    abstract val clientTransport: KrpcTransport
    abstract val serverTransport: KrpcTransport

    private lateinit var backend: KrpcServer
    private lateinit var client: KrpcTestService
    private lateinit var server: KrpcTestServiceBackend

    @BeforeTest
    fun start() {
        backend = KrpcTestServer(serverConfig, serverTransport)
        backend.registerService<KrpcTestService> { ctx ->
            KrpcTestServiceBackend(ctx).also {
                server = it
            }
        }

        client = KrpcTestClient(clientConfig, clientTransport).withService()
    }

    @AfterTest
    fun end() {
        backend.cancel()
    }

    @Test
    fun nonSuspend() = runTest {
        assertEquals(List(10) { it }, client.nonSuspendFlow().toList())
    }

    @Test
    fun nonSuspendErrorOnEmit() = runTest {
        val flow = client.nonSuspendFlowErrorOnReturn()
        assertFails {
            flow.toList()
        }
    }

    @Test
    fun nonSuspendErrorOnReturn() = runTest {
        assertFails {
            client.nonSuspendFlowErrorOnReturn().toList()
        }
    }

    @Test
    fun nonSuspendBidirectional() = runTest {
        assertEquals(
            expected = List(10) { it * 2 },
            actual = client.nonSuspendBidirectional(List(10) { it }.asFlow()).toList(),
        )
        print(1)
    }

    @Test
    fun nonSuspendBidirectionalPayload() = runTest {
        assertEquals(
            expected = List(3) { 2 },
            actual = client.nonSuspendBidirectionalPayload(payload(0)).toList(),
        )
    }

    @Test
    fun empty() {
        backend.cancel()
        client.cancel()
    }

    @Test
    fun returnType() = runTest {
        assertEquals("test", client.returnType())
    }

    @Test
    fun simpleWithParams() = runTest {
        assertEquals("name".reversed(), client.simpleWithParams("name"))
    }

    @Test
    @Ignore // works on my machine issue – timeouts on TC
    fun simpleWithParams100000() = runTest {
        repeat(100000) {
            assertEquals("name".reversed(), client.simpleWithParams("name"))
        }
    }

    @Test
    fun genericReturnType() = runTest {
        assertEquals(listOf("hello", "world"), client.genericReturnType())
    }

    @Test
    fun nonSerializableParameter() = runTest {
        val localDate = LocalDate(2001, 8, 23)
        val resultDate = client.nonSerializableClass(localDate)
        assertEquals(LocalDate(2001, 8, 24), resultDate)

        val localDateTime = LocalDateTime(LocalDate(2001, 8, 23), "17:03")
        val resultDateTime = client.nonSerializableClassWithSerializer(localDateTime)

        assertEquals(
            LocalDateTime(LocalDate(2001, 8, 24), "17:03").toString(),
            resultDateTime,
        )
    }

    @Test
    fun doubleGenericReturnType() = runTest {
        val result = client.doubleGenericReturnType()
        assertEquals(listOf(listOf("1", "2"), listOf("a", "b")), result)
    }

    @Test
    fun paramsSingle() = runTest {
        val result = client.paramsSingle("test")
        assertEquals(Unit, result)
    }

    @Test
    fun paramsDouble() = runTest {
        val result = client.paramsDouble("test", "test2")
        assertEquals(Unit, result)
    }

    @Test
    @Ignore
    fun varargParams() = runTest {
        val result = client.varargParams("test", "test2", "test3")
        assertEquals(Unit, result)
    }

    @Test
    fun genericParams() = runTest {
        val result = client.genericParams(listOf("test", "test2", "test3"))
        assertEquals(Unit, result)
    }

    @Test
    fun doubleGenericParams() = runTest {
        val result = client.doubleGenericParams(listOf(listOf("test", "test2", "test3")))
        assertEquals(Unit, result)
    }

    @Test
    fun mapParams() = runTest {
        val result = client.mapParams(mapOf("key" to mapOf(1 to listOf("test", "test2", "test3"))))
        assertEquals(Unit, result)
    }

    @Test
    fun customType() = runTest {
        val result = client.customType(TestClass())
        assertEquals(TestClass(), result)
    }

    @Test
    open fun nullable() = runTest {
        val result = client.nullable("test")
        assertEquals(TestClass(), result)

        val result2 = client.nullable(null)
        assertEquals(null, result2)
    }

    @Test
    fun variance() = runTest {
        val result = client.variance(TestList(), TestList2<TestClass>())
        assertEquals(TestList(3), result)
    }

    @Test
    fun incomingStreamSyncCollect() = runTest {
        val result = streamScoped {
            client.incomingStreamSyncCollect(flowOf("test1", "test2", "test3"))
        }

        assertEquals(3, result)
    }

    @Test
    fun incomingStreamAsyncCollect() = runTest {
        val result = streamScoped {
            client.incomingStreamAsyncCollect(flowOf("test1", "test2", "test3")).also {
                server.incomingStreamAsyncCollectLatch.await()
            }
        }

        assertEquals(5, result)
    }

    @Test
    fun outgoingStream() = runTest {
        streamScoped {
            val result = client.outgoingStream()
            assertEquals(listOf("a", "b", "c"), result.toList(mutableListOf()))
        }
    }

    @Test
    fun bidirectionalStream() = runTest {
        streamScoped {
            val result = client.bidirectionalStream(flowOf("test1", "test2", "test3"))
            assertEquals(
                listOf("test1".reversed(), "test2".reversed(), "test3".reversed()),
                result.toList(mutableListOf()),
            )
        }
    }

    @Test
    fun streamInDataClass() = runTest {
        streamScoped {
            val result = client.streamInDataClass(payload())
            assertEquals(8, result)
        }
    }

    @Test
    fun streamInStream() = runTest {
        streamScoped {
            val result = client.streamInStream(payloadStream())
            assertEquals(30, result)
        }
    }

    @Test
    fun streamOutDataClass() = runTest {
        streamScoped {
            val result = client.streamOutDataClass()
            assertEquals("test0", result.payload)
            assertEquals(listOf("a0", "b0", "c0"), result.stream.toList(mutableListOf()))
        }
    }

    @Test
    fun streamOfStreamsInReturn() = runTest {
        streamScoped {
            val result = client.streamOfStreamsInReturn().map {
                it.toList(mutableListOf())
            }.toList(mutableListOf())
            assertEquals(listOf(listOf("a", "b", "c"), listOf("1", "2", "3")), result)
        }
    }

    @Test
    fun streamOfPayloadsInReturn() = runTest {
        streamScoped {
            val result = client.streamOfPayloadsInReturn().map {
                it.stream.toList(mutableListOf()).joinToString()
            }.toList(mutableListOf()).joinToString()
            assertEquals(
                "a0, b0, c0, a1, b1, c1, a2, b2, c2, a3, b3, c3, a4, " +
                        "b4, c4, a5, b5, c5, a6, b6, c6, a7, b7, c7, a8, b8, c8, a9, b9, c9",
                result,
            )
        }
    }

    @Test
    fun streamInDataClassWithStream() = runTest {
        streamScoped {
            val result = client.streamInDataClassWithStream(payloadWithPayload())
            assertEquals(5, result)
        }
    }

    @Test
    fun streamInStreamWithStream() = runTest {
        val result = streamScoped {
            client.streamInStreamWithStream(payloadWithPayloadStream())
        }
        assertEquals(5, result)
    }

    @Test
    fun returnPayloadWithPayload() = runTest {
        streamScoped {
            assertContentEquals(expectedPayloadWithPayload(10), client.returnPayloadWithPayload().collect())
        }
    }

    @Test
    fun returnFlowPayloadWithPayload() = runTest {
        streamScoped {
            client.returnFlowPayloadWithPayload().collectIndexed { index, payloadWithPayload ->
                assertContentEquals(expectedPayloadWithPayload(index), payloadWithPayload.collect())
            }
        }
    }

    @Test
    fun bidirectionalFlowOfPayloadWithPayload() = runTest {
        streamScoped {
            val result = client.bidirectionalFlowOfPayloadWithPayload(
                flow {
                    repeat(5) {
                        emit(payloadWithPayload(10))
                    }
                },
            )

            val all = result.toList().onEach {
                assertContentEquals(expectedPayloadWithPayload(10), it.collect())
            }.size

            assertEquals(5, all)
        }
    }

    @Test
    fun bidirectionalAsyncStream() = runTest {
        streamScoped {
            val flow = MutableSharedFlow<Int>(1)
            val result = client.echoStream(flow.take(10))
            launch {
                var id = 0
                result.collect {
                    assertEquals(id, it)
                    id++
                    flow.emit(id)
                }
            }

            flow.emit(0)
        }
    }

    @Test
    fun `RPC should be able to receive 100_000 ints in reasonable time`() = runTest {
        streamScoped {
            val n = 100_000
            assertEquals(client.getNInts(n).last(), n)
        }
    }

    @Test
    fun `RPC should be able to receive 100_000 ints with batching in reasonable time`() = runTest {
        streamScoped {
            val n = 100_000
            assertEquals(client.getNIntsBatched(n).last().last(), n)
        }
    }

    @Test
    open fun testByteArraySerialization() = runTest {
        client.bytes("hello".encodeToByteArray())
        client.nullableBytes(null)
        client.nullableBytes("hello".encodeToByteArray())
    }

    @Test
    @Suppress("detekt.TooGenericExceptionCaught", "detekt.ThrowsCount")
    fun testExceptionSerializationAndPropagating() = runTest {
        try {
            client.throwsIllegalArgument("me")
            fail("Exception expected: throwsIllegalArgument")
        } catch (e : AssertionError) {
            throw e
        } catch (e: Throwable) {
            assertEquals("me", e.message)
        }
        try {
            client.throwsSerializableWithMessageAndCause("me")
            fail("Exception expected: throwsSerializableWithMessageAndCause")
        } catch (e : AssertionError) {
            throw e
        } catch (e: Throwable) {
            assertEquals("me", e.message)
            assertEquals("cause: me", e.cause?.message)
        }
        try {
            client.throwsThrowable("me")
            fail("Exception expected: throwsThrowable")
        } catch (e : AssertionError) {
            throw e
        } catch (e: Throwable) {
            assertEquals("me", e.message)
        }
        try {
            client.throwsUNSTOPPABLEThrowable("me")
            fail("Exception expected: throwsUNSTOPPABLEThrowable")
        } catch (e : AssertionError) {
            throw e
        } catch (e: Throwable) {
            assertEquals("me", e.message)
        }
    }

    @Test
    open fun testNullables() = runTest {
        assertEquals(1, client.nullableInt(1))
        assertNull(client.nullable(null))
    }

    @Test
    open fun testNullableLists() = runTest {
        assertNull(client.nullableList(null))

        assertEquals(emptyList<Int>(), client.nullableList(emptyList()))
        assertEquals(listOf(1), client.nullableList(listOf(1)))
    }

    @Test
    fun testServerCallCancellation() = runTest {
        val flag: Channel<Boolean> = Channel()
        val remote = launch {
            try {
                streamScoped {
                    client.delayForever().collect {
                        flag.send(it)
                    }
                }
            } catch (e: CancellationException) {
                throw e
            }

            fail("Shall not pass here")
        }.apply {
            invokeOnCompletion { cause: Throwable? ->
                if (cause != null && cause !is CancellationException) {
                    throw cause
                }
            }
        }

        flag.receive()
        remote.cancelAndJoin()
    }

    class AtomicTest {
        val atomic = atomic(true)
    }

    @Test
    fun `rpc continuation is called in the correct scope and doesn't block other rpcs`() = runTest {
        if (isJs) {
            println("Test is skipped on JS, because it doesn't support multiple threads.")
            return@runTest
        }

        val inContinuation = Semaphore(1)
        val running = AtomicTest()

        // start a coroutine that block the thread after continuation
        val c1 = async(Dispatchers.Default) { // make a rpc call
            client.answerToAnything("hello")

            // now we are in the continuation
            // important for test: don't use suspending primitives to signal it,
            // as we want to make sure we have a blocked thread,
            // when the second coroutine is launched
            inContinuation.release()

            // let's block the thread
            while (running.atomic.value) {
                // do nothing
            }
        }

        // wait, till the Rpc continuation thread is blocked
        delay(100)
        assertTrue(inContinuation.tryAcquire())

        val c2 = async { // make a call
            // and make sure the continuation is executed,
            // even though another call's continuation has blocked its thread.
            client.answerToAnything("hello")
            running.atomic.compareAndSet(expect = true, update = false)
        }

        awaitAll(c1, c2)
    }

    @Test
    fun testKrpc173() = runTest {
        assertEquals(Unit, client.krpc173())
    }

    @Test
    fun testUnitFlow() = runTest {
        assertEquals(Unit, client.unitFlow().toList().single())
    }

    @Test
    fun testPlainFlowOfInts() = runTest {
        val flow = client.plainFlowOfInts.toList()
        assertEquals(List(5) { it }, flow)
    }

    @Test
    fun testPlainFlowOfFlowsOfInts() = runTest {
        val lists = client.plainFlowOfFlowsOfInts.map {
            it.toList()
        }.toList()

        assertEquals(List(5) { List(5) { it } }, lists)
    }

    @Test
    fun testPlainFlowOfFlowsOfFlowsOfInts() = runTest {
        val lists = client.plainFlowOfFlowsOfFlowsOfInts.map {
            it.map { i -> i.toList() }.toList()
        }.toList()

        assertEquals(List(5) { List(5) { List(5) { it } } }, lists)
    }

    @Test
    fun testSharedFlowOfInts() = runTest {
        val list1 = client.sharedFlowOfInts.take(5).toList()
        val list2 = client.sharedFlowOfInts.take(3).toList()

        assertEquals(List(5) { it }, list1)
        assertEquals(List(3) { it }, list2)
    }

    @Test
    fun testSharedFlowOfFlowsOfInts() = runTest {
        val list1 = client.sharedFlowOfFlowsOfInts.take(5).map {
            it.take(5).toList()
        }.toList()

        val list2 = client.sharedFlowOfFlowsOfInts.take(3).map {
            it.take(3).toList()
        }.toList()

        assertEquals(List(5) { List(5) { it } }, list1)
        assertEquals(List(3) { List(3) { it } }, list2)
    }

    @Test
    fun testSharedFlowOfFlowsOfFlowsOfInts() = runTest {
        val list1 = client.sharedFlowOfFlowsOfFlowsOfInts.take(5).map {
            it.take(5).map { i -> i.take(5).toList() }.toList()
        }.toList()

        val list2 = client.sharedFlowOfFlowsOfFlowsOfInts.take(3).map {
            it.take(3).map { i -> i.take(3).toList() }.toList()
        }.toList()

        assertEquals(List(5) { List(5) { List(5) { it } } }, list1)
        assertEquals(List(3) { List(3) { List(3) { it } } }, list2)
    }

    @Test
    fun testStateFlowOfInts() = runTest {
        val flow = client.awaitFieldInitialization { stateFlowOfInts }

        assertEquals(-1, flow.value)

        client.emitNextForStateFlowOfInts(42)

        assertEquals(42, flow.first { it == 42 })
    }

    @Test
    fun testStateFlowOfFlowsOfInts() = runTest {
        val flow1 = client.awaitFieldInitialization { stateFlowOfFlowsOfInts }
        val flow2 = flow1.value

        assertEquals(-1, flow2.value)

        client.emitNextForStateFlowOfFlowsOfInts(42)

        assertEquals(42, flow2.first { it == 42 })
        assertEquals(42, flow1.first { it.value == 42 }.value)
    }

    @Test
    fun testStateFlowOfFlowsOfFlowsOfInts() = runTest {
        val flow1 = client.awaitFieldInitialization { stateFlowOfFlowsOfFlowsOfInts }
        val flow2 = flow1.value
        val flow3 = flow2.value

        assertEquals(-1, flow3.value)

        client.emitNextForStateFlowOfFlowsOfFlowsOfInts(42)

        assertEquals(42, flow3.first { it == 42 })
        assertEquals(42, flow2.first { it.value == 42 }.value)
        assertEquals(42, flow1.first { it.value.value == 42 }.value.value)
    }

    @Test
    fun testSharedFlowInFunction() = runTest {
        streamScoped {
            val flow = sharedFlowOfT { it }

            val state = client.sharedFlowInFunction(flow)

            assertEquals(1, state.first { it == 1 })
        }
    }

    @Test
    fun testStateFlowInFunction() = runTest {
        streamScoped {
            val flow = stateFlowOfT { it }

            val state = client.stateFlowInFunction(flow)

            flow.emit(42)

            assertEquals(1, state.first { it == 1 })
        }
    }

    @Test
    fun testAwaitAllFields() = runTest {
        with(client.awaitFieldInitialization()) {
            assertEquals(-1, stateFlowOfInts.value)
            assertEquals(-1, stateFlowOfFlowsOfInts.value.value)
            assertEquals(-1, stateFlowOfFlowsOfFlowsOfInts.value.value.value)
        }
    }

    companion object {
        fun expectedPayloadWithPayload(size: Int) = List(size) { listOf("a$it", "b$it", "c$it") }
    }
}

internal expect val isJs: Boolean
