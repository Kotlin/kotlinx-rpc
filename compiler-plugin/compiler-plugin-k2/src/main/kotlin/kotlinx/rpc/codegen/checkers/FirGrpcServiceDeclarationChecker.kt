/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.checkers

import kotlinx.rpc.codegen.FirRpcPredicates
import kotlinx.rpc.codegen.checkers.IdentifierRegexes.identifierRegex
import kotlinx.rpc.codegen.checkers.IdentifierRegexes.packageIdentifierRegex
import kotlinx.rpc.codegen.checkers.diagnostics.FirGrpcDiagnostics
import kotlinx.rpc.codegen.checkers.util.functionParametersRecursionCheck
import kotlinx.rpc.codegen.common.RpcClassId
import kotlinx.rpc.codegen.vsApi
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.SourceElementPositioningStrategies
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.declarations.FirRegularClass
import org.jetbrains.kotlin.fir.declarations.getAnnotationByClassId
import org.jetbrains.kotlin.fir.declarations.getBooleanArgument
import org.jetbrains.kotlin.fir.declarations.getStringArgument
import org.jetbrains.kotlin.fir.extensions.predicateBasedProvider
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol
import org.jetbrains.kotlin.fir.types.isMarkedNullable
import org.jetbrains.kotlin.name.Name

object FirGrpcServiceDeclarationChecker {
    fun check(
        declaration: FirRegularClass,
        context: CheckerContext,
        reporter: DiagnosticReporter,
    ) {
        if (!context.session.predicateBasedProvider.matches(FirRpcPredicates.grpc, declaration)) {
            return
        }

        val isMetaAnnotated = declaration.classKind == ClassKind.ANNOTATION_CLASS
        if (isMetaAnnotated) {
            return
        }

        val annotation = declaration.getAnnotationByClassId(RpcClassId.grpcAnnotation, context.session)
            ?: error("Unexpected unresolved @Grpc annotation type for declaration: ${declaration.symbol.classId.asSingleFqName()}")

        val protoPackage = annotation.getStringArgument(protoPackageName, context.session).orEmpty()

        if (protoPackage.isNotEmpty()) {
            if (!protoPackage.matches(packageIdentifierRegex)) {
                reporter.reportOn(
                    source = annotation.argumentMapping.mapping[protoPackageName]!!.source,
                    factory = FirGrpcDiagnostics.WRONG_PROTO_PACKAGE_VALUE,
                    context = context,
                )
            }
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

            val grpcMethodAnnotation = function.getAnnotationByClassId(RpcClassId.grpcMethodAnnotation, context.session)

            if (grpcMethodAnnotation != null) {
                val grpcMethodName = grpcMethodAnnotation.getStringArgument(grpcMethodNameName, context.session).orEmpty()

                if (grpcMethodName.isNotEmpty()) {
                    if (!grpcMethodName.matches(identifierRegex)) {
                        reporter.reportOn(
                            source = grpcMethodAnnotation.argumentMapping.mapping[grpcMethodNameName]!!.source,
                            factory = FirGrpcDiagnostics.WRONG_PROTO_METHOD_NAME_VALUE,
                            context = context,
                        )
                    }
                }

                val safe = grpcMethodAnnotation.getBooleanArgument(grpcMethodSafeName, context.session) ?: false
                val idempotent = grpcMethodAnnotation.getBooleanArgument(grpcMethodIdempotentName, context.session) ?: false

                // safe = true, idempotent = true is allowed
                // safe = false, idempotent = false is allowed
                // safe = false, idempotent = true is allowed
                // safe = true, idempotent = false is not allowed
                if (safe && !idempotent) {
                    reporter.reportOn(
                        source = grpcMethodAnnotation.source,
                        factory = FirGrpcDiagnostics.WRONG_SAFE_IDEMPOTENT_COMBINATION,
                        context = context,
                        positioningStrategy = SourceElementPositioningStrategies.VALUE_ARGUMENTS_LIST,
                    )
                }
            }
        }
    }

    private val protoPackageName = Name.identifier("protoPackage")
    private val grpcMethodNameName = Name.identifier("name")
    private val grpcMethodSafeName = Name.identifier("safe")
    private val grpcMethodIdempotentName = Name.identifier("idempotent")
}

object IdentifierRegexes {
    val identifierRegex = Regex("[a-zA-Z_][a-zA-Z0-9_]*")
    val packageIdentifierRegex = Regex("[a-zA-Z_][a-zA-Z0-9_]*(\\.[a-zA-Z_][a-zA-Z0-9_]*)*")
}
