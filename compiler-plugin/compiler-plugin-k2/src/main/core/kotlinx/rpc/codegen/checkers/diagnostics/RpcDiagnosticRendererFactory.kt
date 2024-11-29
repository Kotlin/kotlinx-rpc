/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.checkers.diagnostics

import org.jetbrains.kotlin.diagnostics.KtDiagnosticFactoryToRendererMap
import org.jetbrains.kotlin.diagnostics.rendering.BaseDiagnosticRendererFactory
import org.jetbrains.kotlin.fir.analysis.diagnostics.FirDiagnosticRenderers

object RpcDiagnosticRendererFactory : BaseDiagnosticRendererFactory() {
    override val MAP = KtDiagnosticFactoryToRendererMap("Rpc").apply {
        put(
            factory = FirRpcDiagnostics.MISSING_RPC_ANNOTATION,
            message = "Missing @Rpc annotation. " +
                    "All services children of kotlinx.rpc.RemoteService " +
                    "must be annotated with kotlinx.rpc.annotations.Rpc",
        )

        put(
            factory = FirRpcDiagnostics.MISSING_SERIALIZATION_MODULE,
            message = "Missing kotlinx.serialization plugin in the module. " +
                    "Service generation will not be available. " +
                    "Add kotlin(\"plugin.serialization\") to your build.gradle.kts plugins section."
        )

        put(
            factory = FirRpcDiagnostics.WRONG_RPC_ANNOTATION_TARGET,
            message = "@Rpc annotation is only applicable to interfaces.",
        )

        put(
            factory = FirRpcDiagnostics.CHECKED_ANNOTATION_VIOLATION,
            message = "Type argument marked with {0} annotation " +
                    "must be annotated with {0} or an annotation annotated with {0}.",
            rendererA = FirDiagnosticRenderers.RENDER_TYPE,
        )
    }
}
