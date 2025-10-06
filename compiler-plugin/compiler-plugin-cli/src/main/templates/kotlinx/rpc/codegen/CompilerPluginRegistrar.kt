/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen

import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration

@OptIn(ExperimentalCompilerApi::class)
class RpcCompilerPlugin : CompilerPluginRegistrar() {
    override val supportsK2: Boolean = true

//##csm RpcCompilerPlugin.pluginId
//##csm specific=[2.0.0...2.2.99]
//##csm /specific
//##csm default
    override val pluginId: String = PLUGIN_ID
//##csm /default
//##csm /RpcCompilerPlugin.pluginId

    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        registerRpcExtensions(configuration)
    }
}
