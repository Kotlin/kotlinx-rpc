/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.checkers

import kotlinx.rpc.codegen.FirCheckersContext
import kotlinx.rpc.codegen.FirRpcPredicates
import kotlinx.rpc.codegen.checkers.FirCheckedAnnotationHelper.checkTypeArguments
import kotlinx.rpc.codegen.checkers.diagnostics.FirRpcDiagnostics
import kotlinx.rpc.codegen.common.RpcClassId
import kotlinx.rpc.codegen.vsApi
import org.jetbrains.kotlin.KtSourceElement
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.FirElement
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.FirTypeRefSource
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirClassChecker
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirFunctionChecker
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirTypeParameterChecker
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirFunctionCallChecker
import org.jetbrains.kotlin.fir.analysis.checkers.extractArgumentsTypeRefAndSource
import org.jetbrains.kotlin.fir.caches.getValue
import org.jetbrains.kotlin.fir.declarations.FirClass
import org.jetbrains.kotlin.fir.declarations.FirFunction
import org.jetbrains.kotlin.fir.declarations.FirTypeParameter
import org.jetbrains.kotlin.fir.declarations.hasAnnotation
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.fir.extensions.predicateBasedProvider
import org.jetbrains.kotlin.fir.references.toResolvedCallableSymbol
import org.jetbrains.kotlin.fir.resolve.defaultType
import org.jetbrains.kotlin.fir.symbols.FirBasedSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirTypeParameterSymbol
import org.jetbrains.kotlin.fir.types.*
import org.jetbrains.kotlin.name.ClassId

class FirCheckedAnnotationFunctionCallChecker(
    private val ctx: FirCheckersContext,
) : FirFunctionCallChecker(MppCheckerKind.Common) {
    override fun check(
        expression: FirFunctionCall,
        context: CheckerContext,
        reporter: DiagnosticReporter,
    ) {
        checkTypeArguments(
            context = context,
            reporter = reporter,
            ctx = ctx,
            origin = expression,
            originMapper = { it },
            symbolProvider = { it.calleeReference.toResolvedCallableSymbol() },
            typeParameterSymbolsProvider = { it.typeParameterSymbols },
            typeArgumentsProvider = { it.typeArguments },
            typeArgumentsMapper = { it.toConeTypeProjection() },
            sourceProvider = { _, type -> type.source },
        )
    }
}

class FirCheckedAnnotationTypeParameterChecker(
    private val ctx: FirCheckersContext,
) : FirTypeParameterChecker(MppCheckerKind.Common) {
    override fun check(
        declaration: FirTypeParameter,
        context: CheckerContext,
        reporter: DiagnosticReporter,
    ) {
        @Suppress("DuplicatedCode")
        declaration.bounds.forEach { bound ->
            checkTypeArguments(
                context = context,
                reporter = reporter,
                ctx = ctx,
                origin = bound,
                originMapper = { it.coneType },
                symbolProvider = { vsApi { it.toClassSymbolVS(context.session) } },
                typeParameterSymbolsProvider = { it.typeParameterSymbols },
                typeArgumentsProvider = { it.typeArguments.toList() },
                typeArgumentsMapper = { it },
                sourceProvider = { _, _ -> declaration.source },
            )
        }
    }
}

class FirCheckedAnnotationFirClassChecker(
    private val ctx: FirCheckersContext,
) : FirClassChecker(MppCheckerKind.Common) {
    override fun check(
        declaration: FirClass,
        context: CheckerContext,
        reporter: DiagnosticReporter,
    ) {
        @Suppress("DuplicatedCode")
        declaration.superTypeRefs.forEach { superType ->
            checkTypeArguments(
                context = context,
                reporter = reporter,
                ctx = ctx,
                origin = superType,
                originMapper = { it.coneType },
                symbolProvider = { vsApi { it.toClassSymbolVS(context.session) } },
                typeParameterSymbolsProvider = { it.typeParameterSymbols },
                typeArgumentsProvider = { it.typeArguments.toList() },
                typeArgumentsMapper = { it },
                sourceProvider = { ref, _ -> ref.source },
            )
        }
    }
}

