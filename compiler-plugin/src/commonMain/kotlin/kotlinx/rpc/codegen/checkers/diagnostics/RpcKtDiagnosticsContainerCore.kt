/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.checkers.diagnostics

import org.jetbrains.kotlin.diagnostics.rendering.BaseDiagnosticRendererFactory

interface RpcKtDiagnosticsContainerCore {
    fun getRendererFactoryVs() : BaseDiagnosticRendererFactory
}
