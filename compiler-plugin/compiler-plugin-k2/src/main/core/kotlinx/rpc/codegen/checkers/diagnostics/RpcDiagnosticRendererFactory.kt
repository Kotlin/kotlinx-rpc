/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.checkers.diagnostics

import org.jetbrains.kotlin.diagnostics.rendering.BaseDiagnosticRendererFactory
import org.jetbrains.kotlin.diagnostics.rendering.Renderer
import org.jetbrains.kotlin.fir.analysis.diagnostics.FirDiagnosticRenderers

object RpcDiagnosticRendererFactory : BaseDiagnosticRendererFactory() {
    override val MAP by RpcKtDiagnosticFactoryToRendererMap("Rpc") { map ->
        map.put(
            factory = FirRpcDiagnostics.MISSING_SERIALIZATION_MODULE,
            message = "Missing kotlinx.serialization plugin in the module. " +
                    "Service generation will not be available. " +
                    "Add kotlin(\"plugin.serialization\") to your build.gradle.kts plugins section."
        )

        map.put(
            factory = FirRpcDiagnostics.WRONG_RPC_ANNOTATION_TARGET,
            message = "@{0} annotation is only applicable to interfaces and annotation classes.",
            rendererA = FirDiagnosticRenderers.RENDER_TYPE,
        )

        map.put(
            factory = FirRpcDiagnostics.CHECKED_ANNOTATION_VIOLATION,
            message = "{0} type argument is marked with @{1} annotation, but inferred type is {2}. " +
                    "Provide a type that is marked with @{1} annotation explicitly " +
                    "or remove the annotation from the type argument declaration.",
            rendererA = Renderer { it.indexPositionSpelled() },
            rendererB = FirDiagnosticRenderers.RENDER_TYPE,
            rendererC = FirDiagnosticRenderers.SYMBOL,
        )

        map.put(
            factory = FirRpcDiagnostics.NON_SUSPENDING_REQUEST_WITHOUT_STREAMING_RETURN_TYPE,
            message = "Non suspending request function is not allowed for functions that doesn't return Flow.",
        )

        map.put(
            factory = FirRpcDiagnostics.AD_HOC_POLYMORPHISM_IN_RPC_SERVICE,
            message = "Ad-hoc polymorphism is not allowed in @Rpc services. Found {0} '{1}' functions.",
            rendererA = Renderer { it.toString() },
            rendererB = Renderer { it.asString() },
        )

        map.put(
            factory = FirRpcDiagnostics.TYPE_PARAMETERS_IN_RPC_FUNCTION,
            message = "Type parameters are not allowed in Rpc functions.",
        )

        map.put(
            factory = FirRpcDiagnostics.TYPE_PARAMETERS_IN_RPC_INTERFACE,
            message = "Type parameters are not allowed in @Rpc interfaces.",
        )
    }
}

private fun Int.indexPositionSpelled(): String {
    val padded = this + 1
    val suffix = when (padded % 10) {
        1 -> "st"
        2 -> "nd"
        3 -> "rd"
        else -> "th"
    }

    return "$padded$suffix"
}

object RpcStrictModeDiagnosticRendererFactory : BaseDiagnosticRendererFactory() {
    override val MAP by RpcKtDiagnosticFactoryToRendererMap("RpcStrictMode") { map ->
        map.put(
            factory = FirRpcStrictModeDiagnostics.STATE_FLOW_IN_RPC_SERVICE,
            message = message("StateFlow")
        )

        map.put(
            factory = FirRpcStrictModeDiagnostics.SHARED_FLOW_IN_RPC_SERVICE,
            message = message("SharedFlow"),
        )

        map.put(
            factory = FirRpcStrictModeDiagnostics.NESTED_STREAMING_IN_RPC_SERVICE,
            message = message("Nested streaming"),
        )

        map.put(
            factory = FirRpcStrictModeDiagnostics.SUSPENDING_SERVER_STREAMING_IN_RPC_SERVICE,
            message = message("Suspend function declaration with server streaming"),
        )

        map.put(
            factory = FirRpcStrictModeDiagnostics.NON_TOP_LEVEL_SERVER_STREAMING_IN_RPC_SERVICE,
            message = message("Not top-level server-side streaming"),
        )

        map.put(
            factory = FirRpcStrictModeDiagnostics.FIELD_IN_RPC_SERVICE,
            message = message("Field declaration"),
        )
    }

    private fun message(entityName: String): String {
        return "$entityName is prohibited in @Rpc services in strict mode. " +
                "Support will be removed completely in the 0.8.0 release."
    }
}
