/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.extension

import kotlinx.rpc.codegen.VersionSpecificApi
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.util.kotlinFqName

internal class RPCIrContext(
    private val pluginContext: IrPluginContext,
    private val versionSpecificApi: VersionSpecificApi,
) {
    val irBuiltIns = pluginContext.irBuiltIns

    fun generatedClientCompanionObject(serviceDeclaration: IrClass): IrClassSymbol {
        val name = serviceDeclaration.kotlinFqName.shortName()
        return getRpcIrClassSymbol("${name}Client.Companion")
    }

    val optInAnnotation by lazy {
        getIrClassSymbol("kotlin", "OptIn")
    }

    val rpc by lazy {
        getRpcIrClassSymbol("RPC")
    }

    val withRPCClientObjectAnnotation by lazy {
        getRpcIrClassSymbol("WithRPCClientObject", "internal")
    }

    val internalRPCApiAnnotation by lazy {
        getRpcIrClassSymbol("InternalRPCApi", "internal")
    }

    private fun getRpcIrClassSymbol(name: String, subpackage: String? = null): IrClassSymbol {
        val suffix = subpackage?.let { ".$subpackage" } ?: ""
        return getIrClassSymbol("kotlinx.rpc$suffix", name)
    }

    private fun getIrClassSymbol(packageName: String, name: String): IrClassSymbol {
        return versionSpecificApi.referenceClass(pluginContext, packageName, name)
            ?: error("Unable to find symbol. Package: $packageName, name: $name")
    }
}
