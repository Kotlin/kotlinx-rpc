/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.annotations

import kotlinx.rpc.annotations.Rpc

/**
 * Annotation used for marking gRPC services.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.TYPE_PARAMETER)
@Rpc
public annotation class Grpc(val protoPackage: String = "") {
    @Target(AnnotationTarget.FUNCTION)
    public annotation class Method(
        val name: String = "",
        val safe: Boolean = false,
        val idempotent: Boolean = false,
        val sampledToLocalTracing: Boolean = true,
    )
}
