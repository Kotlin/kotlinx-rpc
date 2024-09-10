/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("FunctionName")

package kotlinx.rpc.krpc.test

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.rpc.awaitFieldInitialization
import kotlinx.rpc.krpc.RPCTransport
import kotlinx.rpc.krpc.rpcClientConfig
import kotlinx.rpc.krpc.rpcServerConfig
import kotlinx.rpc.krpc.serialization.RPCSerialFormatConfiguration
import kotlinx.rpc.krpc.server.KRPCServer
import kotlinx.rpc.krpc.streamScoped
import kotlinx.rpc.registerService
import kotlinx.rpc.withService
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.rules.Timeout
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.cancellation.CancellationException
import kotlin.test.*

abstract class KRPCTransportTestBase {
    protected abstract val serializationConfig: RPCSerialFormatConfiguration.() -> Unit

    private val serverConfig by lazy {
        rpcServerConfig {
            sharedFlowParameters {
                replay = KRPCTestServiceBackend.SHARED_FLOW_REPLAY
            }

            serialization {
                serializationConfig()
            }
        }
    }

    private val clientConfig by lazy {
        rpcClientConfig {
            sharedFlowParameters {
                replay = KRPCTestServiceBackend.SHARED_FLOW_REPLAY
            }

            serialization {
                serializationConfig()
            }
        }
    }

    abstract val clientTransport: RPCTransport
    abstract val serverTransport: RPCTransport

    private lateinit var backend: KRPCServer
    private lateinit var client: KRPCTestService

    @BeforeTest
    fun start() {
        backend = KRPCTestServer(serverConfig, serverTransport)
        backend.registerService<KRPCTestService> { KRPCTestServiceBackend(it) }

        client = KRPCTestClient(clientConfig, clientTransport).withService()
    }

    @AfterTest
    fun end() {
        backend.cancel()
    }

    @Rule
    @JvmField
    val globalTimeout: Timeout = Timeout.seconds(30)

    @Test
    fun empty() {
        backend.cancel()
        client.cancel()
    }

    @Test
    fun returnType() {
        runBlocking {
            assertEquals("test", client.returnType())
        }
    }

    @Test
    fun simpleWithParams() {
        runBlocking {
            assertEquals("name".reversed(), client.simpleWithParams("name"))
        }
    }

    @Test
    @Ignore // works on my machine issue – timeouts on TC
    fun simpleWithParams100000() = runBlocking {
        repeat(100000) {
            assertEquals("name".reversed(), client.simpleWithParams("name"))
        }
    }

    @Test
    fun genericReturnType() {
        runBlocking {
            assertEquals(listOf("hello", "world"), client.genericReturnType())
        }
    }

    @Test
    fun doubleGenericReturnType() {
        val result = runBlocking { client.doubleGenericReturnType() }
        assertEquals(listOf(listOf("1", "2"), listOf("a", "b")), result)
    }

    @Test
    fun paramsSingle() {
        val result = runBlocking { client.paramsSingle("test") }
        assertEquals(Unit, result)
    }

    @Test
    fun paramsDouble() {
        val result = runBlocking { client.paramsDouble("test", "test2") }
        assertEquals(Unit, result)
    }

    @Test
    @Ignore
    fun varargParams() {
        val result = runBlocking { client.varargParams("test", "test2", "test3") }
        assertEquals(Unit, result)
    }

    @Test
    fun genericParams() {
        val result = runBlocking { client.genericParams(listOf("test", "test2", "test3")) }
        assertEquals(Unit, result)
    }

    @Test
    fun doubleGenericParams() {
        val result = runBlocking { client.doubleGenericParams(listOf(listOf("test", "test2", "test3"))) }
        assertEquals(Unit, result)
    }

    @Test
    fun mapParams() {
        val result = runBlocking { client.mapParams(mapOf("key" to mapOf(1 to listOf("test", "test2", "test3")))) }
        assertEquals(Unit, result)
    }

    @Test
    fun customType() {
        val result = runBlocking { client.customType(TestClass()) }
        assertEquals(TestClass(), result)
    }

    @Test
    open fun nullable() {
        val result = runBlocking { client.nullable("test") }
        assertEquals(TestClass(), result)

        val result2 = runBlocking { client.nullable(null) }
        assertEquals(null, result2)
    }

    @Test
    fun variance() {
        val result = runBlocking { client.variance(TestList(), TestList2<TestClass>()) }
        assertEquals(TestList<TestClass>(3), result)
    }

    @Test
    fun incomingStreamSyncCollect() {
        val result = runBlocking {
            streamScoped {
                client.incomingStreamSyncCollect(flowOf("test1", "test2", "test3"))
            }
        }
        assertEquals(3, result)
    }

    @Test
    fun outgoingStream() {
        runBlocking {
            streamScoped {
                val result = client.outgoingStream()
                assertEquals(listOf("a", "b", "c"), result.toList(mutableListOf()))
            }
        }
    }

    @Test
    fun bidirectionalStream() {
        runBlocking {
            streamScoped {
                val result = client.bidirectionalStream(flowOf("test1", "test2", "test3"))
                assertEquals(
                    listOf("test1".reversed(), "test2".reversed(), "test3".reversed()),
                    result.toList(mutableListOf()),
                )
            }
        }
    }

