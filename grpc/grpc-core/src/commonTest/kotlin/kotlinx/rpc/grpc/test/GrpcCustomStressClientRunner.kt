/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.rpc.grpc.GrpcClient
import kotlinx.rpc.withService
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Standalone client runner for the custom echo stress setup.
 *
 * Start the server separately (see GrpcCustomStressServerRunner) and then run this on the
 * platform you want to measure client performance for (e.g., JVM or Native).
 */
class GrpcCustomStressClientRunner {

    val concurrency = 60
    val iterationsPerWorker = 150
    val clientStreamBatch = 100
    val bidiBatch = 50

    data class Metrics(
        var unaryOk: Long = 0,
        var serverStreamMsgs: Long = 0,
        var clientStreamOk: Long = 0,
        var bidiMsgs: Long = 0,
        var failures: Long = 0,
    )

    private suspend fun runStress(
        id: Int,
        label: String,
        concurrency: Int,
        iterationsPerWorker: Int,
        unary: suspend (Metrics) -> Unit,
        serverStreaming: suspend (Metrics) -> Unit,
        clientStreaming: suspend (Metrics) -> Unit,
        bidiStreaming: suspend (Metrics) -> Unit,
    ) {
        val totalIterations = concurrency * iterationsPerWorker
        var completedIterations = 0L
        var liveFailures = 0L
        val progressLock = Mutex()

        val aggregate = Metrics()
        val lock = Mutex()

        coroutineScope {
            // Periodic progress reporter
            val reporter = launch {
                while (true) {
                    delay(1_000)
                    val (done, fails) = progressLock.withLock { completedIterations to liveFailures }
                    val percent = if (totalIterations == 0) 100 else (done * 100 / totalIterations)
                    println("[GrpcCustomStressClientRunner-$id][$label][progress] $done/$totalIterations ($percent%) failures=$fails")
                    if (done >= totalIterations.toLong()) break
                }
            }

            repeat(concurrency) { idx ->
                launch {
                    val rnd = Random(idx)
                    val local = Metrics()
                    repeat(iterationsPerWorker) {
                        val choice = rnd.nextInt(4)
                        try {
                            when (choice) {
                                0 -> unary(local)
                                1 -> serverStreaming(local)
                                2 -> clientStreaming(local)
                                else -> bidiStreaming(local)
                            }
                        } catch (ce: CancellationException) {
                            throw ce
                        } catch (t: Throwable) {
                            local.failures++
                            progressLock.withLock { liveFailures++ }
                        } finally {
                            progressLock.withLock { completedIterations++ }
                        }
                    }
                    lock.withLock {
                        aggregate.unaryOk += local.unaryOk
                        aggregate.serverStreamMsgs += local.serverStreamMsgs
                        aggregate.clientStreamOk += local.clientStreamOk
                        aggregate.bidiMsgs += local.bidiMsgs
                        aggregate.failures += local.failures
                    }
                }
            }

            // Ensure reporter finishes before leaving the scope
            reporter.join()
        }

        println(
            "[GrpcCustomStressClientRunner-$id][$label] unaryOk=${aggregate.unaryOk}, " +
                    "serverStreamMsgs=${aggregate.serverStreamMsgs}, clientStreamOk=${aggregate.clientStreamOk}, " +
                    "bidiMsgs=${aggregate.bidiMsgs}, failures=${aggregate.failures}"
        )

        val totalOps = aggregate.unaryOk + aggregate.clientStreamOk + aggregate.serverStreamMsgs + aggregate.bidiMsgs
        require(totalOps > 0) { "No operations completed in stress client" }
        assertEquals(0, aggregate.failures.toInt(), "Some operations failed during stress client")
    }

    // =====================
    // kotlinx-rpc runners
    // =====================
    @Test
    fun runMultipleClientsKotlinx() {
        val n = 10
        repeat(n) {
            runClientKotlinx()
        }
    }

    @Test
    fun runMultipleClientsConcurrentlyKotlinx() = runBlocking {
        val n = 10
        repeat(n) {
            launch {
                runTestKotlinx(it)
            }
        }
    }

    @Test
    fun runClientKotlinx() = runBlocking { runTestKotlinx() }

    suspend fun runTestKotlinx(id: Int = 0) {
        val host = "localhost"
        val port = 50051 // Proto EchoService server

        val grpcClient = GrpcClient(host, port) { usePlaintext() }
        println("[GrpcCustomStressClientRunner-$id][kotlinx] Connecting to $host:$port ...")
        try {
            val svc = grpcClient.withService<EchoService>()

            suspend fun unary(m: Metrics) = unaryEchoOnce(svc, m)
            suspend fun server(m: Metrics) = serverStreamingOnce(svc, m)
            suspend fun client(m: Metrics) = clientStreamingOnce(svc, clientStreamBatch, m)
            suspend fun bidi(m: Metrics) = bidiStreamingOnce(svc, bidiBatch, m)

            runStress(
                id = id,
                label = "kotlinx",
                concurrency = concurrency,
                iterationsPerWorker = iterationsPerWorker,
                unary = ::unary,
                serverStreaming = ::server,
                clientStreaming = ::client,
                bidiStreaming = ::bidi,
            )
        } finally {
            grpcClient.shutdown()
            grpcClient.awaitTermination()
        }
    }

