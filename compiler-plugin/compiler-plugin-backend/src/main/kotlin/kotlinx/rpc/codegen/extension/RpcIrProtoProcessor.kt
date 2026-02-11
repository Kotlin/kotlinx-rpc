/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.extension

import kotlinx.rpc.codegen.common.ProtoClassId
import kotlinx.rpc.codegen.common.ProtoNames
import kotlinx.rpc.codegen.common.RpcClassId
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclarationParent
import org.jetbrains.kotlin.ir.util.fqNameWhenAvailable
import org.jetbrains.kotlin.ir.util.getPackageFragment
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.util.isInterface
import org.jetbrains.kotlin.ir.util.packageFqName
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

internal class RpcIrProtoProcessor(
    @Suppress("unused")
    private val logger: MessageCollector,
) {
    fun visitClass(declaration: IrClass, data: RpcIrContext) {
        if (declaration.hasAnnotation(ProtoClassId.protoMessageAnnotation)
            && declaration.isInterface)
        {
            processMessage(declaration, data)
        }
    }

    private fun processMessage(message: IrClass, context: RpcIrContext) {
        val packageFqName = message.getPackageFragment().packageFqName
        val relativeName = generateSequence(message) { it.parent as? IrClass }
            .toList()
            .asReversed()
            .joinToString(".") { it.name.identifier + "Internal" }

        val internalFqName = packageFqName.child(Name.identifier(relativeName))

        val internalMessage = context.vsApi {
            referenceClass(
                context.pluginContext,
                internalFqName.parent().asString(),
                internalFqName.shortName().asString()
            )
        }?.owner ?: error(
            "Unable to find internal message class $internalFqName of ${message.fqNameWhenAvailable}"
        )

        val descriptorObject = internalMessage.declarations.filterIsInstance<IrClass>()
            .find { it.name == ProtoNames.DESCRIPTOR_NAME }
            ?: error("Expected generated stub class to be present in ${internalMessage.name}. " +
                    "The Proto generation plugin failed to do so.")

        val declaration = ProtoDeclaration(
            message = message,
            messageInternal = internalMessage,
            descriptor = descriptorObject,
        )
        ProtoDescriptorGenerator(declaration, context, logger).generate()
    }
}
