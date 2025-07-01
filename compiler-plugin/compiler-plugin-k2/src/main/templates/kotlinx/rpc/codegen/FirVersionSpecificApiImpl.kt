/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen

//##csm FirVersionSpecificApiImpl.kt-import
//##csm specific=[2.0.0...2.0.10]
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.toRegularClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.types.ConeClassLikeType
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.fir.types.FirResolvedTypeRef
import org.jetbrains.kotlin.fir.types.FirTypeRef
import org.jetbrains.kotlin.fir.types.toClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.fir.declarations.FirRegularClass
import org.jetbrains.kotlin.fir.symbols.FirBasedSymbol
import org.jetbrains.kotlin.fir.types.toRegularClassSymbol
//##csm /specific
//##csm specific=[2.0.11...2.0.21]
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.toRegularClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.fir.types.FirResolvedTypeRef
import org.jetbrains.kotlin.fir.types.FirTypeRef
import org.jetbrains.kotlin.fir.types.toClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.fir.declarations.FirRegularClass
import org.jetbrains.kotlin.fir.symbols.FirBasedSymbol
import org.jetbrains.kotlin.fir.types.toRegularClassSymbol
//##csm /specific
//##csm specific=[2.0.22...2.1.21, 2.2.0-ij251-*]
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.toRegularClassSymbol
import org.jetbrains.kotlin.fir.declarations.FirRegularClass
import org.jetbrains.kotlin.fir.resolve.toClassSymbol
import org.jetbrains.kotlin.fir.resolve.toRegularClassSymbol
import org.jetbrains.kotlin.fir.symbols.FirBasedSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.fir.types.FirResolvedTypeRef
import org.jetbrains.kotlin.fir.types.FirTypeRef
//##csm /specific
//##csm specific=[2.1.22...2.2.10]
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.toRegularClassSymbol
import org.jetbrains.kotlin.fir.resolve.toClassSymbol
import org.jetbrains.kotlin.fir.resolve.toRegularClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.fir.types.FirResolvedTypeRef
import org.jetbrains.kotlin.fir.types.FirTypeRef
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.fir.declarations.FirRegularClass
import org.jetbrains.kotlin.fir.declarations.processAllDeclarations
import org.jetbrains.kotlin.fir.symbols.FirBasedSymbol
//##csm /specific
//##csm specific=[2.2.20...2.*]
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.toRegularClassSymbol
import org.jetbrains.kotlin.fir.resolve.toClassSymbol
import org.jetbrains.kotlin.fir.resolve.toRegularClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.fir.types.FirResolvedTypeRef
import org.jetbrains.kotlin.fir.types.FirTypeRef
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.fir.declarations.FirRegularClass
import org.jetbrains.kotlin.fir.declarations.processAllDeclarations
import org.jetbrains.kotlin.fir.symbols.FirBasedSymbol
//##csm /specific
//##csm /FirVersionSpecificApiImpl.kt-import

object FirVersionSpecificApiImpl : FirVersionSpecificApi {
    override fun ConeKotlinType.toClassSymbolVS(session: FirSession): FirClassSymbol<*>? {
        //##csm ConeKotlinType.toClassSymbolVS
        //##csm specific=[2.0.0...2.0.10]
        return (this as? ConeClassLikeType)?.toClassSymbol(session)
        //##csm /specific
        //##csm default
        return toClassSymbol(session)
        //##csm /default
        //##csm /ConeKotlinType.toClassSymbolVS
    }

    override fun FirRegularClass.declarationsVS(session: FirSession): List<FirBasedSymbol<*>> {
        //##csm FirRegularClass.declarationsVS
        //##csm default
        val declarations = mutableListOf<FirBasedSymbol<*>>()
        processAllDeclarations(session) { symbol ->
            declarations.add(symbol)
        }
        return declarations
        //##csm /default
        //##csm specific=[2.0.0...2.1.21, 2.2.0-ij251-*]
        return declarations.map { it.symbol }
        //##csm /specific
        //##csm /FirRegularClass.declarationsVS
    }

    //##csm FirResolvedTypeRef.coneTypeVS
    //##csm default
    override val FirResolvedTypeRef.coneTypeVS: ConeKotlinType get() = coneType
    //##csm /default
    //##csm specific=[2.0.0...2.0.21]
    override val FirResolvedTypeRef.coneTypeVS: ConeKotlinType get() = type
    //##csm /specific
    //##csm /FirResolvedTypeRef.coneTypeVS

    override fun FirTypeRef.toRegularClassSymbolVS(session: FirSession): FirRegularClassSymbol? {
        return toRegularClassSymbol(session)
    }

    override fun ConeKotlinType.toRegularClassSymbolVS(session: FirSession): FirRegularClassSymbol? {
        return toRegularClassSymbol(session)
    }
}