class FirCheckedAnnotationFirFunctionChecker(
    private val ctx: FirCheckersContext,
) : FirFunctionChecker(MppCheckerKind.Common) {
    override fun check(
        declaration: FirFunction,
        context: CheckerContext,
        reporter: DiagnosticReporter,
    ) {
        declaration.valueParameters.forEach { valueParameter ->
            checkTypeArguments(
                context = context,
                reporter = reporter,
                ctx = ctx,
                origin = valueParameter.returnTypeRef,
                originMapper = { it.coneType },
                symbolProvider = { vsApi { it.toClassSymbolVS(context.session) } },
                typeParameterSymbolsProvider = { it.typeParameterSymbols },
                typeArgumentsProvider = { it.typeArguments.toList() },
                typeArgumentsMapper = { it },
                sourceProvider = { ref, _ -> ref.source },
            )
        }
    }
}

object FirCheckedAnnotationHelper {
    @Suppress("detekt.LongParameterList", "detekt.CyclomaticComplexMethod", "detekt.LongMethod")
    fun <Origin, OriginTransformed, Symbol, TypeArgument> checkTypeArguments(
        context: CheckerContext,
        reporter: DiagnosticReporter,
        ctx: FirCheckersContext,
        origin: Origin,
        originTypeRefSource: FirTypeRefSource? = null,
        originMapper: (Origin) -> OriginTransformed?,
        symbolProvider: (OriginTransformed) -> Symbol?,
        typeParameterSymbolsProvider: (Symbol) -> List<FirTypeParameterSymbol>?,
        typeArgumentsProvider: (OriginTransformed) -> List<TypeArgument>,
        typeArgumentsMapper: (TypeArgument) -> ConeTypeProjection?,
        sourceProvider: (Origin, TypeArgument) -> KtSourceElement?,
    ) {
        if (!ctx.annotationTypeSafetyEnabled) {
            return
        }

        val originTransformed = originMapper(origin) ?: return
        val symbol = symbolProvider(originTransformed) ?: return

        val parametersWithAnnotations = checkedAnnotationsOnTypeParameters(
            session = context.session,
            ctx = ctx,
            typeParameterSymbols = typeParameterSymbolsProvider(symbol),
        )

        val typeArguments = typeArgumentsProvider(originTransformed)

        val extractedOriginSources = extractArgumentsTypeRefAndSource(
            typeRef = originTypeRefSource?.typeRef
                ?: (origin as? FirTypeProjectionWithVariance)?.typeRef
                ?: (origin as? FirTypeRef)
        ).orEmpty()

        parametersWithAnnotations.forEach { (i, annotations) ->
            val typeArgument = typeArguments[i]
            val type = typeArgumentsMapper(typeArgument)?.type
            val classSymbol = vsApi { type?.toClassSymbolVS(context.session) }

            val symbol = when {
                classSymbol != null -> {
                    classSymbol
                }

                typeArgument is ConeTypeParameterType -> {
                    typeArgument.lookupTag.typeParameterSymbol
                }

                typeArgument is FirTypeProjectionWithVariance -> {
                    (typeArgument.typeRef.coneType as? ConeTypeParameterType)
                        ?.lookupTag
                        ?.typeParameterSymbol
                }

                else -> {
                    null
                }
            } ?: return@forEach

            val originSource = extractedOriginSources.getOrNull(i)?.source
                ?: originTypeRefSource?.source
                ?: (origin as? FirTypeProjectionWithVariance)?.source
                ?: (origin as? FirTypeRef)?.source

            val source = when {
                classSymbol != null -> {
                    originSource ?: sourceProvider(origin, typeArgument)
                }

                typeArgument is ConeTypeParameterType -> {
                    originSource ?: typeArgument.lookupTag.typeParameterSymbol.source
                }

                typeArgument is FirTypeProjectionWithVariance -> {
                    extractArgumentsTypeRefAndSource(typeArgument.typeRef)
                        ?.getOrNull(i)
                        ?.source
                        ?: typeArgument.source
                }

                else -> {
                    null
                }
            }

            checkSymbolAnnotated(
                annotations = annotations,
                classSymbol = symbol,
                source = source,
                context = context,
                reporter = reporter,
            )
        }

        typeArguments.forEachIndexed { i, typeArgument ->
            val nextOriginSource = (origin as? FirTypeProjectionWithVariance)
                ?.let { FirTypeRefSource(it.typeRef, it.source) }
                ?: (origin as? FirTypeRef)?.let { FirTypeRefSource(it, it.source) }

            checkTypeArguments<TypeArgument, ConeKotlinType, FirClassSymbol<*>, ConeTypeProjection>(
                context = context,
                reporter = reporter,
                ctx = ctx,
                origin = typeArgument,
                originMapper = {
                    when (typeArgument) {
                        is ConeKotlinTypeProjection -> typeArgument.type
                        is FirTypeProjectionWithVariance -> typeArgument.toConeTypeProjection()
                        else -> null
                    }?.type
                },
                originTypeRefSource = extractedOriginSources.getOrNull(i) ?: nextOriginSource ?: originTypeRefSource,
                symbolProvider = { vsApi { it.toClassSymbolVS(context.session) } },
                typeParameterSymbolsProvider = { it.typeParameterSymbols },
                typeArgumentsProvider = { it.typeArguments.toList() },
                typeArgumentsMapper = { it },
                sourceProvider = { arg, _ ->
                    when (arg) {
                        is FirElement -> arg.source
                        is ConeKotlinTypeProjection -> sourceProvider(origin, arg)
                        else -> null
                    }
                },
            )
        }
    }

