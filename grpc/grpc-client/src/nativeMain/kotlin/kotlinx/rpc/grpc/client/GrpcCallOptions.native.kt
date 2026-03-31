/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalForeignApi::class, InternalNativeRpcApi::class)

package kotlinx.rpc.grpc.client

import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.rpc.grpc.internal.cinterop.GPR_CLOCK_REALTIME
import kotlinx.rpc.grpc.internal.cinterop.GPR_TIMESPAN
import kotlinx.rpc.grpc.internal.cinterop.gpr_inf_future
import kotlinx.rpc.grpc.internal.cinterop.gpr_now
import kotlinx.rpc.grpc.internal.cinterop.gpr_time_add
import kotlinx.rpc.grpc.internal.cinterop.gpr_time_from_millis
import kotlinx.rpc.grpc.internal.cinterop.gpr_timespec
import kotlinx.rpc.grpc.internal.shim.InternalNativeRpcApi

@InternalRpcApi
public fun GrpcCallOptions.rawDeadline(): CValue<gpr_timespec> {
    return timeout?.let {
        gpr_time_add(
            gpr_now(GPR_CLOCK_REALTIME),
            gpr_time_from_millis(it.inWholeMilliseconds, GPR_TIMESPAN)
        )
    } ?: gpr_inf_future(GPR_CLOCK_REALTIME)
}
