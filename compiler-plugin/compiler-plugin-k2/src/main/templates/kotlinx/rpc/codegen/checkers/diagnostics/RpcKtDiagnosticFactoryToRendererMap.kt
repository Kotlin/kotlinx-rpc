/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.checkers.diagnostics

import org.jetbrains.kotlin.diagnostics.KtDiagnosticFactoryToRendererMap

fun RpcKtDiagnosticFactoryToRendererMap(
    name: String,
    init: (KtDiagnosticFactoryToRendererMap) -> Unit,
): Lazy<KtDiagnosticFactoryToRendererMap> {
    //##csm RpcKtDiagnosticFactoryToRendererMap
    //##csm specific=[2.0.0...2.2.10]
    return lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        KtDiagnosticFactoryToRendererMap(name).also(init)
    }
    //##csm /specific
    //##csm default
    return KtDiagnosticFactoryToRendererMap(name, init)
    //##csm /default
    //##csm /RpcKtDiagnosticFactoryToRendererMap
}
