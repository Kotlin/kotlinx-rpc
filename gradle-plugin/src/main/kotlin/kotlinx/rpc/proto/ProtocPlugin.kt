/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.proto

import kotlinx.rpc.proto.ProtocPlugin.Companion.GRPC_JAVA
import kotlinx.rpc.proto.ProtocPlugin.Companion.GRPC_KOTLIN
import kotlinx.rpc.proto.ProtocPlugin.Companion.KOTLIN_MULTIPLATFORM
import kotlinx.rpc.proto.ProtocPlugin.Companion.PROTOBUF_JAVA
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
 * Access to the `protobuf-java` protoc plugin.
 */
public val NamedDomainObjectContainer<ProtocPlugin>.protobufJava: NamedDomainObjectProvider<ProtocPlugin>
    get() = named(PROTOBUF_JAVA)

/**
 * Configures the `protobuf-java` protoc plugin.
 */
public fun NamedDomainObjectContainer<ProtocPlugin>.protobufJava(action: Action<ProtocPlugin>) {
    protobufJava.configure(action)
}

/**
 * Access to the `grpc-java` protoc plugin.
 */
public val NamedDomainObjectContainer<ProtocPlugin>.grpcJava: NamedDomainObjectProvider<ProtocPlugin>
    get() = named(GRPC_JAVA)

/**
     * Configures the grpc-java protoc plugin.
 */
public fun NamedDomainObjectContainer<ProtocPlugin>.grpcJava(action: Action<ProtocPlugin>) {
    grpcJava.configure(action)
}

/**
 * Access to the `grpc-kotlin` protoc plugin.
 */
public val NamedDomainObjectContainer<ProtocPlugin>.grpcKotlin: NamedDomainObjectProvider<ProtocPlugin>
    get() = named(GRPC_KOTLIN)

/**
 * Configures the `grpc-kotlin` protoc plugin.
 */
public fun NamedDomainObjectContainer<ProtocPlugin>.grpcKotlin(action: Action<ProtocPlugin>) {
    grpcKotlin.configure(action)
}

/**
 * Access to a specific protoc plugin.
 */
public open class ProtocPlugin(
    public val name: String,
    private val project: Project,
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
    public val options: MapProperty<String, Any?> = project.objects
        .mapProperty<String, Any?>()
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
         * The name of the `protobuf-java` protoc plugin.
         *
         * @see [protobufJava]
         */
        public const val PROTOBUF_JAVA: String = "java"

        /**
         * The name of the `grpc-java` protoc plugin.
         *
         * @see [grpcJava]
         */
        public const val GRPC_JAVA: String = "grpc-java"

        /**
         * The name of the `grpc-kotlin` protoc plugin.
         *
         * @see [grpcKotlin]
         */
        public const val GRPC_KOTLIN: String = "grpc-kotlin"
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
}
