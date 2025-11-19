/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protoc

import kotlinx.rpc.protoc.ProtocPlugin.Companion.GRPC_KOTLIN_MULTIPLATFORM
import kotlinx.rpc.protoc.ProtocPlugin.Companion.KOTLIN_MULTIPLATFORM
import kotlinx.rpc.buf.tasks.BufGenerateTask
import kotlinx.rpc.util.OS
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.listProperty
import org.gradle.kotlin.dsl.mapProperty
import org.gradle.kotlin.dsl.property
import java.io.File

/**
 * Access to the `kotlin-multiplatform` protoc plugin.
 */
public val NamedDomainObjectContainer<ProtocPlugin>.kotlinMultiplatform: NamedDomainObjectProvider<ProtocPlugin>
    get() = named(KOTLIN_MULTIPLATFORM)

/**
 * Configures the `kotlin-multiplatform` protoc plugin.
 */
public fun NamedDomainObjectContainer<ProtocPlugin>.kotlinMultiplatform(action: Action<ProtocPlugin>) {
    kotlinMultiplatform.configure(action)
}

/**
 * Access to the `grpc-kotlin-multiplatform` protoc plugin.
 */
public val NamedDomainObjectContainer<ProtocPlugin>.grpcKotlinMultiplatform: NamedDomainObjectProvider<ProtocPlugin>
    get() = named(GRPC_KOTLIN_MULTIPLATFORM)

/**
 * Configures the `grpc-kotlin-multiplatform` protoc plugin.
 */
public fun NamedDomainObjectContainer<ProtocPlugin>.grpcKotlinMultiplatform(action: Action<ProtocPlugin>) {
    grpcKotlinMultiplatform.configure(action)
}

/**
 * Access to a specific protoc plugin.
 */
