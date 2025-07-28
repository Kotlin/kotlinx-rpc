/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc

import kotlinx.rpc.GRPC_KOTLIN_VERSION
import kotlinx.rpc.GRPC_VERSION
import kotlinx.rpc.PROTOBUF_VERSION
import kotlinx.rpc.buf.BufExtension
import kotlinx.rpc.buf.configureBufExecutable
import kotlinx.rpc.buf.tasks.registerBufExecTask
import kotlinx.rpc.buf.tasks.registerBufGenerateTask
import kotlinx.rpc.buf.tasks.registerGenerateBufGenYamlTask
import kotlinx.rpc.buf.tasks.registerGenerateBufYamlTask
import kotlinx.rpc.proto.*
import kotlinx.rpc.proto.ProtocPlugin.Companion.GRPC_JAVA
import kotlinx.rpc.proto.ProtocPlugin.Companion.GRPC_KOTLIN
import kotlinx.rpc.proto.ProtocPlugin.Companion.KOTLIN_MULTIPLATFORM
import kotlinx.rpc.proto.ProtocPlugin.Companion.PROTOBUF_JAVA
import kotlinx.rpc.util.ensureDirectoryExists
import org.gradle.api.Action
import org.gradle.api.GradleException
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.newInstance
import org.gradle.kotlin.dsl.the
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.dsl.KotlinBaseExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import javax.inject.Inject

