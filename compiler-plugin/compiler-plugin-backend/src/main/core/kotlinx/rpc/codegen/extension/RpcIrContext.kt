/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.extension

import kotlinx.rpc.codegen.VersionSpecificApi
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrPropertySymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.types.makeNullable
import org.jetbrains.kotlin.ir.util.functions
import org.jetbrains.kotlin.ir.util.isVararg
import org.jetbrains.kotlin.ir.util.nestedClasses
import org.jetbrains.kotlin.ir.util.properties
import org.jetbrains.kotlin.platform.konan.isNative
import org.jetbrains.kotlin.types.Variance

internal class RpcIrContext(
    val pluginContext: IrPluginContext,
    val versionSpecificApi: VersionSpecificApi,
) {
    val irBuiltIns = pluginContext.irBuiltIns

    val anyNullable by lazy {
        irBuiltIns.anyType.makeNullable()
    }

    val arrayOfAnyNullable by lazy {
        irBuiltIns.arrayClass.typeWith(anyNullable, Variance.OUT_VARIANCE)
    }

    val coroutineScope by lazy {
        getIrClassSymbol("kotlinx.coroutines", "CoroutineScope")
    }

    val coroutineContext by lazy {
        getIrClassSymbol("kotlin.coroutines", "CoroutineContext")
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

    val function1 by lazy {
        getIrClassSymbol("kotlin", "Function1")
    }

    val suspendFunction0 by lazy {
        getIrClassSymbol("kotlin.coroutines", "SuspendFunction0")
    }

    val suspendFunction2 by lazy {
        getIrClassSymbol("kotlin.coroutines", "SuspendFunction2")
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

    val rpcClient by lazy {
        getRpcIrClassSymbol("RpcClient")
    }

    val rpcCall by lazy {
        getRpcIrClassSymbol("RpcCall")
    }

    val withServiceDescriptor by lazy {
        getRpcIrClassSymbol("WithServiceDescriptor", "internal")
    }

    val rpcEagerFieldAnnotation by lazy {
        getRpcIrClassSymbol("RpcEagerField")
    }

    val rpcServiceDescriptor by lazy {
        getRpcIrClassSymbol("RpcServiceDescriptor", "descriptor")
    }

    val rpcType by lazy {
        getRpcIrClassSymbol("RpcType", "descriptor")
    }

    val rpcCallable by lazy {
        getRpcIrClassSymbol("RpcCallable", "descriptor")
    }

    private val rpcInvokator by lazy {
        getRpcIrClassSymbol("RpcInvokator", "descriptor")
    }

    val rpcInvokatorMethod by lazy {
        rpcInvokator.subClass("Method")
    }

    val rpcInvokatorField by lazy {
        rpcInvokator.subClass("Field")
    }

    val rpcParameter by lazy {
        getRpcIrClassSymbol("RpcParameter", "descriptor")
    }

    val rpcDeferredField by lazy {
        getRpcIrClassSymbol("RpcDeferredField", "internal")
    }

    val fieldDataObject by lazy {
        getRpcIrClassSymbol("FieldDataObject", "internal")
    }

    val rpcMethodClass by lazy {
        getRpcIrClassSymbol("RpcMethodClass", "internal")
    }

    fun isJsTarget(): Boolean {
        return versionSpecificApi.isJs(pluginContext.platform)
    }

    fun isNativeTarget(): Boolean {
        return pluginContext.platform.isNative()
    }

    fun isWasmTarget(): Boolean {
        return versionSpecificApi.isWasm(pluginContext.platform)
    }

    val functions = Functions()

    inner class Functions {
        val registerPlainFlowField by lazy {
            namedFunction("kotlinx.rpc", "registerPlainFlowField")
        }

        val registerSharedFlowField by lazy {
            namedFunction("kotlinx.rpc", "registerSharedFlowField")
        }

        val registerStateFlowField by lazy {
            namedFunction("kotlinx.rpc", "registerStateFlowField")
        }

        val dataCast by lazy {
            namedFunction("kotlinx.rpc.internal", "dataCast")
        }

        val rpcClientCall by lazy {
            rpcClient.namedFunction("call")
        }

        val provideStubContext by lazy {
            rpcClient.namedFunction("provideStubContext")
        }

        val asArray by lazy {
            rpcMethodClass.namedFunction("asArray")
        }

        val typeOf by lazy {
            namedFunction("kotlin.reflect", "typeOf")
        }

        val emptyArray by lazy {
            namedFunction("kotlin", "emptyArray")
        }

        val scopedClientCall by lazy {
            namedFunction("kotlinx.rpc.internal", "scopedClientCall")
        }

        val lazy by lazy {
            namedFunction("kotlin", "lazy") {
                it.owner.valueParameters.size == 1
            }
        }

        val lazyGetValue by lazy {
            namedFunction("kotlin", "getValue") {
                it.owner.extensionReceiverParameter?.type?.classOrNull == this@RpcIrContext.lazy
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

        private fun IrClassSymbol.namedFunction(name: String): IrSimpleFunction {
            return owner.functions.single { it.name.asString() == name }
        }

        private fun namedFunction(
            packageName: String,
            name: String,
            filterOverloads: ((IrSimpleFunctionSymbol) -> Boolean)? = null,
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

        val rpcServiceDescriptorFqName by lazy {
            rpcServiceDescriptor.namedProperty("fqName")
        }

        private fun IrClassSymbol.namedProperty(name: String): IrPropertySymbol {
            return owner.properties.single { it.name.asString() == name }.symbol
        }
    }

    private fun IrClassSymbol.subClass(name: String): IrClassSymbol {
        return owner.nestedClasses.single { it.name.asString() == name }.symbol
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
