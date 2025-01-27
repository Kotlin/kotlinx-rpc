/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.checkers.diagnostics

import kotlinx.rpc.codegen.StrictModeAggregator
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.diagnostics.SourceElementPositioningStrategies
import org.jetbrains.kotlin.diagnostics.error0
import org.jetbrains.kotlin.diagnostics.error1
import org.jetbrains.kotlin.diagnostics.error2
import org.jetbrains.kotlin.diagnostics.rendering.RootDiagnosticRendererFactory
import org.jetbrains.kotlin.diagnostics.warning0
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtAnnotationEntry

object FirRpcDiagnostics {
    val MISSING_RPC_ANNOTATION by error0<KtAnnotationEntry>()
    val MISSING_SERIALIZATION_MODULE by error0<KtAnnotationEntry>()
    val WRONG_RPC_ANNOTATION_TARGET by error1<KtAnnotationEntry, ConeKotlinType>()
    val CHECKED_ANNOTATION_VIOLATION by error1<KtAnnotationEntry, ConeKotlinType>()
    val NON_SUSPENDING_REQUEST_WITHOUT_STREAMING_RETURN_TYPE by error0<PsiElement>()
    val AD_HOC_POLYMORPHISM_IN_RPC_SERVICE by error2<PsiElement, Int, Name>()
    val TYPE_PARAMETERS_IN_RPC_FUNCTION by error0<PsiElement>(SourceElementPositioningStrategies.TYPE_PARAMETERS_LIST)
    val TYPE_PARAMETERS_IN_RPC_INTERFACE by error0<PsiElement>(SourceElementPositioningStrategies.TYPE_PARAMETERS_LIST)

    init {
        RootDiagnosticRendererFactory.registerFactory(RpcDiagnosticRendererFactory)
    }
}

@Suppress("PropertyName", "detekt.VariableNaming")
class FirRpcStrictModeDiagnostics(val modes: StrictModeAggregator) {
    val STATE_FLOW_IN_RPC_SERVICE by modded0<PsiElement>(modes.stateFlow)
    val SHARED_FLOW_IN_RPC_SERVICE by modded0<PsiElement>(modes.sharedFlow)
    val NESTED_STREAMING_IN_RPC_SERVICE by modded0<PsiElement>(modes.nestedFlow)
    val STREAM_SCOPE_FUNCTION_IN_RPC by modded0<PsiElement>(modes.streamScopedFunctions)
    val SUSPENDING_SERVER_STREAMING_IN_RPC_SERVICE by modded0<PsiElement>(modes.suspendingServerStreaming)
    val NON_TOP_LEVEL_SERVER_STREAMING_IN_RPC_SERVICE by modded0<PsiElement>(modes.notTopLevelServerFlow)
    val FIELD_IN_RPC_SERVICE by modded0<PsiElement>(modes.fields)

    init {
        RootDiagnosticRendererFactory.registerFactory(RpcStrictModeDiagnosticRendererFactory(this))
    }
}
