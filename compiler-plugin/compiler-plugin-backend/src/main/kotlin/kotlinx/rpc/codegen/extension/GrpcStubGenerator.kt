/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.extension

import kotlinx.rpc.codegen.VersionSpecificApiImpl.asConstValueVS
import kotlinx.rpc.codegen.common.RpcClassId
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.builders.IrBlockBodyBuilder
import org.jetbrains.kotlin.ir.builders.declarations.addFunction
import org.jetbrains.kotlin.ir.builders.declarations.addValueParameter
import org.jetbrains.kotlin.ir.builders.irBlock
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.irBranch
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irCallConstructor
import org.jetbrains.kotlin.ir.builders.irEqualsNull
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irIfNull
import org.jetbrains.kotlin.ir.builders.irReturn
import org.jetbrains.kotlin.ir.builders.irTemporary
import org.jetbrains.kotlin.ir.builders.irTrue
import org.jetbrains.kotlin.ir.builders.irWhen
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrClassReference
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin
import org.jetbrains.kotlin.ir.expressions.impl.IrCallImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrGetEnumValueImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrGetObjectValueImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrGetValueImpl
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.classOrFail
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.types.makeNotNull
import org.jetbrains.kotlin.ir.types.makeNullable
import org.jetbrains.kotlin.ir.types.starProjectedType
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.dumpKotlinLike
import org.jetbrains.kotlin.ir.util.file
import org.jetbrains.kotlin.ir.util.getAnnotation
import org.jetbrains.kotlin.ir.util.kotlinFqName
import org.jetbrains.kotlin.ir.util.packageFqName
import org.jetbrains.kotlin.ir.util.primaryConstructor
import org.jetbrains.kotlin.name.Name

private object GrpcDescriptor {
    const val DELEGATE = "delegate"
}

/**
 * Adds gRPC specifics on top of the base stub:
 * the descriptor additionally implements `GrpcServiceDescriptor` and its `delegate` function.
 */
