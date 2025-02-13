/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc

import com.google.protobuf.gradle.ExecutableLocator
import com.google.protobuf.gradle.GenerateProtoTask
import com.google.protobuf.gradle.ProtobufExtension
import org.gradle.api.Action
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.specs.Spec
import org.gradle.api.tasks.TaskCollection
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.property
import org.gradle.kotlin.dsl.the
import javax.inject.Inject

public open class GrpcExtension @Inject constructor(objects: ObjectFactory, private val project: Project) {
    /**
     * Determines whether the gRPC support is enabled in the project.
     *
     * Allows for additional configuration checks.
     */
    public val enabled: Property<Boolean> = objects.property<Boolean>().convention(false)

    /**
     * Access for [GrpcPlugin] for [LOCATOR_NAME] name.
     */
    public fun plugin(action: Action<GrpcPlugin>) {
        pluginAccess(action, LOCATOR_NAME)
    }

    /**
     * Access for [GrpcPlugin] for [GRPC_JAVA_LOCATOR_NAME] name.
     */
    public fun grpcJavaPlugin(action: Action<GrpcPlugin>) {
        pluginAccess(action, GRPC_JAVA_LOCATOR_NAME)
    }

    /**
     * Access for [GrpcPlugin] for [GRPC_KOTLIN_LOCATOR_NAME] name.
     */
    public fun grpcKotlinPlugin(action: Action<GrpcPlugin>) {
        pluginAccess(action, GRPC_KOTLIN_LOCATOR_NAME)
    }

    /**
     * Shortcut for
     *
     * ```kotlin
     * protobuf {
     *     generateProtoTasks.all().all { ... }
     * }
     * ```
     */
    public fun tasks(taskAction: Action<GenerateProtoTask>) {
        project.the<ProtobufExtension>().generateProtoTasks.all().all(taskAction)
    }

    /**
     * Shortcut for
     *
     * ```kotlin
     * protobuf {
     *     generateProtoTasks.all().matching { ... }
     * }
     * ```
     */
    public fun tasksMatching(spec: Spec<in GenerateProtoTask>): TaskCollection<GenerateProtoTask> {
        return project.the<ProtobufExtension>().generateProtoTasks.all().matching(spec)
    }

    private fun pluginAccess(action: Action<GrpcPlugin>, locatorName: String) {
        val extension = project.the<ProtobufExtension>()
        val plugin = object : GrpcPlugin {
            override fun options(options: Action<GenerateProtoTask.PluginOptions>) {
                extension.generateProtoTasks.all().all {
                    options.execute(plugins.maybeCreate(locatorName))
                }
            }

            override fun locator(locatorAction: Action<ExecutableLocator>) {
                extension.plugins {
                    locatorAction.execute(maybeCreate(locatorName))
                }
            }
        }

        action.execute(plugin)
    }

    public companion object {
        /**
         * [com.google.protobuf.gradle.ExecutableLocator]'s name for the `kotlinx-rpc` plugin
         *
         * ```kotlin
         * protobuf {
         *     plugins {
         *         named(LOCATOR_NAME)
         *     }
         * }
         * ```
         *
         * Same name is used for [GenerateProtoTask] plugin in `generateProtoTasks.plugins`
         */
        public const val LOCATOR_NAME: String = "kotlinx-rpc"

        /**
         * [com.google.protobuf.gradle.ExecutableLocator]'s name for the `grpc-java` plugin
         *
         * ```kotlin
         * protobuf {
         *     plugins {
         *         named(GRPC_JAVA_LOCATOR_NAME)
         *     }
         * }
         * ```
         *
         * Same name is used for [GenerateProtoTask] plugin in `generateProtoTasks.plugins`
         */
        public const val GRPC_JAVA_LOCATOR_NAME: String = "grpc"

        /**
         * [com.google.protobuf.gradle.ExecutableLocator]'s name for the `grpc-kotlin` plugin
         *
         * ```kotlin
         * protobuf {
         *     plugins {
         *         named(GRPC_KOTLIN_LOCATOR_NAME)
         *     }
         * }
         * ```
         *
         * Same name is used for [GenerateProtoTask] plugin in `generateProtoTasks.plugins`
         */
        public const val GRPC_KOTLIN_LOCATOR_NAME: String = "grpckt"
    }
}

