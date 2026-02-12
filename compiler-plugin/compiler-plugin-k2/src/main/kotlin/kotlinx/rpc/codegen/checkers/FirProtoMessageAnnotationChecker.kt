/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.checkers

import kotlinx.rpc.codegen.FirRpcPredicates
import kotlinx.rpc.codegen.checkers.diagnostics.FirProtoDiagnostics
import kotlinx.rpc.codegen.common.ProtoClassId
import kotlinx.rpc.codegen.common.ProtoNames
import kotlinx.rpc.codegen.vsApi
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.declarations.FirRegularClass
import org.jetbrains.kotlin.fir.declarations.getAnnotationByClassId
import org.jetbrains.kotlin.fir.declarations.utils.isInterface
import org.jetbrains.kotlin.fir.extensions.predicateBasedProvider
import org.jetbrains.kotlin.fir.parentDeclarationSequence
import org.jetbrains.kotlin.fir.resolve.providers.getRegularClassSymbolByClassId
import org.jetbrains.kotlin.fir.symbols.SymbolInternals
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.Name

object FirProtoMessageAnnotationChecker {
    @OptIn(SymbolInternals::class)
    fun check(
        declaration: FirRegularClass,
        context: CheckerContext,
        reporter: DiagnosticReporter,
    ) {
        // check if the class is actually annotated with @GeneratedProtoMessage
        if (!context.session.predicateBasedProvider.matches(FirRpcPredicates.generatedProtoMessage, declaration)) {
            return
        }

        val annotation = declaration.getAnnotationByClassId(ProtoClassId.protoMessageAnnotation, context.session)
            ?: error("Unexpected unresolved @GeneratedProtoMessage annotation type for declaration: ${declaration.symbol.classId.asSingleFqName()}")

        fun reportNonGeneratedMessage() {
            reporter.reportOn(
                source = annotation.source,
                factory = FirProtoDiagnostics.PROTO_MESSAGE_IS_GENERATED_ONLY,
                context = context,
            )
        }

        // check that the class is actually generated.
        // we do this by checking if there is a corresponding Internal class
        // and if the internal class has a MyMessageInternal.DESCRIPTOR object

        if (!declaration.isInterface) {
            reportNonGeneratedMessage()
        }

        val parentClasses = declaration.parentDeclarationSequence(
            context.session,
            null,
            listOf()
        )?.filterIsInstance<FirRegularClass>()?.toList()?.reversed() ?: emptyList()

        val fullClassHierarchy = parentClasses + listOf(declaration)
        val internalTopLevelName = fullClassHierarchy.joinToString(".") { it.name.asString() + "Internal" }
            .let { Name.identifier(it) }

        val internalClassId = ClassId(declaration.symbol.classId.packageFqName, internalTopLevelName)
        val internalDeclaration = context.session.getRegularClassSymbolByClassId(internalClassId)
            // although this is not safe in general, at the point when the FirRegularClassChecker runs
            // the symbol should already have been resolved to its FirDeclaration
            ?.fir


        if (internalDeclaration == null) {
            // an internal message class does not exist, so this is not a generated message
            reportNonGeneratedMessage()
        } else {
            // check if the internal class extends the message interface



            // check if an internal class has a DESCRIPTOR object declaration
            val descriptorObject = vsApi {
                internalDeclaration.declarationsVS(context.session)
                    .filterIsInstance<FirRegularClassSymbol>()
                    .find { it.name == ProtoNames.DESCRIPTOR_NAME && it.classKind == ClassKind.OBJECT }
            }
            if (descriptorObject == null) {
                reportNonGeneratedMessage()
            }
        }

    }
}