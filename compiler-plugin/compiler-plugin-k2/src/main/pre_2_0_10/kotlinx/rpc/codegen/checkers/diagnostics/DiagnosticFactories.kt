/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("unused")

package kotlinx.rpc.codegen.checkers.diagnostics

import kotlinx.rpc.codegen.StrictMode
import org.jetbrains.kotlin.diagnostics.KtDiagnosticFactory0
import org.jetbrains.kotlin.diagnostics.Severity
import org.jetbrains.kotlin.diagnostics.SourceElementPositioningStrategies
import org.jetbrains.kotlin.utils.DummyDelegate
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

inline fun <reified T> modded0(mode: StrictMode): DiagnosticFactory0DelegateProviderOnNull {
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