    @Test
    fun streamInDataClass() {
        runBlocking {
            streamScoped {
                val result = client.streamInDataClass(payload())
                assertEquals(8, result)
            }
        }
    }

    @Test
    fun streamInStream() {
        runBlocking {
            streamScoped {
                val result = client.streamInStream(payloadStream())
                assertEquals(30, result)
            }
        }
    }

    @Test
    fun streamOutDataClass() {
        runBlocking {
            streamScoped {
                val result = client.streamOutDataClass()
                assertEquals("test0", result.payload)
                assertEquals(listOf("a0", "b0", "c0"), result.stream.toList(mutableListOf()))
            }
        }
    }

    @Test
    fun streamOfStreamsInReturn() {
        runBlocking {
            streamScoped {
                val result = client.streamOfStreamsInReturn().map {
                    it.toList(mutableListOf())
                }.toList(mutableListOf())
                assertEquals(listOf(listOf("a", "b", "c"), listOf("1", "2", "3")), result)
            }
        }
    }

    @Test
    fun streamOfPayloadsInReturn() {
        runBlocking {
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
    }

    @Test
    fun streamInDataClassWithStream() {
        runBlocking {
            streamScoped {
                val result = client.streamInDataClassWithStream(payloadWithPayload())
                assertEquals(5, result)
            }
        }
    }

    @Test
    fun streamInStreamWithStream() {
        runBlocking {
            val result = streamScoped {
                client.streamInStreamWithStream(payloadWithPayloadStream())
            }
            assertEquals(5, result)
        }
    }

    @Test
    fun returnPayloadWithPayload() {
        runBlocking {
            streamScoped {
                client.returnPayloadWithPayload().collectAndPrint()
            }
        }
    }

    @Test
    fun returnFlowPayloadWithPayload() {
        runBlocking {
            streamScoped {
                client.returnFlowPayloadWithPayload().collect {
                    it.collectAndPrint()
                }
            }
        }
    }

    @Test
    fun bidirectionalFlowOfPayloadWithPayload() {
        runBlocking {
            streamScoped {
                val result = client.bidirectionalFlowOfPayloadWithPayload(
                    flow {
                        repeat(5) {
                            emit(payloadWithPayload(10))
                        }
                    },
                )

                result.collect {
                    it.collectAndPrint()
                }
            }
        }
    }

    @Test
    fun bidirectionalAsyncStream() {
        runBlocking {
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
    }

    @Test
    fun `RPC should be able to receive 100_000 ints in reasonable time`() {
        runBlocking {
            streamScoped {
                val n = 100_000
                assertEquals(client.getNInts(n).last(), n)
            }
        }
    }

    @Test
    fun `RPC should be able to receive 100_000 ints with batching in reasonable time`() {
        runBlocking {
            streamScoped {
                val n = 100_000
                assertEquals(client.getNIntsBatched(n).last().last(), n)
            }
        }
    }

    @Test
    open fun testByteArraySerialization() {
        runBlocking {
            client.bytes("hello".toByteArray())
            client.nullableBytes(null)
            client.nullableBytes("hello".toByteArray())
        }
    }

    @Test
    @Suppress("detekt.TooGenericExceptionCaught")
    fun testExceptionSerializationAndPropagating() {
        runBlocking {
            try {
                client.throwsIllegalArgument("me")
            } catch (e: IllegalArgumentException) {
                assertEquals("me", e.message)
            }
            try {
                client.throwsSerializableWithMessageAndCause("me")
            } catch (e: KRPCTestServiceBackend.SerializableTestException) {
                assertEquals("me", e.message)
                assertEquals("cause: me", e.cause?.message)
            }
            try {
                client.throwsThrowable("me")
            } catch (e: Throwable) {
                assertEquals("me", e.message)
            }
            try {
                client.throwsUNSTOPPABLEThrowable("me")
            } catch (e: Throwable) {
                assertEquals("me", e.message)
            }
        }
    }

    @Test
    open fun testNullables() {
        runBlocking {
            assertEquals(1, client.nullableInt(1))
            assertNull(client.nullable(null))
        }
    }

    @Test
    open fun testNullableLists() {
        runBlocking {
            assertNull(client.nullableList(null))

            assertEquals(emptyList<Int>(), client.nullableList(emptyList()))
            assertEquals(listOf(1), client.nullableList(listOf(1)))
        }
    }

    @Test
    fun testServerCallCancellation() {
        runBlocking {
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
    }

    @Test
    fun `rpc continuation is called in the correct scope and doesn't block other rpcs`() {
        runBlocking {
            val inContinuation = Semaphore(1)
            val running = AtomicBoolean(true)

            // start a coroutine that block the thread after continuation
            val c1 = async(Dispatchers.IO) { // make a rpc call
                client.answerToAnything("hello")

                // now we are in the continuation
                // important for test: don't use suspending primitives to signal it,
                // as we want to make sure we have a blocked thread,
                // when the second coroutine is launched
                inContinuation.release()

                // let's block the thread
                while (running.get()) {
                    // do nothing
                }
            }

            // wait, till the Rpc continuation thread is blocked
            assertTrue(inContinuation.tryAcquire(100, TimeUnit.MILLISECONDS))

            val c2 = async { // make a call
                // and make sure the continuation is executed,
                // even though another call's continuation has blocked its thread.
                client.answerToAnything("hello")
                running.set(false)
            }

            awaitAll(c1, c2)
        }
    }

    @Test
    fun testPlainFlowOfInts() {
        runBlocking {
            val flow = client.plainFlowOfInts.toList()
            assertEquals(List(5) { it }, flow)
        }
    }

    @Test
    fun testPlainFlowOfFlowsOfInts() {
        runBlocking {
            val lists = client.plainFlowOfFlowsOfInts.map {
                it.toList()
            }.toList()

            assertEquals(List(5) { List(5) { it } }, lists)
        }
    }

    @Test
    fun testPlainFlowOfFlowsOfFlowsOfInts() {
        runBlocking {
            val lists = client.plainFlowOfFlowsOfFlowsOfInts.map {
                it.map { i -> i.toList() }.toList()
            }.toList()

            assertEquals(List(5) { List(5) { List(5) { it } } }, lists)
        }
    }

    @Test
    fun testSharedFlowOfInts() {
        runBlocking {
            val list1 = client.sharedFlowOfInts.take(5).toList()
            val list2 = client.sharedFlowOfInts.take(3).toList()

            assertEquals(List(5) { it }, list1)
            assertEquals(List(3) { it }, list2)
        }
    }

    @Test
    fun testSharedFlowOfFlowsOfInts() {
        runBlocking {
            val list1 = client.sharedFlowOfFlowsOfInts.take(5).map {
                it.take(5).toList()
            }.toList()

            val list2 = client.sharedFlowOfFlowsOfInts.take(3).map {
                it.take(3).toList()
            }.toList()

            assertEquals(List(5) { List(5) { it } }, list1)
            assertEquals(List(3) { List(3) { it } }, list2)
        }
    }

    @Test
    fun testSharedFlowOfFlowsOfFlowsOfInts() {
        runBlocking {
            val list1 = client.sharedFlowOfFlowsOfFlowsOfInts.take(5).map {
                it.take(5).map { i -> i.take(5).toList() }.toList()
            }.toList()

            val list2 = client.sharedFlowOfFlowsOfFlowsOfInts.take(3).map {
                it.take(3).map { i -> i.take(3).toList() }.toList()
            }.toList()

            assertEquals(List(5) { List(5) { List(5) { it } } }, list1)
            assertEquals(List(3) { List(3) { List(3) { it } } }, list2)
        }
    }

    @Test
    fun testStateFlowOfInts() {
        runBlocking {
            val flow = client.awaitFieldInitialization { stateFlowOfInts }

            assertEquals(-1, flow.value)

            client.emitNextForStateFlowOfInts(42)

            assertEquals(42, flow.first { it == 42 })
        }
    }

    @Test
    fun testStateFlowOfFlowsOfInts() {
        runBlocking {
            val flow1 = client.awaitFieldInitialization { stateFlowOfFlowsOfInts }
            val flow2 = flow1.value

            assertEquals(-1, flow2.value)

            client.emitNextForStateFlowOfFlowsOfInts(42)

            assertEquals(42, flow2.first { it == 42 })
            assertEquals(42, flow1.first { it.value == 42 }.value)
        }
    }

    @Test
    fun testStateFlowOfFlowsOfFlowsOfInts() {
        runBlocking {
            val flow1 = client.awaitFieldInitialization { stateFlowOfFlowsOfFlowsOfInts }
            val flow2 = flow1.value
            val flow3 = flow2.value

            assertEquals(-1, flow3.value)

            client.emitNextForStateFlowOfFlowsOfFlowsOfInts(42)

            assertEquals(42, flow3.first { it == 42 })
            assertEquals(42, flow2.first { it.value == 42 }.value)
            assertEquals(42, flow1.first { it.value.value == 42 }.value.value)
        }
    }

    @Test
    fun testSharedFlowInFunction() {
        runBlocking {
            streamScoped {
                val flow = sharedFlowOfT { it }
                println("hello 1.1")

                val state = client.sharedFlowInFunction(flow)
                println("hello 1.2")

                assertEquals(1, state.first { it == 1 })
            }
        }
    }

    @Test
    fun testStateFlowInFunction() {
        runBlocking {
            streamScoped {
                val flow = stateFlowOfT { it }

                val state = client.stateFlowInFunction(flow)

                flow.emit(42)

                assertEquals(1, state.first { it == 1 })
            }
        }
    }

    @Test
    fun testAwaitAllFields() {
        runBlocking {
            with(client.awaitFieldInitialization()) {
                assertEquals(-1, stateFlowOfInts.value)
                assertEquals(-1, stateFlowOfFlowsOfInts.value.value)
                assertEquals(-1, stateFlowOfFlowsOfFlowsOfInts.value.value.value)
            }
        }
    }
}
