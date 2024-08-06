/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.serialization

import org.jetbrains.kotlin.fir.FirAnnotationContainer
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.expressions.buildResolvedArgumentList
import org.jetbrains.kotlin.fir.expressions.builder.buildAnnotationCall
import org.jetbrains.kotlin.fir.references.builder.buildResolvedNamedReference
import org.jetbrains.kotlin.fir.resolve.defaultType
import org.jetbrains.kotlin.fir.resolve.providers.symbolProvider
import org.jetbrains.kotlin.fir.symbols.impl.FirConstructorSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.fir.types.builder.buildResolvedTypeRef
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.utils.addToStdlib.firstIsInstanceOrNull

fun FirAnnotationContainer.addAnnotation(annotationId: ClassId, session: FirSession) {
    val annotation = session
        .symbolProvider
        .getClassLikeSymbolByClassId(annotationId)
            as? FirRegularClassSymbol ?: return

    val annotationConstructor = annotation
        .declarationSymbols
        .firstIsInstanceOrNull<FirConstructorSymbol>() ?: return

    val annotationCall = buildAnnotationCall {
        argumentList = buildResolvedArgumentList(null, linkedMapOf())
        annotationTypeRef = buildResolvedTypeRef {
            type = annotation.defaultType()
        }
        calleeReference = buildResolvedNamedReference {
            name = annotation.name
            resolvedSymbol = annotationConstructor
        }

        containingDeclarationSymbol = annotationConstructor
    }

    replaceAnnotations(annotations + annotationCall)
}
