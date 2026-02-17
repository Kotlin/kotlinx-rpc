/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protoc.gen.core

import kotlinx.rpc.protoc.gen.core.model.FqName
import kotlinx.rpc.protoc.gen.core.model.fullName
import kotlinx.rpc.protoc.gen.core.model.fullNestedNameAsList
import kotlinx.rpc.protoc.gen.core.model.importPath
import kotlinx.rpc.protoc.gen.core.model.packageName

/**
 * Platform for code generation determines which implicit imports are considered.
 */
enum class Platform {
    Common,
    Jvm,
    Android,
    Js,
    Native,
    Wasm,
    ;

    companion object {
        fun fromString(platform: String): Platform = Platform.entries.first {
            it.name.equals(platform, ignoreCase = true)
        }
    }
}

/**
 * A table that tracks registered [FqName]s for name resolution across all packages.
 * This class handles the registration phase - collecting all names that are visible in the namespace.
 *
 * To resolve names, create a [ScopedFqNameTable] from this table for a specific package.
 */
class FqNameTable(
    /**
     * The current platform for code generation.
     */
    val platform: Platform = Platform.Common,
) {
    /**
     * All registered FqNames that can be resolved.
     */
    internal val registeredNames = mutableSetOf<FqName>()

    /**
     * Map of simple name to [FqName] for all implicit imports of the current platform.
     */
    internal val implicitTypesBySimpleName: Map<String, FqName> =
        ImplicitImports.getImplicitTypesBySimpleName(platform)

    /**
     * Registers a [FqName] so it can be resolved later.
     * Also, implicitly registers all parent names.
     * All names must be registered before resolution.
     *
     * @param fqName The fully qualified name to register.
     */
    fun register(fqName: FqName.Declaration) {
        val maybeImplicit = implicitTypesBySimpleName[fqName.simpleName]
        if (maybeImplicit != null && maybeImplicit == fqName) {
            // skip implicits
            return
        }

        var current: FqName = fqName
        while (current is FqName.Declaration) {
            registeredNames.add(current)
            current = current.parent
        }
    }

    private val lazyNames = mutableListOf<() -> FqName.Declaration>()

    /**
     * Same as [register] but computes on first [scoped] call. [fqNameGetter] will be called only once.
     */
    fun register(fqNameGetter: () -> FqName.Declaration) {
        lazyNames.add(fqNameGetter)
    }

    /**
     * Registers multiple [FqName]s so they can be resolved later.
     * Also, implicitly registers all parent names.
     *
     * @param fqNames The fully qualified names to register.
     */
    fun registerAll(vararg fqNames: FqName.Declaration) {
        registerAll(fqNames.asIterable())
    }

    /**
     * Registers multiple [FqName]s so they can be resolved later.
     * Also, implicitly registers all parent names.
     *
     * @param fqNames The fully qualified names to register.
     */
    fun registerAll(fqNames: Iterable<FqName.Declaration>) {
        fqNames.forEach { register(it) }
    }

    /**
     * Creates a [ScopedFqNameTable] for resolving names at package level in the specified package.
     *
     * @param currentPackage The package in which names will be resolved.
     * @return A scoped table for name resolution in the given package.
     */
    fun scoped(currentPackage: FqName.Package, importsContainer: MutableSet<String>): ScopedFqNameTable {
        val currentLazyNames = this.lazyNames.toList()
        lazyNames.clear()
        currentLazyNames.forEach { getter -> register(getter()) }

        return ScopedFqNameTable(this, currentPackage, emptyList(), importsContainer)
    }
}

/**
 * A scoped view of [FqNameTable] that resolves names within a specific package and scope.
 */
