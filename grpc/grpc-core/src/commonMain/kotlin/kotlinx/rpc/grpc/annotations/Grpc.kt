/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.annotations

import kotlinx.rpc.annotations.Rpc
import kotlinx.rpc.internal.utils.InternalRpcApi

/**
 * Annotation used for marking gRPC services.
 *
 * Internal use only.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.TYPE_PARAMETER)
@Rpc
@InternalRpcApi
public annotation class Grpc
