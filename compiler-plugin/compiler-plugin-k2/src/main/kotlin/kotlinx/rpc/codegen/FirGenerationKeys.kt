/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen

import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.fir.declarations.FirDeclarationOrigin
import org.jetbrains.kotlin.fir.symbols.FirBasedSymbol
import org.jetbrains.kotlin.name.Name

internal class RpcGeneratedStubKey(
    val isGrpc: Boolean,
    private val serviceName: Name,
) : GeneratedDeclarationKey() {
    override fun toString(): String {
        return "RpcGeneratedStubKey.$serviceName"
    }
}

internal val FirBasedSymbol<*>.generatedRpcServiceStubKey: RpcGeneratedStubKey? get() =
    (origin as? FirDeclarationOrigin.Plugin)?.key as? RpcGeneratedStubKey

internal object FirRpcServiceStubCompanionObject : GeneratedDeclarationKey() {
    override fun toString(): String {
        return "FirRpcServiceStubCompanionObject"
    }
}