internal open class DefaultGrpcExtension @Inject constructor(
    objects: ObjectFactory,
    private val project: Project,
) : GrpcExtension {
    override val protocPlugins: NamedDomainObjectContainer<ProtocPlugin> =
        objects.domainObjectContainer(ProtocPlugin::class.java) { name ->
            ProtocPlugin(name, project)
        }

    override fun protocPlugins(action: Action<NamedDomainObjectContainer<ProtocPlugin>>) {
        action.execute(protocPlugins)
    }

    override val buf: BufExtension = project.objects.newInstance<BufExtension>()
    override fun buf(action: Action<BufExtension>) {
        action.execute(buf)
    }

    init {
        project.configureBufExecutable()
        project.configureKotlinMultiplatformPluginJarConfiguration()

        createDefaultProtocPlugins()

        project.protoSourceSets.forEach { protoSourceSet ->
            protoSourceSet.protocPlugin(protocPlugins.protobufJava)
            protoSourceSet.protocPlugin(protocPlugins.grpcJava)
            protoSourceSet.protocPlugin(protocPlugins.grpcKotlin)
            protoSourceSet.protocPlugin(protocPlugins.kotlinMultiplatform)
        }

        project.afterEvaluate {
            project.protoSourceSets.forEach { sourceSet ->
                if (sourceSet !is DefaultProtoSourceSet) {
                    return@forEach
                }

                configureTasks(sourceSet)
            }
        }
    }

    @Suppress("detekt.LongMethod", "detekt.CyclomaticComplexMethod")
    private fun Project.configureTasks(protoSourceSet: DefaultProtoSourceSet) {
        val baseName = protoSourceSet.name

        val buildSourceSetsDir = project.protoBuildDirSourceSets.resolve(baseName)
            .ensureDirectoryExists()

        val buildSourceSetsProtoDir = buildSourceSetsDir.resolve(PROTO_FILES_DIR)
            .ensureDirectoryExists()

        val buildSourceSetsImportDir = buildSourceSetsDir.resolve(PROTO_FILES_IMPORT_DIR)
            .ensureDirectoryExists()

        val pairSourceSet = protoSourceSet.correspondingMainSourceSetOrNull()

        val mainProtocPlugins = pairSourceSet?.protocPlugins?.get().orEmpty()
        val protocPluginNames = (protoSourceSet.protocPlugins.get() + mainProtocPlugins).distinct()

        val includedProtocPlugins = protocPluginNames.map {
            protocPlugins.findByName(it)
                ?: throw GradleException("Protoc plugin $it not found")
        }

        val protoFiles = protoSourceSet.proto

        val generateBufYamlTask = registerGenerateBufYamlTask(
            name = baseName,
            buildSourceSetsDir = buildSourceSetsDir,
            buildSourceSetsProtoDir = buildSourceSetsProtoDir,
            buildSourceSetsImportDir = buildSourceSetsImportDir,
            withImport = pairSourceSet != null,
        )

        val generateBufGenYamlTask = registerGenerateBufGenYamlTask(
            name = baseName,
            buildSourceSetsDir = buildSourceSetsDir,
            protocPlugins = includedProtocPlugins,
        ) {
            dependsOn(generateBufYamlTask)
        }

        val processProtoTask = registerProcessProtoFilesTask(
            name = baseName,
            destination = buildSourceSetsProtoDir,
            protoFiles = protoFiles,
        ) {
            dependsOn(generateBufYamlTask)
            dependsOn(generateBufGenYamlTask)
        }

        val processImportProtoTask = if (pairSourceSet != null) {
            val importProtoFiles = pairSourceSet.proto

            registerProcessProtoFilesTask(
                name = "${baseName}Import",
                destination = buildSourceSetsImportDir,
                protoFiles = importProtoFiles,
            ) {
                dependsOn(generateBufYamlTask)
                dependsOn(generateBufGenYamlTask)
                dependsOn(processProtoTask)
            }
        } else {
            null
        }

        val out = protoBuildDirGenerated.resolve(baseName)

        val destinationFileTree = fileTree(buildSourceSetsProtoDir)

        val bufGenerateTask = registerBufGenerateTask(
            name = baseName,
            workingDir = buildSourceSetsDir,
            outputDirectory = out,
            protoFilesDir = buildSourceSetsProtoDir,
            importFilesDir = buildSourceSetsImportDir,
        ) {
            dependsOn(generateBufGenYamlTask)
            dependsOn(generateBufYamlTask)
            dependsOn(processProtoTask)
            if (processImportProtoTask != null) {
                dependsOn(processImportProtoTask)
            }

            if (pairSourceSet != null) {
                dependsOn(pairSourceSet.generateTask)
            }

            onlyIf { !destinationFileTree.filter { it.extension == "proto" }.isEmpty }
        }

        protoSourceSet.generateTask.set(bufGenerateTask)

        tasks.withType<KotlinCompile>().configureEach {
            // compileKotlin - main
            // compileTestKotlin - test
            // compileKotlinJvm - jvmMain
            // compileTestKotlinJvm - jvmTest
            // compileKotlinIosArm64 - iosArm64Main
            // compileTestKotlinIosArm64 - iosArm64Test
            val taskNameAsSourceSet = name
                .removePrefix("compile").let {
                    val suffix = it.substringBefore("Kotlin").takeIf { prefix ->
                        prefix.isNotEmpty()
                    } ?: "Main"

                    (it.substringAfter("Kotlin")
                        .replaceFirstChar { ch -> ch.lowercase() } + suffix)
                        .takeIf { result -> result != suffix }
                        ?: suffix.lowercase()
                }

            if (taskNameAsSourceSet == baseName) {
                dependsOn(bufGenerateTask)
            }
        }

        tasks.withType<JavaCompile>().configureEach {
            // compileJvmTestJava - test (java, kmp)
            // compileJvmMainJava - main (java, kmp)
            // compileJava - main (java)
            // compileTestJava - test (java)
            val taskNameAsSourceSet = when (name) {
                "compileJvmTestJava" -> "test"
                "compileJvmMainJava" -> "main"
                "compileJava" -> "main"
                "compileTestJava" -> "test"

                else -> throw GradleException("Unknown java compile task name: $name")
            }

            if (taskNameAsSourceSet == baseName) {
                dependsOn(bufGenerateTask)
            }
        }

        includedProtocPlugins.forEach { plugin ->
            // locates correctly jvmMain, main jvmTest, test
            val javaSourceSet = extensions.findByType<JavaPluginExtension>()
                ?.sourceSets?.findByName(baseName)?.java

            if (plugin.isJava.get() && javaSourceSet != null) {
                javaSourceSet.srcDirs(out.resolve(plugin.name))
            } else {
                protoSourceSet.languageSourceSets.get().find { it is KotlinSourceSet }?.let {
                    (it as KotlinSourceSet)
                        .kotlin.srcDirs(out.resolve(plugin.name))
                } ?: error(
                    "Unable to find fitting source directory set " +
                            "for plugin '${plugin.name}' in '$protoSourceSet' proto source set"
                )
            }
        }

        val baseCapital = baseName.replaceFirstChar { it.uppercase() }
        buf.tasks.customTasks.get().forEach { definition ->
            val capital = definition.name.replaceFirstChar { it.uppercase() }
            val taskName = "buf$capital$baseCapital"

            val customTask = registerBufExecTask(
                clazz = definition.kClass,
                workingDir = provider { buildSourceSetsDir },
                name = taskName,
            ) {
                dependsOn(generateBufYamlTask)
                dependsOn(generateBufGenYamlTask)
                dependsOn(processProtoTask)
                if (processImportProtoTask != null) {
                    dependsOn(processImportProtoTask)
                }

                onlyIf { !destinationFileTree.filter { it.extension == "proto" }.isEmpty }
            }

            when {
                baseName.lowercase().endsWith("main") -> {
                    definition.property.mainTask.set(customTask)
                }

                baseName.lowercase().endsWith("test") -> {
                    definition.property.testTask.set(customTask)
                }

                else -> {
                    throw GradleException("Unknown source set name: $baseName")
                }
            }
        }
    }

    private fun createDefaultProtocPlugins() {
        protocPlugins.create(KOTLIN_MULTIPLATFORM) {
            local {
                javaJar(project.kotlinMultiplatformProtocPluginJarPath)
            }

            options.put("debugOutput", "protoc-gen-kotlin-multiplatform.log")
            options.put("messageMode", "interface")
            options.put("explicitApiModeEnabled", project.provider {
                project.the<KotlinBaseExtension>().explicitApi != ExplicitApiMode.Disabled
            })
        }

        protocPlugins.create(GRPC_JAVA) {
            isJava.set(true)

            remote {
                locator.set("buf.build/grpc/java:v$GRPC_VERSION")
            }
        }

        protocPlugins.create(GRPC_KOTLIN) {
            remote {
                locator.set("buf.build/grpc/kotlin:v$GRPC_KOTLIN_VERSION")
            }
        }

        protocPlugins.create(PROTOBUF_JAVA) {
            isJava.set(true)

            remote {
                // for some reason they omit the first digit in this version:
                // https://buf.build/protocolbuffers/java?version=v31.1
                locator.set("buf.build/protocolbuffers/java:v${PROTOBUF_VERSION.substringAfter(".")}")
            }
        }
    }

    private fun DefaultProtoSourceSet.correspondingMainSourceSetOrNull(): DefaultProtoSourceSet? {
        return when {
            name.lowercase().endsWith("main") -> {
                null
            }

            name.lowercase().endsWith("test") -> {
                project.protoSourceSets.findByName(correspondingMainName()) as? DefaultProtoSourceSet
            }

            else -> {
                throw GradleException("Unknown source set name: $name")
            }
        }
    }

    private fun ProtoSourceSet.correspondingMainName(): String {
        return when {
            name == "test" -> "main"
            name.endsWith("Test") -> name.removeSuffix("Test") + "Main"
            else -> throw GradleException("Unknown test source set name: $name")
        }
    }
}
