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
    //##csm FirCheckedAnnotationFunctionCallCheckerVS_context
    //##csm specific=[2.0.0...2.1.21, 2.2.0-ij251-*]
    override fun check(expression: FirFunctionCall, context: CheckerContext, reporter: DiagnosticReporter) {
        FirCheckedAnnotationFunctionCallChecker.check(ctx, expression, context, reporter)
    }
    //##csm /specific
    //##csm default
    context(context: CheckerContext, reporter: DiagnosticReporter)
    override fun check(expression: FirFunctionCall) {
        FirCheckedAnnotationFunctionCallChecker.check(ctx, expression, context, reporter)
    }
    //##csm /default
    //##csm /FirCheckedAnnotationFunctionCallCheckerVS_context
}

class FirCheckedAnnotationTypeParameterCheckerVS(
    private val ctx: FirCheckersContext,
) : FirTypeParameterChecker(MppCheckerKind.Common) {
    //##csm FirCheckedAnnotationTypeParameterCheckerVS_context
    //##csm specific=[2.0.0...2.1.21, 2.2.0-ij251-*]
    override fun check(declaration: FirTypeParameter, context: CheckerContext, reporter: DiagnosticReporter) {
        FirCheckedAnnotationTypeParameterChecker.check(ctx, declaration, context, reporter)
    }
    //##csm /specific
    //##csm default
    context(context: CheckerContext, reporter: DiagnosticReporter)
    override fun check(declaration: FirTypeParameter) {
        FirCheckedAnnotationTypeParameterChecker.check(ctx, declaration, context, reporter)
    }
    //##csm /default
    //##csm /FirCheckedAnnotationTypeParameterCheckerVS_context
}

class FirCheckedAnnotationFirClassCheckerVS(
    private val ctx: FirCheckersContext,
) : FirClassChecker(MppCheckerKind.Common) {
    //##csm FirCheckedAnnotationFirClassCheckerVS_context
    //##csm specific=[2.0.0...2.1.21, 2.2.0-ij251-*]
    override fun check(declaration: FirClass, context: CheckerContext, reporter: DiagnosticReporter) {
        FirCheckedAnnotationFirClassChecker.check(ctx, declaration, context, reporter)
    }
    //##csm /specific
    //##csm default
    context(context: CheckerContext, reporter: DiagnosticReporter)
    override fun check(declaration: FirClass) {
        FirCheckedAnnotationFirClassChecker.check(ctx, declaration, context, reporter)
    }
    //##csm /default
    //##csm /FirCheckedAnnotationFirClassCheckerVS_context
}

class FirCheckedAnnotationFirFunctionCheckerVS(
    private val ctx: FirCheckersContext,
) : FirFunctionChecker(MppCheckerKind.Common) {
    //##csm FirCheckedAnnotationFirFunctionCheckerVS_context
    //##csm specific=[2.0.0...2.1.21, 2.2.0-ij251-*]
    override fun check(declaration: FirFunction, context: CheckerContext, reporter: DiagnosticReporter) {
        FirCheckedAnnotationFirFunctionChecker.check(ctx, declaration, context, reporter)
    }
    //##csm /specific
    //##csm default
    context(context: CheckerContext, reporter: DiagnosticReporter)
    override fun check(declaration: FirFunction) {
        FirCheckedAnnotationFirFunctionChecker.check(ctx, declaration, context, reporter)
    }
    //##csm /default
    //##csm /FirCheckedAnnotationFirFunctionCheckerVS_context
}

class FirRpcAnnotationCheckerVS : FirRegularClassChecker(MppCheckerKind.Common) {
    //##csm FirRpcAnnotationCheckerVS_context
    //##csm specific=[2.0.0...2.1.21, 2.2.0-ij251-*]
    override fun check(declaration: FirRegularClass, context: CheckerContext, reporter: DiagnosticReporter) {
        FirRpcAnnotationChecker.check(declaration, context, reporter)
    }
    //##csm /specific
    //##csm default
    context(context: CheckerContext, reporter: DiagnosticReporter)
    override fun check(declaration: FirRegularClass) {
        FirRpcAnnotationChecker.check(declaration, context, reporter)
    }
    //##csm /default
    //##csm /FirRpcAnnotationCheckerVS_context
}

