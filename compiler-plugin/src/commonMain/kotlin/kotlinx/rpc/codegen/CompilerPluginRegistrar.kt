/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen

import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.devkit.DevKitComponentRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration

class RpcCompilerPlugin : DevKitComponentRegistrar {
    override fun CompilerPluginRegistrar.ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        registerRpcExtensions(configuration)
    }
}
