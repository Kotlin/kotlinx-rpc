/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.extension

import kotlinx.rpc.codegen.VersionSpecificApi
import kotlinx.rpc.codegen.common.RpcClassId
import kotlinx.rpc.codegen.common.RpcNames
import org.jetbrains.kotlin.backend.jvm.functionByName
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.descriptors.DescriptorVisibility
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.builders.IrBlockBodyBuilder
import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.builders.declarations.addConstructor
import org.jetbrains.kotlin.ir.builders.declarations.addFunction
import org.jetbrains.kotlin.ir.builders.declarations.addGetter
import org.jetbrains.kotlin.ir.builders.declarations.addProperty
import org.jetbrains.kotlin.ir.builders.declarations.addValueParameter
import org.jetbrains.kotlin.ir.builders.declarations.buildFun
import org.jetbrains.kotlin.ir.builders.irAs
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irCallConstructor
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irReturn
import org.jetbrains.kotlin.ir.builders.irVararg
import org.jetbrains.kotlin.ir.declarations.IrAnnotationContainer
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrClassReference
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrMemberAccessExpression
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin
import org.jetbrains.kotlin.ir.expressions.IrTypeOperator
import org.jetbrains.kotlin.ir.expressions.impl.IrCallImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrClassReferenceImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrConstImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrConstructorCallImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrFunctionExpressionImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrGetObjectValueImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrGetValueImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrTypeOperatorCallImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrVarargImpl
import org.jetbrains.kotlin.ir.expressions.putConstructorTypeArgument
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrPropertySymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.IrValueSymbol
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.types.createType
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.ir.util.companionObject
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.defaultType
import org.jetbrains.kotlin.ir.util.file
import org.jetbrains.kotlin.ir.util.functions
import org.jetbrains.kotlin.ir.util.getAnnotation
import org.jetbrains.kotlin.ir.util.parentAsClass
import org.jetbrains.kotlin.ir.util.properties
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.SpecialNames
import org.jetbrains.kotlin.types.Variance
import org.jetbrains.kotlin.util.OperatorNameConventions
import kotlin.properties.Delegates

private object Stub {
    const val CLIENT = "__rpc_client"
    const val STUB_ID = "__rpc_stub_id"
}

private object Descriptor {
    const val CALLABLES = "callables"

    // the kotlin class name
    const val FQ_NAME = "fqName"
    const val SIMPLE_NAME = "simpleName"
    const val GET_CALLABLE = "getCallable"
    const val CREATE_INSTANCE = "createInstance"
}

