/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.common

import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

object ClassDeclarations {
    val rpcInterface = ClassId(FqName("kotlinx.rpc"), Name.identifier("RPC"))

    val serializableAnnotation = ClassId(FqName("kotlinx.serialization"), Name.identifier("Serializable"))
    val contextualAnnotation = ClassId(FqName("kotlinx.serialization"), Name.identifier("Contextual"))

    val flow = ClassId(FqName("kotlinx.coroutines.flow"), Name.identifier("Flow"))
    val sharedFlow = ClassId(FqName("kotlinx.coroutines.flow"), Name.identifier("SharedFlow"))
    val stateFlow = ClassId(FqName("kotlinx.coroutines.flow"), Name.identifier("StateFlow"))
}

object RpcNames {
    val SERVICE_STUB_NAME: Name = Name.identifier("\$rpcServiceStub")
    val SERVICE_STUB_NAME_KSP: Name = Name.identifier("_rpcServiceStub")

    const val METHOD_CLASS_NAME_SUFFIX = "\$rpcMethod"
    const val METHOD_CLASS_NAME_SUFFIX_KSP = "_rpcMethod"
}

val Name.rpcMethodClassName: Name get() = Name.identifier("$identifier${RpcNames.METHOD_CLASS_NAME_SUFFIX}")
val Name.rpcMethodClassNameKsp: Name get() = Name.identifier("$identifier${RpcNames.METHOD_CLASS_NAME_SUFFIX_KSP}")
val Name.rpcMethodName: Name get() = Name.identifier(identifier.removeSuffix(RpcNames.METHOD_CLASS_NAME_SUFFIX))
