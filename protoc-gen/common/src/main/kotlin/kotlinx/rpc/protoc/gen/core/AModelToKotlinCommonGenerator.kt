/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protoc.gen.core

import kotlinx.rpc.protoc.gen.core.model.FieldDeclaration
import kotlinx.rpc.protoc.gen.core.model.FieldType
import kotlinx.rpc.protoc.gen.core.model.FileDeclaration
import kotlinx.rpc.protoc.gen.core.model.FqName
import kotlinx.rpc.protoc.gen.core.model.Model
import kotlinx.rpc.protoc.gen.core.model.fullName
import kotlinx.rpc.protoc.gen.core.model.packageName

const val RPC_INTERNAL_PACKAGE_SUFFIX = "_rpc_internal"

abstract class AModelToKotlinCommonGenerator(
    protected val config: Config,
    protected val model: Model,
) {
    protected abstract fun CodeGenerator.generatePublicDeclaredEntities(fileDeclaration: FileDeclaration)

    protected abstract fun CodeGenerator.generateExtensionEntities(fileDeclaration: FileDeclaration)

    protected abstract fun CodeGenerator.generateInternalDeclaredEntities(fileDeclaration: FileDeclaration)

    protected abstract val FileDeclaration.hasPublicGeneratedContent: Boolean
    protected abstract val FileDeclaration.hasExtensionGeneratedContent: Boolean
    protected abstract val FileDeclaration.hasInternalGeneratedContent: Boolean

    protected var currentPackage: FqName = FqName.Package.Root
    protected val internalImports = mutableSetOf<String>()

    /**
     * If the key is imported and not referenced by its FQ name, import all from the set.
     */
    protected val conditionalInternalImports = mutableMapOf<FqName, Set<String>>()

    // todo this actually has to be tied to the resolution in scope
    protected fun referenceExtension(receiver: FqName, extensionName: String) {
        conditionalInternalImports.merge(receiver, setOf("${receiver.packageName()}.$extensionName")) {
            old, new -> old + new
        }
    }

    fun generateKotlinFiles(): List<FileGenerator> {
        return model.files.flatMap { it.generateKotlinFiles() }
    }

    private fun FileDeclaration.generateKotlinFiles(): List<FileGenerator> {
        internalImports.clear()

        return listOfNotNull(
            if (hasPublicGeneratedContent) generatePublicKotlinFile() else null,
            if (hasExtensionGeneratedContent) generateExtensionKotlinFile() else null,
            if (hasInternalGeneratedContent) generateInternalKotlinFile() else null,
        )
    }

    private fun FileDeclaration.generatePublicKotlinFile(): FileGenerator {
        currentPackage = packageName

        return file(config, this@generatePublicKotlinFile.packageName, model.nameTable) {
            filename = this@generatePublicKotlinFile.name
            comments = this@generatePublicKotlinFile.doc

            dependencies.forEach { dependency ->
                importPackage(dependency.packageName.fullName())
            }

            fileOptIns = listOf(
                "%T::class".scoped(FqName.Annotations.ExperimentalRpcApi),
                "%T::class".scoped(FqName.Annotations.InternalRpcApi),
            )

            generatePublicDeclaredEntities(this@generatePublicKotlinFile)
        }
    }

    private fun FileDeclaration.generateExtensionKotlinFile(): FileGenerator {
        currentPackage = packageName

        return file(config, this@generateExtensionKotlinFile.packageName, model.nameTable) {
            filename = this@generateExtensionKotlinFile.name.removeSuffix(".kt") + ".ext.kt"

            fileOptIns = listOf(
                "%T::class".scoped(FqName.Annotations.ExperimentalRpcApi),
                "%T::class".scoped(FqName.Annotations.InternalRpcApi),
            )

            generateExtensionEntities(this@generateExtensionKotlinFile)
        }
    }

    private fun FileDeclaration.generateInternalKotlinFile(): FileGenerator {
        currentPackage = packageName

        return file(config, this@generateInternalKotlinFile.packageName, model.nameTable) {
            filename = this@generateInternalKotlinFile.name
            packagePath = this@generateInternalKotlinFile.packageName.fullName()
                .packageNameSuffixed(RPC_INTERNAL_PACKAGE_SUFFIX)

            fileOptIns = listOf(
                "%T::class".scoped(FqName.Annotations.ExperimentalRpcApi),
                "%T::class".scoped(FqName.Annotations.InternalRpcApi),
            )

            dependencies.forEach { dependency ->
                importPackage(dependency.packageName.fullName())
            }

            generateInternalDeclaredEntities(this@generateInternalKotlinFile)

            internalImports.forEach {
                import(it)
            }

            importConditionally(conditionalInternalImports)
        }
    }

    protected fun FieldDeclaration.typeFqName(): ScopedFormattedString {
        return when (type) {
            is FieldType.Message -> {
                type.dec.value.name.scoped()
            }

            is FieldType.Enum -> type.dec.value.name.scoped()

            is FieldType.OneOf -> type.dec.name.scoped()

            is FieldType.IntegralType -> {
                type.fqName.scoped()
            }

            is FieldType.List -> {
                val fqValue = when (val value = type.value) {
                    is FieldType.Message -> value.dec.value.name
                    is FieldType.IntegralType -> value.fqName
                    is FieldType.Enum -> value.dec.value.name
                    else -> error("Unsupported type: $value")
                }

                "%T<%T>".scoped(FqName.Implicits.List, fqValue)
            }

            is FieldType.Map -> {
                val entry = type.entry

                val fqKey = when (val key = entry.key) {
                    is FieldType.Message -> key.dec.value.name
                    is FieldType.IntegralType -> key.fqName
                    else -> error("Unsupported type: $key")
                }

                val fqValue = when (val value = entry.value) {
                    is FieldType.Message -> value.dec.value.name
                    is FieldType.IntegralType -> value.fqName
                    is FieldType.Enum -> value.dec.value.name
                    else -> error("Unsupported type: $value")
                }

                "%T<%T, %T>".scoped(FqName.Implicits.Map, fqKey, fqValue)
            }
        }.withNullability(nullable)
    }

    protected fun ScopedFormattedString.wrapInFlowIf(condition: Boolean): ScopedFormattedString {
        return if (condition) {
            FqName.KotlinLibs.Flow.scoped().merge(this) { flow, original ->
                "$flow<$original>"
            }
        } else {
            this
        }
    }

    protected fun ScopedFormattedString.withNullability(nullable: Boolean): ScopedFormattedString {
        return wrapIn { "$it${if (nullable) "?" else ""}" }
    }

    protected fun String.packageNameSuffixed(suffix: String): String {
        return if (isEmpty()) suffix else "$this.$suffix"
    }
}
