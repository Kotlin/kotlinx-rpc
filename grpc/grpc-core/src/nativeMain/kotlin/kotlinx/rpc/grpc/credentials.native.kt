/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class)

package kotlinx.rpc.grpc

import cnames.structs.grpc_channel_credentials
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import libkgrpc.grpc_channel_credentials_release
import libkgrpc.grpc_insecure_credentials_create
import libkgrpc.grpc_tls_credentials_create
import libkgrpc.grpc_tls_credentials_options_create
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.ref.createCleaner

public actual abstract class ChannelCredentials internal constructor(
    internal val raw: CPointer<grpc_channel_credentials>,
) {
    internal val rawCleaner = createCleaner(raw) {
        grpc_channel_credentials_release(it)
    }
}

public actual class InsecureChannelCredentials internal constructor(
    raw: CPointer<grpc_channel_credentials>,
) : ChannelCredentials(raw)

public actual class TlsChannelCredentials internal constructor(
    raw: CPointer<grpc_channel_credentials>,
) : ChannelCredentials(raw)


public actual fun InsecureChannelCredentials.Companion.create(): ChannelCredentials {
    return InsecureChannelCredentials(
        grpc_insecure_credentials_create() ?: error("grpc_insecure_credentials_create() returned null")
    )
}

public actual fun TlsChannelCredentials.Companion.create(): ChannelCredentials {
    val raw = grpc_tls_credentials_options_create()?.let {
        grpc_tls_credentials_create(it)
    } ?: error("Failed to create TLS credentials")

    return TlsChannelCredentials(raw)
}