public open class ProtocPlugin internal constructor(
    public val name: String,
    internal val project: Project,
) {
    /**
     * Whether the plugin generates Java code.
     *
     * Plugins that have this property set to `true` will have their output directory
     * added to the source set's `java` source directory set if present.
     */
    public val isJava: Property<Boolean> = project.objects.property<Boolean>().convention(false)

    /**
     * Protoc plugins options.
     *
     * @see <a href="https://buf.build/docs/configuration/v2/buf-gen-yaml/#opt">Buf documentation - opt</a>
     */
    public val options: MapProperty<String, Any> = project.objects
        .mapProperty<String, Any>()
        .convention(emptyMap())

    /**
     * Local protoc plugin artifact.
     *
     * @see <a href="https://buf.build/docs/configuration/v2/buf-gen-yaml/#type-of-plugin">
     *     Buf documentation - Type of plugin
     * </a>
     */
    public fun local(action: Action<Artifact.Local>) {
        artifact.set(Artifact.Local(project).apply(action::execute))
    }

    /**
     * Remote protoc plugin artifact.
     *
     * @see <a href="https://buf.build/docs/configuration/v2/buf-gen-yaml/#type-of-plugin">
     *     Buf documentation - Type of plugin
     * </a>
     */
    public fun remote(action: Action<Artifact.Remote>) {
        artifact.set(Artifact.Remote(project).apply(action::execute))
    }

    /**
     * Protoc plugin artifact.
     *
     * Can be either [Artifact.Local] or [Artifact.Remote].
     *
     * @see <a href="https://buf.build/docs/configuration/v2/buf-gen-yaml/#type-of-plugin">
     *     Buf documentation - Type of plugin
     * </a>
     */
    public val artifact: Property<Artifact> = project.objects.property<Artifact>()

    /**
     * Strategy for this protoc plugin.
     *
     * Optional.
     * Default is Buf's default.
     *
     * @see <a href="https://buf.build/docs/configuration/v2/buf-gen-yaml/#strategy">Buf documentation - strategy</a>
     */
    public val strategy: Property<Strategy?> = project.objects.property<Strategy?>().convention(null)

    /**
     * Whether to include imports except for Well-Known Types.
     *
     * Optional.
     * Default is Buf's default.
     *
     * @see <a href="https://buf.build/docs/configuration/v2/buf-gen-yaml/#include_imports">
     *     Buf documentation - include_imports
     * </a>
     */
    public val includeImports: Property<Boolean?> = project.objects.property<Boolean?>().convention(null)

    /**
     * Whether to include Well-Known Types.
     *
     * Optional.
     * Default is Buf's default.
     *
     * @see <a href="https://buf.build/docs/configuration/v2/buf-gen-yaml/#include_wkt">
     *     Buf documentation - include_wkt
     * </a>
     */
    public val includeWkt: Property<Boolean?> = project.objects.property<Boolean?>().convention(null)

    /**
     * Include only the specified types when generating with this plugin.
     *
     * Optional.
     *
     * @see <a href="https://buf.build/docs/configuration/v2/buf-gen-yaml/#types">
     *     Buf documentation - types
     * </a>
     */
    public val types: ListProperty<String> = project.objects.listProperty()

    /**
     * Exclude the specified types when generating with this plugin.
     *
     * Optional.
     *
     * @see <a href="https://buf.build/docs/configuration/v2/buf-gen-yaml/#exclude-types">
     *     Buf documentation - exclude-types
     * </a>
     */
    public val excludeTypes: ListProperty<String> = project.objects.listProperty()

    public companion object {
        /**
         * The name of the `kotlin-multiplatform` protoc plugin.
         *
         * @see [kotlinMultiplatform]
         */
        public const val KOTLIN_MULTIPLATFORM: String = "kotlin-multiplatform"

        /**
         * The name of the `grpc-kotlin-multiplatform` protoc plugin.
         *
         * @see [grpcKotlinMultiplatform]
         */
        public const val GRPC_KOTLIN_MULTIPLATFORM: String = "grpc-kotlin-multiplatform"
    }

    /**
     * Strategy for a protoc plugin.
     *
     * @see [strategy]
     */
    public enum class Strategy {
        Directory, All;
    }

    /**
     * Artifact for a protoc plugin.
     */
    public sealed class Artifact {
        /**
         * Local protoc plugin artifact.
         *
         * Local artifact is defined by a list of command-line arguments that execute the plugin - [executor]
         *
         * @see <a href="https://buf.build/docs/configuration/v2/buf-gen-yaml/#type-of-plugin">
         *     Buf documentation - Type of plugin
         * </a>
         */
        public class Local(private val project: Project) : Artifact() {
            /**
             * Command-line arguments that execute the plugin.
             */
            public val executor: ListProperty<String> = project.objects.listProperty()

            /**
             * Specifies the files that will be used during execution by the plugin.
             *
             * Useful to properly handle caching in [BufGenerateTask].
             *
             * Automatically includes the plugin's .jar file if [javaJar] was used.
             *
             * Optional.
             *
             * @see [BufGenerateTask.executableFiles]
             */
            public val executableFiles: ListProperty<File> = project.objects.listProperty<File>()
                .convention(emptyList())

            /**
             * Command-line arguments that execute the plugin.
             */
            public fun executor(elements: Provider<List<String>>) {
                executor.set(elements)
            }

            /**
             * Command-line arguments that execute the plugin.
             */
            public fun executor(vararg elements: String) {
                executor.set(elements.toList())
            }

            /**
             * Protoc plugin jar file path.
             *
             * If [executablePath] is not specified, the jar will be executed with Java used for the Gradle build.
             */
            public fun javaJar(jarPath: Provider<String>, executablePath: Provider<String>? = null) {
                executableFiles.add(jarPath.map { project.file(it) })

                if (executablePath == null) {
                    executor(jarPath.map { listOf(OS.javaExePath, "-jar", it) })
                    return
                }

                val list = jarPath.zip(executablePath) { jar, exe ->
                    listOf(exe, "-jar", jar)
                }

                executor(list)
            }

            /**
             * Protoc plugin jar file path.
             *
             * Uses default Java executable, the same as for the Gradle build.
             */
            public fun javaJar(jarPath: String) {
                javaJar(project.provider { jarPath })
            }
        }

        /**
         * Remote protoc plugin artifact.
         *
         * Locator is a BSR Url.
         *
         * @see <a href="https://buf.build/docs/configuration/v2/buf-gen-yaml/#type-of-plugin">
         *     Buf documentation - Type of plugin
         * </a>
         */
        public class Remote(project: Project) : Artifact() {
            public val locator: Property<String> = project.objects.property()
        }
    }

    // todo check if coping works
    internal fun copy(): ProtocPlugin {
        return ProtocPlugin(name, project)
            .also {
                it.isJava.set(isJava)
                it.options.set(options)
                it.artifact.set(artifact)
                it.strategy.set(strategy)
                it.includeImports.set(includeImports)
                it.includeWkt.set(includeWkt)
                it.types.set(types)
                it.excludeTypes.set(excludeTypes)
            }
    }
}