    private suspend fun unaryEchoOnce(svc: EchoService, m: Metrics) {
        val msg = "hello"
        val res: EchoResponse = svc.UnaryEcho(EchoRequest { message = msg })
        assertEquals(msg, res.message)
        m.unaryOk++
    }

    private suspend fun serverStreamingOnce(svc: EchoService, m: Metrics) {
        val msg = "pong"
        val flow = svc.ServerStreamingEcho(EchoRequest { message = msg })
        var c = 0
        flow.onEach {
            assertEquals(msg, it.message)
            c++
        }.collect()
        assertEquals(5, c)
        m.serverStreamMsgs += c
    }

    private suspend fun clientStreamingOnce(svc: EchoService, batch: Int, m: Metrics) {
        val res = svc.ClientStreamingEcho(flow {
            repeat(batch) { i ->
                emit(EchoRequest { message = "m$i" })
            }
        })
        val expected = (0 until batch).joinToString(", ") { "m$it" }
        assertEquals(expected, res.message)
        m.clientStreamOk++
    }

    private suspend fun bidiStreamingOnce(svc: EchoService, batch: Int, m: Metrics) {
        val incoming: Flow<EchoResponse> = svc.BidirectionalStreamingEcho(flow {
            repeat(batch) {
                emit(EchoRequest { message = "z" })
            }
        })
        var c = 0
        incoming.onEach { resp ->
            assertEquals("z", resp.message)
            c++
        }.collect()
        assertEquals(batch, c)
        m.bidiMsgs += c
    }

    // =====================
    // grpc-kmp runners
    // =====================
    @Test
    fun runMultipleClientsKmp() {
        val n = 10
        repeat(n) {
            runClientKmp()
        }
    }

    @Test
    fun runMultipleClientsConcurrentlyKmp() = runBlocking {
        val n = 10
        repeat(n) {
            launch {
                runTestKmp(it)
            }
        }
    }

    @Test
    fun runClientKmp() = runBlocking { runTestKmp() }

    suspend fun runTestKmp(id: Int = 0) {
        val host = "localhost"
        val port = 50051 // Proto EchoService server (see RawClientTest.runServer)

        val channel = io.github.timortel.kmpgrpc.core.Channel.Builder
            .forAddress(host, port)
            .usePlaintext()
            .build()
        val stub = EchoGrpc.EchoServiceStub(channel)

        println("[GrpcCustomStressClientRunner-$id][kmp] Connecting to $host:$port ...")

        suspend fun unary(m: Metrics) = unaryEchoOnceKmp(stub, m)
        suspend fun server(m: Metrics) = serverStreamingOnceKmp(stub, m)
        suspend fun client(m: Metrics) = clientStreamingOnceKmp(stub, clientStreamBatch, m)
        suspend fun bidi(m: Metrics) = bidiStreamingOnceKmp(stub, bidiBatch, m)

        runStress(
            id = id,
            label = "kmp",
            concurrency = concurrency,
            iterationsPerWorker = iterationsPerWorker,
            unary = ::unary,
            serverStreaming = ::server,
            clientStreaming = ::client,
            bidiStreaming = ::bidi,
        )
    }

    private suspend fun unaryEchoOnceKmp(stub: EchoGrpc.EchoServiceStub, m: Metrics) {
        val msg = "hello"
        val res = stub.UnaryEcho(echoRequest { message = msg })
        assertEquals(msg, res.message)
        m.unaryOk++
    }

    private suspend fun serverStreamingOnceKmp(stub: EchoGrpc.EchoServiceStub, m: Metrics) {
        val msg = "pong"
        val flow = stub.ServerStreamingEcho(echoRequest { message = msg; serverStreamReps = 5u })
        var c = 0
        flow.onEach {
            assertEquals(msg, it.message)
            c++
        }.collect()
        // default repetitions in server implementation assumed 5
        assertEquals(5, c)
        m.serverStreamMsgs += c
    }

    private suspend fun clientStreamingOnceKmp(stub: EchoGrpc.EchoServiceStub, batch: Int, m: Metrics) {
        val res = stub.ClientStreamingEcho(flow {
            repeat(batch) { i ->
                emit(echoRequest { message = "m$i" })
            }
        })
        val expected = (0 until batch).joinToString(", ") { "m$it" }
        assertEquals(expected, res.message)
        m.clientStreamOk++
    }

    private suspend fun bidiStreamingOnceKmp(stub: EchoGrpc.EchoServiceStub, batch: Int, m: Metrics) {
        val incoming = stub.BidirectionalStreamingEcho(flow {
            repeat(batch) {
                emit(echoRequest { message = "z" })
            }
        })
        var c = 0
        incoming.onEach { resp ->
            assertEquals("z", resp.message)
            c++
        }.collect()
        assertEquals(batch, c)
        m.bidiMsgs += c
    }
}
