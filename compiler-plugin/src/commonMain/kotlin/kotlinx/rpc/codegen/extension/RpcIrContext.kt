/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.extension

import kotlinx.rpc.codegen.VersionSpecificApi
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrEnumEntry
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrEnumEntrySymbol
import org.jetbrains.kotlin.ir.symbols.IrPropertySymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.makeNullable
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.ir.util.functions
import org.jetbrains.kotlin.ir.util.isVararg
import org.jetbrains.kotlin.ir.util.nestedClasses
import org.jetbrains.kotlin.ir.util.properties
import org.jetbrains.kotlin.platform.jvm.isJvm
import org.jetbrains.kotlin.types.Variance

/**
 * IMPORTANT!!!
 * Difference between a built-in reference and a regular one:
 * - Built-in - lib classes (kotlinx.rpc.*) and stdlib
 * - Regular - user classes (kotlinx.serialization.*, kotlinx.coroutines.*) from other libs
 */
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

    val kTypeClass by lazy {
        referenceIrBuiltinClassSymbol("kotlin.reflect", "KType")
    }

    fun flow(from: IrFile): IrClassSymbol {
        return referenceIrClassSymbol("kotlinx.coroutines.flow", "Flow", from)
    }

    val pair by lazy {
        referenceIrBuiltinClassSymbol("kotlin", "Pair")
    }

    val rpcClient by lazy {
        referenceRpcIrClassSymbol("RpcClient")
    }

    val rpcCall by lazy {
        referenceRpcIrClassSymbol("RpcCall")
    }

    val withProtoDescriptor by lazy {
        referenceRpcIrClassSymbol("WithProtoDescriptor", "protobuf.internal")
    }

    val withServiceDescriptor by lazy {
        referenceRpcIrClassSymbol("WithServiceDescriptor", "internal")
    }

    val rpcServiceDescriptor by lazy {
        referenceRpcIrClassSymbol("RpcServiceDescriptor", "descriptor")
    }

    val grpcServiceDescriptor by lazy {
        referenceIrBuiltinClassSymbol("kotlinx.rpc.grpc.descriptor", "GrpcServiceDescriptor")
    }

    val grpcServiceDelegate by lazy {
        referenceIrBuiltinClassSymbol("kotlinx.rpc.grpc.descriptor", "GrpcServiceDelegate")
    }

    fun grpcPlatformServiceDescriptor(from: IrFile): IrClassSymbol {
        return if (isJvmTarget()) {
            referenceIrClassSymbol("io.grpc", "ServiceDescriptor", from)
        } else {
            referenceIrBuiltinClassSymbol("kotlinx.rpc.grpc.internal", "ServiceDescriptor")
        }
    }

    fun grpcPlatformMethodDescriptor(from: IrFile): IrClassSymbol {
        return if (isJvmTarget()) {
            referenceIrClassSymbol("io.grpc", "MethodDescriptor", from)
        } else {
            referenceIrBuiltinClassSymbol("kotlinx.rpc.grpc.descriptor", "GrpcMethodDescriptor")
        }
    }

    val grpcPlatformMethodType by lazy {
        referenceIrBuiltinClassSymbol("kotlinx.rpc.grpc.descriptor", "GrpcMethodType")
    }

    val grpcPlatformMethodTypeUnary by lazy {
        grpcPlatformMethodType.enumEntry("UNARY")
    }

    val grpcPlatformMethodTypeServerStreaming by lazy {
        grpcPlatformMethodType.enumEntry("SERVER_STREAMING")
    }

    val grpcPlatformMethodTypeClientStreaming by lazy {
        grpcPlatformMethodType.enumEntry("CLIENT_STREAMING")
    }

    val grpcPlatformMethodTypeBidiStreaming by lazy {
        grpcPlatformMethodType.enumEntry("BIDI_STREAMING")
    }

    val grpcMarshaller by lazy {
        referenceIrBuiltinClassSymbol("kotlinx.rpc.grpc.marshaller", "GrpcMarshaller")
    }

    val grpcMarshallerResolver by lazy {
        referenceIrBuiltinClassSymbol("kotlinx.rpc.grpc.marshaller", "GrpcMarshallerResolver")
    }

    val grpcMarshallerConfig by lazy {
        referenceIrBuiltinClassSymbol("kotlinx.rpc.grpc.marshaller", "GrpcMarshallerConfig")
    }

    val configuredGrpcMarshallerDelegate by lazy {
        referenceIrBuiltinClassSymbol("kotlinx.rpc.grpc.marshaller.internal", "ConfiguredGrpcMarshallerDelegate")
    }

    val withGrpcMarshallerAnnotation by lazy {
        referenceIrBuiltinClassSymbol("kotlinx.rpc.grpc.marshaller", "WithGrpcMarshaller")
    }

    val subclassOptInRequired by lazy {
        referenceIrBuiltinClassSymbol("kotlin", "SubclassOptInRequired")
    }

    val protobufMessagesInheritance by lazy {
        referenceRpcIrClassSymbol("ProtobufMessagesInheritance", "protobuf.internal")
    }

    val rpcType by lazy {
        referenceRpcIrClassSymbol("RpcType", "descriptor")
    }

    val rpcTypeDefault by lazy {
        referenceRpcIrClassSymbol("RpcTypeDefault", "descriptor")
    }

    val rpcTypeKrpc by lazy {
        referenceRpcIrClassSymbol("RpcTypeKrpc", "descriptor")
    }

    val rpcCallable by lazy {
        referenceRpcIrClassSymbol("RpcCallable", "descriptor")
    }

    val rpcCallableDefault by lazy {
        referenceRpcIrClassSymbol("RpcCallableDefault", "descriptor")
    }

    private val rpcInvokator by lazy {
        referenceRpcIrClassSymbol("RpcInvokator", "descriptor")
    }

    val rpcInvokatorUnaryResponse by lazy {
        rpcInvokator.subClass("UnaryResponse")
    }

    val rpcInvokatorFlowResponse by lazy {
        rpcInvokator.subClass("FlowResponse")
    }

    val rpcParameter by lazy {
        referenceRpcIrClassSymbol("RpcParameter", "descriptor")
    }

    val rpcParameterDefault by lazy {
        referenceRpcIrClassSymbol("RpcParameterDefault", "descriptor")
    }

    fun kSerializer(from: IrFile): IrClassSymbol {
        return referenceIrClassSymbol("kotlinx.serialization", "KSerializer", from)
    }

    fun kSerializerAnyNullable(from: IrFile): IrType {
        return kSerializer(from).typeWith(anyNullable)
    }

    fun kSerializerAnyNullableKClass(from: IrFile): IrType {
        return irBuiltIns.kClassClass.typeWith(kSerializerAnyNullable(from))
    }

    fun isJvmTarget(): Boolean {
        return pluginContext.platform.isJvm()
    }

    val functions = Functions()

    inner class Functions {
        val rpcClientCall by lazy {
            rpcClient.namedFunction("call")
        }

        val rpcClientCallServerStreaming by lazy {
            rpcClient.namedFunction("callServerStreaming")
        }

        val typeOf by lazy {
            namedBuiltinFunction("kotlin.reflect", "typeOf")
        }

        val emptyArray by lazy {
            namedBuiltinFunction("kotlin", "emptyArray")
        }

        val mapOf by lazy {
            namedBuiltinFunction("kotlin.collections", "mapOf") {
                vsApi {
                    it.owner.valueParametersVS().singleOrNull()?.isVararg ?: false
                }
            }
        }

        val emptyList by lazy {
            namedBuiltinFunction("kotlin.collections", "emptyList")
        }

        val listOf by lazy {
            namedBuiltinFunction("kotlin.collections", "listOf") {
                vsApi {
                    it.owner.valueParametersVS().singleOrNull()?.isVararg ?: false
                }
            }
        }

        val mapGet by lazy {
            irBuiltIns.mapClass.namedFunction("get")
        }

        val arrayGet by lazy {
            irBuiltIns.arrayClass.namedFunction("get")
        }

        val emptyMap by lazy {
            namedBuiltinFunction("kotlin.collections", "emptyMap")
        }

        val to by lazy {
            namedBuiltinFunction("kotlin", "to")
        }

        val serviceDescriptor by lazy {
            namedBuiltinFunction("kotlinx.rpc.grpc.internal", "serviceDescriptor")
        }

        val methodDescriptor by lazy {
            namedBuiltinFunction("kotlinx.rpc.grpc.descriptor", "methodDescriptor")
        }

        val grpcServiceDescriptorDelegate by lazy {
            grpcServiceDescriptor.namedFunction("delegate")
        }

        val grpcMarshallerResolverResolveOrNull by lazy {
            grpcMarshallerResolver.namedFunction("resolveOrNull")
        }

        private fun IrClassSymbol.namedFunction(name: String): IrSimpleFunction {
            return owner.functions.single { it.name.asString() == name }
        }

        private fun namedBuiltinFunction(
            packageName: String,
            name: String,
            filterOverloads: ((IrSimpleFunctionSymbol) -> Boolean)? = null,
        ): IrSimpleFunctionSymbol {
            val found = versionSpecificApi.referenceBuiltinFunctionsVS(pluginContext, packageName, name)

            return if (filterOverloads == null) found.first() else found.first(filterOverloads)
        }

        @Suppress("unused")
        private fun namedFunction(
            packageName: String,
            name: String,
            from: IrFile,
            filterOverloads: ((IrSimpleFunctionSymbol) -> Boolean)? = null,
        ): IrSimpleFunctionSymbol {
            val found = versionSpecificApi.referenceFunctionsVS(pluginContext, packageName, name, from)

            return if (filterOverloads == null) found.first() else found.first(filterOverloads)
        }
    }

    val properties = Properties()

    inner class Properties {
        val rpcServiceDescriptorSimpleName by lazy {
            rpcServiceDescriptor.namedProperty("simpleName")
        }

        val rpcServiceDescriptorFqName by lazy {
            rpcServiceDescriptor.namedProperty("fqName")
        }

        val rpcServiceDescriptorCallables by lazy {
            rpcServiceDescriptor.namedProperty("callables")
        }

        val mapValues by lazy {
            irBuiltIns.mapClass.namedProperty("values")
        }

        private fun IrClassSymbol.namedProperty(name: String): IrPropertySymbol {
            return owner.properties.single { it.name.asString() == name }.symbol
        }
    }

    private fun IrClassSymbol.subClass(name: String): IrClassSymbol {
        return owner.nestedClasses.single { it.name.asString() == name }.symbol
    }

    private fun IrClassSymbol.enumEntry(name: String): IrEnumEntrySymbol {
        return owner.declarations.filterIsInstance<IrEnumEntry>().single { it.name.asString() == name }.symbol
    }

    private fun referenceRpcIrClassSymbol(name: String, subpackage: String? = null): IrClassSymbol {
        val suffix = subpackage?.let { ".$subpackage" } ?: ""
        return referenceIrBuiltinClassSymbol("kotlinx.rpc$suffix", name)
    }

    fun referenceIrClassSymbol(
        packageName: String,
        name: String,
        from: IrFile,
    ): IrClassSymbol {
        return versionSpecificApi.referenceClassVS(pluginContext, packageName, name, from)
            ?: error("Unable to find symbol. Package: $packageName, name: $name")
    }

    fun referenceIrBuiltinClassSymbol(packageName: String, name: String): IrClassSymbol {
        return versionSpecificApi.referenceBuiltinClassVS(pluginContext, packageName, name)
            ?: error("Unable to find built-in symbol. Package: $packageName, name: $name")
    }

    fun <T> vsApi(body: VersionSpecificApi.() -> T): T {
        return versionSpecificApi.run(body)
    }
}
