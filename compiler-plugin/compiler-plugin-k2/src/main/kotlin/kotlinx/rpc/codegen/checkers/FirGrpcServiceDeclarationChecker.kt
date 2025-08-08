/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.checkers

import kotlinx.rpc.codegen.FirRpcPredicates
import kotlinx.rpc.codegen.checkers.diagnostics.FirGrpcDiagnostics
import kotlinx.rpc.codegen.checkers.util.functionParametersRecursionCheck
import kotlinx.rpc.codegen.common.RpcClassId
import kotlinx.rpc.codegen.vsApi
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.declarations.FirRegularClass
import org.jetbrains.kotlin.fir.extensions.predicateBasedProvider
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol
import org.jetbrains.kotlin.fir.types.isMarkedNullable

object FirGrpcServiceDeclarationChecker {
    fun check(
        declaration: FirRegularClass,
        context: CheckerContext,
        reporter: DiagnosticReporter,
    ) {
        if (!context.session.predicateBasedProvider.matches(FirRpcPredicates.grpc, declaration)) {
            return
        }

        vsApi {
            declaration
                .declarationsVS(context.session)
                .filterIsInstance<FirNamedFunctionSymbol>()
        }.onEach { function ->
            if (function.valueParameterSymbols.size > 1) {
                reporter.reportOn(
                    source = function.source,
                    factory = FirGrpcDiagnostics.MULTIPLE_PARAMETERS_IN_GRPC_SERVICE,
                    context = context,
                )
            }

            if (function.valueParameterSymbols.size == 1) {
                val parameterSymbol = function.valueParameterSymbols[0]
                if (parameterSymbol.resolvedReturnType.isMarkedNullable) {
                    reporter.reportOn(
                        source = parameterSymbol.source,
                        factory = FirGrpcDiagnostics.NULLABLE_PARAMETER_IN_GRPC_SERVICE,
                        context = context,
                    )
                }

                functionParametersRecursionCheck(
                    function = function,
                    context = context,
                ) { source, symbol, parents ->
                    if (symbol.classId == RpcClassId.flow && parents.isNotEmpty()) {
                        reporter.reportOn(
                            source = source,
                            factory = FirGrpcDiagnostics.NON_TOP_LEVEL_CLIENT_STREAMING_IN_RPC_SERVICE,
                            context = context,
                        )
                    }
                }
            }

            if (function.resolvedReturnType.isMarkedNullable) {
                reporter.reportOn(
                    source = function.resolvedReturnTypeRef.source,
                    factory = FirGrpcDiagnostics.NULLABLE_RETURN_TYPE_IN_GRPC_SERVICE,
                    context = context,
                )
            }
        }
    }
}
