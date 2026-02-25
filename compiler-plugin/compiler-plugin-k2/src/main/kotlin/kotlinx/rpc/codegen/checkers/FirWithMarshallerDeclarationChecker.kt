/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.checkers

import kotlinx.rpc.codegen.FirRpcPredicates
import kotlinx.rpc.codegen.checkers.diagnostics.FirGrpcDiagnostics
import kotlinx.rpc.codegen.common.RpcClassId
import kotlinx.rpc.codegen.vsApi
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.declarations.FirRegularClass
import org.jetbrains.kotlin.fir.declarations.findArgumentByName
import org.jetbrains.kotlin.fir.declarations.getAnnotationByClassId
import org.jetbrains.kotlin.fir.extensions.predicateBasedProvider
import org.jetbrains.kotlin.fir.resolve.defaultType
import org.jetbrains.kotlin.fir.resolve.getSuperTypes
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.fir.types.classId
import org.jetbrains.kotlin.fir.types.type
import org.jetbrains.kotlin.name.Name

object FirWithMarshallerDeclarationChecker {
    fun check(
        declaration: FirRegularClass,
        context: CheckerContext,
        reporter: DiagnosticReporter,
    ) {
        if (!context.session.predicateBasedProvider.matches(FirRpcPredicates.withMarshaller, declaration)) {
            return
        }

        val withMarshaller = declaration.getAnnotationByClassId(RpcClassId.withMarshallerAnnotation, context.session)
            ?: error(
                "Unexpected unresolved @WithMarshaller annotation type " +
                        "for declaration: ${declaration.symbol.classId.asSingleFqName()}"
            )

        val kClassValue = vsApi {
            withMarshaller.getKClassArgumentVS(MARSHALLER_ARGUMENT_NAME, context.session)
        } ?: error(
            "Unexpected unresolved 'marshaller' argument for @WithMarshaller annotation " +
                    "for declaration: ${declaration.symbol.classId.asSingleFqName()}"
        )

        val marshallerClassSymbol = vsApi { kClassValue.toClassSymbolVS(context.session) }
            ?: error(
                "'marshaller' argument type for @WithMarshaller annotation is not a class " +
                        "for declaration: ${declaration.symbol.classId.asSingleFqName()}"
            )

        val marshallerTargetClass = marshallerClassSymbol.resolveMessageMarshallerTypeArgument(context.session)

        if (marshallerTargetClass.classId != declaration.symbol.classId) {
            reporter.reportOn(
                source = withMarshaller.findArgumentByName(MARSHALLER_ARGUMENT_NAME)?.source,
                factory = FirGrpcDiagnostics.MARSHALLER_TYPE_MISMATCH,
                a = declaration.symbol.defaultType(),
                b = marshallerTargetClass,
                context = context,
            )
        }

        if (marshallerClassSymbol.classKind != ClassKind.OBJECT) {
            reporter.reportOn(
                source = withMarshaller.findArgumentByName(MARSHALLER_ARGUMENT_NAME)?.source,
                factory = FirGrpcDiagnostics.NOT_AN_OBJECT_REFERENCE_IN_WITH_MARSHALLER_ANNOTATION,
                context = context,
            )
        }
    }

    private fun FirClassSymbol<*>.resolveMessageMarshallerTypeArgument(session: FirSession): ConeKotlinType = vsApi {
        val superTypes = getSuperTypes(session, recursive = true, lookupInterfaces = true, substituteSuperTypes = true)

        return superTypes
            .find { it.classId == RpcClassId.messageMarshaller }
            ?.typeArguments?.single()?.type
            ?: error("'MessageMarshaller' supertype not found for $classId")
    }

    private val MARSHALLER_ARGUMENT_NAME = Name.identifier("marshaller")
}
