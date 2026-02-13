/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protoc.gen.core

import kotlinx.rpc.protoc.gen.core.model.FqName

/**
 * Manages implicit imports for different Kotlin platforms.
 * Based on official Kotlin documentation:
 * - [Packages and imports](https://kotlinlang.org/docs/packages.html)
 * - [Language specification](https://kotlinlang.org/spec/packages-and-imports.html)
 */
internal object ImplicitImports {
    private const val RESOURCES_PATH = "implicit-imports"

    private val kotlinPackage = FqName.Package.fromString("kotlin")
    private val kotlinAnnotationPackage = FqName.Package.fromString("kotlin.annotation")
    private val kotlinCollectionsPackage = FqName.Package.fromString("kotlin.collections")
    private val kotlinComparisonsPackage = FqName.Package.fromString("kotlin.comparisons")
    private val kotlinIoPackage = FqName.Package.fromString("kotlin.io")
    private val kotlinRangesPackage = FqName.Package.fromString("kotlin.ranges")
    private val kotlinSequencesPackage = FqName.Package.fromString("kotlin.sequences")
    private val kotlinTextPackage = FqName.Package.fromString("kotlin.text")
    private val javaLangPackage = FqName.Package.fromString("java.lang")
    private val kotlinJvmPackage = FqName.Package.fromString("kotlin.jvm")
    private val kotlinJsPackage = FqName.Package.fromString("kotlin.js")

    /**
     * Loads type names from a resource file and converts them to [FqName] declarations.
     *
     * @param resourceName The name of the resource file (e.g. "kotlin")
     * @param pkg The package for the [FqName] declarations
     * @return Set of [FqName] declarations, or empty set if resource not found
     */
    private fun loadTypesFromResource(resourceName: String, pkg: FqName.Package): Set<FqName> {
        val resourcePath = "/$RESOURCES_PATH/$resourceName.txt"
        val stream = ImplicitImports::class.java.getResourceAsStream(resourcePath)
            ?: error("Resource not found: $resourcePath")

        return stream.bufferedReader().useLines { lines ->
            lines
                .map { it.trim() }
                .filter { it.isNotEmpty() && !it.startsWith("#") }
                .map { FqName.Declaration(it, pkg) }
                .toSet()
        }
    }

    /**
     * All types from `kotlin.*` package that are implicitly imported.
     *
     * Based on [kotlin stdlib](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/)
     */
    private val KOTLIN_IMPLICIT_TYPES by lazy {
        loadTypesFromResource("kotlin", kotlinPackage)
    }

    /**
     * All types from kotlin.annotation.* package that are implicitly imported.
     *
     * Based on [kotlin.annotation](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.annotation/)
     */
    private val KOTLIN_ANNOTATION_IMPLICIT_TYPES by lazy {
        loadTypesFromResource("kotlin.annotation", kotlinAnnotationPackage)
    }

    /**
     * All types from kotlin.collections.* package that are implicitly imported.
     *
     * Based on [kotlin.collections](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/)
     */
    private val KOTLIN_COLLECTIONS_IMPLICIT_TYPES by lazy {
        loadTypesFromResource("kotlin.collections", kotlinCollectionsPackage)
    }

    /**
     * All types from kotlin.comparisons.* package that are implicitly imported.
     *
     * Based on [kotlin.comparisons](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.comparisons/)
     */
    private val KOTLIN_COMPARISONS_IMPLICIT_TYPES by lazy {
        loadTypesFromResource("kotlin.comparisons", kotlinComparisonsPackage)
    }

    /**
     * All types from kotlin.io.* package that are implicitly imported.
     *
     * Based on [kotlin.io](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.io/)
     */
    private val KOTLIN_IO_IMPLICIT_TYPES by lazy {
        loadTypesFromResource("kotlin.io", kotlinIoPackage)
    }

