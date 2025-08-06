/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.checkers.diagnostics

//##csm RpcKtDiagnosticsContainer.kt-imports
//##csm specific=[2.0.0...2.2.10]
import org.jetbrains.kotlin.diagnostics.rendering.RootDiagnosticRendererFactory
//##csm /specific
//##csm /RpcKtDiagnosticsContainer.kt-imports

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

// Automatically done for later versions
fun registerDiagnosticRendererFactories() {
//##csm registerDiagnosticRendererFactories
//##csm specific=[2.0.0...2.2.10]
    RootDiagnosticRendererFactory.registerFactory(FirRpcDiagnostics.getRendererFactoryVs())
    RootDiagnosticRendererFactory.registerFactory(FirRpcStrictModeDiagnostics.getRendererFactoryVs())
//##csm /specific
//##csm /registerDiagnosticRendererFactories
}
