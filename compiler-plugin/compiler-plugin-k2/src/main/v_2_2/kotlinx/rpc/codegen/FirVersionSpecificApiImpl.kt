/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen

import org.jetbrains.kotlin.KtSourceElement
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.declaredFunctions
import org.jetbrains.kotlin.fir.declarations.constructors
import org.jetbrains.kotlin.fir.resolve.toClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.toFirResolvedTypeRef
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.fir.types.FirResolvedTypeRef
import org.jetbrains.kotlin.fir.types.FirTypeRef
import org.jetbrains.kotlin.fir.types.builder.FirResolvedTypeRefBuilder
import org.jetbrains.kotlin.fir.declarations.FirFunction
import org.jetbrains.kotlin.fir.symbols.impl.FirFunctionSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirConstructorSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.fir.declarations.FirRegularClass
import org.jetbrains.kotlin.fir.declarations.processAllDeclarations
import org.jetbrains.kotlin.fir.symbols.FirBasedSymbol

@Suppress("unused")
object FirVersionSpecificApiImpl : FirVersionSpecificApi {
    override fun ConeKotlinType.toFirResolvedTypeRefVS(
        source: KtSourceElement?,
        delegatedTypeRef: FirTypeRef?,
    ): FirResolvedTypeRef {
        return toFirResolvedTypeRef(source, delegatedTypeRef)
    }

    override fun ConeKotlinType.toClassSymbolVS(session: FirSession): FirClassSymbol<*>? {
        return toClassSymbol(session)
    }

    override var FirResolvedTypeRefBuilder.coneTypeVS: ConeKotlinType
        get() = coneType
        set(value) {
            coneType = value
        }

    override fun FirClassSymbol<*>.declaredFunctionsVS(session: FirSession): List<FirFunctionSymbol<*>> {
        return declaredFunctions(session)
    }

    override fun FirRegularClassSymbol.constructorsVS(session: FirSession): List<FirConstructorSymbol> {
        return constructors(session)
    }

    override fun FirRegularClass.declarationsVS(session: FirSession): List<FirBasedSymbol<*>> {
        val declarations = mutableListOf<FirBasedSymbol<*>>()
        processAllDeclarations(session) { symbol ->
            declarations.add(symbol)
        }
        return declarations
    }

    override val FirResolvedTypeRef.coneTypeVS: ConeKotlinType get() = coneType
}
