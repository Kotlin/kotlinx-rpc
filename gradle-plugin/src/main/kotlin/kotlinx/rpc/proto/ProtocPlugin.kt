/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.proto

import kotlinx.rpc.buf.tasks.ResolvedGrpcPlugin
import kotlinx.rpc.proto.ProtocPlugin.Companion.GRPC_JAVA
import kotlinx.rpc.proto.ProtocPlugin.Companion.GRPC_KOTLIN
import kotlinx.rpc.proto.ProtocPlugin.Companion.KXRPC
import kotlinx.rpc.proto.ProtocPlugin.Companion.PROTOBUF_JAVA
import org.gradle.api.Action
import org.gradle.api.GradleException
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

public val NamedDomainObjectContainer<ProtocPlugin>.kxrpc: NamedDomainObjectProvider<ProtocPlugin>
    get() = named(KXRPC)

public fun NamedDomainObjectContainer<ProtocPlugin>.kxrpc(action: Action<ProtocPlugin>) {
    kxrpc.configure(action)
}

public val NamedDomainObjectContainer<ProtocPlugin>.protobufJava: NamedDomainObjectProvider<ProtocPlugin>
    get() = named(PROTOBUF_JAVA)

public fun NamedDomainObjectContainer<ProtocPlugin>.protobufJava(action: Action<ProtocPlugin>) {
    protobufJava.configure(action)
}

public val NamedDomainObjectContainer<ProtocPlugin>.grpcJava: NamedDomainObjectProvider<ProtocPlugin>
    get() = named(GRPC_JAVA)

public fun NamedDomainObjectContainer<ProtocPlugin>.grpcJava(action: Action<ProtocPlugin>) {
    grpcJava.configure(action)
}

public val NamedDomainObjectContainer<ProtocPlugin>.grpcKotlin: NamedDomainObjectProvider<ProtocPlugin>
    get() = named(GRPC_KOTLIN)

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
    public val isJava: Property<Boolean> = project.objects.property<Boolean>().convention(false)

    public val options: MapProperty<String, Any?> = project.objects
        .mapProperty<String, Any?>()
        .convention(emptyMap())

    public fun local(action: Action<Artifact.Local>) {
        artifact.set(Artifact.Local(project).apply(action::execute))
    }

    public fun remote(action: Action<Artifact.Remote>) {
        artifact.set(Artifact.Remote(project).apply(action::execute))
    }

    public val artifact: Property<Artifact> = project.objects.property<Artifact>()

    public val strategy: Property<Strategy?> = project.objects.property<Strategy?>().convention(null)
    public val includeImports: Property<Boolean?> = project.objects.property<Boolean?>().convention(null)
    public val includeWrk: Property<Boolean?> = project.objects.property<Boolean?>().convention(null)
    public val types: ListProperty<String> = project.objects.listProperty()
    public val excludeTypes: ListProperty<String> = project.objects.listProperty()

    public companion object {
        public const val KXRPC: String = "kotlinx-rpc"
        public const val PROTOBUF_JAVA: String = "java"
        public const val GRPC_JAVA: String = "grpc-java"
        public const val GRPC_KOTLIN: String = "grpc-kotlin"
    }

    public enum class Strategy {
        Directory, All;
    }

    public sealed class Artifact {
        internal abstract val type: ResolvedGrpcPlugin.Type

        public class Local(private val project: Project) : Artifact() {
            public val executor: ListProperty<String> = project.objects.listProperty()
            public fun executor(elements: Provider<List<String>>) {
                executor.set(elements)
            }

            public fun javaJar(jarPath: Provider<String>, executablePath: Provider<String>? = null) {
                if (executablePath == null) {
                    executor(jarPath.map { listOf(javaExePath, "-jar", it) })
                    return
                }

                val list = jarPath.zip(executablePath) { jar, exe ->
                    listOf(exe, "-jar", jar)
                }

                executor(list)
            }

            public fun javaJar(jarPath: String) {
                javaJar(project.provider { jarPath })
            }

            override val type: ResolvedGrpcPlugin.Type = ResolvedGrpcPlugin.Type.local

            internal companion object {
                internal val javaExePath: String by lazy {
                    val java = File(System.getProperty("java.home"), if (isWindows) "bin/java.exe" else "bin/java")

                    if (!java.exists()) {
                        throw GradleException("Could not find java executable at " + java.path)
                    }

                    java.path
                }

                internal val isWindows: Boolean by lazy {
                    System.getProperty("os.name").lowercase().contains("win")
                }
            }
        }

        public class Remote(project: Project) : Artifact() {
            public val locator: Property<String> = project.objects.property()
            override val type: ResolvedGrpcPlugin.Type = ResolvedGrpcPlugin.Type.remote
        }
    }
}
