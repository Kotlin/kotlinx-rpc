package org.jetbrains.krpc.codegen

import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.ir.addDispatchReceiver
import org.jetbrains.kotlin.backend.common.lower.createIrBuilder
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.ir.IrBuiltIns
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.backend.js.utils.typeArguments
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.builders.declarations.*
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.declarations.impl.IrVariableImpl
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin
import org.jetbrains.kotlin.ir.expressions.impl.IrCallImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrGetObjectValueImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrInstanceInitializerCallImpl
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.impl.IrVariableSymbolImpl
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.types.createType
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.ir.visitors.IrElementTransformer
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

class IrExtension(private val logger: MessageCollector) : IrGenerationExtension {
    override fun generate(
        moduleFragment: IrModuleFragment,
        pluginContext: IrPluginContext,
    ) {
        val context = IrTransformerContext(pluginContext, moduleFragment.irBuiltins, logger)
        moduleFragment.transform(IrServiceTransformer(logger, moduleFragment.irBuiltins), context)
        moduleFragment.transform(IrRpcServiceOfCallsTransformer(), context)
    }
}

private class IrTransformerContext(
    val pluginContext: IrPluginContext,
    irBuiltins: IrBuiltIns,
    val logger: MessageCollector,
) {
    val kType = getClassDeclaration("kotlin.reflect", "KType")
    val mapGetFunction = irBuiltins.mapClass.functions.singleOrNull {
        it.owner.name.asString() == "get"
    } ?: error("Expected kotlin.collections.Map.get function")

    val rpc = getRpcClassDeclaration("RPC").typeWith()
    val rpcEngine = getRpcClassDeclaration("RPCEngine").typeWith()
    val rpcMethodClassTypeProvider = getRpcClassDeclaration("RPCMethodClassTypeProvider")
    val rpcClientProvider = getRpcClassDeclaration("RPCClientProvider")
    fun rpcClientProvider(serviceDeclaration: IrClass) =
        getRpcClassDeclaration("RPCClientProvider").typeWith(serviceDeclaration.typeWith())

    val rpcStubCallAnnotation = getRpcClassDeclaration("RPCStubCall")
    val rpcReplacementCallAnnotation = getRpcClassDeclaration("RPCReplacementCall")
    val rpcServiceOfStubFunction = getRpcFunctionDeclaration("rpcServiceOf") { symbol ->
        symbol.owner.hasAnnotation(rpcStubCallAnnotation)
    }
    val rpcServiceOfReplacementFunction = getRpcFunctionDeclaration("rpcServiceOf") { symbol ->
        symbol.owner.hasAnnotation(rpcReplacementCallAnnotation)
    }
    val rpcServiceMethodOfStubFunction = getRpcFunctionDeclaration("serviceMethodOf") { symbol ->
        symbol.owner.hasAnnotation(rpcStubCallAnnotation)
    }
    val rpcServiceMethodOfReplacementFunction = getRpcFunctionDeclaration("serviceMethodOf") { symbol ->
        symbol.owner.hasAnnotation(rpcReplacementCallAnnotation)
    }

    fun generatedClientClassType(serviceDeclaration: IrClass): IrClassSymbol {
        val name = serviceDeclaration.kotlinFqName.shortName()
        return getRpcClassDeclaration("${name}Client")
    }

    private fun getRpcClassDeclaration(name: String): IrClassSymbol {
        return getClassDeclaration("org.jetbrains.krpc", name)
    }

    private fun getClassDeclaration(packageName: String, name: String): IrClassSymbol {
        return pluginContext.referenceClass(
            ClassId(
                FqName(packageName),
                FqName(name),
                false
            )
        ) ?: error("Unable to find $packageName.$name declaration")
    }

    private fun getRpcFunctionDeclaration(
        name: String,
        single: (IrSimpleFunctionSymbol) -> Boolean
    ): IrSimpleFunctionSymbol {
        return getTopLevelFunctionDeclaration("org.jetbrains.krpc", name, single)
    }

    @Suppress("SameParameterValue")
    private fun getTopLevelFunctionDeclaration(
        packageName: String,
        name: String,
        single: (IrSimpleFunctionSymbol) -> Boolean
    ): IrSimpleFunctionSymbol {
        return pluginContext.referenceFunctions(CallableId(FqName(packageName), Name.identifier(name)))
            .singleOrNull(single)
            ?: error("Unable to find single top-level $packageName.$name function")
    }

    val generatedServiceCompanionObjects = mutableMapOf<IrClassSymbol, IrClass>()
}