class ScopedFqNameTable internal constructor(
    private val table: FqNameTable,
    private val currentPackage: FqName.Package,
    private val scopePath: List<String>,
    val requiredImports: MutableSet<String>,
) {
    /**
     * Map of simple name to the [FqName] that has been resolved to use that name.
     * This tracks which names are "taken" by previous resolutions, preventing conflicts.
     */
    private val resolvedSimpleNames = mutableMapOf<String, FqName>()

    /**
     * Map of a simple name to implicit [FqName].
     */
    private val implicitTypesBySimpleName: Map<String, FqName> get() = table.implicitTypesBySimpleName

    /**
     * Creates a new [ScopedFqNameTable] for a nested class.
     * Use this when entering a nested class to get proper name resolution.
     *
     * @param nestedClassName The simple name of the nested class to enter.
     * @return A new scoped table for the nested class.
     */
    fun nested(nestedClassName: String): ScopedFqNameTable {
        return ScopedFqNameTable(table, currentPackage, scopePath + nestedClassName, requiredImports)
    }

    /**
     * Resolves the given [FqName] to its shortest unambiguous name in the current scope.
     *
     * @param fqName The fully qualified name to resolve.
     * @return The shortest unambiguous name string.
     * @throws IllegalArgumentException if the name was not registered.
     */
    fun resolve(fqName: FqName): String {
        val isImplicit = implicitTypesBySimpleName[fqName.simpleName] == fqName
        val isRegistered = table.registeredNames.contains(fqName)

        require(isImplicit || isRegistered) {
            "${FqName::class.simpleName} $fqName was not registered"
        }

        val fqNamePackage = fqName.packageName()
        val nestedPath = fqName.fullNestedNameAsList()

        // Check if this FqName is in the current scope path or a sibling/child of an enclosing scope
        val scopeRelativePath = findScopeRelativePath(
            targetFqName = fqName,
            targetPackage = fqNamePackage,
            targetNestedPath = nestedPath,
        )

        if (scopeRelativePath != null) {
            return scopeRelativePath
        }

        // Either implicits or other packages are left as options

        // Check what this leading name would resolve to
        val leadingName = nestedPath.first()
        val resolvedTo = whatDoesNameResolveTo(leadingName, nestedPath)

        // always implicits or already imported names here, but whatDoesNameResolveTo also checks for shadowing
        if (resolvedTo == fqName) {
            // This candidate works - record it and return
            recordTopLevelUsage(leadingName, fqNamePackage)
            return nestedPath.joinToKotlinName()
        }

        // Name doesn't resolve to the target - can we make it work via import?
        if (fqNamePackage != currentPackage) {
            // For nested classes, we need to import the top-level (outermost) class
            // Check if we can import the top-level class
            val canImport = canImportName(leadingName)
            if (canImport) {
                recordTopLevelUsage(leadingName, fqNamePackage)
                return nestedPath.joinToKotlinName()
            }
        }

        // Fall back to fully qualified name
        return fqName.fullName()
    }

    /**
     * Finds a path relative to the current scope, if the target [FqName] is within scope.
     * Returns null if the [FqName] is not reachable via a scope-relative path.
     */
    private fun findScopeRelativePath(
        targetFqName: FqName,
        targetPackage: FqName.Package,
        targetNestedPath: List<String>,
    ): String? {
        // Only consider same-package targets for scope-relative paths
        if (targetPackage != currentPackage) {
            return null
        }

        // Check if target is within or relative to our current scope
        // scopePath = ["MyClass", "String"] means we're inside MyClass.String
        // targetNestedPath = ["MyClass", "String", "String"] means MyClass.String.String

        // Check if target is a direct child or sibling at each scope level
        // We iterate from innermost (current scope) to outermost (package level)
        for (scopeLevel in scopePath.size downTo 0) {
            val scopePrefix = if (scopeLevel == 0) emptyList() else scopePath.subList(0, scopeLevel)

            // Check if target starts with this scope prefix
            if (targetNestedPath.size >= scopePrefix.size &&
                (scopePrefix.isEmpty() || targetNestedPath.subList(0, scopePrefix.size) == scopePrefix)
            ) {
                // Target is within or equal to this scope level
                val relativePath = targetNestedPath.drop(scopePrefix.size)

                if (relativePath.isEmpty()) {
                    // Target is exactly this scope prefix - this shouldn't normally happen at scopeLevel > 0
                    // because that would mean we're trying to reference an enclosing scope
                    continue
                }

                val leadingName = relativePath.first()

                // Check what this leading name resolves to from the current scope (not at scopeLevel)
                val resolvedFqName = whatDoesNameResolveTo(leadingName, relativePath)

                if (resolvedFqName == targetFqName) {
                    return relativePath.joinToKotlinName()
                }
            }
        }

        return null
    }

    /**
     * Determines what a given name path would resolve to in the current scope.
     */
    private fun whatDoesNameResolveTo(leadingName: String, fullPath: List<String>): FqName? {
        // First, try resolving via the leading name
        val leadingResolution = resolveLeadingNameOnly(leadingName)
            ?: return null

        if (fullPath.size == 1) {
            return leadingResolution
        }

        // Build the full path by starting from the leading resolution and adding the rest
        val leadingNestedPath = leadingResolution.fullNestedNameAsList()
        val resultPath = leadingNestedPath + fullPath.drop(1)
        return buildFqNameFromPath(leadingResolution.packageName(), resultPath)
    }

    /**
     * Resolves just a single simple name in the current scope (no path traversal).
     */
    private fun resolveLeadingNameOnly(simpleName: String): FqName? {
        // 1. Check if this name was already resolved via import
        resolvedSimpleNames[simpleName]?.let { return it }

        // 2. Check the scope path from innermost to outermost.
        // Children of enclosing scopes shadow everything else (including enclosing class names)
        for (i in scopePath.size downTo 0) {
            val scopePrefix = scopePath.subList(0, i)

            // Check if simpleName is a child of this enclosing scope
            val candidateNestedPath = scopePrefix + listOf(simpleName)
            val candidate = findRegisteredName(currentPackage, candidateNestedPath)
            if (candidate != null) {
                return candidate
            }
        }

        // 3. Check implicit imports (can return null)
        return implicitTypesBySimpleName[simpleName]
    }

    /**
     * Checks if we can import a top-level name from a different package without a conflict.
     */
    private fun canImportName(simpleName: String): Boolean {
        // Check if already resolved to something else
        if (resolvedSimpleNames.containsKey(simpleName)) {
            return false
        }

        // Check if it would shadow something in current scope/package
        val wouldShadow = resolveLeadingNameOnly(simpleName)
        return wouldShadow == null
    }

    /**
     * Records that a top-level name has been used and adds import if needed.
     */
    private fun recordTopLevelUsage(topLevelName: String, fqNamePackage: FqName.Package) {
        if (resolvedSimpleNames.containsKey(topLevelName)) {
            return // Already recorded
        }

        // Record the resolved name for the leading name
        val topLevelFqName = FqName.Declaration(topLevelName, fqNamePackage)
        resolvedSimpleNames[topLevelName] = topLevelFqName

        // Add import if needed (different package, not in current scope, and not implicit)
        if (fqNamePackage != currentPackage && implicitTypesBySimpleName[topLevelName] != topLevelFqName) {
            requiredImports.add(fqNamePackage.importPath(topLevelName))
        }
    }

    /**
     * Finds a registered name matching the given package and nested path.
     */
    private fun findRegisteredName(pkg: FqName.Package, nestedPath: List<String>): FqName? {
        var current: FqName = pkg
        for (part in nestedPath) {
            current = FqName.Declaration(part, current)
        }
        return if (table.registeredNames.contains(current)) current else null
    }

    /**
     * Builds an [FqName] from a package and a nested path.
     */
    private fun buildFqNameFromPath(pkg: FqName.Package, nestedPath: List<String>): FqName {
        var current: FqName = pkg
        for (part in nestedPath) {
            current = FqName.Declaration(part, current)
        }
        return current
    }

    private fun List<String>.joinToKotlinName(): String = joinToString(".")
}
