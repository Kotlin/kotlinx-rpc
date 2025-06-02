/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.common

import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

object RpcClassId {
    val rpcAnnotation = ClassId(FqName("kotlinx.rpc.annotations"), Name.identifier("Rpc"))
    val checkedTypeAnnotation = ClassId(FqName("kotlinx.rpc.annotations"), Name.identifier("CheckedTypeAnnotation"))

    val serializableAnnotation = ClassId(FqName("kotlinx.serialization"), Name.identifier("Serializable"))
    val contextualAnnotation = ClassId(FqName("kotlinx.serialization"), Name.identifier("Contextual"))

    val flow = ClassId(FqName("kotlinx.coroutines.flow"), Name.identifier("Flow"))
    val sharedFlow = ClassId(FqName("kotlinx.coroutines.flow"), Name.identifier("SharedFlow"))
    val stateFlow = ClassId(FqName("kotlinx.coroutines.flow"), Name.identifier("StateFlow"))
}

object RpcCallableId {
    val streamScoped = CallableId(FqName("kotlinx.rpc.krpc"), Name.identifier("streamScoped"))
    val withStreamScope = CallableId(FqName("kotlinx.rpc.krpc"), Name.identifier("withStreamScope"))
    val StreamScope = CallableId(FqName("kotlinx.rpc.krpc"), Name.identifier("StreamScope"))
    val invokeOnStreamScopeCompletion = CallableId(
        FqName("kotlinx.rpc.krpc"),
        Name.identifier("invokeOnStreamScopeCompletion"),
    )
}

object RpcNames {
    val SERVICE_STUB_NAME: Name = Name.identifier("\$rpcServiceStub")

    const val METHOD_CLASS_NAME_SUFFIX = "\$rpcMethod"
}

val Name.rpcMethodClassName: Name get() = Name.identifier("$identifier${RpcNames.METHOD_CLASS_NAME_SUFFIX}")
val Name.rpcMethodName: Name get() = Name.identifier(identifier.removeSuffix(RpcNames.METHOD_CLASS_NAME_SUFFIX))
