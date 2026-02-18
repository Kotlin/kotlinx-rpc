/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.checkers

import kotlinx.rpc.codegen.FirRpcPredicates
import kotlinx.rpc.codegen.checkers.diagnostics.FirProtoDiagnostics
import kotlinx.rpc.codegen.common.ProtoClassId
import kotlinx.rpc.codegen.common.ProtoNames
import kotlinx.rpc.codegen.doesMatchesClassId
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
            session = context.session,
            dispatchReceiver = null,
            containingDeclarations = listOf()
        )?.filterIsInstance<FirRegularClass>()?.toList()?.reversed() ?: emptyList()

        val topLevelNames = (parentClasses + listOf(declaration))
            .map { Name.identifier(it.name.asString() + "Internal") }

        // for nested classes, we need to construct ClassId properly using createNestedClassId for each level
        val internalClassId = ClassId(
            packageFqName = declaration.symbol.classId.packageFqName,
            topLevelName = topLevelNames.first()
        ).let {
            topLevelNames.drop(1).fold(it) { acc, name ->
                acc.createNestedClassId(name)
            }
        }

        val internalDeclaration = context.session.getRegularClassSymbolByClassId(internalClassId)

        if (internalDeclaration == null) {
            // an internal message class does not exist, so this is not a generated message
            reportNonGeneratedMessage()
        } else {
            // check if the internal class extends the message interface
            val implementsMessage = internalDeclaration.resolvedSuperTypeRefs.any {
                it.doesMatchesClassId(context.session, declaration.symbol.classId)
            }
            if (!implementsMessage) {
                reportNonGeneratedMessage()
            }

            // check if an internal class has a DESCRIPTOR object declaration
            val descriptorObject = vsApi {
                internalDeclaration.declarationsVS(context.session)
                    .filterIsInstance<FirRegularClassSymbol>()
                    .find { it.name == ProtoNames.DESCRIPTOR_NAME
                            && it.classKind == ClassKind.OBJECT
                            && it.resolvedSuperTypeRefs.any {
                                superType -> superType.doesMatchesClassId(context.session, ProtoClassId.protoDescriptor)
                            }
                    }
            }
            if (descriptorObject == null) {
                reportNonGeneratedMessage()
            }
        }

    }
}