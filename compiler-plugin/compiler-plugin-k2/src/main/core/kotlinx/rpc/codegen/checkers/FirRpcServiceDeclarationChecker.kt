/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.checkers

import kotlinx.rpc.codegen.FirCheckersContext
import kotlinx.rpc.codegen.FirRpcPredicates
import kotlinx.rpc.codegen.checkers.diagnostics.FirRpcDiagnostics
import kotlinx.rpc.codegen.common.RpcClassId
import kotlinx.rpc.codegen.vsApi
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirClassChecker
import org.jetbrains.kotlin.fir.declarations.FirClass
import org.jetbrains.kotlin.fir.declarations.FirSimpleFunction
import org.jetbrains.kotlin.fir.declarations.utils.isSuspend
import org.jetbrains.kotlin.fir.extensions.predicateBasedProvider
import org.jetbrains.kotlin.fir.types.coneType

class FirRpcServiceDeclarationChecker(
    @Suppress("unused")
    private val ctx: FirCheckersContext,
) : FirClassChecker(MppCheckerKind.Common) {
    override fun check(
        declaration: FirClass,
        context: CheckerContext,
        reporter: DiagnosticReporter,
    ) {
        if (!context.session.predicateBasedProvider.matches(FirRpcPredicates.rpc, declaration)) {
            return
        }

        if (declaration.typeParameters.isNotEmpty()) {
            reporter.reportOn(
                source = declaration.source,
                factory = FirRpcDiagnostics.TYPE_PARAMETERS_IN_RPC_INTERFACE,
                context = context,
            )
        }

        declaration.declarations.filterIsInstance<FirSimpleFunction>().onEach { function ->
            if (function.typeParameters.isNotEmpty()) {
                reporter.reportOn(
                    source = function.source,
                    factory = FirRpcDiagnostics.TYPE_PARAMETERS_IN_RPC_FUNCTION,
                    context = context,
                )
            }

            val returnType = vsApi { function.returnTypeRef.coneType.toClassSymbolVS(context.session) }
                ?: return@onEach

            if (returnType.classId != RpcClassId.flow && !function.isSuspend) {
                reporter.reportOn(
                    source = function.source,
                    factory = FirRpcDiagnostics.NON_SUSPENDING_REQUEST_WITHOUT_STREAMING_RETURN_TYPE,
                    context = context,
                )
            }
        }
            .groupBy { it.name }
            .filter { (_, list) -> list.size > 1 }
            .forEach { name, functions ->
                functions.forEach { function ->
                    reporter.reportOn(
                        source = function.source,
                        factory = FirRpcDiagnostics.AD_HOC_POLYMORPHISM_IN_RPC_SERVICE,
                        a = functions.size,
                        b = name,
                        context = context,
                    )
                }
            }
    }
}