/**
 * Access to a specific protobuf plugin.
 */
public interface GrpcPlugin {
    /**
     * Access for [GenerateProtoTask.PluginOptions]
     *
     * ```kotlin
     * rpc {
     *     grpc {
     *         plugin {
     *             options {
     *                 option("option=value")
     *             }
     *         }
     *     }
     * }
     * ```
     */
    public fun options(optionsAction: Action<GenerateProtoTask.PluginOptions>)

    /**
     * Access for [ExecutableLocator]
     *
     * ```kotlin
     * rpc {
     *     grpc {
     *         plugin {
     *             locator {
     *                 path = "$buildDirPath/libs/protobuf-plugin-$version-all.jar"
     *             }
     *         }
     *     }
     * }
     * ```
     */
    public fun locator(locatorAction: Action<ExecutableLocator>)
}

internal fun Project.configureGrpc() {
    val grpc = rpcExtension().grpc
    var wasApplied = false

    pluginManager.withPlugin("com.google.protobuf") {
        if (wasApplied) {
            return@withPlugin
        }

        wasApplied = true

        val protobuf = extensions.findByType<ProtobufExtension>()
            ?: run {
                logger.error("Protobuf plugin (com.google.protobuf) was not applied. Please report of this issue.")
                return@withPlugin
            }

        protobuf.configureProtobuf(project = project)
    }

    afterEvaluate {
        if (grpc.enabled.get() && !wasApplied) {
            throw GradleException(
                """
                    gRPC Support is enabled, but 'com.google.protobuf' was not be applied during project evaluation.
                    The 'com.google.protobuf' plugin must be applied to the project first.
                """.trimIndent()
            )
        }
    }
}

private fun ProtobufExtension.configureProtobuf(project: Project) {
    val buildDirPath: String = project.layout.buildDirectory.get().asFile.absolutePath

    protoc {
        artifact = "com.google.protobuf:protoc:$PROTOBUF_VERSION"
    }

    plugins {
        val existed = findByName(GrpcExtension.LOCATOR_NAME) != null
        maybeCreate(GrpcExtension.LOCATOR_NAME).apply {
            if (!existed) {
                artifact = "org.jetbrains.kotlinx:kotlinx-rpc-protobuf-plugin:$LIBRARY_VERSION:all@jar"
            }
        }

        val grpcJavaPluginExisted = findByName(GrpcExtension.GRPC_JAVA_LOCATOR_NAME) != null
        maybeCreate(GrpcExtension.GRPC_JAVA_LOCATOR_NAME).apply {
            if (!grpcJavaPluginExisted) {
                artifact = "io.grpc:protoc-gen-grpc-java:$GRPC_VERSION"
            }
        }

        val grpcKotlinPluginExisted = findByName(GrpcExtension.GRPC_KOTLIN_LOCATOR_NAME) != null
        maybeCreate(GrpcExtension.GRPC_KOTLIN_LOCATOR_NAME).apply {
            if (!grpcKotlinPluginExisted) {
                artifact = "io.grpc:protoc-gen-grpc-kotlin:$GRPC_KOTLIN_VERSION:jdk8@jar"
            }
        }
    }

    generateProtoTasks {
        all().all {
            plugins {
                val existed = findByName(GrpcExtension.LOCATOR_NAME) != null
                maybeCreate(GrpcExtension.LOCATOR_NAME).apply {
                    if (!existed) {
                        option("debugOutput=$buildDirPath/protobuf-plugin.log")
                        option("messageMode=interface")
                    }
                }

                maybeCreate(GrpcExtension.GRPC_JAVA_LOCATOR_NAME)

                maybeCreate(GrpcExtension.GRPC_KOTLIN_LOCATOR_NAME)
            }
        }
    }
}
