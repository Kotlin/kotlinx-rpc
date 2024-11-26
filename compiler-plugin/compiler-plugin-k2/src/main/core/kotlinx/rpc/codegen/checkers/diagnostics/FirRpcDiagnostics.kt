/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.checkers.diagnostics

import org.jetbrains.kotlin.diagnostics.error0
import org.jetbrains.kotlin.diagnostics.error1
import org.jetbrains.kotlin.diagnostics.rendering.RootDiagnosticRendererFactory
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.psi.KtAnnotationEntry

object FirRpcDiagnostics {
    val MISSING_RPC_ANNOTATION by error0<KtAnnotationEntry>()
    val WRONG_RPC_ANNOTATION_TARGET by error0<KtAnnotationEntry>()
    val CHECKED_ANNOTATION_VIOLATION by error1<KtAnnotationEntry, ConeKotlinType>()

    init {
        RootDiagnosticRendererFactory.registerFactory(RpcDiagnosticRendererFactory)
    }
}
