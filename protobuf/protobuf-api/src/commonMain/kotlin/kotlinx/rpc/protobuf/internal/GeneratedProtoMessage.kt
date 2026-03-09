/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.internal

import kotlinx.rpc.grpc.marshaller.HasWithGrpcMarshaller
import kotlinx.rpc.internal.utils.InternalRpcApi

@HasWithGrpcMarshaller
@InternalRpcApi
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE_PARAMETER)
public annotation class GeneratedProtoMessage
