/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc

@Suppress(names = ["RedundantConstructorKeyword"])
public actual class GrpcTrailers actual constructor() {
    public actual fun merge(trailers: GrpcTrailers) {}
}
