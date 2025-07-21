/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc

import kotlinx.rpc.GRPC_KOTLIN_VERSION
import kotlinx.rpc.GRPC_VERSION
import kotlinx.rpc.PROTOBUF_VERSION
import kotlinx.rpc.buf.BufExtension
import kotlinx.rpc.proto.ProtocPlugin.Companion.GRPC_JAVA
import kotlinx.rpc.proto.ProtocPlugin.Companion.GRPC_KOTLIN
import kotlinx.rpc.proto.ProtocPlugin.Companion.KXRPC
import kotlinx.rpc.proto.ProtocPlugin.Companion.PROTOBUF_JAVA
import kotlinx.rpc.buf.configureBufExecutable
import kotlinx.rpc.buf.tasks.BufGenerateTask
import kotlinx.rpc.buf.tasks.registerBufGenYamlUpdateTask
import kotlinx.rpc.buf.tasks.registerBufGenerateTask
import kotlinx.rpc.buf.tasks.registerBufYamlUpdateTask
import kotlinx.rpc.proto.KXRPC_PLUGIN_JAR_CONFIGURATION
import kotlinx.rpc.proto.ProtoSourceSet
import kotlinx.rpc.proto.ProtocPlugin
import kotlinx.rpc.proto.configureKxRpcPluginJarConfiguration
import kotlinx.rpc.proto.configureProtoExtensions
import kotlinx.rpc.proto.grpcJava
import kotlinx.rpc.proto.grpcKotlin
import kotlinx.rpc.proto.kxrpc
import kotlinx.rpc.proto.protoBuildDirGenerated
import kotlinx.rpc.proto.protoBuildDirSourceSets
import kotlinx.rpc.proto.protoSourceSets
import kotlinx.rpc.proto.protobufJava
import kotlinx.rpc.proto.registerProcessProtoFilesTask
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

public open class GrpcExtension @Inject constructor(
    objects: ObjectFactory,
    private val project: Project,
) {
    public val protocPlugins: NamedDomainObjectContainer<ProtocPlugin> =
        objects.domainObjectContainer(ProtocPlugin::class.java) { name ->
            ProtocPlugin(name, project)
        }

    public fun protocPlugins(action: Action<NamedDomainObjectContainer<ProtocPlugin>>) {
        action.execute(protocPlugins)
    }

    public val buf: BufExtension = project.objects.newInstance<BufExtension>()
    public fun buf(action: Action<BufExtension>) {
        action.execute(buf)
    }

    init {
        project.configureBufExecutable()
        project.configureKxRpcPluginJarConfiguration()

        createDefaultProtocPlugins()

        project.configureProtoExtensions { _, _, protoSourceSet ->
            protoSourceSet.protocPlugin(protocPlugins.protobufJava)
            protoSourceSet.protocPlugin(protocPlugins.grpcJava)
            protoSourceSet.protocPlugin(protocPlugins.grpcKotlin)
            protoSourceSet.protocPlugin(protocPlugins.kxrpc)
        }

        project.afterEvaluate {
            project.protoSourceSets.forEach { sourceSet ->
                configureTasks(sourceSet)
            }
        }
    }

    private fun Project.configureTasks(protoSourceSet: ProtoSourceSet) {
        val baseName = protoSourceSet.baseName.get()
        val baseGenDir = project.protoBuildDirSourceSets.resolve(baseName)

        val protocPluginNames = protoSourceSet.collectProtocPlugins().distinct()

        val includedProtocPlugins = protocPluginNames.map {
            protocPlugins.findByName(it)
                ?: throw GradleException("Protoc plugin $it not found")
        }

        val protoFiles = protoSourceSet.proto
        val hasFiles = !protoFiles.isEmpty

        val bufGenUpdateTask = project.registerBufGenYamlUpdateTask(
            name = protoSourceSet.name,
            dir = baseName,
            protocPlugins = includedProtocPlugins,
        ) {
            onlyIf { hasFiles }
        }

        val bufUpdateTask = project.registerBufYamlUpdateTask(protoSourceSet.name, baseName) {
            onlyIf { hasFiles }
        }

        val processProtoTask = project.registerProcessProtoFilesTask(
            name = protoSourceSet.name,
            baseGenDir = project.provider { baseGenDir },
            protoFiles = protoFiles,
        ) {
            onlyIf { hasFiles }

            dependsOn(bufGenUpdateTask)
            dependsOn(bufUpdateTask)
        }

        val out = project.protoBuildDirGenerated.resolve(baseName)

        val capitalName = protoSourceSet.name.replaceFirstChar { it.uppercase() }
        val bufGenerateTask = project.registerBufGenerateTask(
            name = "${BufGenerateTask.NAME_PREFIX}$capitalName",
            workingDir = provider { baseGenDir },
            inputFiles = processProtoTask.map { it.outputs.files },
            outputDirectory = provider { out },
        ) {
            dependsOn(bufGenUpdateTask)
            dependsOn(bufUpdateTask)
            dependsOn(processProtoTask)

            onlyIf { hasFiles }
        }

        project.tasks.withType<KotlinCompile>().configureEach {
            // compileKotlin - main
            // compileTestKotlin - test
            // compileKotlinJvm - jvmMain
            // compileTestKotlinJvm - jvmTest
            // compileKotlinIosArm64 - iosArm64Main
            // compileTestKotlinIosArm64 - iosArm64Test
            val taskNameAsSourceSet = name
                .removePrefix("compile").let {
                    val suffix = it.substringBefore("Kotlin").takeIf {
                        prefix -> prefix.isNotEmpty()
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

        project.tasks.withType<JavaCompile>().configureEach {
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
            val javaSourceSet = project.extensions.findByType<JavaPluginExtension>()
                ?.sourceSets?.findByName(baseName)?.java

            if (plugin.isJava.get() && javaSourceSet != null) {
                javaSourceSet.srcDirs(out.resolve(plugin.name))
            } else {
                protoSourceSet.languageSourceSets.get().find { it is KotlinSourceSet }?.let {
                    (it as KotlinSourceSet)
                        .kotlin.srcDirs(out.resolve(plugin.name))
                } ?: error(
                    "Unable to find fitting source directory set for plugin '${plugin.name}' in '$protoSourceSet' source set"
                )
            }
        }
    }

    private fun createDefaultProtocPlugins() {
        protocPlugins.create(KXRPC) {
            local {
                javaJar(project.configurations.named(KXRPC_PLUGIN_JAR_CONFIGURATION).map { it.singleFile.absolutePath })
            }

            options.put("debugOutput", "protobuf-kxrpc-plugin.log")
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

    private fun ProtoSourceSet.collectProtocPlugins(): List<String> {
        return when {
            name.endsWith("Main") -> {
                protocPlugins.get()
            }

            name.endsWith("Test") -> {
                val main = project.protoSourceSets
                    .getByName(correspondingMainName())

                main.collectProtocPlugins()  + protocPlugins.get()
            }

            else -> throw GradleException("Unknown source set name: $name")
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
