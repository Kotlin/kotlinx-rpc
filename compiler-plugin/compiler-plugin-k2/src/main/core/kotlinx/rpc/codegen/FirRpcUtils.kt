/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen

import org.jetbrains.kotlin.KtSourceElement
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.toAnnotationClassId
import org.jetbrains.kotlin.fir.expressions.FirAnnotation
import org.jetbrains.kotlin.fir.expressions.UnresolvedExpressionTypeAccess
import org.jetbrains.kotlin.fir.extensions.predicate.DeclarationPredicate
import org.jetbrains.kotlin.fir.extensions.predicateBasedProvider
import org.jetbrains.kotlin.fir.resolve.fullyExpandedType
import org.jetbrains.kotlin.fir.symbols.FirBasedSymbol
import org.jetbrains.kotlin.fir.symbols.SymbolInternals
import org.jetbrains.kotlin.fir.types.ConeClassLikeType
import org.jetbrains.kotlin.fir.types.FirTypeRef
import org.jetbrains.kotlin.fir.types.coneTypeSafe
import org.jetbrains.kotlin.name.ClassId

fun FirBasedSymbol<*>.rpcAnnotationSource(
    session: FirSession,
    predicate: DeclarationPredicate,
    classId: ClassId?,
): KtSourceElement? {
    return rpcAnnotation(session, predicate, classId)?.source
}

fun FirBasedSymbol<*>.rpcAnnotation(
    session: FirSession,
    predicate: DeclarationPredicate,
    classId: ClassId?,
): FirAnnotation? {
    return resolvedCompilerAnnotationsWithClassIds.rpcAnnotation(session, predicate, classId)
}

@OptIn(UnresolvedExpressionTypeAccess::class)
fun List<FirAnnotation>.rpcAnnotation(session: FirSession, predicate: DeclarationPredicate, classId: ClassId?): FirAnnotation? {
    return find {
        vsApi {
            it.coneTypeOrNull?.toClassSymbolVS(session)?.let { declaration ->
                session.predicateBasedProvider.matches(predicate, declaration)
            } == true || (classId != null && it.toAnnotationClassId(session) == classId)
        }
    }
}

@Suppress("unused")
@OptIn(SymbolInternals::class)
internal fun FirTypeRef.doesMatchesClassId(session: FirSession, classId: ClassId): Boolean {
    return coneTypeSafe<ConeClassLikeType>()?.fullyExpandedType(session)?.lookupTag?.classId == classId
}
