/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.dokka

import org.jetbrains.dokka.model.DisplaySourceSet
import org.jetbrains.dokka.model.properties.PropertyContainer
import org.jetbrains.dokka.pages.*
import org.jetbrains.dokka.plugability.DokkaContext
import org.jetbrains.dokka.transformers.pages.PageTransformer

private object RpcDevNotice : Style

/**
 * Adds a version subtitle below the h1 title of pages belonging to modules
 * that have a different version than the core library.
 *
 * grpc/protobuf modules and buf/protoc packages get the grpc-dev version.
 */
class ModuleVersionBadgeTransformer(private val context: DokkaContext) : PageTransformer {

    private val config: RpcPluginConfiguration by lazy {
        RpcPluginConfiguration.load()
    }

    private companion object {
        val GRPC_DEV_PACKAGES = setOf(
            "kotlinx.rpc.grpc",
            "kotlinx.rpc.grpc.annotations",
            "kotlinx.rpc.grpc.descriptor",
            "kotlinx.rpc.protobuf",
            "kotlinx.rpc.buf",
            "kotlinx.rpc.buf.tasks",
            "kotlinx.rpc.protoc",
        )
    }

    override fun invoke(input: RootPageNode): RootPageNode {
        val grpcDevVersion = config.grpcDevVersion
        if (grpcDevVersion.isBlank()) return input

        return input.transformContentPagesTree { page ->
            val version = resolveVersion(page)
            if (version != null) {
                page.modified(
                    content = injectVersionSubtitle(page.content, version),
                )
            } else {
                page
            }
        }
    }

    private fun isGrpcDevModule(name: String): Boolean {
        return name.contains("grpc") || name.contains("protobuf")
    }

    private fun isGrpcDevPackage(packageName: String): Boolean {
        return GRPC_DEV_PACKAGES.any { packageName == it || packageName.startsWith("$it.") }
    }

    private fun resolveVersion(page: ContentPage): String? {
        val grpcDevVersion = config.grpcDevVersion

        when (page) {
            is ModulePageNode -> {
                if (isGrpcDevModule(page.name)) return grpcDevVersion
            }

            is PackagePageNode -> {
                if (isGrpcDevPackage(page.name)) return grpcDevVersion
            }

            else -> {
                val packages = page.dri.mapNotNull { it.packageName }.toSet()
                if (packages.any { isGrpcDevPackage(it) }) return grpcDevVersion
            }
        }

        return null
    }

    /**
     * Finds the ContentGroup that contains the h1 header and inserts
     * a version subtitle (h4) right after the h1.
     */
    private fun injectVersionSubtitle(node: ContentNode, version: String): ContentNode {
        if (node !is ContentGroup) return node

        val h1Index = node.children.indexOfFirst { it is ContentHeader && it.level == 1 }
        if (h1Index != -1) {
            val h1 = node.children[h1Index] as ContentHeader
            val subtitle = createVersionSubtitle(version, h1.dci, h1.sourceSets)
            val newChildren = node.children.toMutableList()
            newChildren.add(h1Index + 1, subtitle)
            return node.copy(children = newChildren)
        }

        // Recurse into child groups to find the one containing the h1
        var modified = false
        val newChildren = node.children.map { child ->
            if (!modified) {
                val result = injectVersionSubtitle(child, version)
                if (result !== child) {
                    modified = true
                    result
                } else {
                    child
                }
            } else {
                child
            }
        }
        return if (modified) node.copy(children = newChildren) else node
    }

    private val configurationLink = "https://kotlin.github.io/kotlinx-rpc/grpc-configuration.html"

    private fun createVersionSubtitle(
        version: String,
        dci: DCI,
        sourceSets: Set<DisplaySourceSet>,
    ): ContentGroup {
        val safeVersion = version.replace('.', '\u2024')
        val empty = PropertyContainer.empty<ContentNode>()

        fun text(value: String) = ContentText(
            text = value, dci = dci, sourceSets = sourceSets, style = emptySet(), extra = empty,
        )

        fun group(vararg children: ContentNode) = ContentGroup(
            children = children.toList(), dci = dci, sourceSets = sourceSets, style = emptySet(), extra = empty,
        )

        val versionLine = ContentHeader(
            children = listOf(group(text("Available in a dev version: $safeVersion"))),
            level = 4,
            dci = dci,
            sourceSets = sourceSets,
            style = emptySet(),
            extra = empty,
        )

        val link = ContentResolvedLink(
            children = listOf(text("How to configure")),
            address = configurationLink,
            dci = dci,
            sourceSets = sourceSets,
            style = emptySet(),
            extra = empty,
        )

        val linkLine = ContentHeader(
            children = listOf(group(link)),
            level = 4,
            dci = dci,
            sourceSets = sourceSets,
            style = emptySet(),
            extra = empty,
        )

        return ContentGroup(
            children = listOf(versionLine, linkLine),
            dci = dci,
            sourceSets = sourceSets,
            style = setOf(RpcDevNotice),
            extra = empty,
        )
    }
}
