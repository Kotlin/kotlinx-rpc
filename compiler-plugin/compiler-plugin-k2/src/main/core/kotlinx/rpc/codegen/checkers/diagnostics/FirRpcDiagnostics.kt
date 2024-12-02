/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.checkers.diagnostics

import kotlinx.rpc.codegen.StrictMode
import kotlinx.rpc.codegen.StrictModeAggregator
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.diagnostics.KtDiagnosticFactory0
import org.jetbrains.kotlin.diagnostics.Severity
import org.jetbrains.kotlin.diagnostics.SourceElementPositioningStrategies
import org.jetbrains.kotlin.diagnostics.error0
import org.jetbrains.kotlin.diagnostics.error1
import org.jetbrains.kotlin.diagnostics.rendering.RootDiagnosticRendererFactory
import org.jetbrains.kotlin.diagnostics.warning0
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.utils.DummyDelegate
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

object FirRpcDiagnostics {
    val MISSING_RPC_ANNOTATION by error0<KtAnnotationEntry>()
    val MISSING_SERIALIZATION_MODULE by warning0<KtAnnotationEntry>()
    val WRONG_RPC_ANNOTATION_TARGET by error0<KtAnnotationEntry>()
    val CHECKED_ANNOTATION_VIOLATION by error1<KtAnnotationEntry, ConeKotlinType>()

    init {
        RootDiagnosticRendererFactory.registerFactory(RpcDiagnosticRendererFactory)
    }
}

@Suppress("PropertyName", "detekt.VariableNaming")
class FirRpcStrictModeDiagnostics(val modes: StrictModeAggregator) {
    val STATE_FLOW_IN_RPC_SERVICE by modded0<PsiElement>(modes.stateFlow)
    val SHARED_FLOW_IN_RPC_SERVICE by modded0<PsiElement>(modes.sharedFlow)
    val NESTED_STREAMING_IN_RPC_SERVICE by modded0<PsiElement>(modes.nestedFlow)
    val STREAM_SCOPE_ENTITY_IN_RPC by modded0<PsiElement>(modes.streamScopedFunctions)
    val SUSPENDING_SERVER_STREAMING_IN_RPC_SERVICE by modded0<PsiElement>(modes.suspendingServerStreaming)
    val NON_TOP_LEVEL_SERVER_STREAMING_IN_RPC_SERVICE by modded0<PsiElement>(modes.notTopLevelServerFlow)
    val FIELD_IN_RPC_SERVICE by modded0<PsiElement>(modes.fields)

    init {
        RootDiagnosticRendererFactory.registerFactory(RpcStrictModeDiagnosticRendererFactory(this))
    }
}

private inline fun <reified T> modded0(mode: StrictMode): DiagnosticFactory0DelegateProviderOnNull {
    return DiagnosticFactory0DelegateProviderOnNull(mode, T::class)
}

class DiagnosticFactory0DelegateProviderOnNull(
    private val mode: StrictMode,
    private val psiType: KClass<*>,
) {
    operator fun provideDelegate(
        @Suppress("unused")
        thisRef: Any?,
        prop: KProperty<*>,
    ): ReadOnlyProperty<Any?, KtDiagnosticFactory0?> {
        val severity = when (mode) {
            StrictMode.ERROR -> Severity.ERROR
            StrictMode.WARNING -> Severity.WARNING
            StrictMode.NONE -> null
        } ?: return DummyDelegate(null)

        return DummyDelegate(
            KtDiagnosticFactory0(
                name = prop.name,
                severity = severity,
                defaultPositioningStrategy = SourceElementPositioningStrategies.DEFAULT,
                psiType = psiType,
            ),
        )
    }
}
