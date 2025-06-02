/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("StructuralWrap")

package kotlinx.rpc.codegen.checkers.diagnostics

import kotlinx.rpc.codegen.StrictModeAggregator
import org.jetbrains.kotlin.diagnostics.SourceElementPositioningStrategies
import org.jetbrains.kotlin.diagnostics.error0
import org.jetbrains.kotlin.diagnostics.error1
import org.jetbrains.kotlin.diagnostics.error2
import org.jetbrains.kotlin.diagnostics.error3
import org.jetbrains.kotlin.diagnostics.rendering.BaseDiagnosticRendererFactory
import org.jetbrains.kotlin.fir.symbols.FirBasedSymbol
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtAnnotated
import org.jetbrains.kotlin.psi.KtElement

// ###########################################################################
// ###                     BIG WARNING, LISTEN CLOSELY!                    ###
// # Do NOT use `PsiElement` for `error0` or any other function              #
// # Instead use KtElement, otherwise problems in IDE and in tests may arise #
// ###########################################################################

object FirRpcDiagnostics : RpcKtDiagnosticsContainer() {
    val MISSING_RPC_ANNOTATION by error0<KtAnnotated>()
    val MISSING_SERIALIZATION_MODULE by error0<KtAnnotated>()
    val WRONG_RPC_ANNOTATION_TARGET by error1<KtAnnotated, ConeKotlinType>()
    val CHECKED_ANNOTATION_VIOLATION by error3<KtAnnotated, Int, ConeKotlinType, FirBasedSymbol<*>>()
    val NON_SUSPENDING_REQUEST_WITHOUT_STREAMING_RETURN_TYPE by error0<KtElement>()
    val AD_HOC_POLYMORPHISM_IN_RPC_SERVICE by error2<KtElement, Int, Name>()
    val TYPE_PARAMETERS_IN_RPC_FUNCTION by error0<KtElement>(SourceElementPositioningStrategies.TYPE_PARAMETERS_LIST)
    val TYPE_PARAMETERS_IN_RPC_INTERFACE by error0<KtElement>(SourceElementPositioningStrategies.TYPE_PARAMETERS_LIST)

    override fun getRendererFactoryVs(): BaseDiagnosticRendererFactory {
        return RpcDiagnosticRendererFactory
    }
}

@Suppress("PropertyName", "detekt.VariableNaming")
class FirRpcStrictModeDiagnostics(val modes: StrictModeAggregator) : RpcKtDiagnosticsContainer() {
    val STATE_FLOW_IN_RPC_SERVICE by modded0<KtElement>(modes.stateFlow)
    val SHARED_FLOW_IN_RPC_SERVICE by modded0<KtElement>(modes.sharedFlow)
    val NESTED_STREAMING_IN_RPC_SERVICE by modded0<KtElement>(modes.nestedFlow)
    val STREAM_SCOPE_FUNCTION_IN_RPC by modded0<KtElement>(modes.streamScopedFunctions)
    val SUSPENDING_SERVER_STREAMING_IN_RPC_SERVICE by modded0<KtElement>(modes.suspendingServerStreaming)
    val NON_TOP_LEVEL_SERVER_STREAMING_IN_RPC_SERVICE by modded0<KtElement>(modes.notTopLevelServerFlow)
    val FIELD_IN_RPC_SERVICE by modded0<KtElement>(modes.fields)

    override fun getRendererFactoryVs(): BaseDiagnosticRendererFactory {
        return RpcStrictModeDiagnosticRendererFactory(this)
    }
}
