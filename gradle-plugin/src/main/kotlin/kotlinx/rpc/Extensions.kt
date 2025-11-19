/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("unused")

package kotlinx.rpc

import kotlinx.rpc.protoc.DefaultProtocExtension
import kotlinx.rpc.protoc.ProtocExtension
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.findByType
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.newInstance
import org.gradle.kotlin.dsl.property
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

internal fun Project.rpcExtensionOrNull(): RpcExtension? = extensions.findByType<RpcExtension>()

internal fun Project.rpcExtension(): RpcExtension = rpcExtensionOrNull()
    ?: error("Rpc extension not found. Please apply the plugin to the project")

public open class RpcExtension @Inject constructor(objects: ObjectFactory, project: Project) {
    /**
     * Controls `@Rpc` [annotation type-safety](https://github.com/Kotlin/kotlinx-rpc/pull/240) compile-time checkers.
     *
     * CAUTION: Disabling is considered unsafe.
     * This option is only needed to prevent cases where type-safety analysis fails and valid code can't be compiled.
     */
    @RpcDangerousApi
    public val annotationTypeSafetyEnabled: Provider<Boolean> = objects.property<Boolean>().convention(true)

    /**
     * Strict mode settings.
     * Allows configuring the reporting state of deprecated features.
     */
    public val strict: RpcStrictModeExtension = objects.newInstance<RpcStrictModeExtension>()

    /**
     * Strict mode settings.
     */
    @Deprecated("Strict mode enabled irreversibly. This option can't change it.", level = DeprecationLevel.ERROR)
    public fun strict(configure: Action<RpcStrictModeExtension>) {
        configure.execute(strict)
    }


    /**
     * Protoc settings.
     *
     * Can't be called in a lazy context if not initialized.
     */
    public val protoc: Provider<ProtocExtension> = project.provider {
        if (!protocApplied.get()) {
            error("""
                Protoc extension was not initialized. 
                Please, apply the plugin by using the following declaration:
                
                  rpc {
                      protoc()
                  }
                
                If the error persists, check if you are using this property lazily, 
                i.e. by calling 'protoc.map { }' and not 'protoc.get()', 
                otherwise the order of initialization may be wrong.
            """.trimIndent())
        }

        protocInternal
    }

    /**
     * Protoc settings.
     */
    public fun protoc(configure: Action<ProtocExtension> = Action {}) {
        configure.execute(protocInternal)
    }

    internal val protocApplied = AtomicBoolean(false)

    internal val protocInternal by lazy {
        if (protocApplied.get()) {
            error("Illegal access to protoc extension during DefaultProtocExtension.init")
        }

        protocApplied.set(true)
        objects.newInstance<DefaultProtocExtension>().apply {
            callbacks.forEach { it.execute(this) }
        }
    }

    private val callbacks = mutableListOf<Action<ProtocExtension>>()

    internal fun whenProtocApplied(action: Action<ProtocExtension>) {
        if (protocApplied.get()) {
            action.execute(protoc.get())
            return
        }

        callbacks.add(action)
    }
}

public open class RpcStrictModeExtension @Inject constructor(objects: ObjectFactory) {
    /**
     * `StateFlow`s in RPC services are deprecated,
     * due to their error-prone nature.
     *
     * Consider using plain flows and converting them to state on the application side.
     */
    public val stateFlow: Property<RpcStrictMode> = objects.strictModeProperty()

    /**
     * `SharedFlow`s in RPC services are deprecated,
     * due to their error-prone nature.
     *
     * Consider using plain flows and converting them to state on the application side.
     */
    public val sharedFlow: Property<RpcStrictMode> = objects.strictModeProperty()

    /**
     * Nested flows in RPC services are deprecated,
     * due to their error-prone nature.
     *
     * Consider using plain flows and converting them to state on the application side.
     */
    public val nestedFlow: Property<RpcStrictMode> = objects.strictModeProperty()

    /**
     * StreamScoped functions are deprecated.
     */
    public val streamScopedFunctions: Property<RpcStrictMode> = objects.strictModeProperty()

    /**
     * Suspending functions with server-streaming are deprecated in RPC.
     *
     * Consider returning a Flow in a non-suspending function.
     */
    public val suspendingServerStreaming: Property<RpcStrictMode> = objects.strictModeProperty()

    /**
     * Not top-level flows in the return value are deprecated in RPC for streaming.
     *
     * Consider returning a Flow and requesting other data in a different method.
     */
    public val notTopLevelServerFlow: Property<RpcStrictMode> = objects.strictModeProperty()

    /**
     * Fields in RPC services are deprecated,
     * due to its error-prone nature.
     *
     * Consider using regular streaming.
     */
    @Deprecated("Field are deprecated with level ERROR. This option can't change it.")
    public val fields: Property<RpcStrictMode> = objects.strictModeProperty()

    private fun ObjectFactory.strictModeProperty(
        default: RpcStrictMode = RpcStrictMode.ERROR,
    ): Property<RpcStrictMode> {
        return property(RpcStrictMode::class.java).convention(default)
    }
}

public enum class RpcStrictMode {
    NONE, WARNING, ERROR;
}
