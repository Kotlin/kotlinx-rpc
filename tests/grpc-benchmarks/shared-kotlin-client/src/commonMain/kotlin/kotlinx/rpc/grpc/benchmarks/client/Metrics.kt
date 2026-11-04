/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.benchmarks.client

/**
 * The client-side interval that one latency sample measures.
 *
 * Every benchmark states its metric, so results never pool samples of different intervals.
 */
internal enum class LatencyMetric {
    /** One request send to the completed receive of its unary response. */
    CALL_ROUND_TRIP,

    /** The receive of one response message to the receive of the next message. */
    MESSAGE_INTERARRIVAL,

    /** The start of one request write to the return of the write call. */
    REQUEST_WRITE_SUSPENSION,

    /** One request send to the completed receive of its response on a streaming call. */
    PING_PONG_ROUND_TRIP,
}

/** The unit of the counted operation of a benchmark. */
internal enum class OperationUnit {
    RPC,
    MESSAGE,
}

/** The load shape of every benchmark in this harness. */
internal const val LOAD_SHAPE: String = "closed_loop"
