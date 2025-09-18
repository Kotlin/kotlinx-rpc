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
import kotlinx.io.Buffer
import kotlinx.io.Source
import kotlinx.io.readString
import kotlinx.io.writeString
import kotlinx.rpc.RpcServer
import kotlinx.rpc.grpc.GrpcClient
import kotlinx.rpc.grpc.GrpcServer
import kotlinx.rpc.grpc.annotations.Grpc
import kotlinx.rpc.grpc.codec.SourcedMessageCodec
import kotlinx.rpc.grpc.codec.WithCodec
import kotlinx.rpc.registerService
import kotlinx.rpc.withService
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

private const val SERVER_STREAM_REPS: Int = 5

@WithCodec(EchoMsgRequest.Companion::class)
class EchoMsgRequest(
    val message: String,
) {
    companion object : SourcedMessageCodec<EchoMsgRequest> {
        override fun encodeToSource(value: EchoMsgRequest): Source {
            // Minimal format: just the message string
            return Buffer().apply {
                writeString(value.message)
            }
        }

        override fun decodeFromSource(stream: Source): EchoMsgRequest {
            val message = stream.readString()
            return EchoMsgRequest(message)
        }
    }
}

@WithCodec(EchoMsgResponse.Companion::class)
class EchoMsgResponse(val message: String) {
    companion object : SourcedMessageCodec<EchoMsgResponse> {
        override fun encodeToSource(value: EchoMsgResponse): Source {
            return Buffer().apply { writeString(value.message) }
        }

        override fun decodeFromSource(stream: Source): EchoMsgResponse {
            return EchoMsgResponse(stream.readString())
        }
    }
}

@Grpc
interface CustomEchoService {
    suspend fun UnaryEcho(message: EchoMsgRequest): EchoMsgResponse
    fun ServerStreamingEcho(message: EchoMsgRequest): Flow<EchoMsgResponse>
    suspend fun ClientStreamingEcho(message: Flow<EchoMsgRequest>): EchoMsgResponse
    fun BidirectionalStreamingEcho(message: Flow<EchoMsgRequest>): Flow<EchoMsgResponse>
}

class CustomEchoServiceImpl : CustomEchoService {
    private companion object {
        const val SERVER_STREAM_REPS = 5
    }

    override suspend fun UnaryEcho(message: EchoMsgRequest): EchoMsgResponse {
        // No artificial delay; minimal request contains only the message
        return EchoMsgResponse(message.message)
    }

    override fun ServerStreamingEcho(message: EchoMsgRequest): Flow<EchoMsgResponse> {
        return flow {
            repeat(SERVER_STREAM_REPS) {
                emit(EchoMsgResponse(message.message))
            }
        }
    }

    override suspend fun ClientStreamingEcho(message: Flow<EchoMsgRequest>): EchoMsgResponse {
        var first = true
        val sb = StringBuilder()
        message.collect {
            if (!first) sb.append(", ") else first = false
            sb.append(it.message)
        }
        return EchoMsgResponse(sb.toString())
    }

    override fun BidirectionalStreamingEcho(message: Flow<EchoMsgRequest>): Flow<EchoMsgResponse> {
        return flow {
            message.collect { req ->
                emit(EchoMsgResponse(req.message))
            }
        }
    }
}

class GrpcCustomStressTest {
    data class Metrics(
        var unaryOk: Long = 0,
        var serverStreamMsgs: Long = 0,
        var clientStreamOk: Long = 0,
        var bidiMsgs: Long = 0,
        var failures: Long = 0,
    )

    @Test
    fun stressEchoCustom() = runBlocking {
        val port = 18080
        val grpcServer = GrpcServer(
            port,
            builder = {
                registerService<CustomEchoService> { CustomEchoServiceImpl() }
            }
        )
        grpcServer.start()

        val grpcClient = GrpcClient("localhost", port) {
            usePlaintext()
        }

        try {
            val svc = grpcClient.withService<CustomEchoService>()
            val concurrency = 60
            val iterationsPerWorker = 100
            val clientStreamBatch = 100
            val bidiBatch = 50

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
                        println("[GrpcCustomStressTest][progress] $done/$totalIterations ($percent%) failures=$fails")
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
                                    0 -> unaryEchoOnce(svc, local)
                                    1 -> serverStreamingOnce(svc, local)
                                    2 -> clientStreamingOnce(svc, clientStreamBatch, local)
                                    else -> bidiStreamingOnce(svc, bidiBatch, local)
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
                "[GrpcCustomStressTest] unaryOk=${aggregate.unaryOk}, " +
                        "serverStreamMsgs=${aggregate.serverStreamMsgs}, clientStreamOk=${aggregate.clientStreamOk}, " +
                        "bidiMsgs=${aggregate.bidiMsgs}, failures=${aggregate.failures}"
            )

            val totalOps = aggregate.unaryOk + aggregate.clientStreamOk + aggregate.serverStreamMsgs + aggregate.bidiMsgs
            require(totalOps > 0) { "No operations completed in stress test" }
            assertEquals(0, aggregate.failures.toInt(), "Some operations failed during stress test")
        } finally {
            grpcServer.shutdown()
            grpcServer.awaitTermination()
            grpcClient.shutdown()
            grpcClient.awaitTermination()
        }
    }

    private suspend fun unaryEchoOnce(svc: CustomEchoService, m: Metrics) {
        val msg = "hello"
        val res: EchoMsgResponse = svc.UnaryEcho(EchoMsgRequest(message = msg))
        assertEquals(msg, res.message)
        m.unaryOk++
    }

    private suspend fun serverStreamingOnce(svc: CustomEchoService, m: Metrics) {
        val msg = "pong"
        val flow = svc.ServerStreamingEcho(EchoMsgRequest(message = msg))
        var c = 0
        flow.onEach {
            assertEquals(msg, it.message)
            c++
        }.collect()
        assertEquals(SERVER_STREAM_REPS, c)
        m.serverStreamMsgs += c
    }

    private suspend fun clientStreamingOnce(svc: CustomEchoService, batch: Int, m: Metrics) {
        val res = svc.ClientStreamingEcho(flow {
            repeat(batch) { i ->
                emit(EchoMsgRequest(message = "m$i"))
            }
        })
        val expected = (0 until batch).joinToString(", ") { "m$it" }
        assertEquals(expected, res.message)
        m.clientStreamOk++
    }

    private suspend fun bidiStreamingOnce(svc: CustomEchoService, batch: Int, m: Metrics) {
        val incoming: Flow<EchoMsgResponse> = svc.BidirectionalStreamingEcho(flow {
            repeat(batch) {
                emit(EchoMsgRequest(message = "z"))
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
