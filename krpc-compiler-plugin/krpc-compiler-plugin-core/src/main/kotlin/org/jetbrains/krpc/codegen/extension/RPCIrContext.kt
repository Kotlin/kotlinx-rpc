/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc.codegen.extension

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.util.packageFqName
import org.jetbrains.krpc.codegen.VersionSpecificApi

internal class RPCIrContext(
    private val pluginContext: IrPluginContext,
    private val versionSpecificApi: VersionSpecificApi,
) {
    val irBuiltIns = pluginContext.irBuiltIns

    fun generatedClientCompanionObject(serviceDeclaration: IrClass): IrClassSymbol {
        val name = serviceDeclaration.name.asString()
        val packageName = serviceDeclaration.packageFqName?.asString()
            ?: error("Expected package name for $name")
        return getIrClassSymbol(packageName, "${name}Stub.Companion")
    }

    val optInAnnotation by lazy {
        getIrClassSymbol("kotlin", "OptIn")
    }

    val rpc by lazy {
        getRpcIrClassSymbol("RPC")
    }

    val withRPCStubObjectAnnotation by lazy {
        getRpcIrClassSymbol("WithRPCStubObject", "internal")
    }

    val internalKRPCApiAnnotation by lazy {
        getRpcIrClassSymbol("InternalKRPCApi", "internal")
    }

    private fun getRpcIrClassSymbol(name: String, subpackage: String? = null): IrClassSymbol {
        val suffix = subpackage?.let { ".$subpackage" } ?: ""
        return getIrClassSymbol("org.jetbrains.krpc$suffix", name)
    }

    private fun getIrClassSymbol(packageName: String, name: String): IrClassSymbol {
        return versionSpecificApi.referenceClass(pluginContext, packageName, name)
            ?: error("Unable to find symbol. Package: $packageName, name: $name")
    }
}