internal class GrpcStubGenerator(
    declaration: ServiceDeclaration,
    ctx: RpcIrContext,
    logger: MessageCollector,
) : RpcStubGenerator(declaration, ctx, logger) {
    // TODO: Move the GrpcAnnotation prop accesses into a helper?
    private val grpcAnnotation = declaration.service.getAnnotation(RpcClassId.grpcAnnotation.asSingleFqName())
        ?: error("Expected @Grpc annotation on ${declaration.fqName}")

    // if the protoPackage is not set by the annotation, we use the service kotlin package name
    private val protoPackage: String = vsApi { grpcAnnotation.valueArgumentAtVS(0)?.assertConstString() }?.trim()
        ?: declaration.service.packageFqName?.asString().orEmpty()

    // if the protoServiceName is set in the @Grpc annotation, we use it instead of the service's class name
    private val simpleName: String = vsApi { grpcAnnotation.valueArgumentAtVS(1)?.assertConstString() }?.takeIf { it.isNotBlank() }
        ?: declaration.simpleName

    // the name of the service based on the proto file (or @Grpc annotation)
    private val serviceFqName: String = if (protoPackage.isNotEmpty()) "$protoPackage.$simpleName" else simpleName

    // TODO: Can be done better?
    override fun descriptorSimpleName(): String = simpleName

    override fun IrClass.generateAdditionalDescriptorContent() {
        generateGrpcDelegateProperty()
    }

    /**
     * ```kotlin
     * override fun delegate(resolver: GrpcMarshallerResolver, marshallerConfig: GrpcMarshallerConfig?): GrpcServiceDelegate {
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
                type = ctx.grpcMarshallerResolver.defaultType
            }

            val marshallerConfig = addValueParameter {
                name = Name.identifier("marshallerConfig")
                type = ctx.grpcMarshallerConfig.defaultType.makeNullable()
            }

            body = irBuilder(ctx.pluginContext, symbol).irBlockBody {
                val methodDescriptorMap = irTemporary(
                    value = irMethodDescriptorMap(resolver, marshallerConfig),
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
    private fun IrBlockBodyBuilder.irMethodDescriptorMap(
        resolver: IrValueParameter,
        marshallerConfig: IrValueParameter
    ): IrCallImpl {
        return irMapOf(
            keyType = ctx.irBuiltIns.stringType,
            valueType = ctx.grpcPlatformMethodDescriptor(declaration.stubClass.file).starProjectedType,
            declaration.methods.memoryOptimizedMap { method ->
                stringConst(method.name) to irMethodDescriptor(method, resolver, marshallerConfig)
            },
        )
    }

    /**
     * gRPC Platform MethodDescriptor call
     *
     * ```kotlin
     * // In scope: resolver: GrpcMarshallerResolver
     *
     * methodDescriptor<<request-type>, <response-type>>(
     *     fullMethodName = "${descriptor.serviceFqName}/${<from Grpc.Method annotation> ?: callable.name}",
     *     requestMarshaller = <request-marshaller>,
     *     responseMarshaller = <response-marshaller>,
     *     type = MethodType.<method-type>,
     *     schemaDescriptor = null, // null for now
     *     idempotent = <from Grpc.Method annotation>,
     *     safe = <from Grpc.Method annotation>,
     *     sampledToLocalTracing = <from Grpc.Method annotation>,
     * )
     * ```
     *
     * Where:
     *   - <request-type> - the type of the request, namely the first parameter, unwrapped if a Flow
     *   - <response-type> - the type of the response, unwrapped if a Flow
     *   - <method-name> - the name of the method
     *   - <method-type> - one of MethodType.UNARY, MethodType.SERVER_STREAMING,
     *   MethodType.CLIENT_STREAMING, MethodType.BIDI_STREAMING
     *   - <request-marshaller>/<response-marshaller> - a GrpcMarshaller getter, see [irMarshallerWithConfigCheck]
     */
    private fun IrBlockBodyBuilder.irMethodDescriptor(
        method: ServiceDeclaration.Method,
        resolver: IrValueParameter,
        marshallerConfig: IrValueParameter,
    ): IrCall {
        check(method.arguments.size <= 1) {
            "Only single or none argument methods are allowed here"
        }

        val requestParameterType = method.arguments.getOrNull(0)?.type ?: ctx.irBuiltIns.unitType
        val responseParameterType = method.function.returnType

        val requestType: IrType = requestParameterType.unwrapFlow(declaration.stubClass.file)
        val responseType: IrType = responseParameterType.unwrapFlow(declaration.stubClass.file)

        val methodDescriptorType = ctx.grpcPlatformMethodDescriptor(declaration.stubClass.file)
            .typeWith(requestType, responseType)

        return vsApi {
            irCall(
                type = methodDescriptorType,
                callee = ctx.functions.methodDescriptor,
                typeArgumentsCount = 2,
            )
        }.apply {
            arguments {
                types {
                    +requestType
                    +responseType
                }

                val grpcMethodAnnotation = method.function
                    .getAnnotation(RpcClassId.grpcMethodAnnotation.asSingleFqName())

                // the name from @Grpc.Method if set, otherwise the function name
                val grpcName = vsApi {
                    grpcMethodAnnotation?.getValueArgumentVS(Name.identifier("name"))
                }?.asConstValueVS(String::class)?.takeIf { it.isNotBlank() } ?: method.name

                values {
                    // fullMethodName
                    +stringConst("${serviceFqName}/${grpcName}")

                    // requestMarshaller
                    +irMarshallerWithConfigCheck(requestType, resolver, marshallerConfig)

                    // responseMarshaller
                    +irMarshallerWithConfigCheck(responseType, resolver, marshallerConfig)

                    val flow = ctx.flow(declaration.stubClass.file)
                    // type
                    +IrGetEnumValueImpl(
                        startOffset = UNDEFINED_OFFSET,
                        endOffset = UNDEFINED_OFFSET,
                        type = ctx.grpcPlatformMethodType.defaultType,
                        symbol = when {
                            requestParameterType.classOrNull == flow && responseParameterType.classOrNull == flow -> {
                                ctx.grpcPlatformMethodTypeBidiStreaming
                            }

                            requestParameterType.classOrNull == flow && responseParameterType.classOrNull != flow -> {
                                ctx.grpcPlatformMethodTypeClientStreaming
                            }

                            requestParameterType.classOrNull != flow && responseParameterType.classOrNull == flow -> {
                                ctx.grpcPlatformMethodTypeServerStreaming
                            }

                            else -> {
                                ctx.grpcPlatformMethodTypeUnary
                            }
                        },
                    )

                    // schemaDescriptor
                    +nullConst(ctx.anyNullable)

                    val idempotent = vsApi {
                        grpcMethodAnnotation?.getValueArgumentVS(Name.identifier("idempotent"))
                    }?.asConstValueVS(Boolean::class) ?: false

                    // idempotent
                    +booleanConst(idempotent)

                    val safe = vsApi {
                        grpcMethodAnnotation?.getValueArgumentVS(Name.identifier("safe"))
                    }?.asConstValueVS(Boolean::class) ?: false

                    // safe
                    +booleanConst(safe)

                    val sampledToLocalTracing = vsApi {
                        grpcMethodAnnotation?.getValueArgumentVS(Name.identifier("sampledToLocalTracing"))
                    }?.asConstValueVS(Boolean::class) ?: true

                    // sampledToLocalTracing
                    +booleanConst(sampledToLocalTracing)
                }
            }
        }
    }

    /**
     * Check if the [marshallerConfig] is null. If yes, return a wrapped marshaller that uses the default marshaller configuration.
     * Otherwise, return the marshaller.
     *
     * Delegates actual marshaller resolving to [irMarshaller].
     *
     * ```
     * if (marshallerConfig == null) {
     *    return marshaller // marshaller from irMarshaller
     * } else {
     *   return ConfiguredGrpcMarshallerDelegate(config, marshaller)
     * }
     * ```
     */
    private fun IrBlockBodyBuilder.irMarshallerWithConfigCheck(
        messageType: IrType,
        resolver: IrValueParameter,
        marshallerConfig: IrValueParameter,
    ): IrExpression {

        val marshaller = irTemporary(
            value = irMarshaller(messageType, resolver),
            nameHint = "marshaller",
            isMutable = false,
        )

        val resolvedMarshaller = irCallConstructor(
            callee = ctx.configuredGrpcMarshallerDelegate.constructors.single(),
            typeArguments = listOf(messageType),
        ).apply {
            type = ctx.configuredGrpcMarshallerDelegate.typeWith(messageType)
            arguments {
                values {
                    +irGet(marshallerConfig)
                    +irGet(marshaller)
                }
            }
        }

        return irIfNull(
            type = ctx.grpcMarshaller.typeWith(messageType),
            subject = irGet(marshallerConfig),
            thenPart = irGet(marshaller),
            elsePart = resolvedMarshaller,
        )
    }

    /**
     * If [messageType] is annotated with [RpcIrContext.withGrpcMarshallerAnnotation],
     * we use its marshaller object
     *
     * If not, use [resolver].resolveOrNull()
     */
    private fun IrBlockBodyBuilder.irMarshaller(messageType: IrType, resolver: IrValueParameter): IrExpression {
        val owner = messageType.classOrFail.owner
        val marshallerClassSymbol = computeProtoDeclarationIfNeeded(owner, ctx)
            ?.marshaller?.symbol
            ?: owner.extractMarshallerReference()

        return if (marshallerClassSymbol != null) {
            IrGetObjectValueImpl(
                startOffset = UNDEFINED_OFFSET,
                endOffset = UNDEFINED_OFFSET,
                type = marshallerClassSymbol.defaultType,
                symbol = marshallerClassSymbol,
            )
        } else {
            val marshallerType = ctx.grpcMarshaller.typeWith(messageType)
            val marshallerCall = vsApi {
                irCall(
                    type = marshallerType.makeNullable(),
                    callee = ctx.functions.grpcMarshallerResolverResolveOrNull.symbol,
                    typeArgumentsCount = 0,
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
                        +irTypeOfCall(messageType)
                    }
                }
            }

            irElvis(
                expression = marshallerCall,
                ifNull = irCall(ctx.irBuiltIns.illegalArgumentExceptionSymbol).apply {
                    arguments {
                        values {
                            +stringConst("No marshaller found for ${messageType.classFqName}")
                        }
                    }
                },
            )
        }
    }

    private fun IrClass.extractMarshallerReference(): IrClassSymbol? {
        val withMarshallerAnnotation = getAnnotation(ctx.withGrpcMarshallerAnnotation.owner.kotlinFqName)

        if (withMarshallerAnnotation != null) {
            val classReference = vsApi { withMarshallerAnnotation.argumentsVS }.single() as? IrClassReference
                ?: error("Expected IrClassReference for ${ctx.withGrpcMarshallerAnnotation.owner.kotlinFqName} parameter")

            return classReference.classType.classOrFail
        }

        return null
    }

    private fun IrType.unwrapFlow(from: IrFile): IrType {
        return if (classOrNull == ctx.flow(from)) {
            (this as IrSimpleType).arguments[0].typeOrFail
        } else {
            this
        }
    }

    /**
     * Ir call to `serviceDescriptor`
     *
     * ```kotlin
     * serviceDescriptor(
     *     name = my.proto.package.MyService, // $declaration.serviceFqName
     *     methods = methodDescriptorMap.values, // Collection<MethodDescriptor<*, *>>
     *     schemaDescriptor = null, // for now, only null
     * )
     * ```
     */
    private fun irServiceDescriptor(methodDescriptorMap: IrVariable): IrCallImpl {
        return IrCallImpl(
            startOffset = UNDEFINED_OFFSET,
            endOffset = UNDEFINED_OFFSET,
            type = ctx.grpcPlatformServiceDescriptor(declaration.stubClass.file).defaultType,
            symbol = ctx.functions.serviceDescriptor,
            typeArgumentsCount = 0,
        ).apply {
            arguments {
                values {
                    +stringConst(serviceFqName)

                    +irCallProperty(
                        receiver = IrGetValueImpl(
                            startOffset = UNDEFINED_OFFSET,
                            endOffset = UNDEFINED_OFFSET,
                            type = methodDescriptorMap.type,
                            symbol = methodDescriptorMap.symbol,
                        ),
                        property = ctx.properties.mapValues.owner,
                    ).apply {
                        type = ctx.irBuiltIns.collectionClass.typeWith(
                            ctx.grpcPlatformMethodDescriptor(declaration.stubClass.file).starProjectedType,
                        )
                    }

                    +nullConst(ctx.anyNullable)
                }
            }
        }
    }

    private fun IrBlockBodyBuilder.irElvis(expression: IrExpression, ifNull: IrExpression): IrExpression {
        check(expression.type == ifNull.type || ifNull.type == ctx.irBuiltIns.nothingType) {
            "Type mismatch: ${expression.type.dumpKotlinLike()} != ${ifNull.type.dumpKotlinLike()}"
        }

        return irBlock(origin = IrStatementOrigin.ELVIS, resultType = expression.type.makeNotNull()) {
            val temp = irTemporary(
                value = expression,
                nameHint = "elvis_left_hand_side",
                isMutable = false,
            )
            +irWhen(
                type = expression.type,
                branches = listOf(
                    irBranch(
                        condition = irEqualsNull(irGet(temp)),
                        result = ifNull,
                    ),
                    irBranch(
                        condition = irTrue(),
                        result = irGet(temp),
                    ),
                ),
            )
        }
    }

    private fun IrExpression.assertConstString(): String = vsApi { asConstValueVS(String::class) }
        ?: error("Expected IrConst of kind String, got ${dumpKotlinLike()}")
}