    private fun checkedAnnotationsOnTypeParameters(
        session: FirSession,
        ctx: FirCheckersContext,
        typeParameterSymbols: List<FirTypeParameterSymbol>?,
    ): List<Pair<Int, List<FirClassSymbol<*>>>> {
        return typeParameterSymbols.orEmpty().withIndex().filter { (_, parameter) ->
            session.predicateBasedProvider.matches(
                predicate = FirRpcPredicates.checkedAnnotationMeta,
                declaration = parameter,
            )
        }.map { (i, parameter) ->
            i to ctx.typeParametersCache.getValue(parameter)
        }
    }

    fun checkedAnnotations(
        session: FirSession,
        symbol: FirBasedSymbol<*>,
        visited: Set<FirBasedSymbol<*>> = emptySet(),
    ): List<FirClassSymbol<*>> {
        return symbol.annotations.mapNotNull {
            vsApi { it.resolvedType.toClassSymbolVS(session) }
        }.filter { annotation ->
            when {
                annotation in visited -> false
                annotation.hasAnnotation(RpcClassId.checkedTypeAnnotation, session) -> true
                else -> checkedAnnotations(session, annotation, visited + annotation).isNotEmpty()
            }
        }
    }

    private fun checkSymbolAnnotated(
        annotations: List<FirClassSymbol<*>>,
        classSymbol: FirBasedSymbol<*>,
        source: KtSourceElement?,
        context: CheckerContext,
        reporter: DiagnosticReporter,
    ) {
        for (annotationClass in annotations) {
            val hasCheckedAnnotation = hasCheckedAnnotation(
                session = context.session,
                symbol = classSymbol,
                annotationId = annotationClass.classId,
            )

            if (!hasCheckedAnnotation) {
                reporter.reportOn(
                    source = source,
                    factory = FirRpcDiagnostics.CHECKED_ANNOTATION_VIOLATION,
                    a = annotationClass.defaultType(),
                    context = context,
                )
            }
        }
    }

    private fun hasCheckedAnnotation(
        session: FirSession,
        symbol: FirBasedSymbol<*>,
        annotationId: ClassId,
        visited: Set<FirBasedSymbol<*>> = emptySet(),
    ): Boolean {
        return when {
            symbol in visited -> false
            symbol.hasAnnotation(annotationId, session) -> true
            else -> symbol.annotations.any { annotation ->
                vsApi { annotation.resolvedType.toClassSymbolVS(session) }?.let {
                    hasCheckedAnnotation(session, it, annotationId, visited + symbol)
                } == true
            }
        }
    }
}
