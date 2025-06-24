/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.dokka

import org.jetbrains.dokka.pages.RootPageNode
import org.jetbrains.dokka.plugability.DokkaContext
import org.jetbrains.dokka.transformers.pages.PageTransformer

class AddDocsLinkPageTransformer(private val context: DokkaContext) : PageTransformer {
    override fun invoke(input: RootPageNode): RootPageNode = input
}
