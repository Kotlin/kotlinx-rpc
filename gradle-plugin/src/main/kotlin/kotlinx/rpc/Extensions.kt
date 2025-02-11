/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("unused")

package kotlinx.rpc

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.newInstance
import org.gradle.kotlin.dsl.property
import javax.inject.Inject

fun Project.rpcExtension(): RpcExtension = extensions.findByType<RpcExtension>() ?: RpcExtension(objects)

open class RpcExtension @Inject constructor(objects: ObjectFactory) {
    /**
     * Controls `@Rpc` [annotation type-safety](https://github.com/Kotlin/kotlinx-rpc/pull/240) compile-time checkers.
     *
     * CAUTION: Disabling is considered unsafe.
     * This option is only needed to prevent cases where type-safety analysis fails and valid code can't be compiled.
     */
    @RpcDangerousApi
    val annotationTypeSafetyEnabled = objects.property<Boolean>().convention(true)

    /**
     * Strict mode settings.
     * Allows configuring the reporting state of deprecated features.
     */
    val strict: RpcStrictModeExtension = objects.newInstance<RpcStrictModeExtension>()

    /**
     * Strict mode settings.
     * Allows configuring the reporting state of deprecated features.
     */
    fun strict(configure: Action<RpcStrictModeExtension>) {
        configure.execute(strict)
    }

    /**
     * Grpc settings.
     */
    val grpc: GrpcExtension = objects.newInstance<GrpcExtension>()

    fun grpc(configure: Action<GrpcExtension>) {
        configure.execute(grpc)
    }
}

open class RpcStrictModeExtension @Inject constructor(objects: ObjectFactory) {
    /**
     * `StateFlow`s in RPC services are deprecated,
     * due to their error-prone nature.
     *
     * Consider using plain flows and converting them to state on the application side.
     */
    val stateFlow: Property<RpcStrictMode> = objects.strictModeProperty()

    /**
     * `SharedFlow`s in RPC services are deprecated,
     * due to their error-prone nature.
     *
     * Consider using plain flows and converting them to state on the application side.
     */
    val sharedFlow: Property<RpcStrictMode> = objects.strictModeProperty()

    /**
     * Nested flows in RPC services are deprecated,
     * due to their error-prone nature.
     *
     * Consider using plain flows and converting them to state on the application side.
     */
    val nestedFlow: Property<RpcStrictMode> = objects.strictModeProperty()

    /**
     * WIP: https://youtrack.jetbrains.com/issue/KRPC-133
     * Will be enabled later, when an alternative is ready.
     */
    private val streamScopedFunctions: Property<RpcStrictMode> = objects.strictModeProperty(RpcStrictMode.NONE)

    /**
     * WIP: https://youtrack.jetbrains.com/issue/KRPC-133
     * Will be enabled later, when an alternative is ready.
     */
    private val suspendingServerStreaming: Property<RpcStrictMode> = objects.strictModeProperty(RpcStrictMode.NONE)

    /**
     * Not top-level flows in the return value are deprecated in RPC for streaming.
     *
     * Consider returning a Flow and requesting other data in a different method.
     */
    val notTopLevelServerFlow: Property<RpcStrictMode> = objects.strictModeProperty()

    /**
     * Fields in RPC services are deprecated,
     * due to its error-prone nature.
     *
     * Consider using regular streaming.
     */
    val fields: Property<RpcStrictMode> = objects.strictModeProperty()

    private fun ObjectFactory.strictModeProperty(
        default: RpcStrictMode = RpcStrictMode.WARNING,
    ): Property<RpcStrictMode> {
        return property(RpcStrictMode::class.java).convention(default)
    }
}

enum class RpcStrictMode {
    NONE, WARNING, ERROR;

    fun toCompilerArg(): String = name.lowercase()
}