internal open class RpcStubGenerator(
    protected val declaration: ServiceDeclaration,
    protected val ctx: RpcIrContext,
    @Suppress("unused")
    private val logger: MessageCollector,
) {
    protected var stubClass: IrClass by Delegates.notNull()
        private set

    protected var stubClassThisReceiver: IrValueParameter by Delegates.notNull()
        private set

    fun generate() {
        generateStubClass()

        addWithServiceDescriptorAnnotation()
    }

    /**
     * Type of the descriptor (stub companion object): `RpcMeta.descriptorClass` applied to the service type,
     * e.g. `KrpcServiceDescriptor<MyService>`.
     *
     * Must match the type FIR gives to the generated `descriptor` property of the service interface.
     */
    private fun descriptorType(): IrType {
        val descriptorClass = declaration.service.rpcMetaInformation(RpcNames.RPC_META_DESCRIPTOR_CLASS).classOrNull
            ?: error("Expected a class as 'descriptorClass' of RpcMeta for ${declaration.service.name}")

        val typeParameterCount = descriptorClass.owner.typeParameters.size
        if (typeParameterCount != 1) {
            error(
                "Expected 'descriptorClass' ${descriptorClass.owner.name} of RpcMeta for ${declaration.service.name} " +
                        "to have a single type parameter for the service type, found $typeParameterCount"
            )
        }

        return descriptorClass.typeWith(declaration.serviceType)
    }

    /**
     * Called after the base content of the descriptor (stub companion object) is generated.
     */
    protected open fun IrClass.generateAdditionalDescriptorContent() {}

    /**
     * Value of the descriptor's `simpleName`. Defaults to the service interface name.
     */
    protected open fun descriptorSimpleName(): String = declaration.simpleName

    private fun generateStubClass() {
        declaration.stubClass.apply {
            declarations.removeAll { it !is IrClass } // preserve the companion object and method classes
            annotations = emptyList()

            stubClass = this
            stubClassThisReceiver = thisReceiver
                ?: error("Declared stub class must have thisReceiver: ${stubClass.name.asString()}")

            superTypes = listOf(declaration.serviceType)

            generateStubConstructor()
            generateStubContent()
        }
    }

    private var clientValueParameter: IrValueParameter by Delegates.notNull()
    private var stubIdValueParameter: IrValueParameter by Delegates.notNull()

    /**
     * Constructor of a stub service:
     *
     * ```kotlin
     * class `$rpcServiceStub`(private val __rpc_stub_id: Long, private val __rpc_client: RpcClient) : MyService
     * ```
     */
    private fun IrClass.generateStubConstructor() {
        addConstructor {
            name = this@generateStubConstructor.name
            isPrimary = true
        }.apply {
            stubIdValueParameter = addValueParameter {
                name = Name.identifier(Stub.STUB_ID)
                type = ctx.irBuiltIns.longType
            }

            clientValueParameter = addValueParameter {
                name = Name.identifier(Stub.CLIENT)
                type = ctx.rpcClient.typeWith(
                    declaration.service.rpcMetaInformation(RpcNames.RPC_META_PROTOCOL_CLASS)
                )
            }

            addDefaultConstructor(ctx.pluginContext, this)
        }
    }

    private fun IrClass.generateStubContent() {
        generateProperties()

        generateMethods()

        generateCompanionObject()

        addDescriptorFakeOverride()

        addAnyOverrides(declaration.service)
    }

    private fun IrClass.addDescriptorFakeOverride() {
        val interfaceProperty = declaration.service.properties.singleOrNull {
            it.name == RpcNames.SERVICE_DESCRIPTOR_PROPERTY_NAME && !it.isFakeOverride
        } ?: error(
            "Expected a generated '${RpcNames.SERVICE_DESCRIPTOR_PROPERTY_NAME}' property " +
                    "in ${declaration.service.name.asString()}"
        )

        val interfaceGetter = interfaceProperty.getterOrFail

        addProperty {
            name = interfaceProperty.name
            visibility = DescriptorVisibilities.PUBLIC
            modality = Modality.OPEN
            origin = IrDeclarationOrigin.FAKE_OVERRIDE

            isFakeOverride = true
        }.apply {
            overriddenSymbols = listOf(interfaceProperty.symbol)

            addGetter {
                visibility = DescriptorVisibilities.PUBLIC
                modality = Modality.OPEN
                origin = IrDeclarationOrigin.FAKE_OVERRIDE

                isFakeOverride = true
                returnType = interfaceGetter.returnType
            }.apply {
                overriddenSymbols = listOf(interfaceGetter.symbol)

                vsApi {
                    dispatchReceiverParameterVS = stubClassThisReceiver
                        .copyToVS(this@apply, origin = IrDeclarationOrigin.DEFINED)
                }
            }
        }
    }

    private fun IrClass.generateProperties() {
        stubIdProperty()

        clientProperty()
    }

    private var stubIdProperty: IrProperty by Delegates.notNull()

    /**
     * __rpc_stub_id property from the constructor
     *
     * ```kotlin
     * class `$rpcServiceStub`(private val __rpc_stub_id: Long, private val __rpc_client: RpcClient) : MyService
     * ```
     */
    private fun IrClass.stubIdProperty() {
        stubIdProperty = addConstructorProperty(Stub.STUB_ID, ctx.irBuiltIns.longType, stubIdValueParameter)
    }

    private var clientProperty: IrProperty by Delegates.notNull()

    /**
     * __rpc_client property from the constructor
     *
     * ```kotlin
     * class `$rpcServiceStub`(private val __rpc_stub_id: Long, private val __rpc_client: RpcClient) : MyService
     * ```
     */
    private fun IrClass.clientProperty() {
        clientProperty = addConstructorProperty(
            Stub.CLIENT,
            ctx.rpcClient.typeWith(
                declaration.service.rpcMetaInformation(RpcNames.RPC_META_PROTOCOL_CLASS)
            ),
            clientValueParameter
        )
    }

    private fun IrClass.addConstructorProperty(
        propertyName: String,
        propertyType: IrType,
        valueParameter: IrValueParameter,
        propertyVisibility: DescriptorVisibility = DescriptorVisibilities.PRIVATE,
    ): IrProperty {
        return addConstructorProperty(Name.identifier(propertyName), propertyType, valueParameter, propertyVisibility)
    }

    private fun IrClass.addConstructorProperty(
        propertyName: Name,
        propertyType: IrType,
        valueParameter: IrValueParameter,
        propertyVisibility: DescriptorVisibility = DescriptorVisibilities.PRIVATE,
    ): IrProperty {
        return addProperty {
            name = propertyName
            visibility = propertyVisibility
        }.apply {
            addBackingFieldUtil {
                visibility = DescriptorVisibilities.PRIVATE
                type = propertyType
                vsApi { isFinalVS = true }
            }.apply {
                initializer = factory.createExpressionBody(
                    IrGetValueImpl(
                        startOffset = UNDEFINED_OFFSET,
                        endOffset = UNDEFINED_OFFSET,
                        type = valueParameter.type,
                        symbol = valueParameter.symbol,
                        origin = IrStatementOrigin.INITIALIZE_PROPERTY_FROM_PARAMETER,
                    )
                )
            }

            addDefaultGetter(this@addConstructorProperty, ctx.irBuiltIns) {
                visibility = propertyVisibility
            }
        }
    }

    private fun IrClass.generateMethods() {
        declaration.methods.forEach {
            generateRpcMethod(it)
        }
    }

    /**
     * RPC Methods generation
     *
     * All method classes MUST be already generated, so this plugin can use them.
     *
     * ```kotlin
     *  final override suspend fun <method-name>(<method-args>): <return-type> {
     *      return scopedClientCall(this) { // this: CoroutineScope
     *          __rpc_client.call(RpcCall(
     *              descriptor = Companion,
     *              callableName = "<method-name>",
     *              data = (<method-class>(<method-args>)|<method-object>),
     *              serviceId = __rpc_stub_id,
     *          ))
     *      }
     *  }
     * ```
     *
     * Where:
     *  - `<method-name>` - the name of the RPC method
     *  - `<method-args>` - array of arguments for the method
     *  - `<return-type>` - the return type of the method
     *  - `<method-class>` or `<method-object>` - generated class or object for this method
     */
    private fun IrClass.generateRpcMethod(method: ServiceDeclaration.Method) {
        addFunction {
            name = method.function.name
            visibility = method.function.visibility
            returnType = method.function.returnType
            modality = Modality.OPEN

            isSuspend = method.function.isSuspend
        }.apply {
            val functionThisReceiver = vsApi {
                stubClassThisReceiver.copyToVS(this@apply, origin = IrDeclarationOrigin.DEFINED)
            }.also {
                vsApi {
                    dispatchReceiverParameterVS = it
                }
            }

            val arguments = method.arguments.memoryOptimizedMap { arg ->
                addValueParameter {
                    name = arg.value.name
                    type = arg.type
                }
            }

            overriddenSymbols = listOf(method.function.symbol)

            body = irBuilder(ctx.pluginContext, symbol).irBlockBody {
                if (method.function.isNonSuspendingWithFlowReturn(declaration.stubClass.file)) {
                    +irReturn(
                        irRpcMethodClientCall(
                            method = method,
                            functionThisReceiver = functionThisReceiver,
                            arguments = arguments,
                        )
                    )

                    return@irBlockBody
                }

                val call = irRpcMethodClientCall(
                    method = method,
                    functionThisReceiver = functionThisReceiver,
                    arguments = arguments,
                )

                if (method.function.returnType == ctx.irBuiltIns.unitType) {
                    +call
                } else {
                    +irReturn(call)
                }
            }
        }
    }

    /**
     * Part of [generateRpcMethod] that generates next call:
     *
     * ```kotlin
     * __rpc_client.call(RpcCall(
     *     descriptor = Companion,
     *     callableName = "<method-name>",
     *     arguments = arrayOf(<method-arg-0>, ...),
     *     serviceId = __rpc_stub_id,
     * ))
     * ```
     */
    private fun IrBlockBodyBuilder.irRpcMethodClientCall(
        method: ServiceDeclaration.Method,
        functionThisReceiver: IrValueParameter,
        arguments: List<IrValueParameter>,
    ): IrCall {
        val clientCallee = if (method.function.isNonSuspendingWithFlowReturn(declaration.stubClass.file)) {
            ctx.functions.rpcClientCallServerStreaming.symbol
        } else {
            ctx.functions.rpcClientCall.symbol
        }

        val call = irCall(
            callee = clientCallee,
            type = method.function.returnType,
            typeArgumentsCount = 1,
        ).apply {
            val rpcCallConstructor = irCallConstructor(
                callee = ctx.rpcCall.constructors.single(),
                typeArguments = emptyList(),
            ).apply {
                val callee = if (arguments.isEmpty()) {
                    ctx.functions.emptyArray
                } else {
                    ctx.irBuiltIns.arrayOf
                }

                val argumentsParameter = irCall(callee, type = ctx.arrayOfAnyNullable).apply arrayOfCall@{
                    if (arguments.isEmpty()) {
                        arguments {
                            types { +ctx.anyNullable }
                        }

                        return@arrayOfCall
                    }

                    val vararg = irVararg(
                        elementType = ctx.anyNullable,
                        values = arguments.memoryOptimizedMap { valueParameter ->
                            irGet(valueParameter)
                        },
                    )

                    arguments {
                        types {
                            +ctx.anyNullable
                        }

                        values {
                            +vararg
                        }
                    }
                }

                arguments {
                    values {
                        +irGetDescriptor()

                        +stringConst(method.function.name.asString())

                        +argumentsParameter

                        +irCallProperty(
                            clazz = stubClass,
                            property = stubIdProperty,
                            symbol = functionThisReceiver.symbol,
                        )
                    }
                }
            }

            arguments {
                dispatchReceiver = irCallProperty(
                    clazz = stubClass,
                    property = clientProperty,
                    symbol = functionThisReceiver.symbol,
                )

                types {
                    +method.function.returnType
                }

                values {
                    +rpcCallConstructor
                }
            }
        }

        return call
    }

    private fun irGetDescriptor(): IrExpression {
        val companion = stubClass.companionObject()
            ?: error("Expected companion object in ${stubClass.name}")

        return IrGetObjectValueImpl(
            startOffset = UNDEFINED_OFFSET,
            endOffset = UNDEFINED_OFFSET,
            type = companion.symbol.defaultType,
            symbol = companion.symbol,
        )
    }

    private fun irCallProperty(
        clazz: IrClass,
        property: IrProperty,
        type: IrType? = null,
        symbol: IrValueSymbol? = null,
    ): IrCall {
        return irCallProperty(
            receiver = IrGetValueImpl(
                startOffset = UNDEFINED_OFFSET,
                endOffset = UNDEFINED_OFFSET,
                type = type ?: clazz.defaultType,
                symbol = symbol
                    ?: clazz.thisReceiver?.symbol
                    ?: error("Expected thisReceiver for ${clazz.name.asString()}"),
            ),
            property = property,
        )
    }

    protected fun irCallProperty(receiver: IrExpression, property: IrProperty): IrCall {
        val getter = property.getterOrFail

        return IrCallImpl(
            startOffset = UNDEFINED_OFFSET,
            endOffset = UNDEFINED_OFFSET,
            type = getter.returnType,
            symbol = getter.symbol,
            origin = IrStatementOrigin.GET_PROPERTY,
            typeArgumentsCount = getter.typeParameters.size,
        ).apply {
            arguments {
                dispatchReceiver = receiver
            }
        }
    }

    protected var stubCompanionObject: IrClassSymbol by Delegates.notNull()
        private set
    protected var stubCompanionObjectThisReceiver: IrValueParameter by Delegates.notNull()
        private set

    /**
     * Companion object for the RPC service stub.
     * The empty object should already be generated
     *
     * ```kotlin
     *  companion object : RpcServiceDescriptor<MyProtocol, MyService>
     * ```
     */
    private fun IrClass.generateCompanionObject() {
        companionObject()?.apply {
            // clearing previous declarations, as they may break with the ones added here
            declarations.clear()

            stubCompanionObject = symbol
            stubCompanionObjectThisReceiver = thisReceiver
                ?: error("Stub companion object expected to have thisReceiver: ${name.asString()}")

            // descriptorClass extends RpcServiceDescriptor<Protocol, T>
            superTypes = listOf(descriptorType())

            generateCompanionObjectConstructor(ctx.pluginContext)

            generateCompanionObjectContent()

            generateAdditionalDescriptorContent()

            addAnyOverrides(ctx.rpcServiceDescriptor.owner)
        } ?: error("Expected companion object to be present")
    }

    private fun IrClass.generateCompanionObjectContent() {
        generateSimpleName()

        generateFqName()

        generateInvokators()

        generateCallablesProperty()

        generateGetCallableFunction()

        generateCreateInstanceFunction()
    }

    /**
     * `simpleName` property of the descriptor.
     *
     * ```kotlin
     * override val simpleName = "MyService"
     * ```
     */
    private fun IrClass.generateSimpleName() {
        generateStringOverriddenProperty(
            propertyName = Descriptor.SIMPLE_NAME,
            propertySymbol = ctx.properties.rpcServiceDescriptorSimpleName,
            value = descriptorSimpleName(),
        )
    }

    /**
     * `fqName` property of the descriptor.
     *
     * ```kotlin
     * override val fqName = "my.pkg.MyService"
     * ```
     */
    private fun IrClass.generateFqName() {
        generateStringOverriddenProperty(
            propertyName = Descriptor.FQ_NAME,
            propertySymbol = ctx.properties.rpcServiceDescriptorFqName,
            value = declaration.fqName,
        )
    }

    private val invokators = mutableMapOf<String, IrProperty>()

    private fun IrClass.generateInvokators() {
        declaration.methods.forEach { method ->
            generateInvokator(method)
        }
    }

    /**
     * Generates an invokator (`RpcInvokator`) for this method.
     *
     * For suspend methods:
     * ```kotlin
     * private val <method-name>Invokator = RpcInvokator.UnaryResponse<MyService> {
     *     service: MyService, arguments: Array<Any?> ->
     *
     *     service.<method-name>(arguments[0] as<?> <argument-type-1>, ...)
     * }
     * ```
     *
     * For flow methods:
     * ```kotlin
     * private val <method-name>Invokator = RpcInvokator.FlowResponse<MyService> {
     *     service: MyService, arguments: Array<Any?> ->
     *
     *     service.<method-name>(arguments[0] as<?> <argument-type-1>, ...)
     * }
     * ```
     *
     * Difference is:
     * - for RpcInvokator.UnaryResponse the lambda is `suspend` and returns Any?
     * - for RpcInvokator.FlowResponse the lambda is not `suspend` and returns Flow<Any?>
     *
     * Where:
     *  - `<method-name>` - the name of the method
     *  - <argument-type-k> - type of the kth argument
     */
    private fun IrClass.generateInvokator(method: ServiceDeclaration.Method) {
        invokators[method.name] = addProperty {
            name = Name.identifier("${method.name}Invokator")
            visibility = DescriptorVisibilities.PRIVATE
        }.apply {
            val returnsFlow = !method.function.isSuspend

            val propertyType = when {
                returnsFlow -> ctx.rpcInvokatorFlowResponse.typeWith(declaration.serviceType)
                else -> ctx.rpcInvokatorUnaryResponse.typeWith(declaration.serviceType)
            }

            val resultType = when {
                returnsFlow -> ctx.flow(declaration.stubClass.file).typeWith(ctx.anyNullable)
                else -> ctx.anyNullable
            }

            addBackingFieldUtil {
                visibility = DescriptorVisibilities.PRIVATE
                type = propertyType
                vsApi { isFinalVS = true }
            }.apply backingField@{
                val functionLambda = factory.buildFun {
                    origin = IrDeclarationOrigin.LOCAL_FUNCTION_FOR_LAMBDA
                    name = SpecialNames.ANONYMOUS
                    visibility = DescriptorVisibilities.LOCAL
                    modality = Modality.FINAL
                    returnType = resultType
                    if (!returnsFlow) {
                        isSuspend = true
                    }
                }.apply {
                    parent = this@backingField

                    val serviceParameter = addValueParameter {
                        name = Name.identifier("service")
                        type = declaration.serviceType
                    }

                    val argumentsParameter = addValueParameter {
                        name = Name.identifier("arguments")
                        type = ctx.arrayOfAnyNullable
                    }

                    body = irBuilder(ctx.pluginContext, symbol).irBlockBody {
                        val call = irCall(method.function).apply {
                            arguments {
                                dispatchReceiver = irGet(serviceParameter)

                                values {
                                    method.arguments.forEachIndexed { argIndex, arg ->
                                        val argument = irCall(
                                            callee = ctx.functions.arrayGet.symbol,
                                            type = ctx.anyNullable,
                                        ).apply {
                                            vsApi { originVS = IrStatementOrigin.GET_ARRAY_ELEMENT }

                                            arguments {
                                                dispatchReceiver = irGet(argumentsParameter)

                                                values {
                                                    +intConst(argIndex)
                                                }
                                            }
                                        }

                                        if (vsApi { arg.type.isNullableVS() }) {
                                            +irSafeAs(argument, arg.type)
                                        } else {
                                            +irAs(argument, arg.type)
                                        }
                                    }
                                }
                            }
                        }

                        +irReturn(call)
                    }
                }

                val lambdaType = when {
                    returnsFlow -> ctx.irBuiltIns.functionN(2).typeWith(
                        declaration.serviceType, // service
                        ctx.arrayOfAnyNullable, // data
                        resultType, // returnType
                    )

                    else -> ctx.irBuiltIns.suspendFunctionN(2).typeWith(
                        declaration.serviceType, // service
                        ctx.arrayOfAnyNullable, // data
                        resultType, // returnType
                    )
                }

                val lambda = IrFunctionExpressionImpl(
                    startOffset = UNDEFINED_OFFSET,
                    endOffset = UNDEFINED_OFFSET,
                    type = lambdaType,
                    origin = IrStatementOrigin.LAMBDA,
                    function = functionLambda,
                )

                initializer = factory.createExpressionBody(
                    IrTypeOperatorCallImpl(
                        startOffset = UNDEFINED_OFFSET,
                        endOffset = UNDEFINED_OFFSET,
                        type = propertyType,
                        operator = IrTypeOperator.SAM_CONVERSION,
                        typeOperand = propertyType,
                        argument = lambda,
                    )
                )
            }

            addDefaultGetter(this@generateInvokator, ctx.irBuiltIns) {
                visibility = DescriptorVisibilities.PRIVATE
            }
        }
    }

    protected var callables: IrProperty by Delegates.notNull()
        private set

    /**
     * Callable names map.
     * A map that holds an RpcCallable that describes it.
     *
     * ```kotlin
     *  override val callables: Map<String, RpcCallable<MyService>> = mapOf(
     *      Pair("<callable-name-1>", RpcCallable(...)),
     *      ...
     *      Pair("<callable-name-n>", RpcCallable(...)),
     *  )
     *
     *  // when n=0:
     *  override val callables: Map<String, RpcCallable<MyService>> = emptyMap()
     * ```
     *
     * Where:
     *  - `<callable-name-k>` - the name of the k-th callable in the service
     */
    private fun IrClass.generateCallablesProperty() {
        val interfaceProperty = ctx.properties.rpcServiceDescriptorCallables

        callables = generateMapProperty(
            propertyName = Descriptor.CALLABLES,
            values = declaration.methods.memoryOptimizedMap { callable ->
                stringConst(callable.name) to irRpcCallable(callable)
            },
            valueType = ctx.rpcCallable.typeWith(declaration.serviceType),
            addDefaultGetter = false,
        ).apply {
            // The property must be PUBLIC to match the interface declaration.
            // On Kotlin/Native, a PRIVATE property with a PUBLIC getter does not get
            // a proper itable entry, causing a segfault on interface dispatch (KRPC-560).
            visibility = DescriptorVisibilities.PUBLIC
            overriddenSymbols = listOf(interfaceProperty)

            addDefaultGetter(this@generateCallablesProperty, ctx.irBuiltIns) {
                visibility = DescriptorVisibilities.PUBLIC
                overriddenSymbols = listOf(interfaceProperty.owner.getterOrFail.symbol)
            }
        }
    }

    /**
     * A call to constructor of the RpcCallableDefault.
     *
     * ```kotlin
     * RpcCallableDefault<MyService>(
     *     name = "<callable-name>",
     *     returnType = RpcCall(typeOf<<callable-return-type>>()),
     *     invokator = <callable-invokator>,
     *     parameters = arrayOf( // or emptyArray()
     *         RpcParameter(
     *             "<method-parameter-name-1>",
     *             RpcCall(typeOf<<method-parameter-type-1>>(), <method-parameter-type-1>.annotations),
     *             <method-parameter-1-annotations-list>,
     *         ),
     *         ...
     *     ),
     * )
     *```
     *
     * Where:
     *  - `<callable-name>` - the name of the method (field)
     *  - `<callable-data-type>` - a method class for a method and `FieldDataObject` for fields
     *  - `<callable-return-type>` - the return type for the method and the field type for a field.
     *  For a non-suspending flow the return type is its element type
     *  - `<callable-invokator>` - an invokator, previously generated by [generateInvokators]
     *  - `<method-parameter-name-k>` - if a method, its k-th parameter name
     *  - `<method-parameter-type-k>` - if a method, its k-th parameter type
     */
    private fun irRpcCallable(method: ServiceDeclaration.Method): IrExpression {
        return IrConstructorCallImpl(
            startOffset = UNDEFINED_OFFSET,
            endOffset = UNDEFINED_OFFSET,
            type = ctx.rpcCallable.typeWith(declaration.serviceType),
            symbol = ctx.rpcCallableDefault.constructors.single(),
            typeArgumentsCount = 1,
            constructorTypeArgumentsCount = 1
        ).apply {
            putConstructorTypeArgument(0, declaration.serviceType)

            val returnType = method.unwrappedReturnType()

            val invokator = invokators[method.name]
                ?: error("Expected invokator for ${method.name} in ${declaration.service.name}")

            val parameters = method.arguments

            val callee = if (parameters.isEmpty()) {
                ctx.functions.emptyArray
            } else {
                ctx.irBuiltIns.arrayOf
            }

            val arrayParametersType = ctx.irBuiltIns.arrayClass.typeWith(
                ctx.rpcParameter.defaultType,
                Variance.OUT_VARIANCE,
            )

            val arrayOfCall = IrCallImpl(
                startOffset = UNDEFINED_OFFSET,
                endOffset = UNDEFINED_OFFSET,
                type = arrayParametersType,
                symbol = callee,
                typeArgumentsCount = 1,
            ).apply arrayOfCall@{
                if (parameters.isEmpty()) {
                    arguments {
                        types { +ctx.rpcParameter.defaultType }
                    }

                    return@arrayOfCall
                }

                val vararg = IrVarargImpl(
                    startOffset = UNDEFINED_OFFSET,
                    endOffset = UNDEFINED_OFFSET,
                    type = arrayParametersType,
                    varargElementType = ctx.rpcParameter.defaultType,
                    elements = parameters.memoryOptimizedMap { parameter ->
                        IrConstructorCallImpl(
                            startOffset = UNDEFINED_OFFSET,
                            endOffset = UNDEFINED_OFFSET,
                            type = ctx.rpcParameter.defaultType,
                            symbol = ctx.rpcParameterDefault.constructors.single(),
                            typeArgumentsCount = 0,
                            constructorTypeArgumentsCount = 0,
                        ).apply {
                            arguments {
                                values {
                                    +stringConst(parameter.value.name.asString())
                                    +irRpcTypeCall(parameter.type)
                                    +booleanConst(parameter.isOptional)
                                    +irListOfAnnotations(parameter.value)
                                }
                            }
                        }
                    },
                )

                arguments {
                    types {
                        +ctx.rpcParameter.defaultType
                    }

                    values {
                        +vararg
                    }
                }
            }

            arguments {
                values {
                    // name
                    +stringConst(method.name)

                    // returnType
                    +irRpcTypeCall(returnType)

                    // invokator
                    +irCallProperty(stubCompanionObject.owner, invokator)

                    // parameters
                    +arrayOfCall
                }
            }
        }
    }

    private fun irListOfAnnotations(container: IrAnnotationContainer): IrCallImpl {
        return irListOf(ctx.irBuiltIns.annotationType, container.annotations)
    }

    private fun IrSimpleFunction.isNonSuspendingWithFlowReturn(from: IrFile): Boolean {
        return returnType.classOrNull == ctx.flow(from) && !isSuspend
    }

    /**
     * Return type of the callable: the element type for a non-suspending function returning `Flow`,
     * the return type itself otherwise.
     *
     * Annotations of this type are the ones that apply to the callable's result (e.g. `@Serializable(with = ...)`).
     */
    protected fun ServiceDeclaration.Method.unwrappedReturnType(): IrType {
        return if (function.isNonSuspendingWithFlowReturn(declaration.stubClass.file)) {
            (function.returnType as IrSimpleType).arguments.single().typeOrFail
        } else {
            function.returnType
        }
    }

    /**
     * Accessor function for the `callables` property
     * Defined in `RpcServiceDescriptor`
     *
     * ```kotlin
     *  final override fun getCallable(name: String): RpcCallable<MyService>? = callables[name]
     * ```
     */
    private fun IrClass.generateGetCallableFunction() {
        generateGetFromStringMap(
            functionName = Descriptor.GET_CALLABLE,
            resultType = ctx.rpcCallable.createType(hasQuestionMark = true, emptyList()),
            overriddenSymbol = ctx.rpcServiceDescriptor.functionByName(Descriptor.GET_CALLABLE),
            mapProperty = callables,
        )
    }

    /**
     * Factory method for creating a new instance of RPC service
     *
     * ```kotlin
     *  final override fun createInstance(serviceId: Long, client: RpcClient): MyService {
     *      return `$rpcServiceStub`(serviceId, client)
     *  }
     * ```
     */
    private fun IrClass.generateCreateInstanceFunction() {
        addFunction {
            name = Name.identifier(Descriptor.CREATE_INSTANCE)
            visibility = DescriptorVisibilities.PUBLIC
            modality = Modality.OPEN
            returnType = declaration.serviceType
        }.apply {
            overriddenSymbols = listOf(ctx.rpcServiceDescriptor.functionByName(Descriptor.CREATE_INSTANCE))

            vsApi {
                dispatchReceiverParameterVS = stubCompanionObjectThisReceiver
                    .copyToVS(this@apply, origin = IrDeclarationOrigin.DEFINED)
            }

            val serviceId = addValueParameter {
                name = Name.identifier("serviceId")
                type = ctx.irBuiltIns.longType
            }

            val client = addValueParameter {
                name = Name.identifier("client")
                type = ctx.rpcClient.typeWith(
                    declaration.service.rpcMetaInformation(RpcNames.RPC_META_PROTOCOL_CLASS)
                )
            }

            body = irBuilder(ctx.pluginContext, symbol).irBlockBody {
                +irReturn(
                    irCallConstructor(
                        callee = stubClass.constructors.single().symbol,
                        typeArguments = emptyList(),
                    ).apply {
                        arguments {
                            values {
                                +irGet(serviceId)
                                +irGet(client)
                            }
                        }
                    }
                )
            }
        }
    }

    private fun addWithServiceDescriptorAnnotation() {
        val service = declaration.service

        val annotation = vsApi {
            IrAnnotationVS(
                startOffset = service.startOffset,
                endOffset = service.endOffset,
                type = ctx.withServiceDescriptor.defaultType,
                symbol = ctx.withServiceDescriptor.constructors.single(),
                typeArgumentsCount = 0,
                constructorTypeArgumentsCount = 0,
                valueArgumentsCount = 1,
            )
        }.apply {
            val companionObjectType = stubCompanionObject.defaultType
            arguments {
                values {
                    +IrClassReferenceImpl(
                        startOffset = startOffset,
                        endOffset = endOffset,
                        type = ctx.irBuiltIns.kClassClass.typeWith(companionObjectType),
                        symbol = stubCompanionObject,
                        classType = companionObjectType,
                    )
                }
            }
        }

        vsApi {
            service.addAnnotationVS(annotation)
        }
    }

    /**
     * IR call of the `RpcTypeDefault(KType, List<Annotation>)` constructor
     */
    private fun irRpcTypeCall(type: IrType): IrConstructorCallImpl {
        return IrConstructorCallImpl(
            startOffset = UNDEFINED_OFFSET,
            endOffset = UNDEFINED_OFFSET,
            type = ctx.rpcType.defaultType,
            symbol = ctx.rpcTypeDefault.constructors.single(),
            typeArgumentsCount = 0,
            constructorTypeArgumentsCount = 0,
        ).apply {
            arguments {
                values {
                    +irTypeOfCall(type)
                    +irListOfAnnotations(type)
                }
            }
        }
    }

    /**
     * IR call of the `typeOf<...>()` function
     */
    protected fun irTypeOfCall(type: IrType): IrCall {
        return IrCallImpl(
            startOffset = UNDEFINED_OFFSET,
            endOffset = UNDEFINED_OFFSET,
            type = ctx.kTypeClass.defaultType,
            symbol = ctx.functions.typeOf,
            typeArgumentsCount = 1,
        ).apply {
            arguments {
                types { +type }
            }
        }
    }

    protected fun irMapOf(
        keyType: IrType,
        valueType: IrType,
        elements: List<Pair<IrExpression, IrExpression>>,
        isEmpty: Boolean = elements.isEmpty(),
    ): IrCallImpl {
        return IrCallImpl(
            startOffset = UNDEFINED_OFFSET,
            endOffset = UNDEFINED_OFFSET,
            type = ctx.irBuiltIns.mapClass.typeWith(keyType, valueType),
            symbol = if (isEmpty) ctx.functions.emptyMap else ctx.functions.mapOf,
            typeArgumentsCount = 2,
        ).apply mapApply@{
            if (isEmpty) {
                arguments {
                    types {
                        +keyType
                        +valueType
                    }
                }

                return@mapApply
            }

            val pairType = ctx.pair.typeWith(keyType, valueType)

            val varargType = ctx.irBuiltIns.arrayClass.typeWith(pairType, Variance.OUT_VARIANCE)

            val vararg = IrVarargImpl(
                startOffset = UNDEFINED_OFFSET,
                endOffset = UNDEFINED_OFFSET,
                type = varargType,
                varargElementType = pairType,
                elements = elements.memoryOptimizedMap { (key, value) ->
                    IrCallImpl(
                        startOffset = UNDEFINED_OFFSET,
                        endOffset = UNDEFINED_OFFSET,
                        type = pairType,
                        symbol = ctx.functions.to,
                        typeArgumentsCount = 2,
                    ).apply {
                        arguments {
                            types {
                                +keyType
                                +valueType
                            }

                            extensionReceiver = key

                            values {
                                +value
                            }
                        }
                    }
                },
            )

            arguments {
                types {
                    +keyType
                    +valueType
                }

                values {
                    +vararg
                }
            }
        }
    }

    private fun irListOf(
        valueType: IrType,
        elements: List<IrExpression>,
        isEmpty: Boolean = elements.isEmpty(),
    ): IrCallImpl {
        return IrCallImpl(
            startOffset = UNDEFINED_OFFSET,
            endOffset = UNDEFINED_OFFSET,
            type = ctx.irBuiltIns.listClass.typeWith(valueType),
            symbol = if (isEmpty) ctx.functions.emptyList else ctx.functions.listOf,
            typeArgumentsCount = 1,
        ).apply listApply@{
            if (isEmpty) {
                arguments {
                    types {
                        +valueType
                    }
                }

                return@listApply
            }

            val varargType = ctx.irBuiltIns.arrayClass.typeWith(valueType, Variance.OUT_VARIANCE)

            val vararg = IrVarargImpl(
                startOffset = UNDEFINED_OFFSET,
                endOffset = UNDEFINED_OFFSET,
                type = varargType,
                varargElementType = valueType,
                elements = elements,
            )

            arguments {
                types {
                    +valueType
                }

                values {
                    +vararg
                }
            }
        }
    }

    private fun IrClass.generateStringOverriddenProperty(
        propertyName: String,
        propertySymbol: IrPropertySymbol,
        value: String,
    ) {
        addProperty {
            name = Name.identifier(propertyName)
            visibility = DescriptorVisibilities.PUBLIC
        }.apply {
            overriddenSymbols = listOf(propertySymbol)

            addBackingFieldUtil {
                visibility = DescriptorVisibilities.PRIVATE
                type = ctx.irBuiltIns.stringType
                vsApi { isFinalVS = true }
            }.apply {
                initializer = factory.createExpressionBody(
                    stringConst(value)
                )
            }

            addDefaultGetter(this@generateStringOverriddenProperty, ctx.irBuiltIns) {
                visibility = DescriptorVisibilities.PUBLIC
                overriddenSymbols = listOf(propertySymbol.owner.getterOrFail.symbol)
            }
        }
    }

    private fun IrClass.generateMapProperty(
        propertyName: String,
        values: List<Pair<IrExpression, IrExpression>>,
        valueType: IrType,
        addDefaultGetter: Boolean = true,
    ): IrProperty {
        return addProperty {
            name = Name.identifier(propertyName)
            visibility = DescriptorVisibilities.PRIVATE
            modality = Modality.FINAL
        }.apply {
            val mapType = ctx.irBuiltIns.mapClass.typeWith(ctx.irBuiltIns.stringType, valueType)

            addBackingFieldUtil {
                type = mapType
                vsApi { isFinalVS = true }
                visibility = DescriptorVisibilities.PRIVATE
            }.apply {
                val isEmpty = values.isEmpty()

                initializer = factory.createExpressionBody(
                    irMapOf(
                        keyType = ctx.irBuiltIns.stringType,
                        valueType = valueType,
                        elements = values,
                        isEmpty = isEmpty,
                    )
                )
            }

            if (addDefaultGetter) {
                addDefaultGetter(this@generateMapProperty, ctx.irBuiltIns) {
                    visibility = DescriptorVisibilities.PRIVATE
                }
            }
        }
    }

    private fun IrClass.generateGetFromStringMap(
        functionName: String,
        resultType: IrType,
        overriddenSymbol: IrSimpleFunctionSymbol,
        mapProperty: IrProperty,
    ) {
        addFunction {
            name = Name.identifier(functionName)
            visibility = DescriptorVisibilities.PUBLIC
            modality = Modality.FINAL
            returnType = resultType
        }.apply {
            overriddenSymbols = listOf(overriddenSymbol)

            val functionThisReceiver = vsApi {
                stubCompanionObjectThisReceiver.copyToVS(this@apply, origin = IrDeclarationOrigin.DEFINED)
            }.also {
                vsApi {
                    dispatchReceiverParameterVS = it
                }
            }

            val parameter = addValueParameter {
                name = Name.identifier("name")
                type = ctx.irBuiltIns.stringType
            }

            body = irBuilder(ctx.pluginContext, symbol).irBlockBody {
                +irReturn(
                    irCall(ctx.functions.mapGet.symbol, resultType).apply {
                        vsApi { originVS = IrStatementOrigin.GET_ARRAY_ELEMENT }

                        arguments {
                            dispatchReceiver = irCallProperty(
                                clazz = this@generateGetFromStringMap,
                                property = mapProperty,
                                symbol = functionThisReceiver.symbol,
                            )

                            values {
                                +irGet(parameter)
                            }
                        }
                    }
                )
            }
        }
    }

    // adds fake overrides for toString(), equals(), hashCode() for a class
    private fun IrClass.addAnyOverrides(parent: IrClass? = null) {
        val anyClass = ctx.irBuiltIns.anyClass.owner
        val overriddenClass = parent ?: anyClass

        addFunction {
            name = OperatorNameConventions.EQUALS
            visibility = DescriptorVisibilities.PUBLIC
            modality = Modality.OPEN
            origin = IrDeclarationOrigin.FAKE_OVERRIDE

            isOperator = true
            isFakeOverride = true
            returnType = ctx.irBuiltIns.booleanType
        }.apply {
            overriddenSymbols += overriddenClass.functions.single {
                it.name == OperatorNameConventions.EQUALS
            }.symbol

            vsApi {
                dispatchReceiverParameterVS = anyClass.copyThisReceiver(this@apply)
            }

            addValueParameter {
                name = Name.identifier("other")
                type = ctx.anyNullable
            }
        }

        addFunction {
            name = OperatorNameConventions.HASH_CODE
            visibility = DescriptorVisibilities.PUBLIC
            modality = Modality.OPEN
            origin = IrDeclarationOrigin.FAKE_OVERRIDE

            isFakeOverride = true
            returnType = ctx.irBuiltIns.intType
        }.apply {
            overriddenSymbols += overriddenClass.functions.single {
                it.name == OperatorNameConventions.HASH_CODE
            }.symbol

            vsApi {
                dispatchReceiverParameterVS = anyClass.copyThisReceiver(this@apply)
            }
        }

        addFunction {
            name = OperatorNameConventions.TO_STRING
            visibility = DescriptorVisibilities.PUBLIC
            modality = Modality.OPEN
            origin = IrDeclarationOrigin.FAKE_OVERRIDE

            isFakeOverride = true
            returnType = ctx.irBuiltIns.stringType
        }.apply {
            overriddenSymbols += overriddenClass.functions.single {
                it.name == OperatorNameConventions.TO_STRING
            }.symbol

            vsApi {
                dispatchReceiverParameterVS = anyClass.copyThisReceiver(this@apply)
            }
        }
    }

    private fun IrClass.copyThisReceiver(function: IrFunction) = vsApi {
        thisReceiver?.copyToVS(function, origin = IrDeclarationOrigin.DEFINED)
    }

    protected fun stringConst(value: String) = IrConstImpl.string(
        startOffset = UNDEFINED_OFFSET,
        endOffset = UNDEFINED_OFFSET,
        type = ctx.irBuiltIns.stringType,
        value = value,
    )

    protected fun nullConst(type: IrType) = IrConstImpl.constNull(
        startOffset = UNDEFINED_OFFSET,
        endOffset = UNDEFINED_OFFSET,
        type = type,
    )

    protected fun intConst(value: Int) = IrConstImpl.int(
        startOffset = UNDEFINED_OFFSET,
        endOffset = UNDEFINED_OFFSET,
        type = ctx.irBuiltIns.intType,
        value = value,
    )

    protected fun booleanConst(value: Boolean) = IrConstImpl.boolean(
        startOffset = UNDEFINED_OFFSET,
        endOffset = UNDEFINED_OFFSET,
        type = ctx.irBuiltIns.booleanType,
        value = value,
    )

    protected inline fun <T> vsApi(body: VersionSpecificApi.() -> T): T {
        return ctx.versionSpecificApi.body()
    }

    protected inline fun IrMemberAccessExpression<*>.arguments(body: IrMemberAccessExpressionBuilder.() -> Unit) {
        return arguments(ctx.versionSpecificApi, body)
    }

    fun IrBuilderWithScope.irSafeAs(argument: IrExpression, type: IrType) =
        IrTypeOperatorCallImpl(startOffset, endOffset, type, IrTypeOperator.SAFE_CAST, type, argument)

    fun IrClass.rpcMetaInformation(name: Name): IrType {
        return annotations
            .firstNotNullOf { annotation ->
                val annotationClass = annotation.symbol.owner.parentAsClass

                val rpcMeta = annotationClass.getAnnotation(RpcClassId.rpcMetaAnnotation.asSingleFqName())
                    ?: return@firstNotNullOf null

                val argument = vsApi { rpcMeta.getValueArgumentVS(name) } as? IrClassReference
                    ?: return@firstNotNullOf null

                argument.classType
            }
    }
}
