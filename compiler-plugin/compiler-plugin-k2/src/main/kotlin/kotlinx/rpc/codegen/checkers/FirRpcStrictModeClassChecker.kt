/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.checkers

import kotlinx.rpc.codegen.FirRpcPredicates
import kotlinx.rpc.codegen.checkers.diagnostics.FirRpcStrictModeDiagnostics
import kotlinx.rpc.codegen.checkers.util.functionParametersRecursionCheck
import kotlinx.rpc.codegen.common.RpcClassId
import kotlinx.rpc.codegen.vsApi
import org.jetbrains.kotlin.KtSourceElement
import org.jetbrains.kotlin.descriptors.isAnnotationClass
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.KtDiagnosticFactory0
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.declarations.FirRegularClass
import org.jetbrains.kotlin.fir.declarations.utils.isSuspend
import org.jetbrains.kotlin.fir.extensions.predicateBasedProvider
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirPropertySymbol

object FirRpcStrictModeClassChecker {
    fun check(
        declaration: FirRegularClass,
        context: CheckerContext,
        reporter: DiagnosticReporter,
    ) {
        if (!context.session.predicateBasedProvider.matches(FirRpcPredicates.rpc, declaration)
            // skip checks for annotation classes
            || declaration.classKind.isAnnotationClass
        ) {
            return
        }

        vsApi { declaration.declarationsVS(context.session) }.forEach { declaration ->
            when (declaration) {
                is FirPropertySymbol -> {
                    reporter.reportOn(declaration.source, FirRpcStrictModeDiagnostics.FIELD_IN_RPC_SERVICE, context)
                }

                is FirNamedFunctionSymbol -> {
                    fun reportOn(
                        element: KtSourceElement?,
                        checker: FirRpcStrictModeDiagnostics.() -> KtDiagnosticFactory0,
                    ) {
                        reporter.reportOn(element, FirRpcStrictModeDiagnostics.checker(), context)
                    }

                    val returnClassSymbol = vsApi {
                        declaration.resolvedReturnTypeRef.coneTypeVS.toClassSymbolVS(context.session)
                    }

                    if (returnClassSymbol?.classId == RpcClassId.flow && declaration.isSuspend) {
                        reportOn(declaration.source) { SUSPENDING_SERVER_STREAMING_IN_RPC_SERVICE }
                    }

                    functionParametersRecursionCheck(
                        function = declaration,
                        context = context,
                    ) { source, symbol, parents ->
                        when (symbol.classId) {
                            RpcClassId.stateFlow -> {
                                reportOn(source) { STATE_FLOW_IN_RPC_SERVICE }
                            }

                            RpcClassId.sharedFlow -> {
                                reportOn(source) { SHARED_FLOW_IN_RPC_SERVICE }
                            }

                            RpcClassId.flow -> {
                                if (parents.any { it.classId == RpcClassId.flow }) {
                                    reportOn(source) { NESTED_STREAMING_IN_RPC_SERVICE }
                                } else if (parents.isNotEmpty() && parents[0] == returnClassSymbol) {
                                    reportOn(source) { NON_TOP_LEVEL_SERVER_STREAMING_IN_RPC_SERVICE }
                                }
                            }
                        }
                    }
                }

                else -> {}
            }
        }
    }
}
