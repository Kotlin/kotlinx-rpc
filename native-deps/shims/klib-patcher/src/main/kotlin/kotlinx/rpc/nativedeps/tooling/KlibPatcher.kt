/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.nativedeps.tooling

import kotlin.OptIn
import kotlin.metadata.ExperimentalAnnotationsInMetadata
import kotlin.metadata.KmAnnotation
import kotlin.metadata.KmClass
import kotlin.metadata.KmConstructor
import kotlin.metadata.KmFunction
import kotlin.metadata.KmPackage
import kotlin.metadata.KmProperty
import kotlin.metadata.KmTypeAlias
import kotlin.metadata.internal.common.KmModuleFragment
import kotlinx.metadata.klib.KlibModuleFragmentReadStrategy
import kotlinx.metadata.klib.KlibModuleFragmentWriteStrategy
import kotlinx.metadata.klib.KlibModuleMetadata
import java.io.File
import java.lang.reflect.Method
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.Comparator
import java.util.LinkedHashSet
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.io.path.deleteIfExists
import kotlin.io.path.inputStream
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile
import kotlin.io.path.name
import kotlin.io.path.outputStream
import kotlin.io.path.readBytes
import kotlin.io.path.readLines
import kotlin.io.path.readText
import kotlin.io.path.writeBytes
import kotlin.io.path.writeText
import kotlin.streams.asSequence

/**
 * Patches produced cinterop KLIB metadata so declarations in one target package require explicit opt-in.
 *
 * This is shared internal native-deps build tooling used by the shims build. It is intentionally
 * parameter-driven so the fragile KLIB metadata rewrite logic lives in one place.
 */
@OptIn(ExperimentalAnnotationsInMetadata::class)
public object KlibPatcher {
    private val fragmentPackageNameAccessor: Method = Class.forName("kotlinx.metadata.klib.KlibModuleMetadataKt")
        .getDeclaredMethod("access\$fqNameOrFail", KmModuleFragment::class.java)

    @JvmStatic
    public fun main(args: Array<String>) {
        require(args.size == 5) {
            "Expected <unpacked-klib-dir> <packed-klib-file> <target-package> <annotation-class> " +
                "<dependency-unique-name>, got ${args.toList()}"
        }

        patch(
            Configuration(
                unpackedKlibDir = File(args[0]),
                packedKlibFile = File(args[1]),
                targetPackageName = args[2],
                annotationClassName = args[3],
                annotationDependencyUniqueName = args[4],
            ),
        )
    }

    public fun patch(configuration: Configuration) {
        val tempKlibDir = Files.createTempDirectory("native-deps-internal-native-rpc-api-klib-")
        try {
            configuration.unpackedKlibDir.toPath().copyRecursivelyTo(tempKlibDir)

            val archiveDir = resolveArchiveDir(tempKlibDir)
            val linkdataDir = archiveDir.resolve("linkdata")
            check(linkdataDir.isDirectory()) {
                "Missing linkdata directory: ${linkdataDir.toAbsolutePath()}"
            }

            val metadataProvider = LinkdataLibraryProvider(linkdataDir)
            val metadata = KlibModuleMetadata.read(
                metadataProvider,
                KlibModuleFragmentReadStrategy.DEFAULT,
            )
            val targetFragments = metadata.fragments.filter { fragment ->
                fragment.packageName == configuration.targetPackageName
            }

            check(targetFragments.isNotEmpty()) {
                "Expected cinterop metadata for package ${configuration.targetPackageName} in " +
                    configuration.unpackedKlibDir.absolutePath
            }

            targetFragments.forEach { fragment ->
                fragment.annotate(configuration)
            }

            writeLinkdata(
                linkdataDir = linkdataDir,
                metadataProvider = metadataProvider,
                serializedMetadata = metadata.write(KlibModuleFragmentWriteStrategy.DEFAULT),
            )

            archiveDir.resolve("manifest").patchManifest(configuration)
            repackKlib(
                unpackedKlibInputDir = tempKlibDir,
                archiveDir = archiveDir,
                packedKlibFile = configuration.packedKlibFile.toPath(),
            )
        } finally {
            tempKlibDir.deleteRecursively()
        }
    }