private object RPCGeneratedDeclarationKey : GeneratedDeclarationKey()

private val pluginOrigin = IrDeclarationOrigin.GeneratedByPlugin(RPCGeneratedDeclarationKey)

private class IrServiceTransformer(
    private val logger: MessageCollector,
    private val irBuiltins: IrBuiltIns,
) : IrElementTransformer<IrTransformerContext> {
    override fun visitClass(declaration: IrClass, data: IrTransformerContext): IrStatement {
        val default = super.visitClass(declaration, data)

        if (declaration.isInterface && declaration.superTypes.contains(data.rpc)) {
//            logger.report(CompilerMessageSeverity.WARNING, "Found service interface declaration: ${declaration.name.asString()}")
            addRpcDeclarations(declaration, data)
        }

//        logger.report(CompilerMessageSeverity.WARNING, declaration.dump())

        return default
    }

    private fun addRpcDeclarations(declaration: IrClass, data: IrTransformerContext) {
        val companionObject = declaration.companionObject() ?: run {
            irBuiltins.irFactory.buildClass {
                kind = ClassKind.OBJECT
                isCompanion = true
                name = declaration.kotlinFqName.child(Name.identifier("Companion")).shortName()
                origin = pluginOrigin

                setSourceRange(declaration)
            }.apply {
                val classSymbol = symbol
                declaration.declarations.add(this)
                parent = declaration

                createImplicitParameterDeclarationWithWrappedDescriptor()

                superTypes = listOf(data.rpcClientProvider(declaration), data.rpcMethodClassTypeProvider.typeWith())

                addConstructor {
                    isPrimary = true
                    visibility = DescriptorVisibilities.PRIVATE
                }.apply {
                    body = irBuiltins.createIrBuilder(symbol).irBlockBody {
                        +irDelegatingConstructorCall(irBuiltins.anyType.classOrNull!!.owner.constructors.single())
                        +IrInstanceInitializerCallImpl(startOffset, endOffset, classSymbol, irBuiltins.unitType)
                    }
                }
            }
        }

        // todo replace existing clashes
        companionObject.addGeneratedOverrides(declaration, data)

        data.generatedServiceCompanionObjects[declaration.symbol] = companionObject

        logger.report(CompilerMessageSeverity.WARNING, companionObject.dump())
    }

    private fun IrClass.addGeneratedOverrides(declaration: IrClass, data: IrTransformerContext) {
        val companionObject = this
        val kType = data.kType.typeWith()
        val generatedClientClass = data.generatedClientClassType(declaration)

        addFunction {
            origin = pluginOrigin
            visibility = DescriptorVisibilities.PUBLIC
            name = Name.identifier("client")
            modality = Modality.OPEN

            returnType = declaration.typeWith()

            setSourceRange(this@addGeneratedOverrides)
        }.apply {
            overriddenSymbols = listOf(
                data.rpcClientProvider.owner.functions.first { it.name.asString() == "client" }.symbol
            )

            addDispatchReceiver {
                type = companionObject.defaultType
                origin = pluginOrigin
            }

            val parameter = addValueParameter {
                type = data.rpcEngine
                name = Name.identifier("engine")
            }

            body = irBuiltins.createIrBuilder(symbol).irBlockBody {
                +irReturn(
                    irCallConstructor(
                        generatedClientClass.constructors.first(),
                        emptyList()
                    ).apply {
                        putValueArgument(0, irGet(parameter))
                    }
                )
            }
        }

        addFunction {
            origin = pluginOrigin
            visibility = DescriptorVisibilities.PUBLIC
            name = Name.identifier("methodClassType")
            modality = Modality.OPEN

            returnType = data.kType.createType(true, emptyList())

            setSourceRange(this@addGeneratedOverrides)
        }.apply {
            overriddenSymbols = listOf(
                data.rpcMethodClassTypeProvider.owner.functions.first { it.name.asString() == "methodClassType" }.symbol
            )

            addDispatchReceiver {
                type = companionObject.defaultType
                origin = pluginOrigin
            }

            val parameter = addValueParameter {
                type = irBuiltins.stringType
                name = Name.identifier("methodName")
            }

            body = irBuiltins.createIrBuilder(symbol).irBlockBody {
                val clientCompanionClass = generatedClientClass.owner.companionObject()
                    ?: error("Expected companion object for service client: ${declaration.name.asString()}")

                val clientCompanion = irVal(
                    name = "clientCompanion",
                    value = IrGetObjectValueImpl(
                        startOffset = startOffset,
                        endOffset = endOffset,
                        type = clientCompanionClass.typeWith(),
                        symbol = clientCompanionClass.symbol
                    ),
                )
                +clientCompanion

                val mapType = irBuiltins.mapClass.typeWith(irBuiltins.stringType, kType)
                val clientCompanionMethodsMap = irVal(
                    name = "clientCompanionMethodsMap",
                    value = irGet(
                        type = mapType,
                        receiver = irGet(clientCompanion),
                        getterSymbol = clientCompanionClass.getPropertyGetter("methodNames")
                            ?: error("Expected methodNames property")
                    ),
                )
                +clientCompanionMethodsMap

                val mapCall = IrCallImpl(
                    startOffset = startOffset,
                    endOffset = endOffset,
                    type = data.kType.createType(true, emptyList()),
                    symbol = data.mapGetFunction,
                    typeArgumentsCount = 0,
                    valueArgumentsCount = 1,
                    origin = IrStatementOrigin.GET_ARRAY_ELEMENT,
                ).apply {
                    dispatchReceiver = irGet(clientCompanionMethodsMap)

                    putValueArgument(0, irGet(parameter))
                }

                +irReturn(mapCall)
            }
        }
    }

    private fun IrStatementsBuilder<*>.irVal(name: String, value: IrExpression): IrVariableImpl {
        return IrVariableImpl(
            startOffset = startOffset,
            endOffset = endOffset,
            origin = IrDeclarationOrigin.DEFINED,
            symbol = IrVariableSymbolImpl(),
            name = Name.identifier(name),
            type = value.type,
            isVar = false,
            isConst = false,
            isLateinit = false,
        ).also {
            it.parent = scope.getLocalDeclarationParent()
            it.initializer = value
        }
    }
}

