/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen

import kotlinx.rpc.codegen.checkers.FirCheckedAnnotationHelper
import kotlinx.rpc.codegen.checkers.FirRpcDeclarationCheckers
import kotlinx.rpc.codegen.checkers.FirRpcExpressionCheckers
import kotlinx.rpc.codegen.checkers.diagnostics.FirRpcStrictModeDiagnostics
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.DeclarationCheckers
import org.jetbrains.kotlin.fir.analysis.checkers.expression.ExpressionCheckers
import org.jetbrains.kotlin.fir.analysis.extensions.FirAdditionalCheckersExtension
import org.jetbrains.kotlin.fir.caches.createCache
import org.jetbrains.kotlin.fir.caches.firCachesFactory
import org.jetbrains.kotlin.fir.extensions.FirDeclarationPredicateRegistrar
import org.jetbrains.kotlin.fir.symbols.impl.FirTypeParameterSymbol

class FirRpcAdditionalCheckers(
    session: FirSession,
    serializationIsPresent: Boolean,
    modes: StrictModeAggregator,
) : FirAdditionalCheckersExtension(session) {
    override fun FirDeclarationPredicateRegistrar.registerPredicates() {
        register(FirRpcPredicates.rpc)
        register(FirRpcPredicates.checkedAnnotationMeta)
    }

    private val ctx = FirCheckersContext(session, serializationIsPresent, modes)

    override val declarationCheckers: DeclarationCheckers = FirRpcDeclarationCheckers(ctx)
    override val expressionCheckers: ExpressionCheckers = FirRpcExpressionCheckers(ctx)
}

class FirCheckersContext(
    private val session: FirSession,
    val serializationIsPresent: Boolean,
    modes: StrictModeAggregator,
) {
    val strictModeDiagnostics = FirRpcStrictModeDiagnostics(modes)

    val typeParametersCache = session.firCachesFactory.createCache { typeParameter: FirTypeParameterSymbol ->
        FirCheckedAnnotationHelper.checkedAnnotations(session, typeParameter)
    }
}
