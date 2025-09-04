/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalForeignApi::class)

package kotlinx.rpc.grpc.internal

import cnames.structs.grpc_call
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import libkgrpc.grpc_op
import libkgrpc.grpc_status_code

internal abstract class AbstractNativeCall(
    val raw: CPointer<grpc_call>,
    val cq: CompletionQueue,
) {

    protected abstract val isClosed: Boolean

    protected abstract fun beginOp()
    protected abstract fun endOp()
    protected abstract fun cancelInternal(statusCode: grpc_status_code, message: String)

    /**
     * Submits a batch operation to the [CompletionQueue] and handle the returned [BatchResult].
     * If the batch was successfully submitted, [onSuccess] is called.
     * In any case, [cleanup] is called.
     */
    internal fun runBatch(
        ops: CPointer<grpc_op>,
        nOps: ULong,
        cleanup: () -> Unit = {},
        onSuccess: () -> Unit = {},
    ) {
        // we must not try to run a batch after the call is closed.
        if (isClosed) return cleanup()

        // pre-book the batch, so onClose cannot be called before the batch finished.
        beginOp()

        when (val callResult = cq.runBatch(this@AbstractNativeCall.raw, ops, nOps)) {
            is BatchResult.Submitted -> {
                callResult.future.onComplete { success ->
                    try {
                        if (success) {
                            onSuccess()
                        }
                    } finally {
                        // ignore failure, as it is reflected in the client status op
                        cleanup()
                        endOp()
                    }
                }
            }

            BatchResult.CQShutdown -> {
                cleanup()
                endOp()
                cancelInternal(grpc_status_code.GRPC_STATUS_UNAVAILABLE, "Channel shutdown")
            }

            is BatchResult.SubmitError -> {
                cleanup()
                endOp()
                cancelInternal(
                    grpc_status_code.GRPC_STATUS_INTERNAL,
                    "Batch could not be submitted: ${callResult.error}"
                )
            }
        }
    }

}