/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.checkers

import kotlinx.rpc.codegen.FirCheckersContext
import kotlinx.rpc.codegen.FirRpcPredicates
import kotlinx.rpc.codegen.checkers.diagnostics.FirRpcDiagnostics
import kotlinx.rpc.codegen.isRemoteService
import kotlinx.rpc.codegen.remoteServiceSupertypeSource
import kotlinx.rpc.codegen.rpcAnnotation
import kotlinx.rpc.codegen.rpcAnnotationSource
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirRegularClassChecker
import org.jetbrains.kotlin.fir.declarations.FirRegularClass
import org.jetbrains.kotlin.fir.declarations.utils.isInterface
import org.jetbrains.kotlin.fir.extensions.predicateBasedProvider
import org.jetbrains.kotlin.fir.types.resolvedType

class FirRpcAnnotationChecker(private val ctx: FirCheckersContext) : FirRegularClassChecker(MppCheckerKind.Common) {
    override fun check(
        declaration: FirRegularClass,
        context: CheckerContext,
        reporter: DiagnosticReporter,
    ) {
        val rpcAnnotated = context.session.predicateBasedProvider.matches(FirRpcPredicates.rpc, declaration)
        val rpcMetaAnnotated = context.session.predicateBasedProvider.matches(FirRpcPredicates.rpcMeta, declaration)
        val grpcAnnotated = context.session.predicateBasedProvider.matches(FirRpcPredicates.grpc, declaration)

        val isMetaAnnotated = declaration.classKind != ClassKind.ANNOTATION_CLASS

        if (!declaration.isInterface && isMetaAnnotated && rpcMetaAnnotated) {
            reporter.reportOn(
                source = declaration.symbol.rpcAnnotationSource(context.session, FirRpcPredicates.rpcMeta),
                factory = FirRpcDiagnostics.WRONG_RPC_ANNOTATION_TARGET,
                context = context,
                a = declaration.symbol.rpcAnnotation(context.session, FirRpcPredicates.rpc)?.resolvedType
                    ?: error("Unexpected unresolved annotation type for declaration: ${declaration.symbol.classId.asSingleFqName()}"),
            )
        }

        if (declaration.symbol.isRemoteService(context.session) && !rpcAnnotated) {
            reporter.reportOn(
                source = declaration.symbol.remoteServiceSupertypeSource(context.session),
                factory = FirRpcDiagnostics.MISSING_RPC_ANNOTATION,
                context = context,
            )
        }

        if ((rpcAnnotated || grpcAnnotated) && !ctx.serializationIsPresent && isMetaAnnotated) {
            reporter.reportOn(
                source = declaration.symbol.rpcAnnotationSource(context.session, FirRpcPredicates.rpcMeta),
                factory = FirRpcDiagnostics.MISSING_SERIALIZATION_MODULE,
                context = context,
            )
        }
    }
}
