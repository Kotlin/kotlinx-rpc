/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.checkers

import kotlinx.rpc.codegen.FirCheckersContext
import kotlinx.rpc.codegen.FirRpcPredicates
import kotlinx.rpc.codegen.checkers.diagnostics.FirRpcStrictModeDiagnostics
import kotlinx.rpc.codegen.common.RpcCallableId
import kotlinx.rpc.codegen.common.RpcClassId
import kotlinx.rpc.codegen.vsApi
import org.jetbrains.kotlin.KtSourceElement
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.KtDiagnosticFactory0
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirClassChecker
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirFunctionCallChecker
import org.jetbrains.kotlin.fir.declarations.FirClass
import org.jetbrains.kotlin.fir.declarations.FirProperty
import org.jetbrains.kotlin.fir.declarations.FirSimpleFunction
import org.jetbrains.kotlin.fir.declarations.utils.isSuspend
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.fir.extensions.predicateBasedProvider
import org.jetbrains.kotlin.fir.references.toResolvedCallableSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.types.coneType
import org.jetbrains.kotlin.utils.memoryOptimizedMap
import org.jetbrains.kotlin.utils.memoryOptimizedPlus
import org.jetbrains.kotlinx.serialization.compiler.fir.services.FirSerializablePropertiesProvider
import org.jetbrains.kotlinx.serialization.compiler.fir.services.serializablePropertiesProvider

class FirRpcStrictModeExpressionChecker(
    private val ctx: FirCheckersContext,
) : FirFunctionCallChecker(MppCheckerKind.Common) {
    private val streamScopeFunctions = setOf(
        RpcCallableId.StreamScope,
        RpcCallableId.streamScoped,
        RpcCallableId.withStreamScope,
        RpcCallableId.invokeOnStreamScopeCompletion,
    )

    override fun check(
        expression: FirFunctionCall,
        context: CheckerContext,
        reporter: DiagnosticReporter,
    ) {
        expression.calleeReference.toResolvedCallableSymbol()?.let { symbol ->
            if (symbol.callableId in streamScopeFunctions) {
                ctx.strictModeDiagnostics.STREAM_SCOPE_FUNCTION_IN_RPC?.let {
                    reporter.reportOn(expression.calleeReference.source, it, context)
                }
            }
        }
    }
}

class FirRpcStrictModeClassChecker(private val ctx: FirCheckersContext) : FirClassChecker(MppCheckerKind.Common) {
    override fun check(
        declaration: FirClass,
        context: CheckerContext,
        reporter: DiagnosticReporter,
    ) {
        if (!context.session.predicateBasedProvider.matches(FirRpcPredicates.rpc, declaration)) {
            return
        }

        val serializablePropertiesProvider = context.session.serializablePropertiesProvider
        declaration.declarations.forEach { declaration ->
            when (declaration) {
                is FirProperty -> {
                    ctx.strictModeDiagnostics.FIELD_IN_RPC_SERVICE?.let {
                        reporter.reportOn(declaration.source, it, context)
                    }
                }

                is FirSimpleFunction -> {
                    checkFunction(declaration, context, reporter, serializablePropertiesProvider)
                }

                else -> {}
            }
        }
    }

    private fun checkFunction(
        function: FirSimpleFunction,
        context: CheckerContext,
        reporter: DiagnosticReporter,
        serializablePropertiesProvider: FirSerializablePropertiesProvider,
    ) {
        fun reportOn(element: KtSourceElement?, checker: FirRpcStrictModeDiagnostics.() -> KtDiagnosticFactory0?) {
            reporter.reportOn(element, ctx.strictModeDiagnostics.checker() ?: return, context)
        }

        val returnClassSymbol = vsApi {
            function.returnTypeRef.coneType.toClassSymbolVS(context.session)
        }

        val types = function.valueParameters.memoryOptimizedMap { parameter ->
            parameter.source to vsApi {
                parameter.returnTypeRef.coneType.toClassSymbolVS(context.session)
            }
        } memoryOptimizedPlus (function.returnTypeRef.source to returnClassSymbol)

        types.filter { (_, symbol) ->
            symbol != null
        }.forEach { (source, symbol) ->
            checkSerializableTypes<FirClassSymbol<*>>(
                context = context,
                clazz = symbol!!,
                serializablePropertiesProvider = serializablePropertiesProvider,
            ) { symbol, parents ->
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

                symbol
            }
        }

        if (returnClassSymbol?.classId == RpcClassId.flow && function.isSuspend) {
            reportOn(function.source) { SUSPENDING_SERVER_STREAMING_IN_RPC_SERVICE }
        }
    }

    private fun <ContextElement> checkSerializableTypes(
        context: CheckerContext,
        clazz: FirClassSymbol<*>,
        serializablePropertiesProvider: FirSerializablePropertiesProvider,
        parentContext: List<ContextElement> = emptyList(),
        checker: (FirClassSymbol<*>, List<ContextElement>) -> ContextElement?,
    ) {
        val newElement = checker(clazz, parentContext)
        val nextContext = if (newElement != null) {
            parentContext memoryOptimizedPlus newElement
        } else {
            parentContext
        }

        serializablePropertiesProvider.getSerializablePropertiesForClass(clazz)
            .serializableProperties
            .mapNotNull { property ->
                vsApi {
                    property.propertySymbol.resolvedReturnType.toClassSymbolVS(context.session)
                }
            }.forEach { symbol ->
                checkSerializableTypes(
                    context = context,
                    clazz = symbol,
                    serializablePropertiesProvider = serializablePropertiesProvider,
                    parentContext = nextContext,
                    checker = checker,
                )
            }
    }
}
