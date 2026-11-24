/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server.testing

import kotlinx.rpc.grpc.GrpcMetadata
import kotlinx.rpc.grpc.GrpcStatus
import kotlinx.rpc.grpc.statusCode

/** An adapter operation or contract violation recorded by [FakeServerCall]. */
internal sealed interface ServerCallEvent {
    val label: String

    data class Requested(val count: Int) : ServerCallEvent {
        override val label: String = "request($count)"
    }

    class HeadersSent(val headers: GrpcMetadata) : ServerCallEvent {
        override val label: String = "sendHeaders"
    }

    class MessageSent(val message: Any?) : ServerCallEvent {
        override val label: String = "sendMessage"
    }

    class Closed(val status: GrpcStatus, val trailers: GrpcMetadata) : ServerCallEvent {
        override val label: String = "close(${status.statusCode})"
    }

    /** A violation that [FakeServerCall] records before it throws. */
    class ContractViolation(val message: String) : ServerCallEvent {
        override val label: String = "violation: $message"
    }
}