private class IrRpcServiceOfCallsTransformer : IrElementTransformer<IrTransformerContext> {
    override fun visitCall(expression: IrCall, data: IrTransformerContext): IrElement {
        val call = super.visitCall(expression, data)

        return when (expression.symbol) {
            data.rpcServiceOfStubFunction -> {
                replaceStubCall(expression, data, data.rpcServiceOfReplacementFunction)
            }

            data.rpcServiceMethodOfStubFunction -> {
                replaceStubCall(expression, data, data.rpcServiceMethodOfReplacementFunction)
            }

            else -> call
        }
    }

    private fun replaceStubCall(
        expression: IrCall,
        data: IrTransformerContext,
        replacement: IrSimpleFunctionSymbol,
    ): IrCall {
        val serviceClassSymbol = expression.typeArguments.first()?.classOrNull
            ?: error("Expected class type parameter")

        val serviceCompanionObject = data.generatedServiceCompanionObjects[serviceClassSymbol]
            ?: error("Expected generated companion object for service ${serviceClassSymbol.owner.name.asString()}")

        return irCall(expression, replacement).apply {
            putValueArgument(
                index = 0,
                valueArgument = IrGetObjectValueImpl(
                    startOffset = startOffset,
                    endOffset = endOffset,
                    type = serviceCompanionObject.typeWith(),
                    symbol = serviceCompanionObject.symbol
                )
            )
            putValueArgument(1, expression.getValueArgument(0))
        }
    }
}
