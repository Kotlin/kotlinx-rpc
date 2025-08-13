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
import org.jetbrains.kotlin.fir.declarations.getKClassArgument
import org.jetbrains.kotlin.fir.extensions.predicateBasedProvider
import org.jetbrains.kotlin.fir.resolve.defaultType
import org.jetbrains.kotlin.fir.resolve.getSuperTypes
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.fir.types.classId
import org.jetbrains.kotlin.fir.types.type
import org.jetbrains.kotlin.name.Name

object FirWithCodecDeclarationChecker {
    fun check(
        declaration: FirRegularClass,
        context: CheckerContext,
        reporter: DiagnosticReporter,
    ) {
        if (!context.session.predicateBasedProvider.matches(FirRpcPredicates.withCodec, declaration)) {
            return
        }

        val withCodec = declaration.getAnnotationByClassId(RpcClassId.withCodecAnnotation, context.session)
            ?: error(
                "Unexpected unresolved @WithCodec annotation type " +
                        "for declaration: ${declaration.symbol.classId.asSingleFqName()}"
            )

        val kClassValue = withCodec.getKClassArgument(CODEC_ARGUMENT_NAME, context.session)
            ?: error(
                "Unexpected unresolved 'codec' argument for @WithCodec annotation " +
                        "for declaration: ${declaration.symbol.classId.asSingleFqName()}"
            )

        val codecClassSymbol = vsApi { kClassValue.toClassSymbolVS(context.session) }
            ?: error(
                "'codec' argument type for @WithCodec annotation is not a class " +
                        "for declaration: ${declaration.symbol.classId.asSingleFqName()}"
            )

        val codecTargetClass = codecClassSymbol.resolveMessageCodecTypeArgument(context.session)

        if (codecTargetClass.classId != declaration.symbol.classId) {
            reporter.reportOn(
                source = withCodec.findArgumentByName(CODEC_ARGUMENT_NAME)?.source,
                factory = FirGrpcDiagnostics.CODEC_TYPE_MISMATCH,
                a = declaration.symbol.defaultType(),
                b = codecTargetClass,
                context = context,
            )
        }

        if (codecClassSymbol.classKind != ClassKind.OBJECT) {
            reporter.reportOn(
                source = withCodec.findArgumentByName(CODEC_ARGUMENT_NAME)?.source,
                factory = FirGrpcDiagnostics.NOT_AN_OBJECT_REFERENCE_IN_WITH_CODEC_ANNOTATION,
                context = context,
            )
        }
    }

    private fun FirClassSymbol<*>.resolveMessageCodecTypeArgument(session: FirSession): ConeKotlinType = vsApi {
        val superTypes = getSuperTypes(session, recursive = true, lookupInterfaces = true, substituteSuperTypes = true)

        return superTypes
            .find { it.classId == RpcClassId.messageCodec }
            ?.typeArguments?.single()?.type
            ?: error("'MessageCodec' supertype not found for $classId")
    }

    private val CODEC_ARGUMENT_NAME = Name.identifier("codec")
}