    private fun resolveArchiveDir(unpackedKlibDir: Path): Path = when {
        unpackedKlibDir.resolve("linkdata").isDirectory() -> unpackedKlibDir
        unpackedKlibDir.resolve("default/linkdata").isDirectory() -> unpackedKlibDir.resolve("default")
        else -> unpackedKlibDir
    }

    private fun KmModuleFragment.annotate(configuration: Configuration) {
        val packageFragment = requireNotNull(pkg) {
            "Expected package metadata for $packageName"
        }

        packageFragment.functions.forEach { it.annotate(configuration) }
        packageFragment.properties.forEach { it.annotate(configuration) }
        packageFragment.typeAliases.forEach { it.annotate(configuration) }
        classes.forEach { it.annotate(configuration) }
    }

    private fun KmClass.annotate(configuration: Configuration) {
        annotations.addInternalNativeRpcApiMarker(configuration)
        constructors.forEach { it.annotate(configuration) }
        functions.forEach { it.annotate(configuration) }
        properties.forEach { it.annotate(configuration) }
        typeAliases.forEach { it.annotate(configuration) }
    }

    private fun KmConstructor.annotate(configuration: Configuration) {
        annotations.addInternalNativeRpcApiMarker(configuration)
    }

    private fun KmFunction.annotate(configuration: Configuration) {
        annotations.addInternalNativeRpcApiMarker(configuration)
    }

    private fun KmProperty.annotate(configuration: Configuration) {
        annotations.addInternalNativeRpcApiMarker(configuration)
    }

    private fun KmTypeAlias.annotate(configuration: Configuration) {
        annotations.addInternalNativeRpcApiMarker(configuration)
    }

    private fun MutableList<KmAnnotation>.addInternalNativeRpcApiMarker(configuration: Configuration) {
        if (none { annotation -> annotation.className == configuration.annotationClassName }) {
            add(configuration.annotation)
        }
    }

    private val KmModuleFragment.packageName: String
        get() = fragmentPackageNameAccessor.invoke(null, this) as String

    private fun writeLinkdata(
        linkdataDir: Path,
        metadataProvider: LinkdataLibraryProvider,
        serializedMetadata: KlibModuleMetadata.SerializedKlibMetadata,
    ) {
        linkdataDir.resolve("module").writeBytes(serializedMetadata.header)

        serializedMetadata.fragmentNames.forEachIndexed { index, packageName ->
            val packageFragments = serializedMetadata.fragments[index]
            val packageDir = linkdataDir.resolve(metadataProvider.packageDirectoryName(packageName))
            val outputNames = metadataProvider.existingPartNames(packageName)
                .takeIf { it.size == packageFragments.size }
                ?: generatedPartNames(packageName, packageFragments.size)

            packageDir.deleteRecursively()
            Files.createDirectories(packageDir)

            packageFragments.forEachIndexed { partIndex, packageFragment ->
                packageDir.resolve(outputNames[partIndex]).writeBytes(packageFragment)
            }
        }
    }

    private fun generatedPartNames(packageName: String, fragmentCount: Int): List<String> {
        val suffix = packageName.substringAfterLast('.', packageName)
            .ifBlank { "root" }
            .replace('.', '_')
        return List(fragmentCount) { index -> "${index}_${suffix}.knm" }
    }

    private fun Path.patchManifest(configuration: Configuration) {
        check(isRegularFile()) {
            "Missing KLIB manifest: ${toAbsolutePath()}"
        }

        val updatedLines = readLines().map { line ->
            if (!line.startsWith("depends=")) {
                line
            } else {
                val dependencies = line.removePrefix("depends=")
                    .split(' ')
                    .filter { dependency -> dependency.isNotBlank() }
                    .toMutableList()
                if (configuration.annotationDependencyUniqueName !in dependencies) {
                    dependencies += configuration.annotationDependencyUniqueName
                }
                "depends=${dependencies.joinToString(" ")}"
            }
        }

        writeText(updatedLines.joinToString(separator = "\n", postfix = "\n"))
    }