class FirRpcServiceDeclarationCheckerVS(
    private val ctx: FirCheckersContext,
) : FirRegularClassChecker(MppCheckerKind.Common) {
    //##csm FirRpcServiceDeclarationCheckerVS_context
    //##csm specific=[2.0.0...2.1.21, 2.2.0-ij251-*]
    override fun check(declaration: FirRegularClass, context: CheckerContext, reporter: DiagnosticReporter) {
        FirRpcServiceDeclarationChecker.check(ctx, declaration, context, reporter)
    }
    //##csm /specific
    //##csm default
    context(context: CheckerContext, reporter: DiagnosticReporter)
    override fun check(declaration: FirRegularClass) {
        FirRpcServiceDeclarationChecker.check(ctx, declaration, context, reporter)
    }
    //##csm /default
    //##csm /FirRpcServiceDeclarationCheckerVS_context
}

class FirRpcStrictModeClassCheckerVS : FirRegularClassChecker(MppCheckerKind.Common) {
    //##csm FirRpcStrictModeClassCheckerVS_context
    //##csm specific=[2.0.0...2.1.21, 2.2.0-ij251-*]
    override fun check(declaration: FirRegularClass, context: CheckerContext, reporter: DiagnosticReporter) {
        FirRpcStrictModeClassChecker.check(declaration, context, reporter)
    }
    //##csm /specific
    //##csm default
    context(context: CheckerContext, reporter: DiagnosticReporter)
    override fun check(declaration: FirRegularClass) {
        FirRpcStrictModeClassChecker.check(declaration, context, reporter)
    }
    //##csm /default
    //##csm /FirRpcStrictModeClassCheckerVS_context
}

class FirGrpcServiceDeclarationCheckerVS : FirRegularClassChecker(MppCheckerKind.Common) {
    //##csm FirGrpcServiceDeclarationCheckerVS_context
    //##csm specific=[2.0.0...2.1.21, 2.2.0-ij251-*]
    override fun check(declaration: FirRegularClass, context: CheckerContext, reporter: DiagnosticReporter) {
        FirGrpcServiceDeclarationChecker.check(declaration, context, reporter)
    }
    //##csm /specific
    //##csm default
    context(context: CheckerContext, reporter: DiagnosticReporter)
    override fun check(declaration: FirRegularClass) {
        FirGrpcServiceDeclarationChecker.check(declaration, context, reporter)
    }
    //##csm /default
    //##csm /FirGrpcServiceDeclarationCheckerVS_context
}

class FirWithCodecDeclarationCheckerVS : FirRegularClassChecker(MppCheckerKind.Common) {
    //##csm FirWithCodecDeclarationChecker_context
    //##csm specific=[2.0.0...2.1.21, 2.2.0-ij251-*]
    override fun check(declaration: FirRegularClass, context: CheckerContext, reporter: DiagnosticReporter) {
        FirWithCodecDeclarationChecker.check(declaration, context, reporter)
    }
    //##csm /specific
    //##csm default
    context(context: CheckerContext, reporter: DiagnosticReporter)
    override fun check(declaration: FirRegularClass) {
        FirWithCodecDeclarationChecker.check(declaration, context, reporter)
    }
    //##csm /default
    //##csm /FirWithCodecDeclarationChecker_context
}

class FirProtoMessageAnnotationCheckerVS : FirRegularClassChecker(MppCheckerKind.Common) {
    //##csm FirWithCodecDeclarationChecker_context
    //##csm specific=[2.0.0...2.1.21, 2.2.0-ij251-*]
    override fun check(declaration: FirRegularClass, context: CheckerContext, reporter: DiagnosticReporter) {
        FirProtoMessageAnnotationChecker.check(declaration, context, reporter)
    }
    //##csm /specific
    //##csm default
    context(context: CheckerContext, reporter: DiagnosticReporter)
    override fun check(declaration: FirRegularClass) {
        FirProtoMessageAnnotationChecker.check(declaration, context, reporter)
    }
    //##csm /default
    //##csm /FirWithCodecDeclarationChecker_context
}

