/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen

//##csm FirVersionSpecificApiImpl.kt-import
//##csm specific=[2.1.0...2.1.21, 2.2.0-ij251-*]
import org.jetbrains.kotlin.KtSourceElement
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfigurationKey
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.toRegularClassSymbol
import org.jetbrains.kotlin.fir.declarations.FirRegularClass
import org.jetbrains.kotlin.fir.declarations.getBooleanArgument
import org.jetbrains.kotlin.fir.declarations.getStringArgument
import org.jetbrains.kotlin.fir.declarations.getKClassArgument
import org.jetbrains.kotlin.fir.expressions.FirAnnotation
import org.jetbrains.kotlin.fir.resolve.toClassSymbol
import org.jetbrains.kotlin.fir.resolve.toRegularClassSymbol
import org.jetbrains.kotlin.fir.symbols.FirBasedSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.resolve.providers.symbolProvider
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.fir.toFirResolvedTypeRef
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.fir.types.FirResolvedTypeRef
import org.jetbrains.kotlin.fir.types.FirTypeRef
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.Name
//##csm /specific
//##csm specific=[2.1.22...2.2.10]
import org.jetbrains.kotlin.KtSourceElement
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfigurationKey
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.toRegularClassSymbol
import org.jetbrains.kotlin.fir.resolve.toClassSymbol
import org.jetbrains.kotlin.fir.resolve.toRegularClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.toFirResolvedTypeRef
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.fir.types.FirResolvedTypeRef
import org.jetbrains.kotlin.fir.types.FirTypeRef
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.fir.declarations.FirRegularClass
import org.jetbrains.kotlin.fir.declarations.processAllDeclarations
import org.jetbrains.kotlin.fir.declarations.getBooleanArgument
import org.jetbrains.kotlin.fir.declarations.getStringArgument
import org.jetbrains.kotlin.fir.declarations.getKClassArgument
import org.jetbrains.kotlin.fir.expressions.FirAnnotation
import org.jetbrains.kotlin.fir.resolve.providers.getRegularClassSymbolByClassId
import org.jetbrains.kotlin.fir.symbols.FirBasedSymbol
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.Name
//##csm /specific
//##csm specific=[2.2.20...2.2.*]
import org.jetbrains.kotlin.KtSourceElement
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfigurationKey
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.toRegularClassSymbol
import org.jetbrains.kotlin.fir.resolve.toClassSymbol
import org.jetbrains.kotlin.fir.resolve.toRegularClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.toFirResolvedTypeRef
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.fir.types.FirResolvedTypeRef
import org.jetbrains.kotlin.fir.types.FirTypeRef
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.fir.declarations.FirRegularClass
import org.jetbrains.kotlin.fir.declarations.processAllDeclarations
import org.jetbrains.kotlin.fir.declarations.getBooleanArgument
import org.jetbrains.kotlin.fir.declarations.getStringArgument
import org.jetbrains.kotlin.fir.declarations.getKClassArgument
import org.jetbrains.kotlin.fir.expressions.FirAnnotation
import org.jetbrains.kotlin.fir.resolve.providers.getRegularClassSymbolByClassId
import org.jetbrains.kotlin.fir.symbols.FirBasedSymbol
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.Name
//##csm /specific
//##csm specific=[2.3.0...2.*]
import org.jetbrains.kotlin.KtSourceElement
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfigurationKey
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.resolve.toClassSymbol
import org.jetbrains.kotlin.fir.resolve.toRegularClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.toFirResolvedTypeRef
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.fir.types.FirResolvedTypeRef
import org.jetbrains.kotlin.fir.types.FirTypeRef
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.fir.declarations.FirRegularClass
import org.jetbrains.kotlin.fir.declarations.processAllDeclarations
import org.jetbrains.kotlin.fir.declarations.getBooleanArgument
import org.jetbrains.kotlin.fir.declarations.getStringArgument
import org.jetbrains.kotlin.fir.declarations.getKClassArgument
import org.jetbrains.kotlin.fir.expressions.FirAnnotation
import org.jetbrains.kotlin.fir.resolve.providers.getRegularClassSymbolByClassId
import org.jetbrains.kotlin.fir.symbols.FirBasedSymbol
import org.jetbrains.kotlin.fir.types.toRegularClassSymbol
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.Name
//##csm /specific
//##csm /FirVersionSpecificApiImpl.kt-import

