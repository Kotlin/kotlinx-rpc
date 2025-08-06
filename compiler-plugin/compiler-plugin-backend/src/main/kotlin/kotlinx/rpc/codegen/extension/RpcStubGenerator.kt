/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.extension

import kotlinx.rpc.codegen.VersionSpecificApi
import kotlinx.rpc.codegen.common.RpcClassId
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
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
import org.jetbrains.kotlin.ir.builders.declarations.addProperty
import org.jetbrains.kotlin.ir.builders.declarations.addValueParameter
import org.jetbrains.kotlin.ir.builders.declarations.buildFun
import org.jetbrains.kotlin.ir.builders.irAs
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irCallConstructor
import org.jetbrains.kotlin.ir.builders.irDelegatingConstructorCall
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irReturn
import org.jetbrains.kotlin.ir.builders.irVararg
import org.jetbrains.kotlin.ir.declarations.IrAnnotationContainer
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
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
import org.jetbrains.kotlin.ir.expressions.impl.IrInstanceInitializerCallImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrTypeOperatorCallImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrVarargImpl
import org.jetbrains.kotlin.ir.expressions.putConstructorTypeArgument
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrPropertySymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.IrSymbol
import org.jetbrains.kotlin.ir.symbols.IrValueSymbol
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.types.createType
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.ir.util.classId
import org.jetbrains.kotlin.ir.util.companionObject
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.defaultType
import org.jetbrains.kotlin.ir.util.dumpKotlinLike
import org.jetbrains.kotlin.ir.util.functions
import org.jetbrains.kotlin.ir.util.getPropertyGetter
import org.jetbrains.kotlin.ir.util.isObject
import org.jetbrains.kotlin.ir.util.packageFqName
import org.jetbrains.kotlin.ir.util.primaryConstructor
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
    const val FQ_NAME = "fqName"
    const val SIMPLE_NAME = "simpleName"
    const val GET_CALLABLE = "getCallable"
    const val CREATE_INSTANCE = "createInstance"
}

private object GrpcDescriptor {
    const val DELEGATE = "delegate"
}

