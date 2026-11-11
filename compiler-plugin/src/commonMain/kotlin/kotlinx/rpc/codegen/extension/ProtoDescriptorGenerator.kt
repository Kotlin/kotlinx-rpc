/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.extension

import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.expressions.impl.IrClassReferenceImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrVarargImpl
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.ir.util.companionObject
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.defaultType

internal class ProtoDeclaration(
    val message: IrClass,
    val messageInternal: IrClass,
    val descriptor: IrClass,
    val marshaller: IrClass,
)

/**
 * Currently, this only generates the @WithProtoDescriptor annotation for the message interface.
 * The proto-gen plugin generates the actual descriptor as part of the internal message class,
 * similar to MARSHALLER.
 *
 * ```
 * class MyMessageInternal : MyMessage {
 *     @InternalRpcApi
 *     public object DESCRIPTOR: ProtoDescriptor<MyMessage> = ...
 * }
 * ```
 */
internal class ProtoDescriptorGenerator(
    private val declaration: ProtoDeclaration,
    private val ctx: RpcIrContext,
    @Suppress("unused")
    private val logger: MessageCollector,
) {

    fun generate() {
        addWithProtoDescriptorAnnotation()
        addWithGrpcMarshallerAnnotation()
        addSubclassOptInRequiredAnnotation()

        declaration.message.companionObject()?.apply {
            generateCompanionObjectConstructor(ctx.pluginContext)
        }
    }

    /**
     * Add the @WithProtoDescriptor annotation to the message interface.
     * This is used by `protoDescriptorOf()` to get the descriptor.
     * The java implementation uses reflection to find the descriptor object.
     *
     * ```
     * @WithProtoDescriptor(MyMessageInternal.DESCRIPTOR::class)
     * interface MyMessage {}
     * ```
     */
    private fun addWithProtoDescriptorAnnotation() {
        addWithSomethingAnnotation(ctx.withProtoDescriptor, ProtoDeclaration::descriptor)
    }

    /**
     * Add the @WithGrpcMarshaller annotation to the message interface.
     * This is used by `grpcMarshallerOf()` to get the GrpcMarshaller object.
     * The java implementation uses reflection to find the descriptor object.
     *
     * ```
     * @WithGrpcMarshaller(MyMessageInternal.MARSHALLER::class)
     * interface MyMessage {}
     * ```
     */
    private fun addWithGrpcMarshallerAnnotation() {
        addWithSomethingAnnotation(ctx.withGrpcMarshallerAnnotation, ProtoDeclaration::marshaller)
    }

    /**
     * Add the @SubclassOptInRequired(ProtobufMessagesInheritance::class) annotation
     * to the message interface. Uses [IrPluginContext.metadataDeclarationRegistrar]
     * so the annotation is serialized into metadata and visible to downstream modules.
     */
    private fun addSubclassOptInRequiredAnnotation() {
        val message = declaration.message
        val annotationSymbol = ctx.subclassOptInRequired
        val markerClass = ctx.protobufMessagesInheritance

        ctx.vsApi {
            val annotation = IrAnnotationVS(
                startOffset = message.startOffset,
                endOffset = message.endOffset,
                type = annotationSymbol.defaultType,
                symbol = annotationSymbol.constructors.single(),
                typeArgumentsCount = 0,
                constructorTypeArgumentsCount = 0,
                valueArgumentsCount = 1,
            ).apply {
                val markerType = markerClass.defaultType
                arguments(this@vsApi) {
                    values {
                        +IrVarargImpl(
                            startOffset = message.startOffset,
                            endOffset = message.endOffset,
                            type = ctx.irBuiltIns.arrayClass.typeWith(
                                ctx.irBuiltIns.kClassClass.typeWith(ctx.irBuiltIns.annotationType)
                            ),
                            varargElementType = ctx.irBuiltIns.kClassClass.typeWith(ctx.irBuiltIns.annotationType),
                            elements = listOf(
                                IrClassReferenceImpl(
                                    startOffset = message.startOffset,
                                    endOffset = message.endOffset,
                                    type = ctx.irBuiltIns.kClassClass.typeWith(markerType),
                                    symbol = markerClass,
                                    classType = markerType,
                                )
                            ),
                        )
                    }
                }
            }

            addMetadataVisibleAnnotationVS(
                context = ctx.pluginContext,
                declaration = message,
                annotation = annotation,
            )
        }
    }

    /**
     * Add the @With<Something> annotation to the message interface.
     *
     * ```
     * @With<Something>(MyMessageInternal.<SOMETHING>::class)
     * interface MyMessage {}
     * ```
     */
    private fun addWithSomethingAnnotation(
        withSomethingAnnotation: IrClassSymbol,
        somethingClass: ProtoDeclaration.() -> IrClass,
    ) {
        val message = declaration.message

        ctx.vsApi {
            val annotation = IrAnnotationVS(
                startOffset = message.startOffset,
                endOffset = message.endOffset,
                type = withSomethingAnnotation.defaultType,
                symbol = withSomethingAnnotation.constructors.single(),
                typeArgumentsCount = 0,
                constructorTypeArgumentsCount = 0,
                valueArgumentsCount = 1,
            ).apply {
                val clazz = declaration.somethingClass()
                val descriptorObjectType = clazz.defaultType
                arguments(this@vsApi) {
                    values {
                        +IrClassReferenceImpl(
                            startOffset = startOffset,
                            endOffset = endOffset,
                            type = ctx.irBuiltIns.kClassClass.typeWith(descriptorObjectType),
                            symbol = clazz.symbol,
                            classType = descriptorObjectType,
                        )
                    }
                }
            }

            message.addAnnotationVS(annotation)
        }
    }
}
