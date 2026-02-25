/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.extension

import kotlinx.rpc.codegen.common.ProtoClassId
import kotlinx.rpc.codegen.common.ProtoNames
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.irAttribute
import org.jetbrains.kotlin.ir.util.fqNameWhenAvailable
import org.jetbrains.kotlin.ir.util.getPackageFragment
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.util.isInterface
import org.jetbrains.kotlin.name.Name

internal fun computeProtoDeclarationIfNeeded(messageClass: IrClass, context: RpcIrContext): ProtoDeclaration? {
    if (messageClass.hasAnnotation(ProtoClassId.protoMessageAnnotation) && messageClass.isInterface) {
        return computeProtoDeclaration(messageClass, context)
    }

    return null
}

private var IrClass.protoDeclarationOrNull: ProtoDeclaration? by irAttribute(copyByDefault = false)

private fun computeProtoDeclaration(
    messageClass: IrClass,
    context: RpcIrContext,
): ProtoDeclaration {
    if (messageClass.protoDeclarationOrNull != null) {
        return messageClass.protoDeclarationOrNull!!
    }

    val packageFqName = messageClass.getPackageFragment().packageFqName
    // transforms Outer.Nested to OuterInternal.NestedInternal
    val relativeName = generateSequence(messageClass) { it.parent as? IrClass }
        .toList()
        .asReversed()
        .joinToString(".") { it.name.identifier + "Internal" }

    val internalFqName = packageFqName.child(Name.identifier(relativeName))

    val internalMessage = context.vsApi {
        referenceBuiltinClassVS(
            context = context.pluginContext,
            packageName = internalFqName.parent().asString(),
            name = internalFqName.shortName().asString(),
        )
    }?.owner ?: error(
        "Unable to find internal message class $internalFqName of ${messageClass.fqNameWhenAvailable}"
    )

    val descriptorObject = internalMessage.declarations.filterIsInstance<IrClass>()
        .find { it.name == ProtoNames.DESCRIPTOR_NAME }
        ?: error("Expected generated ${ProtoNames.DESCRIPTOR_NAME} class to be present in ${internalMessage.name}. " +
                "The Proto generation plugin failed to do so.")

    val codecObject = internalMessage.declarations.filterIsInstance<IrClass>()
        .find { it.name == ProtoNames.CODEC_NAME }
        ?: error("Expected generated ${ProtoNames.CODEC_NAME} class to be present in ${internalMessage.name}. " +
                "The Proto generation plugin failed to do so.")

    val declaration = ProtoDeclaration(
        message = messageClass,
        messageInternal = internalMessage,
        descriptor = descriptorObject,
        codec = codecObject,
    )
    messageClass.protoDeclarationOrNull = declaration
    return declaration
}

internal class RpcIrProtoProcessor(
    @Suppress("unused")
    private val logger: MessageCollector,
) {
    fun visitClass(declaration: IrClass, data: RpcIrContext) {
        computeProtoDeclarationIfNeeded(declaration, data)?.let {
            ProtoDescriptorGenerator(it, data, logger).generate()
        }
    }
}
