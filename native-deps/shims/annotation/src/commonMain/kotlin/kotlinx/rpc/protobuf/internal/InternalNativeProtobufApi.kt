/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.internal

/**
 * Marks declarations from the published protobuf-shim native interop as internal implementation detail.
 *
 * This API exists only for the intended internal consumer of protobuf-shim.
 * External libraries and applications must not treat it as a supported public API.
 *
 * It may change or be removed at any time without notice and is not covered by backward-compatibility guarantees.
 */
@RequiresOptIn(
    level = RequiresOptIn.Level.ERROR,
    message = "protobuf-shim native interop declarations are internal implementation details, unsupported for external use, and may change without compatibility guarantees.",
)
@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.CONSTRUCTOR,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.TYPEALIAS,
)
public annotation class InternalNativeProtobufApi
