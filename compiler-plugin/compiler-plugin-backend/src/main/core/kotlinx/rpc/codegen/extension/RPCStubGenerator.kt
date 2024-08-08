/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.extension

import kotlinx.rpc.codegen.VersionSpecificApi
import kotlinx.rpc.codegen.common.rpcMethodClassName
import kotlinx.rpc.codegen.common.rpcMethodClassNameKsp
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.backend.jvm.functionByName
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.descriptors.DescriptorVisibility
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.builders.declarations.*
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin
import org.jetbrains.kotlin.ir.expressions.impl.*
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrSymbol
import org.jetbrains.kotlin.ir.symbols.IrValueSymbol
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.SpecialNames
import org.jetbrains.kotlin.types.Variance
import org.jetbrains.kotlin.util.OperatorNameConventions
import kotlin.properties.Delegates

private const val CLIENT_PROPERTY = "__rpc_client"
private const val STUB_ID_PROPERTY = "__rpc_stub_id"
private const val METHOD_NAMES_PROPERTY = "methodNames"
private const val METHOD_TYPE_OF_FUNCTION = "methodTypeOf"
private const val WITH_CLIENT_METHOD = "withClient"
private const val RPC_FIELDS_METHOD = "rpcFields"

@Suppress("detekt.LargeClass", "detekt.TooManyFunctions")
internal class RPCStubGenerator(
    private val declaration: ServiceDeclaration,
    private val ctx: RPCIrContext,
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
     * class MyServiceStub(private val __rpc_stub_id: Long, private val __rpc_client: RPCClient) : MyService
     * ```
     */
    private fun IrClass.generateStubConstructor() {
        addConstructor {
            name = this@generateStubConstructor.name
            isPrimary = true
        }.apply {
            stubIdValueParameter = addValueParameter {
                name = Name.identifier(STUB_ID_PROPERTY)
                type = ctx.irBuiltIns.longType
            }

            clientValueParameter = addValueParameter {
                name = Name.identifier(CLIENT_PROPERTY)
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

        coroutineContextProperty()

        declaration.fields.forEach {
            rpcFlowField(it)
        }
    }

    private var stubIdProperty: IrProperty by Delegates.notNull()

    /**
     * __rpc_stub_id property from the constructor
     *
     * ```kotlin
     * class MyServiceStub(private val __rpc_stub_id: Long, private val __rpc_client: RPCClient) : MyService
     * ```
     */
    private fun IrClass.stubIdProperty() {
        stubIdProperty = addConstructorProperty(STUB_ID_PROPERTY, ctx.irBuiltIns.longType, stubIdValueParameter)
    }

    private var clientProperty: IrProperty by Delegates.notNull()

    /**
     * __rpc_client property from the constructor
     *
     * ```kotlin
     * class MyServiceStub(private val __rpc_stub_id: Long, private val __rpc_client: RPCClient) : MyService
     * ```
     */
    private fun IrClass.clientProperty() {
        clientProperty = addConstructorProperty(CLIENT_PROPERTY, ctx.rpcClient.defaultType, clientValueParameter)
    }

    private fun IrClass.addConstructorProperty(
        propertyName: String,
        propertyType: IrType,
        valueParameter: IrValueParameter,
        propertyVisibility: DescriptorVisibility = DescriptorVisibilities.PRIVATE
    ): IrProperty {
        return addConstructorProperty(Name.identifier(propertyName), propertyType, valueParameter, propertyVisibility)
    }

    private fun IrClass.addConstructorProperty(
        propertyName: Name,
        propertyType: IrType,
        valueParameter: IrValueParameter,
        propertyVisibility: DescriptorVisibility = DescriptorVisibilities.PRIVATE
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

    private var coroutineContextProperty: IrProperty by Delegates.notNull()

    /**
     * `coroutineContext` property from `RPC` interface
     *
     * ```kotlin
     * final override val coroutineContext: CoroutineContext = __rpc_client.provideStubContext(__rpc_stub_id)
     * ```
     */
    private fun IrClass.coroutineContextProperty() {
        coroutineContextProperty = addProperty {
            name = Name.identifier("coroutineContext")
            visibility = DescriptorVisibilities.PUBLIC
            modality = Modality.FINAL
        }.apply {
            overriddenSymbols = listOf(ctx.properties.rpcClientCoroutineContext.symbol)

            addBackingFieldUtil {
                visibility = DescriptorVisibilities.PRIVATE
                type = ctx.coroutineContext.defaultType
                vsApi { isFinalVS = true }
            }.apply {
                val coroutineContextClass = ctx.coroutineContext.owner

                initializer = factory.createExpressionBody(
                    IrCallImpl(
                        startOffset = UNDEFINED_OFFSET,
                        endOffset = UNDEFINED_OFFSET,
                        type = coroutineContextClass.typeWith(),
                        symbol = ctx.functions.provideStubContext.symbol,
                        valueArgumentsCount = 1,
                        typeArgumentsCount = 0,
                    ).apply {
                        dispatchReceiver = irCallProperty(stubClass, clientProperty)

                        putValueArgument(0, irCallProperty(stubClass, stubIdProperty))
                    }
                )
            }

            addDefaultGetter(this@coroutineContextProperty, ctx.irBuiltIns) {
                val serviceCoroutineContext = declaration.service.getPropertyGetter("coroutineContext")
                    ?: error(
                        "RPC services expected to have \"coroutineContext\" property with getter: " +
                                declaration.service.dump()
                    )

                overriddenSymbols = listOf(serviceCoroutineContext)
            }
        }
    }

    /**
     * RPC fields.
     * Can be of two kinds: Lazy and Eager (defined by `@RPCEagerField` annotation)
     *
     * Lazy:
     * ``` kotlin
     *  final override val <field-name>: <field-type> by lazy {
     *      client.register<field-kind>FlowField(
     *          this, // CoroutineScope
     *          RPCField("<service-name>", __rpc_stub_id, "<field-name>", typeOf<<field-type>>())
     *      )
     *  }
     * ```
     *
     *
     * Eager:
     * ```kotlin
     *  final override val <field-name>: <field-type> =
     *      client.register<field-kind>FlowField(
     *          this, // CoroutineScope
     *          RPCField("<service-name>", __rpc_stub_id, "<field-name>", typeOf<<field-type>>())
     *      )
     * ```
     *
     * Where:
     *  - `<field-name>` - the name of the RPC field
     *  - `<field-type>` - actual type of the field. Can be either Flot<T>, SharedFlow<T> or StateFlow<T>
     *  - `<field-kind>` - [ServiceDeclaration.FlowField.Kind]
     *  - `<service-name>` - FQ name of the RPC service
     */
    @Suppress(
        "detekt.NestedBlockDepth",
        "detekt.LongMethod",
        "detekt.CyclomaticComplexMethod",
    )
    private fun IrClass.rpcFlowField(field: ServiceDeclaration.FlowField) {
        val isLazy = !field.property.hasAnnotation(ctx.rpcEagerFieldAnnotation)

        val servicePropertyGetter = field.property.getterOrFail

        addProperty {
            name = field.property.name
            visibility = field.property.visibility
            modality = Modality.FINAL
            isDelegated = isLazy
        }.apply {
            val fieldProperty = this

            overriddenSymbols = listOf(field.property.symbol)

            val fieldType = servicePropertyGetter.returnType

            val fieldTypeParameter = (fieldType as IrSimpleType).arguments.single()

            val registerFunction = when (field.flowKind) {
                ServiceDeclaration.FlowField.Kind.Plain -> ctx.functions.registerPlainFlowField
                ServiceDeclaration.FlowField.Kind.Shared -> ctx.functions.registerSharedFlowField
                ServiceDeclaration.FlowField.Kind.State -> ctx.functions.registerStateFlowField
            }.symbol

            val registerCall = IrCallImpl(
                startOffset = UNDEFINED_OFFSET,
                endOffset = UNDEFINED_OFFSET,
                type = fieldType,
                symbol = registerFunction,
                typeArgumentsCount = 1,
                valueArgumentsCount = 2,
            ).apply {
                dispatchReceiver = irCallProperty(stubClass, clientProperty)
                putTypeArgument(0, fieldTypeParameter.typeOrFail)

                putValueArgument(
                    index = 0,
                    valueArgument = IrGetValueImpl(
                        startOffset = UNDEFINED_OFFSET,
                        endOffset = UNDEFINED_OFFSET,
                        type = ctx.coroutineScope.defaultType,
                        symbol = stubClassThisReceiver.symbol,
                    ),
                )

                val fieldArgument = IrConstructorCallImpl(
                    startOffset = UNDEFINED_OFFSET,
                    endOffset = UNDEFINED_OFFSET,
                    type = ctx.rpcField.defaultType,
                    symbol = ctx.rpcField.constructors.single(),
                    typeArgumentsCount = 0,
                    constructorTypeArgumentsCount = 0,
                    valueArgumentsCount = 4,
                ).apply {
                    putValueArgument(0, stringConst(declaration.service.kotlinFqName.asString()))
                    putValueArgument(1, irCallProperty(stubClass, stubIdProperty))
                    putValueArgument(2, stringConst(field.property.name.asString()))

                    val typeOfCall = IrCallImpl(
                        startOffset = UNDEFINED_OFFSET,
                        endOffset = UNDEFINED_OFFSET,
                        type = ctx.kTypeClass.defaultType,
                        symbol = ctx.functions.typeOf,
                        typeArgumentsCount = 1,
                        valueArgumentsCount = 0,
                    ).apply {
                        putTypeArgument(0, fieldType)
                    }

                    putValueArgument(3, typeOfCall)
                }

                putValueArgument(1, fieldArgument)
            }

            if (!isLazy) {
                addBackingFieldUtil {
                    type = fieldType
                    visibility = DescriptorVisibilities.PRIVATE
                    vsApi { isFinalVS = true }
                }.apply {
                    initializer = factory.createExpressionBody(registerCall)
                }

                addDefaultGetter(this@rpcFlowField, ctx.irBuiltIns) {
                    visibility = field.property.visibility
                    overriddenSymbols = listOf(servicePropertyGetter.symbol)
                }
            } else {
                val lazyFieldType = ctx.lazy.typeWith(fieldType)

                val lazyField = addBackingFieldUtil {
                    origin = IrDeclarationOrigin.PROPERTY_DELEGATE
                    name = propertyDelegateName(this@apply.name)
                    visibility = DescriptorVisibilities.PRIVATE
                    type = lazyFieldType
                    vsApi { isFinalVS = true }
                }.apply {
                    val propertyDelegate = this

                    // initializer for Lazy delegate with lambda
                    // inside lambda - 'registerCall' expression is used
                    initializer = factory.createExpressionBody(
                        IrCallImpl(
                            startOffset = UNDEFINED_OFFSET,
                            endOffset = UNDEFINED_OFFSET,
                            type = lazyFieldType,
                            symbol = ctx.functions.lazy,
                            typeArgumentsCount = 1,
                            valueArgumentsCount = 1,
                        ).apply {
                            putTypeArgument(0, fieldType)

                            val lambdaType = ctx.function0.typeWith(fieldType)

                            val lambdaFunction = factory.buildFun {
                                origin = IrDeclarationOrigin.LOCAL_FUNCTION_FOR_LAMBDA
                                name = SpecialNames.ANONYMOUS
                                visibility = DescriptorVisibilities.LOCAL
                                returnType = fieldType
                            }.apply {
                                parent = propertyDelegate

                                body = irBuilder(symbol).irBlockBody {
                                    +irReturn(registerCall)
                                }
                            }

                            val lambda = IrFunctionExpressionImpl(
                                startOffset = UNDEFINED_OFFSET,
                                endOffset = UNDEFINED_OFFSET,
                                type = lambdaType,
                                origin = IrStatementOrigin.LAMBDA,
                                function = lambdaFunction,
                            )

                            putValueArgument(0, lambda)
                        }
                    )
                }

                // Invocation of `operator fun getValue(thisRef: Any?, property: KProperty<*>): T` for delegates
                addGetter {
                    origin = IrDeclarationOrigin.DELEGATED_PROPERTY_ACCESSOR
                    visibility = this@apply.visibility
                    returnType = fieldType
                }.apply {
                    val propertyGetter = this

                    overriddenSymbols = listOf(servicePropertyGetter.symbol)

                    val getterThisReceiver = vsApi {
                        stubClassThisReceiver.copyToVS(propertyGetter, origin = IrDeclarationOrigin.DEFINED)
                    }.also {
                        dispatchReceiverParameter = it
                    }

                    body = irBuilder(symbol).irBlockBody {
                        +irReturn(
                            irCall(
                                type = fieldType,
                                callee = ctx.functions.lazyGetValue,
                                typeArgumentsCount = 1,
                                valueArgumentsCount = 2,
                            ).apply {
                                putTypeArgument(0, fieldType)

                                extensionReceiver = IrGetFieldImpl(
                                    startOffset = UNDEFINED_OFFSET,
                                    endOffset = UNDEFINED_OFFSET,
                                    symbol = lazyField.symbol,
                                    type = lazyFieldType,
                                    receiver = IrGetValueImpl(
                                        startOffset = UNDEFINED_OFFSET,
                                        endOffset = UNDEFINED_OFFSET,
                                        type = stubClass.defaultType,
                                        symbol = getterThisReceiver.symbol,
                                    ),
                                )

                                putValueArgument(
                                    index = 0,
                                    valueArgument = IrGetValueImpl(
                                        startOffset = UNDEFINED_OFFSET,
                                        endOffset = UNDEFINED_OFFSET,
                                        type = stubClass.defaultType,
                                        symbol = getterThisReceiver.symbol,
                                    )
                                )

                                putValueArgument(
                                    index = 1,
                                    valueArgument = IrPropertyReferenceImpl(
                                        startOffset = UNDEFINED_OFFSET,
                                        endOffset = UNDEFINED_OFFSET,
                                        type = ctx.kProperty1.typeWith(stubClass.defaultType, fieldType),
                                        symbol = fieldProperty.symbol,
                                        typeArgumentsCount = 0,
                                        field = null,
                                        getter = propertyGetter.symbol,
                                        setter = null,
                                        origin = IrStatementOrigin.PROPERTY_REFERENCE_FOR_DELEGATE,
                                    )
                                )
                            }
                        )
                    }
                }
            }
        }
    }

    private fun IrClass.generateMethods() {
        declaration.methods.forEach {
            generateRpcMethod(it)
        }
    }

    private val methodClasses = mutableListOf<IrClass>()

    /**
     * RPC Methods generation
     *
     * All method classes MUST be already generated, so this plugin can use them.
     *
     * ```kotlin
     *  final override suspend fun <method-name>(<method-args>): <return-type> {
     *      return scopedClientCall(this) { // this: CoroutineScope
     *          __rpc_client.call(RPCCall(
     *              "<service-name>",
     *              __rpc_stub_id,
     *              "<method-name>",
     *              RPCCall.Type.Method,
     *              (<method-class>(<method-args>)|<method-object>),
     *              typeOf<<method-class>>(),
     *              typeOf<<return-type>>(),
     *          ))
     *      }
     *  }
     * ```
     *
     * Where:
     *  - `<method-name>` - the name of the RPC method
     *  - `<method-args>` - array of arguments for the method
     *  - `<return-type>` - the return type of the method
     *  - `<service-name>` - FQ name of the RPC service
     *  - `<method-class>`/`<method-object>` - generated class or object for this method
     */
    @Suppress(
        "detekt.NestedBlockDepth",
        "detekt.LongMethod",
    )
    private fun IrClass.generateRpcMethod(method: ServiceDeclaration.Method) {
        val isMethodObject = method.arguments.isEmpty()

        val methodClassName = method.function.name.rpcMethodClassName
        val methodClassNameKsp = method.function.name.rpcMethodClassNameKsp
        val methodClass: IrClass = initiateAndGetMethodClass(methodClassName, methodClassNameKsp, method)

        addFunction {
            name = method.function.name
            visibility = method.function.visibility
            returnType = method.function.returnType
            modality = Modality.OPEN

            isSuspend = true
        }.apply {
            val functionThisReceiver = vsApi {
                stubClassThisReceiver.copyToVS(this@apply, origin = IrDeclarationOrigin.DEFINED)
            }.also {
                dispatchReceiverParameter = it
            }

            val declaredFunction = this

            val arguments = method.arguments.memoryOptimizedMap { arg ->
                addValueParameter {
                    name = arg.value.name
                    type = arg.type
                }
            }

            overriddenSymbols = listOf(method.function.symbol)

            body = irBuilder(symbol).irBlockBody {
                +irReturn(
                    irCall(
                        callee = ctx.functions.scopedClientCall,
                        type = method.function.returnType,
                    ).apply {
                        putTypeArgument(0, method.function.returnType)

                        putValueArgument(0, irGet(ctx.coroutineScope.defaultType, functionThisReceiver.symbol))

                        // suspend lambda
                        // it's type is not available at runtime, but in fact exists
                        val lambdaType = ctx.suspendFunction0.typeWith(method.function.returnType)

                        val functionLambda = factory.buildFun {
                            origin = IrDeclarationOrigin.LOCAL_FUNCTION_FOR_LAMBDA
                            name = SpecialNames.ANONYMOUS
                            visibility = DescriptorVisibilities.LOCAL
                            modality = Modality.FINAL
                            returnType = method.function.returnType
                            isSuspend = true
                        }.apply {
                            parent = declaredFunction

                            body = irBuilder(symbol).irBlockBody {
                                val call = irRpcMethodClientCall(
                                    method = method,
                                    functionThisReceiver = functionThisReceiver,
                                    isMethodObject = isMethodObject,
                                    methodClass = methodClass,
                                    arguments = arguments,
                                )

                                if (method.function.returnType == ctx.irBuiltIns.unitType) {
                                    +call
                                } else {
                                    +irReturn(call)
                                }
                            }
                        }

                        val lambda = IrFunctionExpressionImpl(
                            startOffset = UNDEFINED_OFFSET,
                            endOffset = UNDEFINED_OFFSET,
                            type = lambdaType,
                            origin = IrStatementOrigin.LAMBDA,
                            function = functionLambda,
                        )

                        putValueArgument(1, lambda)
                    }
                )
            }
        }
    }

    /**
     * Frontend plugins generate the following:
     * ```kotlin
     * // Given rpc method:
     * suspend fun hello(arg1: String, arg2: Int)
     *
     * // Frontend generates:
     * @Serializable
     * class hello$rpcMethod {
     *    constructor(arg1: String, arg2: String)
     *
     *    val arg1: String
     *    val arg2: Int
     * }
     * ```
     *
     * This method generates missing getters and backing fields' values.
     * And adds RPCMethodClassArguments supertype with `asArray` method implemented.
     *
     * Resulting class:
     * ```kotlin
     * @Serializable
     * class hello$rpcMethod(
     *    val arg1: String,
     *    val arg2: Int,
     * ) : RPCMethodClassArguments {
     *    // or emptyArray when no arguments
     *    override fun asArray(): Array<Any?> = arrayOf(arg1, arg2)
     * }
     * ```
     */
    private fun IrClass.initiateAndGetMethodClass(
        methodClassName: Name,
        methodClassNameKsp: Name,
        method: ServiceDeclaration.Method
    ): IrClass {
        val methodClass = findDeclaration<IrClass> {
            it.name == methodClassName || it.name == methodClassNameKsp
        } ?: error(
            "Expected $methodClassName or $methodClassNameKsp class to be present in stub class " +
                    "${declaration.service.name}${declaration.stubClass.name}"
        )

        methodClasses.add(methodClass)

        val methodClassThisReceiver = methodClass.thisReceiver
            ?: error("Expected ${methodClass.name} of ${declaration.stubClass.name} to have a thisReceiver")

        val properties = if (methodClass.isClass) {
            val argNames = method.arguments.memoryOptimizedMap { it.value.name }.toSet()

            // remove frontend generated properties
            // new ones will be used instead
            methodClass.declarations.removeAll { it is IrProperty && it.name in argNames }

            // primary constructor, serialization may add another
            val constructor = methodClass.constructors.single {
                method.arguments.size == it.valueParameters.size
            }

            vsApi { constructor.isPrimaryVS = true }
            methodClass.addDefaultConstructor(constructor)

            constructor.valueParameters.memoryOptimizedMap { valueParam ->
                methodClass.addConstructorProperty(
                    propertyName = valueParam.name,
                    propertyType = valueParam.type,
                    valueParameter = valueParam,
                    propertyVisibility = DescriptorVisibilities.PUBLIC,
                )
            }
        } else {
            emptyList()
        }

        methodClass.superTypes = listOf(ctx.rpcMethodClassArguments.defaultType)

        methodClass.addFunction {
            name = ctx.functions.asArray.name
            visibility = DescriptorVisibilities.PUBLIC
            returnType = ctx.arrayOfAnyNullable
            modality = Modality.OPEN
        }.apply {
            overriddenSymbols = listOf(ctx.functions.asArray.symbol)

            val asArrayThisReceiver = vsApi {
                methodClassThisReceiver.copyToVS(this@apply, origin = IrDeclarationOrigin.DEFINED)
            }.also {
                dispatchReceiverParameter = it
            }

            body = irBuilder(symbol).irBlockBody {
                val callee = if (methodClass.isObject) {
                    ctx.functions.emptyArray
                } else {
                    ctx.irBuiltIns.arrayOf
                }

                val arrayOfCall = irCall(callee, type = ctx.arrayOfAnyNullable).apply arrayOfCall@{
                    putTypeArgument(0, ctx.anyNullable)

                    if (methodClass.isObject) {
                        return@arrayOfCall
                    }

                    val vararg = irVararg(
                        elementType = ctx.anyNullable,
                        values = properties.memoryOptimizedMap { property ->
                            irCallProperty(methodClass, property, symbol = asArrayThisReceiver.symbol)
                        },
                    )

                    putValueArgument(0, vararg)
                }

                +irReturn(arrayOfCall)
            }
        }

        return methodClass
    }

    /**
     * Part of [generateRpcMethod] that generates next call:
     *
     * ```kotlin
     * __rpc_client.call(RPCCall(
     *     "<service-name>",
     *     __rpc_stub_id,
     *     "<method-name>",
     *     RPCCall.Type.Method,
     *     (<method-class>(<method-args>)|<method-object>),
     *     typeOf<<method-class>>(),
     *     typeOf<<return-type>>(),
     * ))
     * ```
     */
    @Suppress("detekt.NestedBlockDepth")
    private fun IrBlockBodyBuilder.irRpcMethodClientCall(
        method: ServiceDeclaration.Method,
        functionThisReceiver: IrValueParameter,
        isMethodObject: Boolean,
        methodClass: IrClass,
        arguments: List<IrValueParameter>
    ): IrCall {
        val call = irCall(
            callee = ctx.functions.rpcClientCall.symbol,
            type = method.function.returnType,
            typeArgumentsCount = 1,
            valueArgumentsCount = 1,
        ).apply {
            dispatchReceiver = irCallProperty(
                clazz = stubClass,
                property = clientProperty,
                symbol = functionThisReceiver.symbol,
            )

            putTypeArgument(0, method.function.returnType)

            val rpcCallConstructor = irCallConstructor(
                callee = ctx.rpcCall.constructors.single(),
                typeArguments = emptyList(),
            ).apply {
                putValueArgument(
                    index = 0,
                    valueArgument = stringConst(declaration.service.kotlinFqName.asString()),
                )

                putValueArgument(
                    index = 1,
                    valueArgument = irCallProperty(stubClass, stubIdProperty, symbol = functionThisReceiver.symbol),
                )

                putValueArgument(
                    index = 2,
                    valueArgument = stringConst(method.function.name.asString()),
                )

                putValueArgument(
                    index = 3,
                    valueArgument = IrGetEnumValueImpl(
                        startOffset = UNDEFINED_OFFSET,
                        endOffset = UNDEFINED_OFFSET,
                        type = ctx.rpcCallType.defaultType,
                        symbol = ctx.rpcCallTypeMethod,
                    ),
                )

                val dataParameter = if (isMethodObject) {
                    irGetObject(methodClass.symbol)
                } else {
                    irCallConstructor(
                        // serialization plugin adds additional constructor with more arguments
                        callee = methodClass.constructors.single {
                            it.valueParameters.size == method.arguments.size
                        }.symbol,
                        typeArguments = emptyList(),
                    ).apply {
                        arguments.forEachIndexed { i, valueParameter ->
                            putValueArgument(i, irGet(valueParameter))
                        }
                    }
                }

                putValueArgument(4, dataParameter)

                putValueArgument(
                    index = 5,
                    valueArgument = irCall(
                        ctx.functions.typeOf,
                    ).apply {
                        putTypeArgument(0, methodClass.defaultType)
                    },
                )

                putValueArgument(
                    index = 6,
                    valueArgument = irCall(
                        ctx.functions.typeOf,
                    ).apply {
                        putTypeArgument(0, method.function.returnType)
                    },
                )
            }

            putValueArgument(0, rpcCallConstructor)
        }
        return call
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

        return IrCallImpl(
            startOffset = UNDEFINED_OFFSET,
            endOffset = UNDEFINED_OFFSET,
            type = getter.returnType,
            symbol = getter.symbol,
            origin = IrStatementOrigin.GET_PROPERTY,
            valueArgumentsCount = getter.valueParameters.size,
            typeArgumentsCount = getter.typeParameters.size,
        ).apply {
            dispatchReceiver = receiver
        }
    }

    private var stubCompanionObject: IrClassSymbol by Delegates.notNull()
    private var stubCompanionObjectThisReceiver: IrValueParameter by Delegates.notNull()

    /**
     * Companion object for the RPC service stub.
     * The empty object should already be generated
     *
     * ```kotlin
     *  companion object : RPCStubObject<MyServiceStub>
     * ```
     */
    private fun IrClass.generateCompanionObject() {
        companionObject()?.apply {
            // clearing previous declarations, as they may break with the ones added here
            declarations.clear()

            stubCompanionObject = symbol
            stubCompanionObjectThisReceiver = thisReceiver
                ?: error("Stub companion object expected to have thisReceiver: ${name.asString()}")

            superTypes = listOf(ctx.rpcStubObject.typeWith(declaration.serviceType))

            generateCompanionObjectConstructor()

            generateCompanionObjectContent()

            addAnyOverrides(ctx.rpcStubObject.owner)
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
        generateMethodNamesProperty()

        generateMethodTypeOfFunction()

        generateWithClientFunction()

        generateRpcFieldsFunction()
    }

    private var methodNamesMap: IrProperty by Delegates.notNull()

    /**
     * Method names property.
     * A map that holds a KType of the method class by method string name
     *
     * ```kotlin
     *  private val methodNames: Map<String, KType> = mapOf(
     *      Pair("<method-name-1>", typeOf<<method-class-1>>()),
     *      ...
     *      Pair("<method-name-n>", typeOf<<method-class-n>>()),
     *  )
     *
     *  // when n=0:
     *  private val methodNames: Map<String, KType> = emptyMap()
     * ```
     *
     * Where:
     *  - `<method-name-k>` - the name of the k-th method in the service
     *  - `<method-class-k>` - the method class for the k-th method in the service
     */
    private fun IrClass.generateMethodNamesProperty() {
        methodNamesMap = addProperty {
            name = Name.identifier(METHOD_NAMES_PROPERTY)
            visibility = DescriptorVisibilities.PRIVATE
            modality = Modality.FINAL
        }.apply {
            val mapType = ctx.irBuiltIns.mapClass.typeWith(
                ctx.irBuiltIns.stringType,
                ctx.kTypeClass.defaultType,
            )

            addBackingFieldUtil {
                type = mapType
                vsApi { isFinalVS = true }
                visibility = DescriptorVisibilities.PRIVATE
            }.apply {
                val isEmpty = declaration.methods.isEmpty()

                initializer = factory.createExpressionBody(
                    IrCallImpl(
                        startOffset = UNDEFINED_OFFSET,
                        endOffset = UNDEFINED_OFFSET,
                        type = mapType,
                        symbol = if (isEmpty) ctx.functions.emptyMap else ctx.functions.mapOf,
                        valueArgumentsCount = if (isEmpty) 0 else 1,
                        typeArgumentsCount = 2,
                    ).apply mapApply@{
                        putTypeArgument(0, ctx.irBuiltIns.stringType)
                        putTypeArgument(1, ctx.kTypeClass.defaultType)

                        if (isEmpty) {
                            return@mapApply
                        }

                        val pairType = ctx.pair.typeWith(
                            ctx.irBuiltIns.stringType,
                            ctx.kTypeClass.defaultType,
                        )

                        val varargType = ctx.irBuiltIns.arrayClass.typeWith(pairType, Variance.OUT_VARIANCE)

                        val vararg = IrVarargImpl(
                            startOffset = UNDEFINED_OFFSET,
                            endOffset = UNDEFINED_OFFSET,
                            type = varargType,
                            varargElementType = pairType,
                            elements = declaration.methods.memoryOptimizedMapIndexed { i, method ->
                                IrCallImpl(
                                    startOffset = UNDEFINED_OFFSET,
                                    endOffset = UNDEFINED_OFFSET,
                                    type = pairType,
                                    symbol = ctx.functions.to,
                                    typeArgumentsCount = 2,
                                    valueArgumentsCount = 1,
                                ).apply {
                                    putTypeArgument(0, ctx.irBuiltIns.stringType)
                                    putTypeArgument(1, ctx.kTypeClass.defaultType)

                                    extensionReceiver = stringConst(method.function.name.asString())

                                    val type = IrCallImpl(
                                        startOffset = UNDEFINED_OFFSET,
                                        endOffset = UNDEFINED_OFFSET,
                                        type = ctx.kTypeClass.defaultType,
                                        symbol = ctx.functions.typeOf,
                                        typeArgumentsCount = 1,
                                        valueArgumentsCount = 0,
                                    ).apply {
                                        putTypeArgument(0, methodClasses[i].defaultType)
                                    }

                                    putValueArgument(0, type)
                                }
                            },
                        )

                        putValueArgument(0, vararg)
                    }
                )
            }

            addDefaultGetter(this@generateMethodNamesProperty, ctx.irBuiltIns) {
                visibility = DescriptorVisibilities.PRIVATE
            }
        }
    }

    /**
     * Accessor function for `methodNames` map
     * Defined in `RPCStubObject`
     *
     * ```kotlin
     *  final override fun methodTypeOf(methodName: String): KType? = methodNames[methodName]
     * ```
     */
    private fun IrClass.generateMethodTypeOfFunction() {
        val kTypeNullable = ctx.kTypeClass.createType(hasQuestionMark = true, emptyList())

        addFunction {
            name = Name.identifier(METHOD_TYPE_OF_FUNCTION)
            visibility = DescriptorVisibilities.PUBLIC
            modality = Modality.OPEN
            returnType = kTypeNullable
        }.apply {
            overriddenSymbols = listOf(ctx.rpcStubObject.functionByName(METHOD_TYPE_OF_FUNCTION))

            val functionThisReceiver = vsApi {
                stubCompanionObjectThisReceiver.copyToVS(this@apply, origin = IrDeclarationOrigin.DEFINED)
            }.also {
                dispatchReceiverParameter = it
            }

            val methodName = addValueParameter {
                name = Name.identifier("methodName")
                type = ctx.irBuiltIns.stringType
            }

            body = irBuilder(symbol).irBlockBody {
                +irReturn(
                    irCall(ctx.functions.mapGet.symbol, kTypeNullable).apply {
                        vsApi { originVS = IrStatementOrigin.GET_ARRAY_ELEMENT }

                        dispatchReceiver = irCallProperty(
                            clazz = this@generateMethodTypeOfFunction,
                            property = methodNamesMap,
                            symbol = functionThisReceiver.symbol,
                        )

                        putValueArgument(0, irGet(methodName))
                    }
                )
            }
        }
    }

    /**
     * Factory method for creating a new instance of RPC service
     *
     * ```kotlin
     *  final override fun withClient(serviceId: Long, client: RPCClient): MyService {
     *      return MyServiceStub(serviceId, client)
     *  }
     * ```
     */
    private fun IrClass.generateWithClientFunction() {
        addFunction {
            name = Name.identifier(WITH_CLIENT_METHOD)
            visibility = DescriptorVisibilities.PUBLIC
            modality = Modality.OPEN
            returnType = declaration.serviceType
        }.apply {
            overriddenSymbols = listOf(ctx.rpcStubObject.functionByName(WITH_CLIENT_METHOD))

            dispatchReceiverParameter = vsApi {
                stubCompanionObjectThisReceiver.copyToVS(this@apply, origin = IrDeclarationOrigin.DEFINED)
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
                        putValueArgument(0, irGet(serviceId))
                        putValueArgument(1, irGet(client))
                    }
                )
            }
        }
    }

    /**
     * Function for getting a list of all RPC fields in a given service as [RPCDeferredField<*>]
     *
     * ```kotlin
     *  final override fun rpcFields(service: MyService): List<RPCDeferredField<*>> {
     *      return listOf( // or emptyList() if no fields
     *          service.<field-1>,
     *          ...
     *          service.<field-n>,
     *      ) as List<RPCDeferredField<*>>
     *  }
     * ```
     *
     * Where:
     *  - `<field-k>` - k-th field of a given service
     */
    private fun IrClass.generateRpcFieldsFunction() {
        val listType = ctx.irBuiltIns.listClass.typeWith(ctx.rpcDeferredField.starProjectedType)

        addFunction {
            name = Name.identifier(RPC_FIELDS_METHOD)
            visibility = DescriptorVisibilities.PUBLIC
            modality = Modality.OPEN

            returnType = listType
        }.apply {
            overriddenSymbols = listOf(ctx.rpcStubObject.functionByName(RPC_FIELDS_METHOD))

            dispatchReceiverParameter = vsApi {
                stubCompanionObjectThisReceiver.copyToVS(this@apply, origin = IrDeclarationOrigin.DEFINED)
            }

            val service = addValueParameter {
                name = Name.identifier("service")
                type = declaration.serviceType
            }

            body = irBuilder(symbol).irBlockBody {
                val isEmpty = declaration.fields.isEmpty()

                val anyListType = ctx.irBuiltIns.listClass.typeWith(ctx.anyNullable)

                val listCall = irCall(
                    callee = if (isEmpty) ctx.functions.emptyList else ctx.functions.listOf,
                    type = anyListType,
                ).apply listApply@{
                    putTypeArgument(0, ctx.anyNullable)

                    if (isEmpty) {
                        return@listApply
                    }

                    val vararg = IrVarargImpl(
                        startOffset = UNDEFINED_OFFSET,
                        endOffset = UNDEFINED_OFFSET,
                        type = ctx.arrayOfAnyNullable,
                        varargElementType = ctx.anyNullable,
                        elements = declaration.fields.memoryOptimizedMap {
                            irCallProperty(irGet(service), it.property)
                        }
                    )

                    putValueArgument(0, vararg)
                }

                +irReturn(irAs(listCall, listType))
            }
        }
    }

    // Associated object annotation works on JS and Native platforms.
    // See https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/find-associated-object.html
    private fun addAssociatedObjectAnnotationIfPossible() {
        if (ctx.isJsTarget() || ctx.isNativeTarget()) {
            addAssociatedObjectAnnotation()
        }
    }

    private fun addAssociatedObjectAnnotation() {
        val service = declaration.service

        service.annotations += IrConstructorCallImpl(
            startOffset = service.startOffset,
            endOffset = service.endOffset,
            type = ctx.withRPCStubObjectAnnotation.defaultType,
            symbol = ctx.withRPCStubObjectAnnotation.constructors.single(),
            typeArgumentsCount = 0,
            constructorTypeArgumentsCount = 0,
            valueArgumentsCount = 1,
        ).apply {
            val companionObjectType = stubCompanionObject.defaultType
            putValueArgument(
                index = 0,
                valueArgument = IrClassReferenceImpl(
                    startOffset = startOffset,
                    endOffset = endOffset,
                    type = ctx.irBuiltIns.kClassClass.typeWith(companionObjectType),
                    symbol = stubCompanionObject,
                    classType = companionObjectType,
                )
            )
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

            dispatchReceiverParameter = anyClass.thisReceiver

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

            dispatchReceiverParameter = anyClass.thisReceiver
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

            dispatchReceiverParameter = anyClass.thisReceiver
        }
    }

    private fun stringConst(value: String) = IrConstImpl.string(
        startOffset = UNDEFINED_OFFSET,
        endOffset = UNDEFINED_OFFSET,
        type = ctx.irBuiltIns.stringType,
        value = value,
    )

    private fun <T> vsApi(body: VersionSpecificApi.() -> T): T {
        return ctx.versionSpecificApi.body()
    }
}
