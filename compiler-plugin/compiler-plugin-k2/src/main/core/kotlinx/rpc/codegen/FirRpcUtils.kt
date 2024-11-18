/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen

import kotlinx.rpc.codegen.common.RpcClassId
import org.jetbrains.kotlin.KtSourceElement
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.getAnnotationByClassId
import org.jetbrains.kotlin.fir.expressions.FirAnnotation
import org.jetbrains.kotlin.fir.resolve.fullyExpandedType
import org.jetbrains.kotlin.fir.symbols.FirBasedSymbol
import org.jetbrains.kotlin.fir.symbols.SymbolInternals
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.types.ConeClassLikeType
import org.jetbrains.kotlin.fir.types.FirResolvedTypeRef
import org.jetbrains.kotlin.fir.types.FirTypeRef
import org.jetbrains.kotlin.fir.types.coneTypeSafe
import org.jetbrains.kotlin.name.ClassId

fun FirClassSymbol<*>.isRemoteService(session: FirSession): Boolean = resolvedSuperTypeRefs.any {
    it.doesMatchesClassId(session, RpcClassId.remoteServiceInterface)
}

fun FirBasedSymbol<*>.rpcAnnotationSource(session: FirSession): KtSourceElement? {
    return rpcAnnotation(session)?.source
}

fun FirBasedSymbol<*>.rpcAnnotation(session: FirSession): FirAnnotation? {
    return resolvedCompilerAnnotationsWithClassIds.rpcAnnotation(session)
}

fun List<FirAnnotation>.rpcAnnotation(session: FirSession): FirAnnotation? {
    return getAnnotationByClassId(RpcClassId.rpcAnnotation, session)
}

fun FirClassSymbol<*>.remoteServiceSupertypeSource(session: FirSession): KtSourceElement? {
    return remoteServiceSupertype(session)?.source
}

fun FirClassSymbol<*>.remoteServiceSupertype(session: FirSession): FirResolvedTypeRef? {
    return resolvedSuperTypeRefs.find { it.doesMatchesClassId(session, RpcClassId.remoteServiceInterface) }
}

@OptIn(SymbolInternals::class)
internal fun FirTypeRef.doesMatchesClassId(session: FirSession, classId: ClassId): Boolean {
    return coneTypeSafe<ConeClassLikeType>()?.fullyExpandedType(session)?.lookupTag?.classId == classId
}
