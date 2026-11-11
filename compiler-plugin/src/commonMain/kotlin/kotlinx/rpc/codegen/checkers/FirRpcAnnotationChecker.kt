/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.checkers

import kotlinx.rpc.codegen.FirRpcPredicates
import kotlinx.rpc.codegen.checkers.diagnostics.FirRpcDiagnostics
import kotlinx.rpc.codegen.common.RpcClassId
import kotlinx.rpc.codegen.rpcAnnotation
import kotlinx.rpc.codegen.rpcAnnotationSource
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.declarations.FirRegularClass
import org.jetbrains.kotlin.fir.declarations.utils.isInterface
import org.jetbrains.kotlin.fir.extensions.predicateBasedProvider
import org.jetbrains.kotlin.fir.types.resolvedType

object FirRpcAnnotationChecker {
    fun check(
        declaration: FirRegularClass,
        context: CheckerContext,
        reporter: DiagnosticReporter,
    ) {
        val rpcMetaAnnotated = context.session.predicateBasedProvider.matches(FirRpcPredicates.rpc, declaration)

        val isMetaAnnotated = declaration.classKind != ClassKind.ANNOTATION_CLASS

        if (!declaration.isInterface && isMetaAnnotated && rpcMetaAnnotated) {
            reporter.reportOn(
                source = declaration.symbol.rpcAnnotationSource(
                    session = context.session,
                    predicate = FirRpcPredicates.rpc,
                    classId = RpcClassId.rpcAnnotation,
                ),
                factory = FirRpcDiagnostics.WRONG_RPC_ANNOTATION_TARGET,
                context = context,
                a = declaration.symbol.rpcAnnotation(
                    session = context.session,
                    predicate = FirRpcPredicates.rpc,
                    classId = RpcClassId.rpcAnnotation,
                )?.resolvedType
                    ?: error("Unexpected unresolved annotation type for declaration: ${declaration.symbol.classId.asSingleFqName()}"),
            )
        }
    }
}
