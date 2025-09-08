/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc

public actual typealias ChannelCredentials = io.grpc.ChannelCredentials

public actual typealias TlsChannelCredentials = io.grpc.TlsChannelCredentials

public actual typealias InsecureChannelCredentials = io.grpc.InsecureChannelCredentials

public actual fun InsecureChannelCredentials.create(): ChannelCredentials {
    return io.grpc.InsecureChannelCredentials.create()
}

public actual fun TlsChannelCredentials.create(): ChannelCredentials {
    return io.grpc.TlsChannelCredentials.create()
}