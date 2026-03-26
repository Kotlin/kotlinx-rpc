/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.nativedeps.tooling;

import kotlin.metadata.KmAnnotation;
import kotlin.metadata.KmClass;
import kotlin.metadata.KmConstructor;
import kotlin.metadata.KmFunction;
import kotlin.metadata.KmPackage;
import kotlin.metadata.KmProperty;
import kotlin.metadata.KmTypeAlias;
import kotlin.metadata.internal.common.KmModuleFragment;
import kotlinx.metadata.klib.KlibModuleFragmentReadStrategy;
import kotlinx.metadata.klib.KlibModuleFragmentWriteStrategy;
import kotlinx.metadata.klib.KlibModuleMetadata;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Patches the produced protobuf-shim cinterop KLIB after cinterop finishes.
 *
 * <p>protobuf-shim is published as a KLIB because the build needs it, but the declarations generated into the
 * {@code kotlinx.rpc.protobuf.internal.cinterop} package are internal implementation detail, not supported public API. The cinterop
 * toolchain does not load compiler plugin registrars on this build path, so the narrow reliable hook is to rewrite
 * the produced KLIB metadata immediately after cinterop and before the artifact is consumed or published.
 */
public final class KlibPatcher {
    private static final String TARGET_PACKAGE_NAME = "kotlinx.rpc.protobuf.internal.cinterop";
    private static final String INTERNAL_NATIVE_RPC_API_ANNOTATION_CLASS_NAME =
        "kotlinx/rpc/protobuf/internal/InternalNativeRpcApi";
    private static final String INTERNAL_NATIVE_RPC_API_DEPENDENCY_UNIQUE_NAME =
        "org.jetbrains.kotlinx\\:kotlinx-rpc-protobuf-shim-annotation";
    private static final KmAnnotation INTERNAL_NATIVE_RPC_API_ANNOTATION =
        new KmAnnotation(INTERNAL_NATIVE_RPC_API_ANNOTATION_CLASS_NAME, Collections.emptyMap());
    private static final Method FRAGMENT_PACKAGE_NAME_ACCESSOR = createFragmentPackageNameAccessor();

    private KlibPatcher() {
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            throw new IllegalArgumentException("Expected <unpacked-klib-dir> <packed-klib-file>, got " + List.of(args));
        }

