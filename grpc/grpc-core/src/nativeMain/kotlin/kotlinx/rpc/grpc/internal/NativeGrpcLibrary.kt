/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalNativeApi::class, ExperimentalForeignApi::class)

package kotlinx.rpc.grpc.internal

import kotlinx.cinterop.ExperimentalForeignApi
import libgrpcpp_c.grpc_init
import libgrpcpp_c.grpc_shutdown
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.ref.createCleaner

internal object NativeGrpcLibrary {
    init {
        // init grpc library
        grpc_init()
    }

    val ref = Any()
    private val cleaner = createCleaner(ref) {
        println("Cleaner invoked")
        grpc_shutdown()
    }
}