/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.checkers

import kotlinx.rpc.codegen.FirRpcPredicates
import kotlinx.rpc.codegen.checkers.diagnostics.FirRpcStrictModeDiagnostics
import kotlinx.rpc.codegen.common.RpcClassId
import kotlinx.rpc.codegen.vsApi
import org.jetbrains.kotlin.KtSourceElement
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.KtDiagnosticFactory0
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.extractArgumentsTypeRefAndSource
import org.jetbrains.kotlin.fir.analysis.checkers.toClassLikeSymbol
import org.jetbrains.kotlin.fir.declarations.FirRegularClass
import org.jetbrains.kotlin.fir.declarations.utils.isSuspend
import org.jetbrains.kotlin.fir.extensions.predicateBasedProvider
import org.jetbrains.kotlin.fir.scopes.impl.toConeType
import org.jetbrains.kotlin.fir.symbols.impl.FirClassLikeSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirPropertySymbol
import org.jetbrains.kotlin.fir.types.FirTypeRef
import org.jetbrains.kotlin.utils.memoryOptimizedMap
import org.jetbrains.kotlin.utils.memoryOptimizedPlus

object FirRpcStrictModeClassChecker {
    fun check(
        declaration: FirRegularClass,
        context: CheckerContext,
        reporter: DiagnosticReporter,
    ) {
        if (!context.session.predicateBasedProvider.matches(FirRpcPredicates.rpc, declaration)) {
            return
        }

        val serializablePropertiesProvider = context.session.serializablePropertiesProvider
        vsApi { declaration.declarationsVS(context.session) }.forEach { declaration ->
            when (declaration) {
                is FirPropertySymbol -> {
                    reporter.reportOn(declaration.source, FirRpcStrictModeDiagnostics.FIELD_IN_RPC_SERVICE, context)
                }

                is FirNamedFunctionSymbol -> {
                    checkFunction(declaration, context, reporter, serializablePropertiesProvider)
                }

                else -> {}
            }
        }
    }

    private fun checkFunction(
        function: FirNamedFunctionSymbol,
        context: CheckerContext,
        reporter: DiagnosticReporter,
        serializablePropertiesProvider: FirSerializablePropertiesProvider,
    ) {
        fun reportOn(element: KtSourceElement?, checker: FirRpcStrictModeDiagnostics.() -> KtDiagnosticFactory0?) {
            reporter.reportOn(element, FirRpcStrictModeDiagnostics.checker() ?: return, context)
        }

        val returnClassSymbol = vsApi {
            function.resolvedReturnTypeRef.coneTypeVS.toClassSymbolVS(context.session)
        }

        val types = function.valueParameterSymbols.memoryOptimizedMap { parameter ->
            parameter.source to vsApi {
                parameter.resolvedReturnTypeRef
            }
        } memoryOptimizedPlus (function.resolvedReturnTypeRef.source to function.resolvedReturnTypeRef)

        types.forEach { (source, symbol) ->
            checkSerializableTypes<FirClassLikeSymbol<*>>(
                context = context,
                typeRef = symbol,
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
        typeRef: FirTypeRef,
        serializablePropertiesProvider: FirSerializablePropertiesProvider,
        parentContext: List<ContextElement> = emptyList(),
        checker: (FirClassLikeSymbol<*>, List<ContextElement>) -> ContextElement?,
    ) {
        val symbol = typeRef.toClassLikeSymbol(context.session) ?: return
        val newElement = checker(symbol, parentContext)
        val nextContext = if (newElement != null) {
            parentContext memoryOptimizedPlus newElement
        } else {
            parentContext
        }

        if (symbol !is FirClassSymbol<*>) {
            return
        }

        val extracted = extractArgumentsTypeRefAndSource(typeRef)
            .orEmpty()
            .withIndex()
            .associate { (i, refSource) ->
                symbol.typeParameterSymbols[i].toConeType() to refSource.typeRef
            }

        val flowProps: List<FirTypeRef> = if (symbol.classId == RpcClassId.flow) {
            listOf(extracted.values.toList()[0]!!)
        } else {
            emptyList()
        }

        serializablePropertiesProvider.getSerializablePropertiesForClass(symbol)
            .mapNotNull { property ->
                val resolvedTypeRef = property.resolvedReturnTypeRef
                if (resolvedTypeRef.toClassLikeSymbol(context.session) != null) {
                    resolvedTypeRef
                } else {
                    extracted[property.resolvedReturnType]
                }
            }.memoryOptimizedPlus(flowProps)
            .forEach { symbol ->
                checkSerializableTypes(
                    context = context,
                    typeRef = symbol,
                    serializablePropertiesProvider = serializablePropertiesProvider,
                    parentContext = nextContext,
                    checker = checker,
                )
            }
    }
}
