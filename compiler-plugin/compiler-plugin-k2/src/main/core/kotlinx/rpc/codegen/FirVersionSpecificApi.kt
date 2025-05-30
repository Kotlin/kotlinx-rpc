/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen

import org.jetbrains.kotlin.KtSourceElement
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.FirRegularClass
import org.jetbrains.kotlin.fir.symbols.FirBasedSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirConstructorSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirFunctionSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.fir.types.FirResolvedTypeRef
import org.jetbrains.kotlin.fir.types.FirTypeRef
import org.jetbrains.kotlin.fir.types.builder.FirResolvedTypeRefBuilder

interface FirVersionSpecificApi {
    fun ConeKotlinType.toFirResolvedTypeRefVS(
        source: KtSourceElement? = null,
        delegatedTypeRef: FirTypeRef? = null,
    ): FirResolvedTypeRef

    fun ConeKotlinType.toClassSymbolVS(
        session: FirSession,
    ): FirClassSymbol<*>?

    var FirResolvedTypeRefBuilder.coneTypeVS: ConeKotlinType

    fun FirClassSymbol<*>.declaredFunctionsVS(session: FirSession): List<FirFunctionSymbol<*>>

    fun FirRegularClassSymbol.constructorsVS(session: FirSession): List<FirConstructorSymbol>

    fun FirRegularClass.declarationsVS(session: FirSession): List<FirBasedSymbol<*>>

    val FirResolvedTypeRef.coneTypeVS: ConeKotlinType
}

inline fun <T> vsApi(body: FirVersionSpecificApi.() -> T): T {
    return FirVersionSpecificApiImpl.body()
}
