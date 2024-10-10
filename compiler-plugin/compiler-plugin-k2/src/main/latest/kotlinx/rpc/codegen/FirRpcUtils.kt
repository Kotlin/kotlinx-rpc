/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen

import kotlinx.rpc.codegen.common.RpcClassId
import org.jetbrains.kotlin.KtSourceElement
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.getAnnotationByClassId
import org.jetbrains.kotlin.fir.expressions.FirAnnotation
import org.jetbrains.kotlin.fir.symbols.FirBasedSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.types.classId

fun FirClassSymbol<*>.isRpc(): Boolean = resolvedSuperTypes.any {
    it.classId == RpcClassId.rpcInterface
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
