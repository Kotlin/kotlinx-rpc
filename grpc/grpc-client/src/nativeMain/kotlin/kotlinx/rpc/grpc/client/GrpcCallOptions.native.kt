/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalForeignApi::class)

package kotlinx.rpc.grpc.client

import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.rpc.internal.utils.InternalRpcApi
import libkgrpc.GPR_CLOCK_REALTIME
import libkgrpc.GPR_TIMESPAN
import libkgrpc.gpr_inf_future
import libkgrpc.gpr_now
import libkgrpc.gpr_time_add
import libkgrpc.gpr_time_from_millis
import libkgrpc.gpr_timespec

@InternalRpcApi
public fun GrpcCallOptions.rawDeadline(): CValue<gpr_timespec> {
    return timeout?.let {
        gpr_time_add(
            gpr_now(GPR_CLOCK_REALTIME),
            gpr_time_from_millis(it.inWholeMilliseconds, GPR_TIMESPAN)
        )
    } ?: gpr_inf_future(GPR_CLOCK_REALTIME)
}