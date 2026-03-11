/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen

import org.jetbrains.kotlin.KtSourceElement
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.config.CompilerConfigurationKey
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.FirRegularClass
import org.jetbrains.kotlin.fir.expressions.FirAnnotation
import org.jetbrains.kotlin.fir.plugin.DeclarationBuildingContext
import org.jetbrains.kotlin.fir.symbols.FirBasedSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirCallableSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.fir.types.FirResolvedTypeRef
import org.jetbrains.kotlin.fir.types.FirTypeRef
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.Name

interface FirVersionSpecificApi {
    fun ConeKotlinType.toClassSymbolVS(
        session: FirSession,
    ): FirClassSymbol<*>?

    fun FirRegularClass.declarationsVS(session: FirSession): List<FirBasedSymbol<*>> = symbol.declarationsVS(session)

    fun FirRegularClassSymbol.declarationsVS(session: FirSession): List<FirBasedSymbol<*>>

    val FirResolvedTypeRef.coneTypeVS: ConeKotlinType

    fun FirTypeRef.toRegularClassSymbolVS(session: FirSession): FirRegularClassSymbol?

    fun ConeKotlinType.toRegularClassSymbolVS(session: FirSession): FirRegularClassSymbol?

    fun ConeKotlinType.toFirResolvedTypeRefVS(
        source: KtSourceElement? = null,
        delegatedTypeRef: FirTypeRef? = null
    ): FirResolvedTypeRef

    val messageCollectorKey: CompilerConfigurationKey<MessageCollector>

    fun FirSession.getRegularClassSymbolByClassIdVS(classId: ClassId): FirRegularClassSymbol?

    fun FirAnnotation.getKClassArgumentVS(name: Name, session: FirSession): ConeKotlinType?

    fun FirAnnotation.getBooleanArgumentVS(name: Name, session: FirSession): Boolean?

    fun FirAnnotation.getStringArgumentVS(name: Name, session: FirSession): String?

    fun FirClassSymbol<*>.forAllCallablesVS(session: FirSession, body: (FirCallableSymbol<*>) -> Unit)

    var DeclarationBuildingContext<*>.sourceVS: KtSourceElement?
}

inline fun <T> vsApi(body: FirVersionSpecificApi.() -> T): T {
    return FirVersionSpecificApiImpl.body()
}
