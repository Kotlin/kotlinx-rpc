/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc

@OptIn(ExperimentalSubclassOptIn::class)
@SubclassOptInRequired
public interface GrpcCompression {

    public val name: String

    public object None : GrpcCompression {
        override val name: String = "identity"
    }

    public object Gzip : GrpcCompression {
        override val name: String = "gzip"
    }
}