package org.jetbrains.krpc.codegen

import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.ir.addDispatchReceiver
import org.jetbrains.kotlin.backend.common.lower.createIrBuilder
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.ir.IrBuiltIns
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.backend.js.utils.typeArguments
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.builders.declarations.addConstructor
import org.jetbrains.kotlin.ir.builders.declarations.addFunction
import org.jetbrains.kotlin.ir.builders.declarations.addValueParameter
import org.jetbrains.kotlin.ir.builders.declarations.buildClass
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.impl.IrGetObjectValueImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrInstanceInitializerCallImpl
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.types.IrSimpleType
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
        val context = IrTransformerContext(pluginContext, logger)
        moduleFragment.transform(IrServiceTransformer(logger, moduleFragment.irBuiltins), context)
        moduleFragment.transform(IrRpcServiceOfCallsTransformer(), context)
    }
}

private class IrTransformerContext(
    val pluginContext: IrPluginContext,
    val logger: MessageCollector,
) {
    val kType = getClassDeclaration("kotlin.reflect", "KType")

    val rpc = getRpcClassDeclaration("RPC").typeWith()
    val rpcEngine = getRpcClassDeclaration("RPCEngine").typeWith()
    val rpcMethodClassTypeProvider = getRpcClassDeclaration("RPCMethodClassTypeProvider")
    val rpcClientProvider = getRpcClassDeclaration("RPCClientProvider")
    fun rpcClientProvider(serviceDeclaration: IrClass) = getRpcClassDeclaration("RPCClientProvider").typeWith(serviceDeclaration.typeWith())
    val rpcStubCallAnnotation = getRpcClassDeclaration("RPCStubCall")
    val rpcReplacementCallAnnotation = getRpcClassDeclaration("RPCReplacementCall")
    val rpcServiceOfStubFunction = getRpcFunctionDeclaration("rpcServiceOf") { symbol ->
        symbol.owner.hasAnnotation(rpcStubCallAnnotation)
    }
    val rpcServiceOfReplacementFunction = getRpcFunctionDeclaration("rpcServiceOf") { symbol ->
        symbol.owner.hasAnnotation(rpcReplacementCallAnnotation)
    }

    fun clientGeneratedClassType(serviceDeclaration: IrClass): IrClassSymbol {
        val name = serviceDeclaration.kotlinFqName.asString()
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

    private fun getRpcFunctionDeclaration(name: String, single: (IrSimpleFunctionSymbol) -> Boolean): IrSimpleFunctionSymbol {
        return getFunctionDeclaration("org.jetbrains.krpc", "RPCKt", name, single)
    }

    private fun getFunctionDeclaration(packageName: String, className: String, name: String, single: (IrSimpleFunctionSymbol) -> Boolean): IrSimpleFunctionSymbol {
        return pluginContext.referenceFunctions(CallableId(FqName(packageName), Name.identifier(name))).singleOrNull(single)
            ?: error("Unable to find single $packageName.$className.$name function")
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
        val clientProvider = data.rpcClientProvider(declaration)

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

                superTypes = listOf(clientProvider, data.rpcMethodClassTypeProvider.typeWith())

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
        companionObject.addGeneratedOverrides(declaration, clientProvider, data)

        data.generatedServiceCompanionObjects[declaration.symbol] = companionObject

//        logger.report(CompilerMessageSeverity.WARNING, companionObject.dump())
    }

    private fun IrClass.addGeneratedOverrides(declaration: IrClass, clientProvider: IrSimpleType, data: IrTransformerContext) {
        val companionObject = this

        addFunction {
            origin = pluginOrigin
            visibility = DescriptorVisibilities.PUBLIC
            name = Name.identifier("client")
            modality = Modality.OPEN

            returnType = declaration.typeWith()

            setSourceRange(this@addGeneratedOverrides)
        }.apply {
            overriddenSymbols = listOf(data.rpcClientProvider.owner.functions.first { it.name.asString() == "client" }.symbol)

            addDispatchReceiver {
                type = companionObject.defaultType
                origin = pluginOrigin
            }

            val parameter = addValueParameter {
                type = data.rpcEngine
                name = Name.identifier("engine")
            }

            body = irBuiltins.createIrBuilder(symbol).irBlockBody {
                +irReturn(irCallConstructor(data.clientGeneratedClassType(declaration).constructors.first(), emptyList()).apply {
                    putValueArgument(0, irGet(parameter))
                })
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
            overriddenSymbols = listOf(data.rpcMethodClassTypeProvider.owner.functions.first { it.name.asString() == "methodClassType" }.symbol)

            addDispatchReceiver {
                type = companionObject.defaultType
                origin = pluginOrigin
            }

            val parameter = addValueParameter {
                type = irBuiltins.stringType
                name = Name.identifier("methodName")
            }

            body = irBuiltins.createIrBuilder(symbol).irBlockBody {
                +irReturn(irNull())
            }
        }
    }
}

private class IrRpcServiceOfCallsTransformer : IrElementTransformer<IrTransformerContext> {
    override fun visitCall(expression: IrCall, data: IrTransformerContext): IrElement {
        val call = super.visitCall(expression, data)

        if (expression.symbol == data.rpcServiceOfStubFunction) {
            return replaceRpcServiceOfCall(expression, data)
        }

        return call
    }

    private fun replaceRpcServiceOfCall(expression: IrCall, data: IrTransformerContext): IrCall {
        val serviceClassSymbol = expression.typeArguments.first()?.classOrNull
            ?: error("Expected class type parameter")

        val serviceCompanionObject = data.generatedServiceCompanionObjects[serviceClassSymbol]
            ?: error("Expected generated companion object for service ${serviceClassSymbol.owner.name.asString()}")

        return irCall(expression, data.rpcServiceOfReplacementFunction).apply {
            putValueArgument(0, IrGetObjectValueImpl(startOffset, endOffset, serviceCompanionObject.typeWith(), serviceCompanionObject.symbol))
            putValueArgument(1, expression.getValueArgument(0))
        }
    }
}
