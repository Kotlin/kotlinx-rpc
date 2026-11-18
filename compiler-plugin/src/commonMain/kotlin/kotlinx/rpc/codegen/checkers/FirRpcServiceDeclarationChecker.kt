/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.checkers

import kotlinx.rpc.codegen.FirRpcPredicates
import kotlinx.rpc.codegen.allDeclarations
import kotlinx.rpc.codegen.checkers.diagnostics.FirRpcDiagnostics
import kotlinx.rpc.codegen.common.RpcClassId
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirRegularClassChecker
import org.jetbrains.kotlin.fir.declarations.FirRegularClass
import org.jetbrains.kotlin.fir.declarations.utils.isSuspend
import org.jetbrains.kotlin.fir.extensions.predicateBasedProvider
import org.jetbrains.kotlin.fir.resolve.toClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol

class FirRpcServiceDeclarationChecker : FirRegularClassChecker(MppCheckerKind.Common) {
    context(context: CheckerContext, reporter: DiagnosticReporter)
    override fun check(declaration: FirRegularClass) {
        if (!context.session.predicateBasedProvider.matches(FirRpcPredicates.rpc, declaration)) {
            return
        }

        if (declaration.typeParameters.isNotEmpty()) {
            reporter.reportOn(
                source = declaration.source,
                factory = FirRpcDiagnostics.TYPE_PARAMETERS_IN_RPC_INTERFACE,
            )
        }

        declaration
            .allDeclarations(context.session)
            .filterIsInstance<FirNamedFunctionSymbol>()
            .onEach { function ->
            if (function.typeParameterSymbols.isNotEmpty()) {
                reporter.reportOn(
                    source = function.source,
                    factory = FirRpcDiagnostics.TYPE_PARAMETERS_IN_RPC_FUNCTION,
                )
            }

            val returnType = function.resolvedReturnTypeRef.coneType.toClassSymbol()
                ?: return@onEach

            if (returnType.classId != RpcClassId.flow && !function.isSuspend) {
                reporter.reportOn(
                    source = function.source,
                    factory = FirRpcDiagnostics.NON_SUSPENDING_REQUEST_WITHOUT_STREAMING_RETURN_TYPE,
                )
            }
        }
            .groupBy { it.name }
            .filter { (_, list) -> list.size > 1 }
            .forEach { (name, functions) ->
                functions.forEach { function ->
                    reporter.reportOn(
                        source = function.source,
                        factory = FirRpcDiagnostics.AD_HOC_POLYMORPHISM_IN_RPC_SERVICE,
                        a = functions.size,
                        b = name,
                    )
                }
            }
    }
}
