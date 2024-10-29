/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.checkers

import kotlinx.rpc.codegen.FirRpcPredicates
import kotlinx.rpc.codegen.checkers.diagnostics.FirRpcDiagnostics
import kotlinx.rpc.codegen.isRemoteService
import kotlinx.rpc.codegen.remoteServiceSupertypeSource
import kotlinx.rpc.codegen.rpcAnnotationSource
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirRegularClassChecker
import org.jetbrains.kotlin.fir.declarations.FirRegularClass
import org.jetbrains.kotlin.fir.declarations.utils.isInterface
import org.jetbrains.kotlin.fir.extensions.predicateBasedProvider

object FirRpcAnnotationChecker : FirRegularClassChecker(MppCheckerKind.Common) {
    override fun check(
        declaration: FirRegularClass,
        context: CheckerContext,
        reporter: DiagnosticReporter,
    ) {
        val rpcAnnotated = context.session.predicateBasedProvider.matches(FirRpcPredicates.rpc, declaration)

        if (!declaration.isInterface && rpcAnnotated) {
            reporter.reportOn(
                source = declaration.symbol.rpcAnnotationSource(context.session),
                factory = FirRpcDiagnostics.WRONG_RPC_ANNOTATION_TARGET,
                context = context,
            )
        }

        if (declaration.symbol.isRemoteService(context.session) && !rpcAnnotated) {
            reporter.reportOn(
                source = declaration.symbol.remoteServiceSupertypeSource(context.session),
                factory = FirRpcDiagnostics.MISSING_RPC_ANNOTATION,
                context = context,
            )
        }
    }
}
