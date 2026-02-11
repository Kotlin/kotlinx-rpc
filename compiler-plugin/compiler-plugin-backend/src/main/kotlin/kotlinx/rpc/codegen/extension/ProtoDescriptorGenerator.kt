/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.extension

import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.expressions.impl.IrClassReferenceImpl
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.defaultType


/**
 * Currently, this only generates the @WithProtoDescriptor annotation for the message interface.
 * The proto-gen plugin generates the actual descriptor as part of the internal message class,
 * similar to CODEC.
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
        addObjectAnnotation()
    }

    /**
     * Add the @WithProtoDescriptor annotation to the message interface.
     * This is used by `protoDescriptorOf()` to get the descriptor.
     * The java implementation uses reflection to find the descriptor object.
     *
     * ```
     * @WithProtoDescriptor(MyMessageInternal.$protoDescriptor::class)
     * interface MyMessage {}
     * ```
     */
    private fun addObjectAnnotation() {
        val message = declaration.message

        message.annotations += ctx.vsApi {
            IrConstructorCallImplVS(
                startOffset = message.startOffset,
                endOffset = message.endOffset,
                type = ctx.withProtoDescriptor.defaultType,
                symbol = ctx.withProtoDescriptor.constructors.single(),
                typeArgumentsCount = 0,
                constructorTypeArgumentsCount = 0,
                valueArgumentsCount = 1,
            ).apply {
                val descriptorObjectType = declaration.descriptor.defaultType
                arguments(this@vsApi) {
                    values {
                        +IrClassReferenceImpl(
                            startOffset = startOffset,
                            endOffset = endOffset,
                            type = ctx.irBuiltIns.kClassClass.typeWith(descriptorObjectType),
                            symbol = declaration.descriptor.symbol,
                            classType = descriptorObjectType,
                        )
                    }
                }
            }
        }
    }


}