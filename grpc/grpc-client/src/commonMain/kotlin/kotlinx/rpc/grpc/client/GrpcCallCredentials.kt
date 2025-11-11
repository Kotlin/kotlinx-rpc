/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client

import kotlinx.rpc.grpc.GrpcMetadata

public interface GrpcCallCredentials {

    public suspend fun GrpcMetadata.applyOnMetadata(callOptions: GrpcCallOptions)

    public val requiresTransportSecurity: Boolean
        get() = true
}

public operator fun GrpcCallCredentials.plus(other: GrpcCallCredentials): GrpcCallCredentials {
    return CombinedCallCredentials(this, other)
}

public fun GrpcCallCredentials.combine(other: GrpcCallCredentials): GrpcCallCredentials = this + other

public object  EmptyCallCredentials : GrpcCallCredentials {
    override suspend fun GrpcMetadata.applyOnMetadata(callOptions: GrpcCallOptions) {
        // do nothing
    }
    override val requiresTransportSecurity: Boolean = false
}

internal class CombinedCallCredentials(
    private val first: GrpcCallCredentials,
    private val second: GrpcCallCredentials
) : GrpcCallCredentials {
    override suspend fun GrpcMetadata.applyOnMetadata(
        callOptions: GrpcCallOptions
    ) {
        with(first) {
            this@applyOnMetadata.applyOnMetadata(callOptions)
        }
        with(second) {
            this@applyOnMetadata.applyOnMetadata(callOptions)
        }
    }

    override val requiresTransportSecurity: Boolean = first.requiresTransportSecurity || second.requiresTransportSecurity

}