    private fun repackKlib(
        unpackedKlibInputDir: Path,
        archiveDir: Path,
        packedKlibFile: Path,
    ) {
        val archiveRoot = archiveDir.parent
            ?.takeIf { parent -> parent == unpackedKlibInputDir }
            ?: requireNotNull(archiveDir.parent) { "$archiveDir must have a parent directory" }

        Files.createDirectories(requireNotNull(packedKlibFile.parent) { "Packed KLIB must have a parent" })

        ZipOutputStream(packedKlibFile.outputStream()).use { zip ->
            archiveDir.walkFiles().forEach { path ->
                val entryName = archiveRoot.relativize(path).toString().replace(File.separatorChar, '/')
                zip.putNextEntry(ZipEntry(entryName))
                path.inputStream().use { input -> input.copyTo(zip) }
                zip.closeEntry()
            }
        }
    }

    private fun Path.copyRecursivelyTo(targetDir: Path) {
        walkAll().forEach { sourcePath ->
            val targetPath = targetDir.resolve(this.relativize(sourcePath).toString())
            if (sourcePath.isDirectory()) {
                Files.createDirectories(targetPath)
            } else {
                Files.createDirectories(requireNotNull(targetPath.parent))
                Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING)
            }
        }
    }

    private fun Path.deleteRecursively() {
        if (!Files.exists(this)) return
        walkAllDescending().forEach { path -> path.deleteIfExists() }
    }

    private fun Path.walkFiles(): List<Path> = walkAll().filter { it.isRegularFile() }

    private fun Path.walkAll(): List<Path> = Files.walk(this).use { stream ->
        stream.asSequence().sorted().toList()
    }

    private fun Path.walkAllDescending(): List<Path> = Files.walk(this).use { stream ->
        stream.asSequence().sortedWith(Comparator.reverseOrder()).toList()
    }

    public data class Configuration(
        val unpackedKlibDir: File,
        val packedKlibFile: File,
        val targetPackageName: String,
        val annotationClassName: String,
        val annotationDependencyUniqueName: String,
    ) {
        val annotation: KmAnnotation = KmAnnotation(annotationClassName, emptyMap())
    }

    private class LinkdataLibraryProvider(
        private val linkdataDir: Path,
    ) : KlibModuleMetadata.MetadataLibraryProvider {
        override val moduleHeaderData: ByteArray =
            linkdataDir.resolve("module").readBytes()

        override fun packageMetadataParts(fqName: String): Set<String> =
            packagePartFiles(fqName, requireDirectory = false)
                .mapTo(LinkedHashSet()) { file -> file.name }

        override fun packageMetadata(fqName: String, partName: String): ByteArray =
            linkdataDir.resolve(packageDirectoryName(fqName)).resolve(partName).readBytes()

        fun existingPartNames(packageName: String): List<String> =
            packagePartFiles(packageName, requireDirectory = true).map { file -> file.name }

        fun packageDirectoryName(packageName: String): String = "package_$packageName"

        private fun packagePartFiles(
            packageName: String,
            requireDirectory: Boolean,
        ): List<Path> {
            val packageDir = linkdataDir.resolve(packageDirectoryName(packageName))
            if (!packageDir.isDirectory()) {
                check(!requireDirectory) {
                    "Missing package metadata directory for $packageName: ${packageDir.toAbsolutePath()}"
                }
                return emptyList()
            }

            return Files.list(packageDir).use { stream ->
                stream.asSequence()
                    .filter { path -> path.isRegularFile() }
                    .sortedBy { path -> path.name }
                    .toList()
            }
        }
    }
}
