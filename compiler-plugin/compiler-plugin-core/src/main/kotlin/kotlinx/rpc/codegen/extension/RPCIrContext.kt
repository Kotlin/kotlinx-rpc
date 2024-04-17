/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.extension

import kotlinx.rpc.codegen.VersionSpecificApi
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrEnumEntry
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.types.makeNullable
import org.jetbrains.kotlin.ir.util.functions
import org.jetbrains.kotlin.ir.util.isVararg
import org.jetbrains.kotlin.ir.util.properties
import org.jetbrains.kotlin.platform.konan.isNative

internal class RPCIrContext(
    val pluginContext: IrPluginContext,
    val versionSpecificApi: VersionSpecificApi,
) {
    val irBuiltIns = pluginContext.irBuiltIns

    val anyNullable by lazy {
        irBuiltIns.anyType.makeNullable()
    }

    val optInAnnotation by lazy {
        getIrClassSymbol("kotlin", "OptIn")
    }

    val extensionFunctionType by lazy {
        getIrClassSymbol("kotlin", "ExtensionFunctionType")
    }

    val coroutineScope by lazy {
        getIrClassSymbol("kotlinx.coroutines", "CoroutineScope")
    }

    val coroutineContext by lazy {
        getIrClassSymbol("kotlin.coroutines", "CoroutineContext")
    }

    val completableJob by lazy {
        getIrClassSymbol("kotlinx.coroutines", "CompletableJob")
    }

    val kTypeClass by lazy {
        getIrClassSymbol("kotlin.reflect", "KType")
    }

    val lazy by lazy {
        getIrClassSymbol("kotlin", "Lazy")
    }

    val function0 by lazy {
        getIrClassSymbol("kotlin", "Function0")
    }

    val suspendFunction1 by lazy {
        getIrClassSymbol("kotlin.coroutines", "SuspendFunction1")
    }

    val flow by lazy {
        getIrClassSymbol("kotlinx.coroutines.flow", "Flow")
    }

    val sharedFlow by lazy {
        getIrClassSymbol("kotlinx.coroutines.flow", "SharedFlow")
    }

    val stateFlow by lazy {
        getIrClassSymbol("kotlinx.coroutines.flow", "StateFlow")
    }

    val kProperty1 by lazy {
        getIrClassSymbol("kotlin.reflect", "KProperty1")
    }

    val pair by lazy {
        getIrClassSymbol("kotlin", "Pair")
    }

    val rpc by lazy {
        getRpcIrClassSymbol("RPC")
    }

    val rpcClient by lazy {
        getRpcIrClassSymbol("RPCClient")
    }

    val rpcCall by lazy {
        getRpcIrClassSymbol("RPCCall")
    }

    val rpcCallType by lazy {
        getRpcIrClassSymbol("RPCCall.Type")
    }

    val rpcCallTypeMethod by lazy {
        rpcCallType.owner.declarations.filterIsInstance<IrEnumEntry>().single {
            it.name.asString() == "Method"
        }.symbol
    }

    val rpcField by lazy {
        getRpcIrClassSymbol("RPCField")
    }

    val withRPCStubObjectAnnotation by lazy {
        getRpcIrClassSymbol("WithRPCStubObject", "internal")
    }

    val rpcEagerFieldAnnotation by lazy {
        getRpcIrClassSymbol("RPCEagerField")
    }

    val rpcStubObject by lazy {
        getRpcIrClassSymbol("RPCStubObject", "internal")
    }

    val rpcDeferredField by lazy {
        getRpcIrClassSymbol("RPCDeferredField", "internal")
    }

    fun isJsTarget(): Boolean {
        return versionSpecificApi.isJs(pluginContext.platform)
    }

    fun isNativeTarget(): Boolean {
        return pluginContext.platform.isNative()
    }

    val functions = Functions()

    inner class Functions {
        val registerPlainFlowField by lazy {
            rpcClient.namedFunction("registerPlainFlowField")
        }

        val registerSharedFlowField by lazy {
            rpcClient.namedFunction("registerSharedFlowField")
        }

        val registerStateFlowField by lazy {
            rpcClient.namedFunction("registerStateFlowField")
        }

        val rpcClientCall by lazy {
            rpcClient.namedFunction("call")
        }

        val typeOf by lazy {
            namedFunction("kotlin.reflect", "typeOf")
        }

        val lazy by lazy {
            namedFunction("kotlin", "lazy") {
                it.owner.valueParameters.size == 1
            }
        }

        val lazyGetValue by lazy {
            namedFunction("kotlin", "getValue") {
                it.owner.extensionReceiverParameter?.type?.classOrNull == this@RPCIrContext.lazy
            }
        }

        val listOf by lazy {
            namedFunction("kotlin.collections", "listOf") {
                it.owner.valueParameters.singleOrNull()?.isVararg ?: false
            }
        }

        val emptyList by lazy {
            namedFunction("kotlin.collections", "emptyList")
        }

        val mapOf by lazy {
            namedFunction("kotlin.collections", "mapOf") {
                it.owner.valueParameters.singleOrNull()?.isVararg ?: false
            }
        }

        val mapGet by lazy {
            irBuiltIns.mapClass.namedFunction("get")
        }

        val emptyMap by lazy {
            namedFunction("kotlin.collections", "emptyMap")
        }

        val to by lazy {
            namedFunction("kotlin", "to")
        }

        val withContext by lazy {
            namedFunction("kotlinx.coroutines", "withContext")
        }

        val job by lazy {
            namedFunction("kotlinx.coroutines", "Job")
        }

        private fun IrClassSymbol.namedFunction(name: String): IrSimpleFunction {
            return owner.functions.single { it.name.asString() == name }
        }

        private fun namedFunction(
            packageName: String,
            name: String,
            filterOverloads: ((IrSimpleFunctionSymbol) -> Boolean)? = null
        ): IrSimpleFunctionSymbol {
            val found = versionSpecificApi.referenceFunctions(pluginContext, packageName, name)

            return if (filterOverloads == null) found.first() else found.first(filterOverloads)
        }
    }

    val properties = Properties()

    inner class Properties {
        val rpcClientCoroutineContext by lazy {
            rpcClient.namedProperty("coroutineContext")
        }

        private fun IrClassSymbol.namedProperty(name: String): IrProperty {
            return owner.properties.single { it.name.asString() == name }
        }
    }

    private fun getRpcIrClassSymbol(name: String, subpackage: String? = null): IrClassSymbol {
        val suffix = subpackage?.let { ".$subpackage" } ?: ""
        return getIrClassSymbol("kotlinx.rpc$suffix", name)
    }

    fun getIrClassSymbol(packageName: String, name: String): IrClassSymbol {
        return versionSpecificApi.referenceClass(pluginContext, packageName, name)
            ?: error("Unable to find symbol. Package: $packageName, name: $name")
    }
}
