package org.jetbrains.krpc.codegen

import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeArgument

class RPCSymbolProcessorContext(private val resolver: Resolver) {
    val rpcType by lazy { resolveClass("org.jetbrains.krpc.RPC") }

    val plainFlowType by lazy { resolveClass("kotlinx.coroutines.flow.Flow") }

    val sharedFlowType by lazy { resolveClass("kotlinx.coroutines.flow.SharedFlow") }

    val stateFlowType by lazy { resolveClass("kotlinx.coroutines.flow.StateFlow") }

    val rpcEagerProperty by lazy { resolveClass("org.jetbrains.krpc.RPCEagerProperty") }

    private fun resolveClass(name: String, typeArguments: List<KSTypeArgument> = emptyList()): KSType {
        return resolver.getClassDeclarationByName(name)?.asType(typeArguments)
            ?: codegenError("Could not find $name declaration")
    }
}
