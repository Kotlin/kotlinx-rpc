/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.benchmark

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.io.Buffer
import kotlinx.io.Source
import kotlinx.io.readByteArray
import kotlinx.rpc.grpc.GrpcClient
import kotlinx.rpc.grpc.annotations.Grpc
import kotlinx.rpc.grpc.codec.MessageCodec
import kotlinx.rpc.grpc.codec.MessageCodecResolver
import kotlinx.rpc.grpc.codec.SourcedMessageCodec
import kotlinx.rpc.grpc.test.EchoRequest
import kotlinx.rpc.grpc.test.EchoRequestInternal
import kotlinx.rpc.grpc.test.EchoResponse
import kotlinx.rpc.grpc.test.invoke
import kotlinx.rpc.protobuf.input.stream.asSource
import kotlinx.rpc.withService
import kotlin.math.round
import kotlin.math.roundToInt
import kotlin.reflect.KType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.TimeSource
import kotlin.time.measureTime

/**
 * A lightweight performance benchmark for the kotlinx.rpc gRPC client inspired by the Dart sample.
 *
 * It spins up an in-process Echo server implementation and performs:
 *  - Unary throughput/latency test
 *  - Server streaming throughput test
 *  - Client streaming aggregation test
 *  - Bidirectional streaming echo test
 *
 * The goal is not to be a rigorous benchmark, but to provide a comparable sanity check that runs
 * quickly in CI and prints human-readable metrics.
 */
class GrpcClientBenchmarkTest {

    private val host = "localhost"

    // Use a dedicated port unlikely to conflict with other manual tests
    private val port = 50051

    // Helper to compute a percentile (returns milliseconds as Double)
    private fun percentileMillis(latenciesMicros: List<Long>, p: Double): Double {
        if (latenciesMicros.isEmpty()) return 0.0
        val sorted = latenciesMicros.sorted()
        val idx = ((p * (sorted.size - 1)).coerceIn(0.0, (sorted.size - 1).toDouble())).roundToInt()
        return sorted[idx] / 1000.0
    }

    val myMessageCodecResolver = object : MessageCodecResolver {
        override fun resolveOrNull(kType: KType): MessageCodec<*>? {
            return when (kType.classifier) {
                ByteArray::class -> object : SourcedMessageCodec<ByteArray> {
                    override fun encodeToSource(value: ByteArray): Source {
                        return Buffer().apply { write(value) }
                    }

                    override fun decodeFromSource(stream: Source): ByteArray {
                        return stream.readByteArray()
                    }
                }

                else -> null
            }
        }
    }

