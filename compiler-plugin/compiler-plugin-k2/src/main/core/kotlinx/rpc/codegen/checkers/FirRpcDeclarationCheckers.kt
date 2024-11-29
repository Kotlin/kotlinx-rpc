/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.checkers

import kotlinx.rpc.codegen.FirCheckersContext
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.DeclarationCheckers
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirClassChecker
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirFunctionChecker
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirRegularClassChecker
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirTypeParameterChecker
import org.jetbrains.kotlin.fir.analysis.checkers.expression.ExpressionCheckers
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirFunctionCallChecker

class FirRpcDeclarationCheckers(ctx: FirCheckersContext) : DeclarationCheckers() {
    override val regularClassCheckers: Set<FirRegularClassChecker> = setOf(
        FirRpcAnnotationChecker(ctx),
    )

    override val classCheckers: Set<FirClassChecker> = setOf(
        FirCheckedAnnotationFirClassChecker(ctx),
    )

    override val functionCheckers: Set<FirFunctionChecker> = setOf(
        FirCheckedAnnotationFirFunctionChecker(ctx)
    )

    override val typeParameterCheckers: Set<FirTypeParameterChecker> = setOf(
        FirCheckedAnnotationTypeParameterChecker(ctx)
    )
}

class FirRpcExpressionCheckers(ctx: FirCheckersContext) : ExpressionCheckers() {
    override val functionCallCheckers: Set<FirFunctionCallChecker> = setOf(
        FirCheckedAnnotationFunctionCallChecker(ctx),
    )
}
