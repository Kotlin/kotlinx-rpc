/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.checkers

import kotlinx.rpc.codegen.FirCheckersContext
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirClassChecker
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirFunctionChecker
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirRegularClassChecker
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirTypeParameterChecker
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirFunctionCallChecker
import org.jetbrains.kotlin.fir.declarations.FirClass
import org.jetbrains.kotlin.fir.declarations.FirFunction
import org.jetbrains.kotlin.fir.declarations.FirRegularClass
import org.jetbrains.kotlin.fir.declarations.FirTypeParameter
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall

class FirCheckedAnnotationFunctionCallCheckerVS(
    private val ctx: FirCheckersContext,
) : FirFunctionCallChecker(MppCheckerKind.Common) {
    context(context: CheckerContext, reporter: DiagnosticReporter)
    override fun check(expression: FirFunctionCall) {
        FirCheckedAnnotationFunctionCallChecker.check(ctx, expression, context, reporter)
    }
}

class FirCheckedAnnotationTypeParameterCheckerVS(
    private val ctx: FirCheckersContext,
) : FirTypeParameterChecker(MppCheckerKind.Common) {
    context(context: CheckerContext, reporter: DiagnosticReporter)
    override fun check(declaration: FirTypeParameter) {
        FirCheckedAnnotationTypeParameterChecker.check(ctx, declaration, context, reporter)
    }
}

class FirCheckedAnnotationFirClassCheckerVS(
    private val ctx: FirCheckersContext,
) : FirClassChecker(MppCheckerKind.Common) {
    context(context: CheckerContext, reporter: DiagnosticReporter)
    override fun check(declaration: FirClass) {
        FirCheckedAnnotationFirClassChecker.check(ctx, declaration, context, reporter)
    }
}

class FirCheckedAnnotationFirFunctionCheckerVS(
    private val ctx: FirCheckersContext,
) : FirFunctionChecker(MppCheckerKind.Common) {
    context(context: CheckerContext, reporter: DiagnosticReporter)
    override fun check(declaration: FirFunction) {
        FirCheckedAnnotationFirFunctionChecker.check(ctx, declaration, context, reporter)
    }
}

class FirRpcAnnotationCheckerVS(private val ctx: FirCheckersContext) : FirRegularClassChecker(MppCheckerKind.Common) {
    context(context: CheckerContext, reporter: DiagnosticReporter)
    override fun check(declaration: FirRegularClass) {
        FirRpcAnnotationChecker.check(ctx, declaration, context, reporter)
    }
}

class FirRpcServiceDeclarationCheckerVS(
    private val ctx: FirCheckersContext,
) : FirRegularClassChecker(MppCheckerKind.Common) {
    context(context: CheckerContext, reporter: DiagnosticReporter)
    override fun check(declaration: FirRegularClass) {
        FirRpcServiceDeclarationChecker.check(ctx, declaration, context, reporter)
    }
}

class FirRpcStrictModeClassCheckerVS : FirRegularClassChecker(MppCheckerKind.Common) {
    context(context: CheckerContext, reporter: DiagnosticReporter)
    override fun check(declaration: FirRegularClass) {
        FirRpcStrictModeClassChecker.check(declaration, context, reporter)
    }
}
