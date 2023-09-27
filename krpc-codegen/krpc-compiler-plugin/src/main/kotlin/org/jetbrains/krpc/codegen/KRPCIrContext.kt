package org.jetbrains.krpc.codegen

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.util.kotlinFqName
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName

internal class KRPCIrContext(private val pluginContext: IrPluginContext) {
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

    val internalKRPCApiAnnotation by lazy {
        getRpcIrClassSymbol("InternalKRPCApi", "internal")
    }

    val rpcClientObjectClass by lazy {
        getRpcIrClassSymbol("RPCClientObject", "internal")
    }

    private fun getRpcIrClassSymbol(name: String, subpackage: String? = null): IrClassSymbol {
        val suffix = subpackage?.let { ".$subpackage" } ?: ""
        return getIrClassSymbol("org.jetbrains.krpc$suffix", name)
    }

    private fun getIrClassSymbol(packageName: String, name: String): IrClassSymbol {
        return pluginContext.referenceClass(
            ClassId(
                FqName(packageName),
                FqName(name),
                false
            )
        ) ?: error("Unable to find symbol. Package: $packageName, name: $name")
    }
}
