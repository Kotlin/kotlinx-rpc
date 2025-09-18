/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.checkers.diagnostics

import kotlinx.rpc.codegen.checkers.IdentifierRegexes
import org.jetbrains.kotlin.diagnostics.rendering.BaseDiagnosticRendererFactory
import org.jetbrains.kotlin.diagnostics.rendering.Renderer
import org.jetbrains.kotlin.fir.analysis.diagnostics.FirDiagnosticRenderers

object RpcDiagnosticRendererFactory : BaseDiagnosticRendererFactory() {
    override val MAP by RpcKtDiagnosticFactoryToRendererMap("Rpc") { map ->
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
        return "$entityName is prohibited in @Rpc services. "
    }
}

object GrpcDiagnosticRendererFactory : BaseDiagnosticRendererFactory() {
    override val MAP by RpcKtDiagnosticFactoryToRendererMap("Grpc") { map ->
        map.put(
            factory = FirGrpcDiagnostics.WRONG_PROTO_PACKAGE_VALUE,
            message = "'protoPackage' parameter value must be a valid package name (${IdentifierRegexes.packageIdentifierRegex.pattern}) or empty",
        )

        map.put(
            factory = FirGrpcDiagnostics.WRONG_PROTO_METHOD_NAME_VALUE,
            message = "'name' parameter value must be a valid identifier (${IdentifierRegexes.identifierRegex.pattern}) or empty",
        )

        map.put(
            factory = FirGrpcDiagnostics.WRONG_SAFE_IDEMPOTENT_COMBINATION,
            message = "'safe = true' and 'idempotent = false' are mutually exclusive.",
        )

        map.put(
            factory = FirGrpcDiagnostics.NULLABLE_PARAMETER_IN_GRPC_SERVICE,
            message = "Nullable type is not allowed in @Grpc service function parameters.",
        )

        map.put(
            factory = FirGrpcDiagnostics.NULLABLE_RETURN_TYPE_IN_GRPC_SERVICE,
            message = "Nullable type is not allowed in @Grpc service function return types.",
        )

        map.put(
            factory = FirGrpcDiagnostics.NON_TOP_LEVEL_CLIENT_STREAMING_IN_RPC_SERVICE,
            message = "Non top-level client-side streaming is not allowed in @Grpc services.",
        )

        map.put(
            factory = FirGrpcDiagnostics.MULTIPLE_PARAMETERS_IN_GRPC_SERVICE,
            message = "Multiple parameters are not allowed in @Grpc service functions.",
        )

        map.put(
            factory = FirGrpcDiagnostics.NOT_AN_OBJECT_REFERENCE_IN_WITH_CODEC_ANNOTATION,
            message = "'codec' parameter must reference an object, not a class or an interface.",
        )

        map.put(
            factory = FirGrpcDiagnostics.CODEC_TYPE_MISMATCH,
            message = "Codec type mismatch. Expected {0}, got {1}.",
            rendererA = FirDiagnosticRenderers.RENDER_TYPE,
            rendererB = FirDiagnosticRenderers.RENDER_TYPE,
        )
    }
}