    /**
     * All types from kotlin.ranges.* package that are implicitly imported.
     *
     * Based on [kotlin.ranges](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.ranges/)
     */
    private val KOTLIN_RANGES_IMPLICIT_TYPES by lazy {
        loadTypesFromResource("kotlin.ranges", kotlinRangesPackage)
    }

    /**
     * All types from kotlin.sequences.* package that are implicitly imported.
     *
     * Based on [kotlin.sequences](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.sequences/)
     */
    private val KOTLIN_SEQUENCES_IMPLICIT_TYPES by lazy {
        loadTypesFromResource("kotlin.sequences", kotlinSequencesPackage)
    }

    /**
     * All types from kotlin.text.* package that are implicitly imported.
     *
     * Based on [kotlin.text](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.text/)
     */
    private val KOTLIN_TEXT_IMPLICIT_TYPES by lazy {
        loadTypesFromResource("kotlin.text", kotlinTextPackage)
    }

    /**
     * All types from java.lang.* package that are implicitly imported on JVM.
     *
     * Based on [java.lang](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/package-summary.html)
     */
    private val JAVA_LANG_IMPLICIT_TYPES by lazy {
        loadTypesFromResource("java.lang", javaLangPackage)
    }

    /**
     * All types from kotlin.jvm.* package that are implicitly imported on JVM platform.
     *
     * Based on [kotlin.jvm](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.jvm/)
     */
    private val KOTLIN_JVM_IMPLICIT_TYPES by lazy {
        loadTypesFromResource("kotlin.jvm", kotlinJvmPackage)
    }

    /**
     * All types from kotlin.js.* package that are implicitly imported on JS platform.
     *
     * Based on [kotlin.js](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.js/)
     */
    private val KOTLIN_JS_IMPLICIT_TYPES by lazy {
        loadTypesFromResource("kotlin.js", kotlinJsPackage)
    }

    private val COMMON_IMPLICIT_TYPES by lazy {
        KOTLIN_IMPLICIT_TYPES +
                KOTLIN_ANNOTATION_IMPLICIT_TYPES +
                KOTLIN_COLLECTIONS_IMPLICIT_TYPES +
                KOTLIN_COMPARISONS_IMPLICIT_TYPES +
                KOTLIN_IO_IMPLICIT_TYPES +
                KOTLIN_RANGES_IMPLICIT_TYPES +
                KOTLIN_SEQUENCES_IMPLICIT_TYPES +
                KOTLIN_TEXT_IMPLICIT_TYPES
    }

    /**
     * Returns all implicitly imported types for the given platform.
     *
     * Platform-specific implicit imports:
     * - Common: kotlin.*, kotlin.annotation.*, kotlin.collections.*, kotlin.comparisons.*,
     *           kotlin.io.*, kotlin.ranges.*, kotlin.sequences.*, kotlin.text.*
     * - JVM: + java.lang.*, kotlin.jvm.*
     * - JS: + kotlin.js.*
     * - Native: (uses only common packages)
     * - Wasm: (uses only common packages)
     */
    fun getImplicitTypes(platform: Platform): Set<FqName> {
        return when (platform) {
            Platform.Common -> COMMON_IMPLICIT_TYPES
            Platform.Jvm, Platform.Android -> JAVA_LANG_IMPLICIT_TYPES + COMMON_IMPLICIT_TYPES + KOTLIN_JVM_IMPLICIT_TYPES
            Platform.Js -> COMMON_IMPLICIT_TYPES + KOTLIN_JS_IMPLICIT_TYPES
            Platform.Native -> COMMON_IMPLICIT_TYPES
            Platform.Wasm -> COMMON_IMPLICIT_TYPES
        }
    }

    /**
     * Returns a map of a simple name to [FqName] for all implicit imports of the given platform.
     */
    fun getImplicitTypesBySimpleName(platform: Platform): Map<String, FqName> {
        return getImplicitTypes(platform).associateBy { it.simpleName }
    }
}