@Suppress("detekt.LargeClass", "detekt.TooManyFunctions")
internal class RpcStubGenerator(
    private val declaration: ServiceDeclaration,
    private val ctx: RpcIrContext,
    @Suppress("unused")
    private val logger: MessageCollector,
) {
    private fun irBuilder(symbol: IrSymbol): DeclarationIrBuilder =
        DeclarationIrBuilder(ctx.pluginContext, symbol, symbol.owner.startOffset, symbol.owner.endOffset)

    private var stubClass: IrClass by Delegates.notNull()
    private var stubClassThisReceiver: IrValueParameter by Delegates.notNull()

    fun generate() {
        generateStubClass()

        addAssociatedObjectAnnotationIfPossible()
    }

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
                type = ctx.rpcClient.defaultType
            }

            addDefaultConstructor(this)
        }
    }

    private fun IrClass.generateStubContent() {
        generateProperties()

        generateMethods()

        generateCompanionObject()

        addAnyOverrides(declaration.service)
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
        clientProperty = addConstructorProperty(Stub.CLIENT, ctx.rpcClient.defaultType, clientValueParameter)
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
    @Suppress(
        "detekt.NestedBlockDepth",
        "detekt.LongMethod",
    )
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

            body = irBuilder(symbol).irBlockBody {
                if (method.function.isNonSuspendingWithFlowReturn()) {
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
    @Suppress("detekt.NestedBlockDepth")
    private fun IrBlockBodyBuilder.irRpcMethodClientCall(
        method: ServiceDeclaration.Method,
        functionThisReceiver: IrValueParameter,
        arguments: List<IrValueParameter>,
    ): IrCall {
        val clientCallee = if (method.function.isNonSuspendingWithFlowReturn()) {
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

    private fun irCallProperty(receiver: IrExpression, property: IrProperty): IrCall {
        val getter = property.getterOrFail

        return vsApi {
            IrCallImplVS(
                startOffset = UNDEFINED_OFFSET,
                endOffset = UNDEFINED_OFFSET,
                type = getter.returnType,
                symbol = getter.symbol,
                origin = IrStatementOrigin.GET_PROPERTY,
                valueArgumentsCount = vsApi { getter.valueParametersVS() }.size,
                typeArgumentsCount = getter.typeParameters.size,
            )
        }.apply {
            arguments {
                dispatchReceiver = receiver
            }
        }
    }

    private var stubCompanionObject: IrClassSymbol by Delegates.notNull()
    private var stubCompanionObjectThisReceiver: IrValueParameter by Delegates.notNull()

    /**
     * Companion object for the RPC service stub.
     * The empty object should already be generated
     *
     * ```kotlin
     *  companion object : RpcServiceDescriptor<MyService>
     * ```
     */
    private fun IrClass.generateCompanionObject() {
        companionObject()?.apply {
            // clearing previous declarations, as they may break with the ones added here
            declarations.clear()

            stubCompanionObject = symbol
            stubCompanionObjectThisReceiver = thisReceiver
                ?: error("Stub companion object expected to have thisReceiver: ${name.asString()}")

            superTypes = listOfNotNull(
                ctx.rpcServiceDescriptor.typeWith(declaration.serviceType),
                if (declaration.isGrpc) ctx.grpcServiceDescriptor.typeWith(declaration.serviceType) else null,
            )

            generateCompanionObjectConstructor()

            generateCompanionObjectContent()

            addAnyOverrides(ctx.rpcServiceDescriptor.owner)
        } ?: error("Expected companion object to be present")
    }

    private fun IrClass.generateCompanionObjectConstructor() {
        // default object constructor
        addConstructor {
            name = this@generateCompanionObjectConstructor.name
            isPrimary = true
            visibility = DescriptorVisibilities.PRIVATE
        }.apply {
            addDefaultConstructor(this)
        }
    }

    private fun IrClass.generateCompanionObjectContent() {
        generateSimpleName()

        generateFqName()

        generateInvokators()

        generateCallablesProperty()

        generateGetCallableFunction()

        generateCallablesProperty()

        generateCreateInstanceFunction()

        if (declaration.isGrpc) {
            generateGrpcDelegateProperty()
        }
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
            value = declaration.simpleName,
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
        declaration.methods.forEach { callable ->
            generateInvokator(callable)
        }
    }

    /**
     * Generates an invokator (`RpcInvokator`) for this callable.
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
    @Suppress(
        "detekt.NestedBlockDepth",
        "detekt.LongMethod",
        "detekt.CyclomaticComplexMethod",
    )
    private fun IrClass.generateInvokator(callable: ServiceDeclaration.Callable) {
        check(callable is ServiceDeclaration.Method) {
            "Only methods are allowed here"
        }

        invokators[callable.name] = addProperty {
            name = Name.identifier("${callable.name}Invokator")
            visibility = DescriptorVisibilities.PRIVATE
        }.apply {
            val returnsFlow = !callable.function.isSuspend

            val propertyType = when {
                returnsFlow -> ctx.rpcInvokatorFlowResponse.typeWith(declaration.serviceType)
                else -> ctx.rpcInvokatorUnaryResponse.typeWith(declaration.serviceType)
            }

            val resultType = when {
                returnsFlow -> ctx.flow.typeWith(ctx.anyNullable)
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

                    body = irBuilder(symbol).irBlockBody {
                        val call = irCall(callable.function).apply {
                            arguments {
                                dispatchReceiver = irGet(serviceParameter)

                                values {
                                    callable.arguments.forEachIndexed { argIndex, arg ->
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

    private var callables: IrProperty by Delegates.notNull()

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
        val interfaceProperty = ctx.rpcServiceDescriptor.findPropertyByName(Descriptor.CALLABLES)
            ?: error("Expected RpcServiceDescriptor.callables property to exist")

        callableMap = generateMapProperty(
            propertyName = Descriptor.CALLABLES,
            values = declaration.methods.memoryOptimizedMap { callable ->
                stringConst(callable.name) to irRpcCallable(callable)
            },
            valueType = ctx.rpcCallable.typeWith(declaration.serviceType),
        ).apply {
            overriddenSymbols = listOf(interfaceProperty)

            addDefaultGetter(this@generateCallablesProperty, ctx.irBuiltIns) {
                visibility = DescriptorVisibilities.PUBLIC
                overriddenSymbols = listOf(ctx.rpcServiceDescriptor.getPropertyGetter(Descriptor.CALLABLES)!!)
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
    private fun irRpcCallable(callable: ServiceDeclaration.Callable): IrExpression {
        return vsApi {
            IrConstructorCallImplVS(
                startOffset = UNDEFINED_OFFSET,
                endOffset = UNDEFINED_OFFSET,
                type = ctx.rpcCallable.typeWith(declaration.serviceType),
                symbol = ctx.rpcCallableDefault.constructors.single(),
                typeArgumentsCount = 1,
                valueArgumentsCount = if (declaration.isGrpc) 5 else 4,
                constructorTypeArgumentsCount = 1,
            )
        }.apply {
            putConstructorTypeArgument(0, declaration.serviceType)

            callable as ServiceDeclaration.Method

            val returnType = when {
                callable.function.isNonSuspendingWithFlowReturn() -> {
                    (callable.function.returnType as IrSimpleType).arguments.single().typeOrFail
                }

                else -> {
                    callable.function.returnType
                }
            }

            val invokator = invokators[callable.name]
                ?: error("Expected invokator for ${callable.name} in ${declaration.service.name}")

            val parameters = callable.arguments

            val callee = if (parameters.isEmpty()) {
                ctx.functions.emptyArray
            } else {
                ctx.irBuiltIns.arrayOf
            }

            val arrayParametersType = ctx.irBuiltIns.arrayClass.typeWith(
                ctx.rpcParameter.defaultType,
                Variance.OUT_VARIANCE,
            )

            val arrayOfCall = vsApi {
                IrCallImplVS(
                    startOffset = UNDEFINED_OFFSET,
                    endOffset = UNDEFINED_OFFSET,
                    type = arrayParametersType,
                    symbol = callee,
                    typeArgumentsCount = 1,
                    valueArgumentsCount = if (parameters.isEmpty()) 0 else 1,
                )
            }.apply arrayOfCall@{
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
                        vsApi {
                            IrConstructorCallImplVS(
                                startOffset = UNDEFINED_OFFSET,
                                endOffset = UNDEFINED_OFFSET,
                                type = ctx.rpcParameter.defaultType,
                                symbol = ctx.rpcParameterDefault.constructors.single(),
                                typeArgumentsCount = 0,
                                constructorTypeArgumentsCount = 0,
                                valueArgumentsCount = 3,
                            )
                        }.apply {
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
                    +stringConst(callable.name)

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

    private fun IrSimpleFunction.isNonSuspendingWithFlowReturn(): Boolean {
        return returnType.classOrNull == ctx.flow && !isSuspend
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
            mapProperty = callableMap,
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
                type = ctx.rpcClient.defaultType
            }

            body = irBuilder(symbol).irBlockBody {
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

    /**
     * ```kotlin
     * override fun delegate(resolver: MessageCodecResolver): GrpcServiceDelegate {
     *     val methodDescriptorMap = ...
     *     val serviceDescriptor = ...
     *
     *     return GrpcServiceDelegate(methodDescriptorMap, serviceDescriptor)
     * }
     * ```
     */
    private fun IrClass.generateGrpcDelegateProperty() {
        addFunction {
            name = Name.identifier(GrpcDescriptor.DELEGATE)
            modality = Modality.FINAL
            visibility = DescriptorVisibilities.PUBLIC
            returnType = ctx.grpcServiceDelegate.defaultType
        }.apply delegate@{
            overriddenSymbols = listOf(ctx.functions.grpcServiceDescriptorDelegate.symbol)

            vsApi {
                dispatchReceiverParameterVS = stubCompanionObjectThisReceiver
                    .copyToVS(this@delegate, origin = IrDeclarationOrigin.DEFINED)
            }

            val resolver = addValueParameter {
                name = Name.identifier("resolver")
                type = ctx.grpcMessageCodecResolver.defaultType
            }

            body = irBuilder(symbol).irBlockBody {
                val methodDescriptorMap = irTemporary(
                    value = irMethodDescriptorMap(resolver),
                    nameHint = "methodDescriptorMap",
                )

                val serviceDescriptor = irTemporary(
                    value = irServiceDescriptor(methodDescriptorMap),
                    nameHint = "serviceDescriptor",
                )

                +irReturn(
                    irCall(
                        callee = ctx.grpcServiceDelegate.owner.primaryConstructor!!.symbol,
                        type = ctx.grpcServiceDelegate.defaultType,
                    ).apply {
                        arguments {
                            values {
                                +irGet(methodDescriptorMap)
                                +irGet(serviceDescriptor)
                            }
                        }
                    }
                )
            }
        }
    }

    /**
     * A map that holds MethodDescriptors.
     *
     * ```kotlin
     *  mapOf<String, MethodDescriptors<*, *>>(
     *      Pair("<callable-name-1>", methodDescriptor(...)),
     *      ...
     *      Pair("<callable-name-n>", methodDescriptor(...)),
     *  )
     *
     *  // when n=0:
     *  emptyMap<String, MethodDescriptors<*, *>>()
     * ```
     *
     * Where:
     *  - `<callable-name-k>` - the name of the k-th callable in the service
     */
    private fun irMethodDescriptorMap(resolver: IrValueParameter): IrCallImpl {
        return irMapOf(
            keyType = ctx.irBuiltIns.stringType,
            valueType = ctx.grpcPlatformMethodDescriptor.starProjectedType,
            declaration.methods.memoryOptimizedMap { callable ->
                stringConst(callable.name) to irMethodDescriptor(callable, resolver)
            },
        )
    }

    /**
     * Ir call to `serviceDescriptor`
     *
     * ```kotlin
     * serviceDescriptor(
     *     name = MyService, // simpleName
     *     methods = methodDescriptorMap.values, // Collection<MethodDescriptor<*, *>>
     *     schemaDescriptor = null, // for now, only null
     * )
     * ```
     */
    private fun irServiceDescriptor(methodDescriptorMap: IrVariable): IrCallImpl {
        return vsApi {
            IrCallImplVS(
                startOffset = UNDEFINED_OFFSET,
                endOffset = UNDEFINED_OFFSET,
                type = ctx.grpcPlatformServiceDescriptor.defaultType,
                symbol = ctx.functions.serviceDescriptor,
                typeArgumentsCount = 0,
                valueArgumentsCount = 3,
            )
        }.apply {
            arguments {
                values {
                    +stringConst(declaration.simpleName)

                    +irCallProperty(
                        receiver = IrGetValueImpl(
                            startOffset = UNDEFINED_OFFSET,
                            endOffset = UNDEFINED_OFFSET,
                            type = methodDescriptorMap.type,
                            symbol = methodDescriptorMap.symbol,
                        ),
                        property = ctx.properties.mapValues.owner,
                    )

                    +nullConst(ctx.anyNullable)
                }
            }
        }
    }

    /**
     * gRPC Platform MethodDescriptor call
     *
     * ```kotlin
     * // In scope: resolver: MessageCodecResolver
     *
     * methodDescriptor<<request-type>, <response-type>>(
     *     fullMethodName = "${descriptor.simpleName}/${callable.name}",
     *     requestCodec = <request-codec>,
     *     responseCodec = <response-codec>,
     *     type = MethodType.<method-type>,
     *     schemaDescriptor = null, // null for now
     *     // todo understand these values
     *     idempotent = true,
     *     safe = true,
     *     sampledToLocalTracing = true,
     * )
     * ```
     *
     * Where:
     *   - <request-type> - the type of the request, namely the first parameter, unwrapped if a Flow
     *   - <response-type> - the type of the response, unwrapped if a Flow
     *   - <method-name> - the name of the method
     *   - <method-type> - one of MethodType.UNARY, MethodType.SERVER_STREAMING,
     *   MethodType.CLIENT_STREAMING, MethodType.BIDI_STREAMING
     *   - <request-codec>/<response-codec> - a MessageCodec getter, see [irCodec]
     */
    private fun irMethodDescriptor(callable: ServiceDeclaration.Callable, resolver: IrValueParameter): IrCall {
        check(callable is ServiceDeclaration.Method) {
            "Only methods are allowed here"
        }

        check(callable.arguments.size == 1) {
            "Only single argument methods are allowed here"
        }

        val requestParameterType = callable.arguments[0].type
        val responseParameterType = callable.function.returnType

        val requestType: IrType = requestParameterType.unwrapFlow()
        val responseType: IrType = responseParameterType.unwrapFlow()

        val methodDescriptorType = ctx.grpcPlatformMethodDescriptor.typeWith(requestType, responseType)

        return vsApi {
            IrCallImplVS(
                startOffset = UNDEFINED_OFFSET,
                endOffset = UNDEFINED_OFFSET,
                type = methodDescriptorType,
                symbol = ctx.functions.methodDescriptor,
                typeArgumentsCount = 2,
                valueArgumentsCount = 8,
            )
        }.apply {
            arguments {
                types {
                    +requestType
                    +responseType
                }

                values {
                    // fullMethodName
                    +stringConst("${declaration.simpleName}/${callable.name}")

                    // requestCodec
                    +irCodec(requestType, resolver)

                    // responseCodec
                    +irCodec(responseType, resolver)

                    // type
                    +IrGetEnumValueImpl(
                        startOffset = UNDEFINED_OFFSET,
                        endOffset = UNDEFINED_OFFSET,
                        type = ctx.grpcPlatformMethodType.defaultType,
                        symbol = when {
                            requestParameterType.classOrNull == ctx.flow && responseParameterType.classOrNull == ctx.flow -> {
                                ctx.grpcPlatformMethodTypeBidiStreaming
                            }

                            requestParameterType.classOrNull == ctx.flow && responseParameterType.classOrNull != ctx.flow -> {
                                ctx.grpcPlatformMethodTypeClientStreaming
                            }

                            requestParameterType.classOrNull != ctx.flow && responseParameterType.classOrNull == ctx.flow -> {
                                ctx.grpcPlatformMethodTypeServerStreaming
                            }

                            else -> {
                                ctx.grpcPlatformMethodTypeUnary
                            }
                        },
                    )

                    // schemaDescriptor
                    +nullConst(ctx.anyNullable)

                    // todo figure out these
                    // idempotent
                    +booleanConst(true)

                    // safe
                    +booleanConst(true)

                    // sampledToLocalTracing
                    +booleanConst(true)
                }
            }
        }
    }

    /**
     * If [type] is annotated with [RpcIrContext.withCodecAnnotation],
     * we use its codec object
     *
     * If not, use [resolver].resolve()
     */
    private fun irCodec(type: IrType, resolver: IrValueParameter): IrExpression {
        val owner = type.classOrFail.owner
        val protobufMessage = owner.getAnnotation(ctx.withCodecAnnotation.owner.kotlinFqName)

        return if (protobufMessage != null) {
            val classReference = protobufMessage.arguments.single() as? IrClassReference
                ?: error("Expected IrClassReference for ${ctx.withCodecAnnotation.owner.kotlinFqName} parameter")

            val codec = classReference.classType

            IrGetObjectValueImpl(
                startOffset = UNDEFINED_OFFSET,
                endOffset = UNDEFINED_OFFSET,
                type = codec,
                symbol = codec.classOrFail,
            )
        } else {
            vsApi {
                IrCallImplVS(
                    startOffset = UNDEFINED_OFFSET,
                    endOffset = UNDEFINED_OFFSET,
                    type = ctx.grpcMessageCodec.typeWith(type),
                    symbol = ctx.functions.grpcMessageCodecResolverResolve.symbol,
                    typeArgumentsCount = 0,
                    valueArgumentsCount = 1,
                )
            }.apply {
                arguments {
                    dispatchReceiver = IrGetValueImpl(
                        startOffset = UNDEFINED_OFFSET,
                        endOffset = UNDEFINED_OFFSET,
                        type = resolver.type,
                        symbol = resolver.symbol,
                    )

                    values {
                        +irTypeOfCall(type)
                    }
                }
            }
        }
    }

    // Associated object annotation works on JS, WASM, and Native platforms.
    // See https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/find-associated-object.html
    private fun addAssociatedObjectAnnotationIfPossible() {
        if (ctx.isJsTarget() || ctx.isNativeTarget() || ctx.isWasmTarget()) {
            addAssociatedObjectAnnotation()
        }
    }

    private fun addAssociatedObjectAnnotation() {
        val service = declaration.service

        service.annotations += vsApi {
            IrConstructorCallImplVS(
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
    }

    /**
     * IR call of the `RpcType(KType, List<Annotation>)` function
     */
    private fun irRpcTypeCall(type: IrType): IrConstructorCallImpl {
        // todo change to extension after KRPC-178
        val withSerializableAnnotations = type.annotations.any {
            it.type.isSerializableAnnotation()
        }

        return vsApi {
            IrConstructorCallImplVS(
                startOffset = UNDEFINED_OFFSET,
                endOffset = UNDEFINED_OFFSET,
                type = ctx.rpcType.defaultType,
                symbol = (if (withSerializableAnnotations) ctx.rpcTypeKrpc else ctx.rpcTypeDefault)
                    .constructors.single(),
                typeArgumentsCount = 0,
                valueArgumentsCount = if (withSerializableAnnotations) 3 else 2,
                constructorTypeArgumentsCount = 0,
            )
        }.apply {
            arguments {
                values {
                    +irTypeOfCall(type)
                    +irListOfAnnotations(type)

                    if (withSerializableAnnotations) {
                        +irMapOf(
                            keyType = ctx.kSerializerAnyNullableKClass,
                            valueType = ctx.kSerializerAnyNullable,
                            elements = type.annotations
                                .filter { it.type.isSerializableAnnotation() }
                                .memoryOptimizedMap {
                                    val kClassValue = vsApi { it.argumentsVS }.singleOrNull()
                                            as? IrClassReference
                                        ?: error("Expected single not null value parameter of KSerializer::class for @Serializable annotation on type '${type.dumpKotlinLike()}'")

                                    kClassValue to kClassValue.classType.irCreateInstance()
                                }
                        )
                    }
                }
            }
        }
    }

    private fun IrType.irCreateInstance(): IrExpression {
        val classSymbol =
            classOrNull ?: error("Expected class type for type to create instance '${dumpKotlinLike()}'")

        return if (classSymbol.owner.isObject) {
            IrGetObjectValueImpl(
                startOffset = UNDEFINED_OFFSET,
                endOffset = UNDEFINED_OFFSET,
                type = this,
                symbol = classSymbol,
            )
        } else {
            val constructor = classSymbol.owner.primaryConstructor
                ?: error("Expected primary constructor for a serializer '${dumpKotlinLike()}'")

            if (vsApi { constructor.parametersVS.isNotEmpty() }) {
                error(
                    "Primary constructor for a serializer '${dumpKotlinLike()}' can't have parameters: " +
                            vsApi { constructor.parametersVS }.joinToString { it.dumpKotlinLike() }
                )
            }

            vsApi {
                IrConstructorCallImplVS(
                    startOffset = UNDEFINED_OFFSET,
                    endOffset = UNDEFINED_OFFSET,
                    type = ctx.rpcType.defaultType,
                    symbol = constructor.symbol,
                    typeArgumentsCount = constructor.typeParameters.size,
                    valueArgumentsCount = 0,
                    constructorTypeArgumentsCount = constructor.typeParameters.size,
                )
            }.apply {
                arguments {
                    types {
                        repeat(classSymbol.owner.typeParameters.size) {
                            +ctx.anyNullable
                        }
                    }
                }
            }
        }
    }

    private fun IrType.isSerializableAnnotation(): Boolean =
        classOrNull?.owner?.classId == RpcClassId.serializableAnnotation

    /**
     * IR call of the `typeOf<...>()` function
     */
    private fun irTypeOfCall(type: IrType): IrCall {
        return vsApi {
            IrCallImplVS(
                startOffset = UNDEFINED_OFFSET,
                endOffset = UNDEFINED_OFFSET,
                type = ctx.kTypeClass.defaultType,
                symbol = ctx.functions.typeOf,
                typeArgumentsCount = 1,
                valueArgumentsCount = 0,
            )
        }.apply {
            arguments {
                types { +type }
            }
        }
    }

    // default constructor implementation
    private fun IrClass.addDefaultConstructor(constructor: IrConstructor) {
        constructor.body = irBuilder(constructor.symbol).irBlockBody {
            +irDelegatingConstructorCall(context.irBuiltIns.anyClass.owner.constructors.single())
            +IrInstanceInitializerCallImpl(
                startOffset = startOffset,
                endOffset = endOffset,
                classSymbol = this@addDefaultConstructor.symbol,
                type = context.irBuiltIns.unitType,
            )
        }
    }

    private fun irMapOf(
        keyType: IrType,
        valueType: IrType,
        elements: List<Pair<IrExpression, IrExpression>>,
        isEmpty: Boolean = elements.isEmpty(),
    ): IrCallImpl {
        return vsApi {
            IrCallImplVS(
                startOffset = UNDEFINED_OFFSET,
                endOffset = UNDEFINED_OFFSET,
                type = ctx.irBuiltIns.mapClass.typeWith(keyType, valueType),
                symbol = if (isEmpty) ctx.functions.emptyMap else ctx.functions.mapOf,
                typeArgumentsCount = 2,
                valueArgumentsCount = if (isEmpty) 0 else 1,
            )
        }.apply mapApply@{
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
                    vsApi {
                        IrCallImplVS(
                            startOffset = UNDEFINED_OFFSET,
                            endOffset = UNDEFINED_OFFSET,
                            type = pairType,
                            symbol = ctx.functions.to,
                            typeArgumentsCount = 2,
                            valueArgumentsCount = 1,
                        )
                    }.apply {
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
        return vsApi {
            IrCallImplVS(
                startOffset = UNDEFINED_OFFSET,
                endOffset = UNDEFINED_OFFSET,
                type = ctx.irBuiltIns.listClass.typeWith(valueType),
                symbol = if (isEmpty) ctx.functions.emptyList else ctx.functions.listOf,
                typeArgumentsCount = 1,
                valueArgumentsCount = if (isEmpty) 0 else 1,
            )
        }.apply listApply@{
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

            addDefaultGetter(this@generateMapProperty, ctx.irBuiltIns) {
                visibility = DescriptorVisibilities.PRIVATE
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

            body = irBuilder(symbol).irBlockBody {
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

    private fun stringConst(value: String) = IrConstImpl.string(
        startOffset = UNDEFINED_OFFSET,
        endOffset = UNDEFINED_OFFSET,
        type = ctx.irBuiltIns.stringType,
        value = value,
    )

    private fun nullConst(type: IrType) = IrConstImpl.constNull(
        startOffset = UNDEFINED_OFFSET,
        endOffset = UNDEFINED_OFFSET,
        type = type,
    )

    private fun intConst(value: Int) = IrConstImpl.int(
        startOffset = UNDEFINED_OFFSET,
        endOffset = UNDEFINED_OFFSET,
        type = ctx.irBuiltIns.intType,
        value = value,
    )

    private fun booleanConst(value: Boolean) = IrConstImpl.boolean(
        startOffset = UNDEFINED_OFFSET,
        endOffset = UNDEFINED_OFFSET,
        type = ctx.irBuiltIns.booleanType,
        value = value,
    )

    private fun IrType.unwrapFlow(): IrType {
        return if (classOrNull == ctx.flow) {
            (this as IrSimpleType).arguments[0].typeOrFail
        } else {
            this
        }
    }

    private inline fun <T> vsApi(body: VersionSpecificApi.() -> T): T {
        return ctx.versionSpecificApi.body()
    }

    private inline fun IrMemberAccessExpression<*>.arguments(body: IrMemberAccessExpressionBuilder.() -> Unit) {
        return arguments(ctx.versionSpecificApi, body)
    }

    fun IrBuilderWithScope.irSafeAs(argument: IrExpression, type: IrType) =
        IrTypeOperatorCallImpl(startOffset, endOffset, type, IrTypeOperator.SAFE_CAST, type, argument)
}
