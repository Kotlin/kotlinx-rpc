/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.common

import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

object RpcClassId {
    val rpcAnnotation = ClassId(FqName("kotlinx.rpc.annotations"), Name.identifier("Rpc"))
    val grpcAnnotation = ClassId(FqName("kotlinx.rpc.grpc.annotations"), Name.identifier("Grpc"))
    val grpcMethodAnnotation = ClassId(FqName("kotlinx.rpc.grpc.annotations"), Name.identifier("Grpc.Method"))
    val withCodecAnnotation = ClassId(FqName("kotlinx.rpc.grpc.codec"), Name.identifier("WithCodec"))
    val checkedTypeAnnotation = ClassId(FqName("kotlinx.rpc.annotations"), Name.identifier("CheckedTypeAnnotation"))

    val serializableAnnotation = ClassId(FqName("kotlinx.serialization"), Name.identifier("Serializable"))
    val serializationTransient = ClassId.topLevel(FqName("kotlinx.serialization.Transient"))

    val flow = ClassId(FqName("kotlinx.coroutines.flow"), Name.identifier("Flow"))
    val sharedFlow = ClassId(FqName("kotlinx.coroutines.flow"), Name.identifier("SharedFlow"))
    val stateFlow = ClassId(FqName("kotlinx.coroutines.flow"), Name.identifier("StateFlow"))

    val messageCodec = ClassId(FqName("kotlinx.rpc.grpc.codec"), Name.identifier("MessageCodec"))
}

object RpcNames {
    val SERVICE_STUB_NAME: Name = Name.identifier("\$rpcServiceStub")

    val CHECK_FOR_ARGUMENT_NAME = Name.identifier("checkFor")
}
