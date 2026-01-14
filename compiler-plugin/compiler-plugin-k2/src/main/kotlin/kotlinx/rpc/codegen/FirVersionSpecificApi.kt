/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen

import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.config.CompilerConfigurationKey
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.FirRegularClass
import org.jetbrains.kotlin.fir.symbols.FirBasedSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.fir.types.FirResolvedTypeRef
import org.jetbrains.kotlin.fir.types.FirTypeRef

interface FirVersionSpecificApi {
    fun ConeKotlinType.toClassSymbolVS(
        session: FirSession,
    ): FirClassSymbol<*>?

    fun FirRegularClass.declarationsVS(session: FirSession): List<FirBasedSymbol<*>>

    val FirResolvedTypeRef.coneTypeVS: ConeKotlinType

    fun FirTypeRef.toRegularClassSymbolVS(session: FirSession): FirRegularClassSymbol?

    fun ConeKotlinType.toRegularClassSymbolVS(session: FirSession): FirRegularClassSymbol?

    val messageCollectorKey: CompilerConfigurationKey<MessageCollector>
}

inline fun <T> vsApi(body: FirVersionSpecificApi.() -> T): T {
    return FirVersionSpecificApiImpl.body()
}
