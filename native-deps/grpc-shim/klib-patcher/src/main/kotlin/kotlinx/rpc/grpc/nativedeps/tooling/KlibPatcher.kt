/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(kotlin.metadata.ExperimentalAnnotationsInMetadata::class)

package kotlinx.rpc.grpc.nativedeps.tooling

import kotlin.metadata.KmAnnotation
import kotlin.metadata.KmClass
import kotlin.metadata.KmConstructor
import kotlin.metadata.KmFunction
import kotlin.metadata.KmProperty
import kotlin.metadata.KmTypeAlias
import kotlin.metadata.internal.common.KmModuleFragment
import kotlinx.metadata.klib.KlibModuleMetadata
import java.io.File
import java.lang.reflect.Method
import java.nio.file.Files
import kotlin.io.path.createTempDirectory
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * Patches the produced grpc-shim cinterop KLIB after cinterop finishes.
 *
 * grpc-shim is published as a KLIB because the build needs it, but the declarations generated into the
 * `grpcCoreInterop` package are internal implementation detail, not supported public API. The cinterop toolchain
 * does not load compiler plugin registrars on this build path, so the narrow reliable hook is to rewrite the
 * produced KLIB metadata immediately after cinterop and before the artifact is consumed or published.
 */
internal object KlibPatcher {
    private const val targetPackageName = "grpcCoreInterop"
    private const val internalNativeRpcApiAnnotationClassName = "kotlinx/rpc/grpc/nativedeps/InternalNativeRpcApi"
    private const val internalNativeRpcApiDependencyUniqueName =
        "org.jetbrains.kotlinx\\:kotlinx-rpc-grpc-core-shim-annotation"
    private val internalNativeRpcApiAnnotation = KmAnnotation(internalNativeRpcApiAnnotationClassName, emptyMap())
    private val fragmentPackageNameAccessor: Method = Class.forName("kotlinx.metadata.klib.KlibModuleMetadataKt")
        .getDeclaredMethod("access\$fqNameOrFail", KmModuleFragment::class.java)

    @JvmStatic
    fun main(args: Array<String>) {
        require(args.size == 2) {
            "Expected <unpacked-klib-dir> <packed-klib-file>, got ${args.toList()}"
        }
        patch(
            unpackedKlibDir = File(args[0]),
            packedKlibFile = File(args[1]),
        )
    }

    internal fun patch(unpackedKlibDir: File, packedKlibFile: File) {
        val tempKlibDir = createTempDirectory(prefix = "grpc-shim-unsupported-api-klib-").toFile()
        try {
            unpackedKlibDir.copyRecursively(target = tempKlibDir, overwrite = true)
            val archiveDir = resolveArchiveDir(tempKlibDir)
            val linkdataDir = archiveDir.resolve("linkdata")
            check(linkdataDir.isDirectory) {
                "Missing linkdata directory: ${linkdataDir.absolutePath}"
            }

            val metadataProvider = LinkdataLibraryProvider(linkdataDir)
            val metadata = KlibModuleMetadata.read(metadataProvider)
            val targetFragments = metadata.fragments.filter { fragmentPackageName(it) == targetPackageName }

            check(targetFragments.isNotEmpty()) {
                "Expected grpc-shim cinterop metadata for package $targetPackageName in ${unpackedKlibDir.absolutePath}"
            }

            targetFragments.forEach(::annotateFragment)

            writeLinkdata(
                linkdataDir = linkdataDir,
                metadataProvider = metadataProvider,
                serializedMetadata = metadata.write(),
            )
            patchManifest(archiveDir.resolve("manifest"))
            repackKlib(
                unpackedKlibInputDir = tempKlibDir,
                archiveDir = archiveDir,
                packedKlibFile = packedKlibFile,
            )
        } finally {
            tempKlibDir.deleteRecursively()
        }
    }

    private fun resolveArchiveDir(unpackedKlibDir: File): File = when {
        unpackedKlibDir.resolve("linkdata").isDirectory -> unpackedKlibDir
        unpackedKlibDir.resolve("default/linkdata").isDirectory -> unpackedKlibDir.resolve("default")
        else -> unpackedKlibDir
    }

    private fun annotateFragment(fragment: KmModuleFragment) {
        val packageFragment = requireNotNull(fragment.pkg) {
            "Expected package metadata for ${fragmentPackageName(fragment)}"
        }
        packageFragment.functions.forEach(::annotate)
        packageFragment.properties.forEach(::annotate)
        packageFragment.typeAliases.forEach(::annotate)
        fragment.classes.forEach(::annotate)
    }

    private fun annotate(clazz: KmClass) {
        clazz.annotations.addInternalNativeRpcApiMarker()
        clazz.constructors.forEach(::annotate)
        clazz.functions.forEach(::annotate)
        clazz.properties.forEach(::annotate)
        clazz.typeAliases.forEach(::annotate)
    }

