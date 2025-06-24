/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.dokka

import org.jetbrains.dokka.CoreExtensions
import org.jetbrains.dokka.base.DokkaBase
import org.jetbrains.dokka.plugability.DokkaPlugin
import org.jetbrains.dokka.plugability.DokkaPluginApiPreview
import org.jetbrains.dokka.plugability.PluginApiPreviewAcknowledgement

@Suppress("unused")
class RpcDokkaPlugin : DokkaPlugin() {
    @OptIn(DokkaPluginApiPreview::class)
    override fun pluginApiPreviewAcknowledgement() = PluginApiPreviewAcknowledgement

    val rpcInternalApiTransformer by extending {
        plugin<DokkaBase>().preMergeDocumentableTransformer providing ::HideInternalRpcApiTransformer
    }

    val pageTransformer by extending {
        CoreExtensions.pageTransformer providing ::AddDocsLinkPageTransformer
    }
}
