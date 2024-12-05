/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.checkers.diagnostics

import kotlinx.rpc.codegen.StrictMode
import kotlinx.rpc.codegen.StrictModeAggregator
import org.jetbrains.kotlin.diagnostics.KtDiagnosticFactoryToRendererMap
import org.jetbrains.kotlin.diagnostics.rendering.BaseDiagnosticRendererFactory
import org.jetbrains.kotlin.diagnostics.rendering.Renderer
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

        put(
            factory = FirRpcDiagnostics.NON_SUSPENDING_REQUEST_WITHOUT_STREAMING_RETURN_TYPE,
            message = "Non suspending request function is not allowed for functions that doesn't return Flow.",
        )

        put(
            factory = FirRpcDiagnostics.AD_HOC_POLYMORPHISM_IN_RPC_SERVICE,
            message = "Ad-hoc polymorphism is not allowed in @Rpc services. Found {0} '{1}' functions.",
            rendererA = Renderer { it.toString() },
            rendererB = Renderer { it.asString() },
        )

        put(
            factory = FirRpcDiagnostics.TYPE_PARAMETERS_IN_RPC_FUNCTION,
            message = "Type parameters are not allowed in Rpc functions.",
        )

        put(
            factory = FirRpcDiagnostics.TYPE_PARAMETERS_IN_RPC_INTERFACE,
            message = "Type parameters are not allowed in @Rpc interfaces.",
        )
    }
}

class RpcStrictModeDiagnosticRendererFactory(
    private val diagnostics: FirRpcStrictModeDiagnostics,
) : BaseDiagnosticRendererFactory() {
    override val MAP = KtDiagnosticFactoryToRendererMap("Rpc").apply {
        diagnostics.STATE_FLOW_IN_RPC_SERVICE?.let {
            put(
                factory = it,
                message = message("StateFlow") { stateFlow },
            )
        }

        diagnostics.SHARED_FLOW_IN_RPC_SERVICE?.let {
            put(
                factory = it,
                message = message("SharedFlow") { sharedFlow },
            )
        }

        diagnostics.NESTED_STREAMING_IN_RPC_SERVICE?.let {
            put(
                factory = it,
                message = message("Nested streaming") { nestedFlow },
            )
        }

        diagnostics.STREAM_SCOPE_FUNCTION_IN_RPC?.let {
            put(
                factory = it,
                message = message("Stream scope usage") { streamScopedFunctions },
            )
        }

        diagnostics.SUSPENDING_SERVER_STREAMING_IN_RPC_SERVICE?.let {
            put(
                factory = it,
                message = message("Suspend function declaration with server streaming") { suspendingServerStreaming },
            )
        }

        diagnostics.NON_TOP_LEVEL_SERVER_STREAMING_IN_RPC_SERVICE?.let {
            put(
                factory = it,
                message = message("Not top-level server-side streaming") { sharedFlow },
            )
        }

        diagnostics.FIELD_IN_RPC_SERVICE?.let {
            put(
                factory = it,
                message = message("Field declaration") { fields },
            )
        }
    }

    private fun message(entityName: String, selector: StrictModeAggregator.() -> StrictMode): String {
        val actionWord = when (diagnostics.modes.selector()) {
            StrictMode.NONE -> ""
            StrictMode.WARNING -> "deprecated"
            StrictMode.ERROR -> "prohibited"
        }

        return "$entityName is $actionWord in @Rpc services in strict mode."
    }
}