    private fun annotate(constructor: KmConstructor) {
        constructor.annotations.addInternalNativeRpcApiMarker()
    }

    private fun annotate(function: KmFunction) {
        function.annotations.addInternalNativeRpcApiMarker()
    }

    private fun annotate(property: KmProperty) {
        property.annotations.addInternalNativeRpcApiMarker()
    }

    private fun annotate(typeAlias: KmTypeAlias) {
        typeAlias.annotations.addInternalNativeRpcApiMarker()
    }

    private fun MutableList<KmAnnotation>.addInternalNativeRpcApiMarker() {
        if (none { it.className == internalNativeRpcApiAnnotationClassName }) {
            add(internalNativeRpcApiAnnotation)
        }
    }

    private fun fragmentPackageName(fragment: KmModuleFragment): String {
        return fragmentPackageNameAccessor.invoke(null, fragment) as String
    }

    private fun writeLinkdata(
        linkdataDir: File,
        metadataProvider: LinkdataLibraryProvider,
        serializedMetadata: KlibModuleMetadata.SerializedKlibMetadata,
    ) {
        linkdataDir.resolve("module").writeBytes(serializedMetadata.header)

        val serializedByPackage = serializedMetadata.fragmentNames.zip(serializedMetadata.fragments)

        serializedByPackage.forEach { (packageName, fragments) ->
            val packageDir = linkdataDir.resolve(metadataProvider.packageDirectoryName(packageName))
            val outputNames = metadataProvider.existingPartNames(packageName).takeIf { it.size == fragments.size }
                ?: generatedPartNames(packageName, fragments.size)

            packageDir.deleteRecursively()
            packageDir.mkdirs()

            fragments.forEachIndexed { index, bytes ->
                packageDir.resolve(outputNames[index]).writeBytes(bytes)
            }
        }
    }

    private fun generatedPartNames(packageName: String, fragmentCount: Int): List<String> {
        val suffix = packageName.substringAfterLast('.', packageName).ifBlank { "root" }.replace('.', '_')
        return List(fragmentCount) { index -> "${index}_$suffix.knm" }
    }

    private fun patchManifest(manifestFile: File) {
        check(manifestFile.isFile) {
            "Missing KLIB manifest: ${manifestFile.absolutePath}"
        }
        val lines = manifestFile.readLines()
        val updatedLines = lines.map { line ->
            if (!line.startsWith("depends=")) return@map line

            val dependencies = line.removePrefix("depends=")
                .split(' ')
                .filter(String::isNotBlank)
                .toMutableList()
            if (internalNativeRpcApiDependencyUniqueName !in dependencies) {
                dependencies += internalNativeRpcApiDependencyUniqueName
            }
            "depends=${dependencies.joinToString(" ")}"
        }
        manifestFile.writeText(updatedLines.joinToString(separator = "\n", postfix = "\n"))
    }

    private fun repackKlib(unpackedKlibInputDir: File, archiveDir: File, packedKlibFile: File) {
        val archiveRoot = if (archiveDir.parentFile == unpackedKlibInputDir) {
            unpackedKlibInputDir
        } else {
            archiveDir.parentFile ?: error("Expected ${archiveDir.absolutePath} to have a parent directory")
        }

        packedKlibFile.parentFile.mkdirs()
        ZipOutputStream(packedKlibFile.outputStream().buffered()).use { zip ->
            Files.walk(archiveDir.toPath()).use { paths ->
                paths.filter { path -> Files.isRegularFile(path) }
                    .sorted()
                    .forEach { path ->
                        val entryName = archiveRoot.toPath()
                            .relativize(path)
                            .toString()
                            .replace(File.separatorChar, '/')
                        zip.putNextEntry(ZipEntry(entryName))
                        Files.copy(path, zip)
                        zip.closeEntry()
                    }
            }
        }
    }

    private class LinkdataLibraryProvider(
        private val linkdataDir: File,
    ) : KlibModuleMetadata.MetadataLibraryProvider {
        override val moduleHeaderData: ByteArray
            get() = linkdataDir.resolve("module").readBytes()

        override fun packageMetadataParts(fqName: String): Set<String> =
            packagePartFiles(fqName).mapTo(linkedSetOf()) { it.name }

        override fun packageMetadata(fqName: String, partName: String): ByteArray =
            linkdataDir.resolve(packageDirectoryName(fqName)).resolve(partName).readBytes()

        fun existingPartNames(packageName: String): List<String> = packagePartFiles(packageName).map(File::getName)

        fun packageDirectoryName(packageName: String): String = "package_" + packageName.replace('.', '_')

        private fun packagePartFiles(packageName: String): List<File> {
            val packageDir = linkdataDir.resolve(packageDirectoryName(packageName))
            check(packageDir.isDirectory) {
                "Missing package metadata directory for $packageName: ${packageDir.absolutePath}"
            }
            return packageDir.listFiles()
                .orEmpty()
                .filter(File::isFile)
                .sortedBy(File::getName)
        }
    }
}