        patch(new File(args[0]), new File(args[1]));
    }

    public static void patch(File unpackedKlibDir, File packedKlibFile) {
        Path tempKlibDir = null;
        try {
            tempKlibDir = Files.createTempDirectory("protobuf-shim-internal-native-rpc-api-klib-");
            copyRecursively(unpackedKlibDir.toPath(), tempKlibDir);

            File archiveDir = resolveArchiveDir(tempKlibDir.toFile());
            File linkdataDir = new File(archiveDir, "linkdata");
            if (!linkdataDir.isDirectory()) {
                throw new IllegalStateException("Missing linkdata directory: " + linkdataDir.getAbsolutePath());
            }

            LinkdataLibraryProvider metadataProvider = new LinkdataLibraryProvider(linkdataDir);
            KlibModuleMetadata metadata = KlibModuleMetadata.Companion.read(
                metadataProvider,
                KlibModuleFragmentReadStrategy.Companion.getDEFAULT()
            );
            List<KmModuleFragment> targetFragments = metadata.getFragments()
                .stream()
                .filter(fragment -> TARGET_PACKAGE_NAME.equals(fragmentPackageName(fragment)))
                .collect(Collectors.toList());

            if (targetFragments.isEmpty()) {
                throw new IllegalStateException(
                    "Expected protobuf-shim cinterop metadata for package " + TARGET_PACKAGE_NAME + " in " +
                        unpackedKlibDir.getAbsolutePath()
                );
            }

            // annotate all kotlin fragments (constructors, functions, properties, type aliases) with
            // the @InternalNativeRpcApi annotation
            targetFragments.forEach(KlibPatcher::annotateFragment);

            writeLinkdata(
                linkdataDir,
                metadataProvider,
                metadata.write(KlibModuleFragmentWriteStrategy.Companion.getDEFAULT())
            );

            // add annotation as dependency in the manifest
            patchManifest(new File(archiveDir, "manifest"));
            // zips the new klib dir to the packed klib file
            repackKlib(tempKlibDir, archiveDir.toPath(), packedKlibFile.toPath());
        } catch (IOException exception) {
            throw new RuntimeException("Failed to patch " + packedKlibFile.getAbsolutePath(), exception);
        } finally {
            if (tempKlibDir != null) {
                deleteRecursively(tempKlibDir);
            }
        }
    }

    private static Method createFragmentPackageNameAccessor() {
        try {
            return Class.forName("kotlinx.metadata.klib.KlibModuleMetadataKt")
                .getDeclaredMethod("access$fqNameOrFail", KmModuleFragment.class);
        } catch (ClassNotFoundException | NoSuchMethodException exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private static File resolveArchiveDir(File unpackedKlibDir) {
        if (new File(unpackedKlibDir, "linkdata").isDirectory()) {
            return unpackedKlibDir;
        }
        if (new File(unpackedKlibDir, "default/linkdata").isDirectory()) {
            return new File(unpackedKlibDir, "default");
        }
        return unpackedKlibDir;
    }

    private static void annotateFragment(KmModuleFragment fragment) {
        KmPackage packageFragment = Objects.requireNonNull(
            fragment.getPkg(),
            "Expected package metadata for " + fragmentPackageName(fragment)
        );
        packageFragment.getFunctions().forEach(KlibPatcher::annotate);
        packageFragment.getProperties().forEach(KlibPatcher::annotate);
        packageFragment.getTypeAliases().forEach(KlibPatcher::annotate);
        fragment.getClasses().forEach(KlibPatcher::annotate);
    }

    private static void annotate(KmClass clazz) {
        addInternalNativeRpcApiMarker(clazz.getAnnotations());
        clazz.getConstructors().forEach(KlibPatcher::annotate);
        clazz.getFunctions().forEach(KlibPatcher::annotate);
        clazz.getProperties().forEach(KlibPatcher::annotate);
        clazz.getTypeAliases().forEach(KlibPatcher::annotate);
    }

    private static void annotate(KmConstructor constructor) {
        addInternalNativeRpcApiMarker(constructor.getAnnotations());
    }

    private static void annotate(KmFunction function) {
        addInternalNativeRpcApiMarker(function.getAnnotations());
    }

    private static void annotate(KmProperty property) {
        addInternalNativeRpcApiMarker(property.getAnnotations());
    }

    private static void annotate(KmTypeAlias typeAlias) {
        addInternalNativeRpcApiMarker(typeAlias.getAnnotations());
    }

    private static void addInternalNativeRpcApiMarker(List<KmAnnotation> annotations) {
        boolean present = annotations.stream()
            .anyMatch(annotation -> INTERNAL_NATIVE_RPC_API_ANNOTATION_CLASS_NAME.equals(annotation.getClassName()));
        if (!present) {
            annotations.add(INTERNAL_NATIVE_RPC_API_ANNOTATION);
        }
    }

    private static String fragmentPackageName(KmModuleFragment fragment) {
        try {
            return (String) FRAGMENT_PACKAGE_NAME_ACCESSOR.invoke(null, fragment);
        } catch (IllegalAccessException | InvocationTargetException exception) {
            throw new RuntimeException("Failed to resolve package name for KLIB fragment", exception);
        }
    }

    private static void writeLinkdata(
        File linkdataDir,
        LinkdataLibraryProvider metadataProvider,
        KlibModuleMetadata.SerializedKlibMetadata serializedMetadata
    ) throws IOException {
        Files.write(new File(linkdataDir, "module").toPath(), serializedMetadata.getHeader());

        List<String> fragmentNames = serializedMetadata.getFragmentNames();
        List<List<byte[]>> fragments = serializedMetadata.getFragments();

        for (int index = 0; index < fragmentNames.size(); index++) {
            String packageName = fragmentNames.get(index);
            List<byte[]> packageFragments = fragments.get(index);
            File packageDir = new File(linkdataDir, metadataProvider.packageDirectoryName(packageName));
            List<String> outputNames = metadataProvider.existingPartNames(packageName);
            if (outputNames.size() != packageFragments.size()) {
                outputNames = generatedPartNames(packageName, packageFragments.size());
            }

            deleteRecursively(packageDir.toPath());
            Files.createDirectories(packageDir.toPath());

            for (int partIndex = 0; partIndex < packageFragments.size(); partIndex++) {
                Files.write(new File(packageDir, outputNames.get(partIndex)).toPath(), packageFragments.get(partIndex));
            }
        }
    }

    private static List<String> generatedPartNames(String packageName, int fragmentCount) {
        String suffix = packageName.contains(".")
            ? packageName.substring(packageName.lastIndexOf('.') + 1)
            : packageName;
        if (suffix.isBlank()) {
            suffix = "root";
        }
        suffix = suffix.replace('.', '_');

        List<String> names = new ArrayList<>(fragmentCount);
        for (int index = 0; index < fragmentCount; index++) {
            names.add(index + "_" + suffix + ".knm");
        }
        return names;
    }

    private static void patchManifest(File manifestFile) throws IOException {
        if (!manifestFile.isFile()) {
            throw new IllegalStateException("Missing KLIB manifest: " + manifestFile.getAbsolutePath());
        }

        List<String> updatedLines = new ArrayList<>();
        for (String line : Files.readAllLines(manifestFile.toPath())) {
            if (!line.startsWith("depends=")) {
                updatedLines.add(line);
                continue;
            }

            List<String> dependencies = Stream.of(line.substring("depends=".length()).split(" "))
                .filter(dependency -> !dependency.isBlank())
                .collect(Collectors.toCollection(ArrayList::new));
            if (!dependencies.contains(INTERNAL_NATIVE_RPC_API_DEPENDENCY_UNIQUE_NAME)) {
                dependencies.add(INTERNAL_NATIVE_RPC_API_DEPENDENCY_UNIQUE_NAME);
            }
            updatedLines.add("depends=" + String.join(" ", dependencies));
        }

        Files.writeString(manifestFile.toPath(), String.join("\n", updatedLines) + "\n");
    }

    private static void repackKlib(Path unpackedKlibInputDir, Path archiveDir, Path packedKlibFile) throws IOException {
        Path archiveRoot = Objects.equals(archiveDir.getParent(), unpackedKlibInputDir)
            ? unpackedKlibInputDir
            : Objects.requireNonNull(archiveDir.getParent(), archiveDir + " must have a parent directory");

        Files.createDirectories(Objects.requireNonNull(packedKlibFile.getParent(), "Packed KLIB must have a parent"));

        try (ZipOutputStream zip = new ZipOutputStream(Files.newOutputStream(packedKlibFile))) {
            try (Stream<Path> paths = Files.walk(archiveDir)) {
                for (Path path : paths.filter(Files::isRegularFile).sorted().collect(Collectors.toList())) {
                    String entryName = archiveRoot.relativize(path).toString().replace(File.separatorChar, '/');
                    zip.putNextEntry(new ZipEntry(entryName));
                    Files.copy(path, zip);
                    zip.closeEntry();
                }
            }
        }
    }

    private static void copyRecursively(Path sourceDir, Path targetDir) throws IOException {
        try (Stream<Path> paths = Files.walk(sourceDir)) {
            for (Path sourcePath : paths.collect(Collectors.toList())) {
                Path relative = sourceDir.relativize(sourcePath);
                Path targetPath = targetDir.resolve(relative.toString());
                if (Files.isDirectory(sourcePath)) {
                    Files.createDirectories(targetPath);
                } else {
                    Files.createDirectories(Objects.requireNonNull(targetPath.getParent()));
                    Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }

    private static void deleteRecursively(Path path) {
        if (!Files.exists(path)) {
            return;
        }

        try (Stream<Path> paths = Files.walk(path)) {
            for (Path candidate : paths.sorted(Comparator.reverseOrder()).collect(Collectors.toList())) {
                Files.deleteIfExists(candidate);
            }
        } catch (IOException exception) {
            throw new RuntimeException("Failed to delete " + path, exception);
        }
    }

    private static final class LinkdataLibraryProvider implements KlibModuleMetadata.MetadataLibraryProvider {
        private final File linkdataDir;

        private LinkdataLibraryProvider(File linkdataDir) {
            this.linkdataDir = linkdataDir;
        }

        @Override
        public byte[] getModuleHeaderData() {
            try {
                return Files.readAllBytes(new File(linkdataDir, "module").toPath());
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }
        }

        @Override
        public Set<String> packageMetadataParts(String fqName) {
            return packagePartFiles(fqName, false).stream()
                .map(File::getName)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        }

        @Override
        public byte[] packageMetadata(String fqName, String partName) {
            try {
                return Files.readAllBytes(new File(new File(linkdataDir, packageDirectoryName(fqName)), partName).toPath());
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }
        }

        private List<String> existingPartNames(String packageName) {
            return packagePartFiles(packageName, true).stream().map(File::getName).collect(Collectors.toList());
        }

        private String packageDirectoryName(String packageName) {
            return "package_" + packageName;
        }

        private List<File> packagePartFiles(String packageName, boolean requireDirectory) {
            File packageDir = new File(linkdataDir, packageDirectoryName(packageName));
            if (!packageDir.isDirectory()) {
                if (requireDirectory) {
                    throw new IllegalStateException(
                        "Missing package metadata directory for " + packageName + ": " + packageDir.getAbsolutePath()
                    );
                }
                return List.of();
            }

            File[] files = packageDir.listFiles(File::isFile);
            if (files == null) {
                return List.of();
            }

            return Stream.of(files)
                .sorted(Comparator.comparing(File::getName))
                .collect(Collectors.toList());
        }
    }
}
