/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("unused")

package kotlinx.rpc

import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.newInstance
import javax.inject.Inject

open class RpcExtension @Inject constructor(objects: ObjectFactory) {
    val strict: RpcStrictModeExtension = objects.newInstance<RpcStrictModeExtension>()

    fun strict(configure: Action<RpcStrictModeExtension>) {
        configure.execute(strict)
    }
}

open class RpcStrictModeExtension @Inject constructor(objects: ObjectFactory) {
    val stateFlow: Property<RpcStrictMode> = objects.strictModeProperty()
    val sharedFlow: Property<RpcStrictMode> = objects.strictModeProperty()
    val nestedFlow: Property<RpcStrictMode> = objects.strictModeProperty()
    val streamScopedFunctions: Property<RpcStrictMode> = objects.strictModeProperty()
    val suspendingServerStreaming: Property<RpcStrictMode> = objects.strictModeProperty()
    val notTopLevelServerFlow: Property<RpcStrictMode> = objects.strictModeProperty()
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
