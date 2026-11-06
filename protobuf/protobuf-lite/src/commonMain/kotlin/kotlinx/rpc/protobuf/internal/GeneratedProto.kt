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

@InternalRpcApi
@Target(AnnotationTarget.CLASS)
public annotation class GeneratedProtoEnum

@InternalRpcApi
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FIELD,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.FILE,
)
public annotation class GeneratedProtoDeclaration
