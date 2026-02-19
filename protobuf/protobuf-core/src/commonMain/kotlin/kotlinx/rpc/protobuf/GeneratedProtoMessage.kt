/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf

import kotlinx.rpc.annotations.CheckedTypeAnnotation
import kotlinx.rpc.grpc.codec.HasWithCodec
import kotlinx.rpc.internal.utils.InternalRpcApi

@HasWithCodec
@InternalRpcApi
@CheckedTypeAnnotation
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE_PARAMETER)
public annotation class GeneratedProtoMessage