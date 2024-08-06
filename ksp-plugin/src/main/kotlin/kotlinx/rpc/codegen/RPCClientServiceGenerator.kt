/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("detekt:all")

package kotlinx.rpc.codegen

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.symbol.KSType

class RPCClientServiceGenerator(private val codegen: CodeGenerator) {
    fun generate(service: RPCServiceDeclaration) {
        val writer = codegen.createNewFile(
            dependencies = Dependencies(aggregating = true, service.file),
            packageName = service.packageName,
            fileName = service.simpleName.withStubSuffix(),
            extensionName = "kt",
        ).bufferedWriter(charset = Charsets.UTF_8).codeWriter()

        generate(writer, service)
    }

    private fun generate(
        writer: CodeWriter,
        service: RPCServiceDeclaration,
    ) {
        writer.writeLine("@file:Suppress(\"RedundantUnitReturnType\", \"RemoveRedundantQualifierName\", \"USELESS_CAST\", \"UNCHECKED_CAST\", \"ClassName\", \"MemberVisibilityCanBePrivate\", \"KotlinRedundantDiagnosticSuppress\", \"UnusedImport\", \"detekt.all\")")
        writer.writeLine("@file:OptIn(InternalRPCApi::class)")
        writer.newLine()

        if (service.packageName.isNotBlank()) {
            writer.writeLine("package ${service.packageName}")
            writer.newLine()
        }

        generateImports(writer, service)

        generateStubClass(service, writer)

        writer.flush()
    }

    private fun generateStubClass(service: RPCServiceDeclaration, writer: CodeWriter) {
        writer.writeLine("@Deprecated(level = DeprecationLevel.HIDDEN, message = \"Internal kotlinx.rpc API, do not use!\")")
        writer.writeLine("class ${service.simpleName.withStubSuffix()} {")

        with(writer.nested()) {
            service.functions.forEach {
                it.generateFunctionClass(this)
            }

            writeLine("companion object")
        }

        writer.writeLine("}")
    }

    private fun generateImports(writer: CodeWriter, service: RPCServiceDeclaration) {
        writer.writeLine("import kotlinx.coroutines.*")
        writer.writeLine("import kotlinx.serialization.*")
        writer.writeLine("import kotlinx.rpc.internal.*")
        writer.writeLine("import kotlinx.rpc.*")
        writer.writeLine("import kotlin.reflect.typeOf")
        writer.writeLine("import kotlin.coroutines.CoroutineContext")
        service.collectRootImports().forEach {
            writer.writeLine("import ${it.simpleName.asString()}")
        }
        writer.newLine()
        writer.newLine()
    }

    private fun KSType.toCode(): String {
        val qualifier = declaration.qualifiedName?.asString()
            ?: codegenError<AbsentQualifiedNameCodeGenerationException>(declaration)

        val arguments = arguments.joinToString {
            val variance =
                it.variance.label.let { variance -> if (variance.isBlank() || variance == "*") "" else "$variance " }
            "$variance${it.type?.resolve()?.toCode() ?: ""}"
        }.let {
            if (it.isEmpty()) "" else "<$it>"
        }

        val nullability = when {
            isMarkedNullable -> "?"
            else -> ""
        }

        return "$qualifier$arguments$nullability"
    }


    private fun RPCServiceDeclaration.Function.generateFunctionClass(writer: CodeWriter) {
        writer.writeLine("@Serializable")
        val classOrObject = if (argumentTypes.isEmpty()) "object" else "class"
        writer.writeLine("$classOrObject ${name.functionGeneratedClass()}${if (argumentTypes.isEmpty()) "" else "("}")
        if (argumentTypes.isNotEmpty()) {
            with(writer.nested()) {
                argumentTypes.forEach { arg ->
                    val prefix = if (arg.isContextual) {
                        "@Contextual "
                    } else {
                        ""
                    }

                    val type = if (arg.isVararg) "List<${arg.type.toCode()}>" else arg.type.toCode()
                    writeLine("${prefix}val ${arg.name}: $type,")
                }
            }
            writer.writeLine(")")
        }
        writer.newLine()
    }
}

fun String.withStubSuffix() = "${this}_rpcServiceStub"

fun String.functionGeneratedClass() = "${this}_rpcMethod"
