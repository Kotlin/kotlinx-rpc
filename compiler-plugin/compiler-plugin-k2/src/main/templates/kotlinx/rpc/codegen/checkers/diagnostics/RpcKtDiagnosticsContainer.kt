/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.checkers.diagnostics

//##csm RpcKtDiagnosticsContainer
//##csm specific=[2.0.0...2.2.10]
abstract class RpcKtDiagnosticsContainer : RpcKtDiagnosticsContainerCore
//##csm /specific
//##csm default
import org.jetbrains.kotlin.diagnostics.KtDiagnosticsContainer
import org.jetbrains.kotlin.diagnostics.rendering.BaseDiagnosticRendererFactory

abstract class RpcKtDiagnosticsContainer : KtDiagnosticsContainer(), RpcKtDiagnosticsContainerCore {
    override fun getRendererFactory(): BaseDiagnosticRendererFactory {
        return getRendererFactoryVs()
    }
}
//##csm /default
//##csm /RpcKtDiagnosticsContainer