object FirVersionSpecificApiImpl : FirVersionSpecificApi {
    override fun ConeKotlinType.toClassSymbolVS(session: FirSession): FirClassSymbol<*>? {
        return toClassSymbol(session)
    }

    //##csm FirRegularClass.declarationsVS
    //##csm default
    override fun FirRegularClassSymbol.declarationsVS(session: FirSession): List<FirBasedSymbol<*>> {
        val declarations = mutableListOf<FirBasedSymbol<*>>()
        processAllDeclarations(session) { symbol ->
            declarations.add(symbol)
        }
        return declarations
    }
    //##csm /default
    //##csm specific=[2.1.0...2.1.21, 2.2.0-ij251-*]
    @org.jetbrains.kotlin.fir.symbols.SymbolInternals
    override fun FirRegularClassSymbol.declarationsVS(session: FirSession): List<FirBasedSymbol<*>> {
        return fir.declarations.map { it.symbol }
    }
    //##csm /specific
    //##csm /FirRegularClass.declarationsVS

    override val FirResolvedTypeRef.coneTypeVS: ConeKotlinType get() = coneType

    override fun FirTypeRef.toRegularClassSymbolVS(session: FirSession): FirRegularClassSymbol? {
        return toRegularClassSymbol(session)
    }

    override fun ConeKotlinType.toRegularClassSymbolVS(session: FirSession): FirRegularClassSymbol? {
        return toRegularClassSymbol(session)
    }

    override val messageCollectorKey: CompilerConfigurationKey<MessageCollector>
        get() = CommonConfigurationKeys.MESSAGE_COLLECTOR_KEY

    override fun ConeKotlinType.toFirResolvedTypeRefVS(
        source: KtSourceElement?,
        delegatedTypeRef: FirTypeRef?,
    ): FirResolvedTypeRef {
        return toFirResolvedTypeRef(source, delegatedTypeRef)
    }

    override fun FirSession.getRegularClassSymbolByClassIdVS(classId: ClassId): FirRegularClassSymbol? {
        //##csm ConeKotlinType.toClassSymbolVS
        //##csm default
        return getRegularClassSymbolByClassId(classId)
        //##csm /default
        //##csm specific=[2.1.0...2.1.21, 2.2.0-ij251-*]
        // this is the same implementation as getRegularClassSymbolByClassId() in 2.1.22+
        return symbolProvider.getClassLikeSymbolByClassId(classId) as? FirRegularClassSymbol
        //##csm /specific
        //##csm /ConeKotlinType.toClassSymbolVS
    }

    override fun FirAnnotation.getKClassArgumentVS(name: Name, session: FirSession): ConeKotlinType? {
        //##csm FirAnnotation.getKClassArgumentVS
        //##csm default
        return getKClassArgument(name)
        //##csm /default
        //##csm specific=[2.1.0...2.3.99]
        return getKClassArgument(name, session)
        //##csm /specific
        //##csm /FirAnnotation.getKClassArgumentVS
    }

    override fun FirAnnotation.getBooleanArgumentVS(name: Name, session: FirSession): Boolean? {
        //##csm FirAnnotation.getBooleanArgumentVS
        //##csm default
        return getBooleanArgument(name)
        //##csm /default
        //##csm specific=[2.1.0...2.3.99]
        return getBooleanArgument(name, session)
        //##csm /specific
        //##csm /FirAnnotation.getBooleanArgumentVS
    }

    override fun FirAnnotation.getStringArgumentVS(name: Name, session: FirSession): String? {
        //##csm FirAnnotation.getStringArgumentVS
        //##csm default
        return getStringArgument(name)
        //##csm /default
        //##csm specific=[2.1.0...2.3.99]
        return getStringArgument(name, session)
        //##csm /specific
        //##csm /FirAnnotation.getStringArgumentVS
    }
}