    @Test
    fun unary_performance_throughput_and_latency() = runBlocking {
        val client = GrpcClient(host, port) {
            usePlaintext()
            useMessageCodecResolver(myMessageCodecResolver)
        }
        try {
            val svc = client.withService<EchoService>()

            val totalRequests = 100_000
            val concurrency = 10

            val latenciesUs = ArrayList<Long>(totalRequests)
            val latMutex = Mutex()

            val req =
                EchoRequestInternal.CODEC.encode(EchoRequest.Companion { message = "ping" }).asSource().readByteArray()

            val elapsedMs = measureTime {
                coroutineScope {
                    val sem = Semaphore(concurrency)
                    val tasks = (0 until totalRequests).map { i ->
                        async<Unit> {
                            sem.withPermit {
                                val mark = TimeSource.Monotonic.markNow()
                                val resp = svc.UnaryEcho(req)
                                assertEquals(req.size, resp.size)
                                val durUs = mark.elapsedNow().inWholeMicroseconds
                                latMutex.lock()
                                try {
                                    latenciesUs.add(durUs)
                                } finally {
                                    latMutex.unlock()
                                }
                            }
                        }
                    }
                    tasks.awaitAll()
                }
            }.inWholeMilliseconds

            val rps = totalRequests / (elapsedMs / 1000.0)
            val p50 = percentileMillis(latenciesUs, 0.5)
            val p90 = percentileMillis(latenciesUs, 0.9)

            fun fmt1(x: Double): String = (round(x * 10.0) / 10.0).toString()
            fun fmt2(x: Double): String = (round(x * 100.0) / 100.0).toString()
            println(
                "[Unary] n=$totalRequests, concurrency=$concurrency, time=${elapsedMs}ms, rps=${fmt1(rps)}, p50=${
                    fmt2(
                        p50
                    )
                }ms, p90=${fmt2(p90)}ms"
            )

            assertTrue(rps > 0)
            assertTrue(elapsedMs < 10_000, "Unary benchmark took too long")
        } finally {
            client.shutdown()
            client.awaitTermination()
        }
    }

    @Test
    fun server_streaming_performance() = runBlocking {
        val client = GrpcClient(host, port) { usePlaintext() }
        try {
            val svc = client.withService<EchoService>()
            val reps = 300

            val elapsed = measureTime {
                var count = 0
                svc.ServerStreamingEcho(EchoRequest.Companion { message = "hello"; serverStreamReps = reps.toUInt() })
                    .onEach { resp ->
                        assertEquals("hello", resp.message)
                        count++
                    }
                    .collect()
                assertEquals(reps, count)
            }

            val elapsedMs = elapsed.inWholeMilliseconds
            val mps = reps / (elapsedMs / 1000.0)
            fun fmt1(x: Double): String = (round(x * 10.0) / 10.0).toString()
            println("[SrvStream] reps=$reps, time=${elapsedMs}ms, msgs/sec=${fmt1(mps)}")

            assertTrue(elapsedMs < 10_000, "Server streaming benchmark took too long")
        } finally {
            client.shutdown()
            client.awaitTermination()
        }
    }

    @Test
    fun client_streaming_performance() = runBlocking {
        val client = GrpcClient(host, port) { usePlaintext() }
        try {
            val svc = client.withService<EchoService>()
            val n = 200

            val elapsed = measureTime {
                val resp = svc.ClientStreamingEcho(flow {
                    repeat(n) { i -> emit(EchoRequest.Companion { message = "m$i" }) }
                })
                assertTrue(resp.message.contains("received=$n"))
            }

            val elapsedMs = elapsed.inWholeMilliseconds
            println("[CliStream] sent=$n, time=${elapsedMs}ms")

            assertTrue(elapsedMs < 10_000, "Client streaming benchmark took too long")
        } finally {
            client.shutdown()
            client.awaitTermination()
        }
    }

    @Test
    fun bidirectional_streaming_performance() = runBlocking {
        val client = GrpcClient(host, port) { usePlaintext() }
        try {
            val svc = client.withService<EchoService>()
            val n = 200

            val elapsed = measureTime {
                val received = mutableListOf<String>()
                val stream: Flow<EchoResponse> = svc.BidirectionalStreamingEcho(flow {
                    repeat(n) { i -> emit(EchoRequest.Companion { message = "b$i" }) }
                })
                stream.onEach { received.add(it.message) }.collect()

                assertEquals(n, received.size)
                assertEquals("b0", received.first())
                assertEquals("b${n - 1}", received.last())
            }

            val elapsedMs = elapsed.inWholeMilliseconds
            println("[Bidi] sent=$n, time=${elapsedMs}ms")

            assertTrue(elapsedMs < 10_000, "Bidi streaming benchmark took too long")
        } finally {
            client.shutdown()
            client.awaitTermination()
        }
    }
}

@Grpc(protoPackage = "kotlinx.rpc.grpc.test")
private interface EchoService {
    suspend fun UnaryEcho(message: ByteArray): ByteArray
    fun ServerStreamingEcho(message: EchoRequest): Flow<EchoResponse>
    suspend fun ClientStreamingEcho(message: Flow<EchoRequest>): EchoResponse
    fun BidirectionalStreamingEcho(message: Flow<EchoRequest>): Flow<EchoResponse>
}