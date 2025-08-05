/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.model

import org.slf4j.Logger

// The NameResolver isn't used as we rely on the Java Descriptor for message resolving.
// However, it might be useful if we write a bootstrapped plugin in K/N,
// so we leave it here and for tests
@Suppress("unused")
internal class NameResolver private constructor(
    private val logger: Logger,
    private val root: Node = Node(FqName.Package.Root.simpleName, FqName.Package.Root),
    private val scope: Node = root,
    private val imports: List<Node> = emptyList(),
) {
    fun add(name: FqName) {
        val parentNode = getParentNodeFor(name)

        if (parentNode.children.containsKey(name.simpleName)) {
            error("Name ${name.fullName()} already exists")
        }

        parentNode.children[name.simpleName] = Node(name.simpleName, name, parentNode)
    }

    private fun getParentNodeFor(name: FqName): Node {
        var next: FqName = name
        val parents = mutableListOf<FqName>()
        while (next.parent != next) {
            next = next.parent
            if (next != next.parent) {
                parents += next
            }
        }

        var parentNode = root
        parents.reversed().forEach { parent ->
            parentNode = parentNode.resolve(parent)
        }
        return parentNode
    }

    private fun Node.resolve(name: FqName): Node {
        if (name is FqName.Declaration && !children.containsKey(name.simpleName)) {
            error("Name ${name.fullName()} doesn't exist, can't resolve ${name.fullName()}")
        }

        return children.getOrPut(name.simpleName) {
            Node(name.simpleName, name, this)
        }
    }

    fun resolve(name: String): FqName {
        return resolveOrNull(name) ?: error("Name $name doesn't exist")
    }

    fun resolveOrNull(name: String): FqName? {
        val (parents, simpleName) = name.asParentsAndSimpleName()

        return resolveInScope(parents, simpleName, scope)
            ?: run {
                for (import in imports) {
                    resolveInScope(parents, simpleName, import)
                        ?.let { return it }
                }

                null
            }
    }

    private fun resolveInScope(parents: List<String>, simpleName: String, scope: Node): FqName? {
        val inScope = resolveFromNode(scope, parents, simpleName)

        return when (inScope) {
            is ResolveResult.Success -> {
                inScope.fqName
            }

            is ResolveResult.PartialResolve -> {
                null
            }

            is ResolveResult.NoResolve -> {
                val inRoot = resolveFromNode(root, parents, simpleName)

                when (inRoot) {
                    is ResolveResult.Success -> {
                        inRoot.fqName
                    }

                    else -> {
                        var node = scope.parent
                        while (node != null && node.fqName is FqName.Declaration) {
                            val inImport = resolveFromNode(node, parents, simpleName)

                            if (inImport is ResolveResult.Success) {
                                return inImport.fqName
                            }

                            node = node.parent
                        }

                        null
                    }
                }
            }
        }
    }

    private fun resolveFromNode(start: Node, parents: List<String>, simpleName: String): ResolveResult {
        val declarationOnlyResolve = start.fqName != FqName.Package.Root && start.fqName is FqName.Package
        var node: Node? = start
        var i = 0
        var entered = false
        while (i != parents.size) {
            node = node?.children[parents[i++]]
            if (node == null) {
                break
            } else if (node.fqName is FqName.Package && declarationOnlyResolve) {
                return ResolveResult.NoResolve
            }
            entered = true
        }

        if (node != null) {
            val name = node.children[simpleName]
            if (name != null) {
                return ResolveResult.Success(name.fqName)
            }
        }

        return if (entered) {
            ResolveResult.PartialResolve
        } else {
            ResolveResult.NoResolve
        }
    }

    sealed interface ResolveResult {
        data class Success(val fqName: FqName) : ResolveResult
        data object PartialResolve : ResolveResult
        data object NoResolve : ResolveResult
    }

    fun withImports(imports: List<FqName.Package>): NameResolver {
        return NameResolver(logger, root, scope, imports.map { getParentNodeFor(it).resolve(it) })
    }

    fun withScope(name: FqName): NameResolver {
        val node = getParentNodeFor(name).resolve(name)
        return NameResolver(logger, root, node, imports)
    }

    companion object {
        fun create(logger: Logger): NameResolver {
            return NameResolver(logger)
        }
    }

    private class Node(
        val name: String,
        val fqName: FqName,
        val parent: Node? = null,
        val children: MutableMap<String, Node> = mutableMapOf<String, Node>(),
    ) {
        private var _list: List<String>? = null

        fun asList(): List<String> {
            if (_list != null) {
                return _list!!
            }

            if (parent == null) {
                return emptyList()
            }

            _list = parent.asList() + name
            return _list!!
        }
    }

    @Suppress("unused")
    fun pprint(): String {
        return buildString { pprint(root, 0) }
    }

    private fun StringBuilder.pprint(node: Node, indent: Int) {
        val spaces = " ".repeat(indent)
        appendLine("$spaces${node.fqName.fullName()}")
        for (child in node.children.values) {
            pprint(child, indent + 4)
        }
    }
}
