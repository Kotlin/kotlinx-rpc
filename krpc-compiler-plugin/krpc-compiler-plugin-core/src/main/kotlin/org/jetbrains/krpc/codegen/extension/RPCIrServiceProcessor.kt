/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc.codegen.extension

import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.expressions.impl.IrClassReferenceImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrConstructorCallImpl
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.isInterface
import org.jetbrains.kotlin.ir.visitors.IrElementTransformer

internal class RPCIrServiceProcessor(
    @Suppress("unused")
    private val logger: MessageCollector,
) : IrElementTransformer<RPCIrContext> {
    override fun visitClass(declaration: IrClass, data: RPCIrContext): IrStatement {
        if (declaration.isInterface &&
            // data.rpc is resolved lazily, so first check is rather heuristic
            declaration.maybeRPC() &&
            declaration.superTypes.contains(data.rpc.typeWith())
        ) {
            addAssociatedObjectAnnotation(declaration, data)
        }

        return super.visitClass(declaration, data)
    }

    private fun IrClass.maybeRPC() = superTypes.any { it.classFqName?.asString()?.contains("RPC") == true }

    private fun addAssociatedObjectAnnotation(declaration: IrClass, data: RPCIrContext) {
        declaration.annotations += IrConstructorCallImpl(
            startOffset = declaration.startOffset,
            endOffset = declaration.endOffset,
            type = data.optInAnnotation.typeWith(),
            symbol = data.optInAnnotation.constructors.single(),
            typeArgumentsCount = 0,
            constructorTypeArgumentsCount = 0,
            valueArgumentsCount = 1,
        ).apply {
            val internalKRPCApiClassReferenceType = data.internalKRPCApiAnnotation.typeWith()

            putValueArgument(
                index = 0,
                valueArgument = IrClassReferenceImpl(
                    startOffset = startOffset,
                    endOffset = endOffset,
                    type = data.irBuiltIns.kClassClass.typeWith(internalKRPCApiClassReferenceType),
                    symbol = data.internalKRPCApiAnnotation,
                    classType = internalKRPCApiClassReferenceType
                )
            )
        }

        declaration.annotations += IrConstructorCallImpl(
            startOffset = declaration.startOffset,
            endOffset = declaration.endOffset,
            type = data.withRPCStubObjectAnnotation.typeWith(),
            symbol = data.withRPCStubObjectAnnotation.constructors.single(),
            typeArgumentsCount = 0,
            constructorTypeArgumentsCount = 0,
            valueArgumentsCount = 1,
        ).apply {
            val companionObject = data.generatedClientCompanionObject(declaration)
            val companionObjectType = data.generatedClientCompanionObject(declaration).typeWith()
            putValueArgument(
                index = 0,
                valueArgument = IrClassReferenceImpl(
                    startOffset = startOffset,
                    endOffset = endOffset,
                    type = data.irBuiltIns.kClassClass.typeWith(companionObjectType),
                    symbol = companionObject,
                    classType = companionObjectType,
                )
            )
        }
    }
}
