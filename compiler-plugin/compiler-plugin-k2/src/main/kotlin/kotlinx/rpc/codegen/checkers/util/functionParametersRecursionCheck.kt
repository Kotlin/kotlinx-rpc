/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.checkers.util

import kotlinx.rpc.codegen.checkers.FirSerializablePropertiesProvider
import kotlinx.rpc.codegen.checkers.serializablePropertiesProvider
import kotlinx.rpc.codegen.common.RpcClassId
import kotlinx.rpc.codegen.vsApi
import org.jetbrains.kotlin.KtSourceElement
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.extractArgumentsTypeRefAndSource
import org.jetbrains.kotlin.fir.analysis.checkers.toClassLikeSymbol
import org.jetbrains.kotlin.fir.scopes.impl.toConeType
import org.jetbrains.kotlin.fir.symbols.impl.FirClassLikeSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol
import org.jetbrains.kotlin.fir.types.FirTypeRef
import org.jetbrains.kotlin.utils.memoryOptimizedMap
import org.jetbrains.kotlin.utils.memoryOptimizedPlus
import kotlin.collections.get
import kotlin.collections.orEmpty
import kotlin.collections.withIndex

internal fun functionParametersRecursionCheck(
    function: FirNamedFunctionSymbol,
    context: CheckerContext,
    checker: (KtSourceElement?, FirClassLikeSymbol<*>, List<FirClassLikeSymbol<*>>) -> Unit,
) {
    val types = function.valueParameterSymbols.memoryOptimizedMap { parameter ->
        parameter.source to vsApi {
            parameter.resolvedReturnTypeRef
        }
    } memoryOptimizedPlus (function.resolvedReturnTypeRef.source to function.resolvedReturnTypeRef)

    types.forEach { (source, symbol) ->
        checkSerializableTypes(
            context = context,
            typeRef = symbol,
            serializablePropertiesProvider = context.session.serializablePropertiesProvider,
            checker = { classSymbol, parentContext -> checker(source, classSymbol, parentContext) },
        )
    }
}

private fun checkSerializableTypes(
    context: CheckerContext,
    typeRef: FirTypeRef,
    serializablePropertiesProvider: FirSerializablePropertiesProvider,
    parentContext: List<FirClassLikeSymbol<*>> = emptyList(),
    checker: (FirClassLikeSymbol<*>, List<FirClassLikeSymbol<*>>) -> Unit,
) {
    val symbol = typeRef.toClassLikeSymbol(context.session) ?: return

    checker(symbol, parentContext)

    if (symbol !is FirClassSymbol<*>) {
        return
    }

    val nextContext = parentContext memoryOptimizedPlus symbol

    if (symbol in parentContext && symbol.typeParameterSymbols.isEmpty()) {
        return
    }

    val typeParameters = extractArgumentsTypeRefAndSource(typeRef)
        .orEmpty()
        .withIndex()
        .associate { (i, refSource) ->
            symbol.typeParameterSymbols[i].toConeType() to refSource.typeRef
        }

    val flowProps: List<FirTypeRef> = if (symbol.classId == RpcClassId.flow) {
        listOf(typeParameters.values.toList()[0]!!)
    } else {
        emptyList()
    }

    serializablePropertiesProvider.getSerializablePropertiesForClass(symbol)
        .mapNotNull { property ->
            val resolvedTypeRef = property.resolvedReturnTypeRef
            if (resolvedTypeRef.toClassLikeSymbol(context.session) != null) {
                resolvedTypeRef
            } else {
                typeParameters[property.resolvedReturnType]
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
