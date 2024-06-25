/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen

import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.fir.declarations.FirDeclarationOrigin
import org.jetbrains.kotlin.fir.symbols.FirBasedSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirFunctionSymbol
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlinx.serialization.compiler.fir.SerializationPluginKey

internal class RPCGeneratedStubKey(
    private val serviceName: Name,
    val functions: List<FirFunctionSymbol<*>>,
) : GeneratedDeclarationKey() {
    override fun toString(): String {
        return "RPCGeneratedStubKey.$serviceName"
    }
}

internal val FirBasedSymbol<*>.generatedRpcServiceStubKey: RPCGeneratedStubKey? get() =
    (origin as? FirDeclarationOrigin.Plugin)?.key as? RPCGeneratedStubKey

internal class RPCGeneratedRpcMethodClassKey(
    val rpcMethod: FirFunctionSymbol<*>,
) : GeneratedDeclarationKey() {
    val isObject = rpcMethod.valueParameterSymbols.isEmpty()

    override fun toString(): String {
        return "RPCGeneratedRpcMethodClassKey.${rpcMethod.name}"
    }
}

internal val FirBasedSymbol<*>.generatedRpcMethodClassKey: RPCGeneratedRpcMethodClassKey? get() =
    (origin as? FirDeclarationOrigin.Plugin)?.key as? RPCGeneratedRpcMethodClassKey

internal object FirRpcServiceStubCompanionObject : GeneratedDeclarationKey() {
    override fun toString(): String {
        return "FirRpcServiceStubCompanionObject"
    }
}

internal val FirClassSymbol<*>.isFromSerializationPlugin: Boolean get() {
    return (origin as? FirDeclarationOrigin.Plugin)?.key is SerializationPluginKey
}
