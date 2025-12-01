/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protoc.gen.core

import kotlinx.rpc.protoc.gen.core.model.FieldDeclaration
import kotlinx.rpc.protoc.gen.core.model.FieldType
import kotlinx.rpc.protoc.gen.core.model.FileDeclaration
import kotlinx.rpc.protoc.gen.core.model.FqName
import kotlinx.rpc.protoc.gen.core.model.MessageDeclaration
import kotlinx.rpc.protoc.gen.core.model.Model
import kotlinx.rpc.protoc.gen.core.model.fullName

const val RPC_INTERNAL_PACKAGE_SUFFIX = "_rpc_internal"
const val MSG_INTERNAL_SUFFIX = "Internal"
const val PB_PKG = "kotlinx.rpc.protobuf.internal"
const val INTERNAL_RPC_API_ANNO = "kotlinx.rpc.internal.utils.InternalRpcApi"
const val WITH_CODEC_ANNO = "kotlinx.rpc.grpc.codec.WithCodec"

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
    protected val additionalPublicImports = mutableSetOf<String>()
    protected val additionalInternalImports = mutableSetOf<String>()

    fun generateKotlinFiles(): List<FileGenerator> {
        return model.files.flatMap { it.generateKotlinFiles() }
    }

    private fun FileDeclaration.generateKotlinFiles(): List<FileGenerator> {
        additionalPublicImports.clear()
        additionalInternalImports.clear()

        return listOfNotNull(
            if (hasPublicGeneratedContent) generatePublicKotlinFile() else null,
            if (hasExtensionGeneratedContent) generateExtensionKotlinFile() else null,
            if (hasInternalGeneratedContent) generateInternalKotlinFile() else null,
        )
    }

    private fun FileDeclaration.generatePublicKotlinFile(): FileGenerator {
        currentPackage = packageName

        return file(config) {
            filename = this@generatePublicKotlinFile.name
            packageName = this@generatePublicKotlinFile.packageName.safeFullName()
            packagePath = this@generatePublicKotlinFile.packageName.safeFullName()
            comments = this@generatePublicKotlinFile.doc

            dependencies.forEach { dependency ->
                importPackage(dependency.packageName.safeFullName())
            }

            fileOptIns = listOf("ExperimentalRpcApi::class", "InternalRpcApi::class")

            generatePublicDeclaredEntities(this@generatePublicKotlinFile)

            import("kotlinx.rpc.internal.utils.*")

            additionalPublicImports.forEach {
                import(it)
            }
        }
    }

    private fun FileDeclaration.generateExtensionKotlinFile(): FileGenerator {
        currentPackage = packageName

        return file(config) {
            filename = this@generateExtensionKotlinFile.name.removeSuffix(".kt") + ".ext.kt"
            packageName = this@generateExtensionKotlinFile.packageName.safeFullName()
            packagePath = this@generateExtensionKotlinFile.packageName.safeFullName()

            fileOptIns = listOf("ExperimentalRpcApi::class", "InternalRpcApi::class")

            generateExtensionEntities(this@generateExtensionKotlinFile)

            import("kotlinx.rpc.internal.utils.*")

            additionalPublicImports.forEach {
                import(it)
            }
        }
    }

    private fun FileDeclaration.generateInternalKotlinFile(): FileGenerator {
        currentPackage = packageName

        return file(config) {
            filename = this@generateInternalKotlinFile.name
            packageName = this@generateInternalKotlinFile.packageName.safeFullName()
            packagePath =
                this@generateInternalKotlinFile.packageName.safeFullName()
                    .packageNameSuffixed(RPC_INTERNAL_PACKAGE_SUFFIX)

            fileOptIns = listOf("ExperimentalRpcApi::class", "$INTERNAL_RPC_API_ANNO::class")

            dependencies.forEach { dependency ->
                importPackage(dependency.packageName.safeFullName())
            }

            generateInternalDeclaredEntities(this@generateInternalKotlinFile)

            import("$PB_PKG.*")
            import("kotlinx.rpc.internal.utils.*")

            additionalInternalImports.forEach {
                import(it)
            }
        }
    }

    protected fun FieldDeclaration.typeFqName(): String {
        return when (type) {
            is FieldType.Message -> {
                type.dec.value.name.safeFullName()
            }

            is FieldType.Enum -> type.dec.value.name.safeFullName()

            is FieldType.OneOf -> type.dec.name.safeFullName()

            is FieldType.IntegralType -> {
                type.fqName.simpleName
            }

            is FieldType.List -> {
                val fqValue = when (val value = type.value) {
                    is FieldType.Message -> value.dec.value.name
                    is FieldType.IntegralType -> value.fqName
                    is FieldType.Enum -> value.dec.value.name
                    else -> error("Unsupported type: $value")
                }

                "List<${fqValue.safeFullName()}>"
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

                "Map<${fqKey.safeFullName()}, ${fqValue.safeFullName()}>"
            }

        }.withNullability(nullable)
    }

    protected fun String.wrapInFlowIf(condition: Boolean): String {
        return if (condition) "Flow<$this>" else this
    }

    protected fun FqName.safeFullName(classSuffix: String = ""): String {
        importRootDeclarationIfNeeded(this)

        return fullName(classSuffix)
    }

    protected fun importRootDeclarationIfNeeded(
        declaration: FqName,
        nameToImport: String = declaration.simpleName,
        internalOnly: Boolean = false,
    ) {
        if (declaration is FqName.Package) {
            return
        }

        if (declaration.parent == FqName.Package.Root && currentPackage != FqName.Package.Root && nameToImport.isNotBlank()) {
            additionalInternalImports.add(nameToImport)
            if (!internalOnly) {
                additionalPublicImports.add(nameToImport)
            }
        }
    }

    protected fun FieldType.Message.internalConstructor() =
        dec.value.internalClassFullName() + "()"

    protected fun MessageDeclaration.internalClassFullName(): String {
        return name.safeFullName(MSG_INTERNAL_SUFFIX)
    }

    protected fun MessageDeclaration.presenceClassFullName(): String {
        return internalClassFullName() + ".Presence"
    }

    protected fun MessageDeclaration.internalClassName(): String {
        return name.simpleName + MSG_INTERNAL_SUFFIX
    }

    protected fun String.withNullability(nullable: Boolean): String {
        return "$this${if (nullable) "?" else ""}"
    }

    protected fun String.packageNameSuffixed(suffix: String): String {
        return if (isEmpty()) suffix else "$this.$suffix"
    }
}
