/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("detekt:all")

package org.jetbrains.krpc.codegen

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.symbol.KSType

class RPCClientServiceGenerator(private val codegen: CodeGenerator) {
    companion object {
        private const val REGISTER_PLAIN_FLOW_FIELD_METHOD = "registerPlainFlowField"
        private const val REGISTER_SHARED_FLOW_FIELD_METHOD = "registerSharedFlowField"
        private const val REGISTER_STATE_FLOW_FIELD_METHOD = "registerStateFlowField"
    }

    fun generate(service: RPCServiceDeclaration) {
        val writer = codegen.createNewFile(
            dependencies = Dependencies(aggregating = true, service.file),
            packageName = "org.jetbrains.krpc",
            fileName = service.simpleName.withClientImplSuffix(),
            extensionName = "kt",
        ).bufferedWriter(charset = Charsets.UTF_8).codeWriter()

        generate(writer, service)
    }

    private fun generate(
        writer: CodeWriter,
        service: RPCServiceDeclaration,
    ) {
        writer.write("@file:Suppress(\"RedundantUnitReturnType\", \"RemoveRedundantQualifierName\", \"USELESS_CAST\", \"UNCHECKED_CAST\", \"ClassName\", \"MemberVisibilityCanBePrivate\", \"KotlinRedundantDiagnosticSuppress\", \"UnusedImport\")")
        writer.newLine()
        writer.write("@file:OptIn(InternalKRPCApi::class)")
        writer.newLine()
        writer.newLine()

        writer.write("package org.jetbrains.krpc")
        writer.newLine()
        writer.newLine()

        generateImports(writer, service)

        writer.write("@Suppress(\"unused\")")
        writer.newLine()
        writer.write("class ${service.simpleName.withClientImplSuffix()}(private val engine: RPCClientEngine) : ${service.fullName} {")
        writer.newLine()


        val nested = writer.nested()

        nested.write("override val coroutineContext: CoroutineContext = engine.coroutineContext + Job()")
        nested.newLine()
        nested.newLine()

        service.fields.forEach {
            it.toCode(nested)
        }

        service.functions.forEach {
            it.toCode(nested)
        }
        generateProviders(writer.nested(), service)
        writer.write("}")

        writer.flush()
    }

    private fun generateImports(writer: CodeWriter, service: RPCServiceDeclaration) {
        writer.write("import kotlinx.coroutines.Job")
        writer.newLine()
        writer.write("import kotlinx.coroutines.withContext")
        writer.newLine()
        writer.write("import kotlinx.serialization.Serializable")
        writer.newLine()
        writer.write("import kotlinx.serialization.Contextual")
        writer.newLine()
        writer.write("import org.jetbrains.krpc.internal.*")
        writer.newLine()
        writer.write("import kotlin.reflect.typeOf")
        writer.newLine()
        writer.write("import kotlin.coroutines.CoroutineContext")
        writer.newLine()
        service.collectRootImports().forEach {
            writer.write("import ${it.simpleName.asString()}")
            writer.newLine()
        }
        writer.newLine()
        writer.newLine()
    }

    private fun RPCServiceDeclaration.Function.toCode(writer: CodeWriter) {
        generateFunctionClass(writer)

        val returnTypeGenerated = if (returnType.isUnit()) ": Unit" else ": ${returnType.toCode()}"
        writer.write("override suspend fun ${name}(${argumentTypes.joinToString { it.toCode() }})$returnTypeGenerated = withContext(coroutineContext) {")
        writer.newLine()
        generateBody(writer.nested())
        writer.write("}")
        writer.newLine()
        writer.newLine()
    }

    private fun RPCServiceDeclaration.Function.Argument.toCode(): String {
        val prefix = if (isVararg) "vararg " else ""
        return "$prefix$name: ${type.toCode()}"
    }

    private fun RPCServiceDeclaration.FlowField.toCode(writer: CodeWriter) {
        val method = when (flowType) {
            RPCServiceDeclaration.FlowField.Type.Plain -> REGISTER_PLAIN_FLOW_FIELD_METHOD
            RPCServiceDeclaration.FlowField.Type.Shared -> REGISTER_SHARED_FLOW_FIELD_METHOD
            RPCServiceDeclaration.FlowField.Type.State -> REGISTER_STATE_FLOW_FIELD_METHOD
        }

        val codeType = type.toCode()

        val (prefix, suffix) = when {
            isEager -> "=" to ""
            else -> "by lazy {" to " }"
        }

        val codeDeclaration = "override val $name: $codeType $prefix engine.$method(\"$name\\\$field\", typeOf<$codeType>())$suffix"

        writer.write(codeDeclaration)
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

    private fun KSType.toCodeWithStarTypeArguments(): String {
        val qualifier = declaration.qualifiedName?.asString()
            ?: codegenError<AbsentQualifiedNameCodeGenerationException>(declaration)

        val typeParameters = if (arguments.isNotEmpty()) {
            "<${arguments.joinToString { "*" }}>"
        } else ""

        val nullability = when {
            isMarkedNullable -> "?"
            else -> ""
        }

        return "$qualifier$typeParameters$nullability"
    }

    private fun KSType?.isUnit(): Boolean {
        return this == null || this.declaration.simpleName.getShortName() == "Unit"
    }

    private fun RPCServiceDeclaration.Function.generateBody(writer: CodeWriter) {
        writer.write("val returnType = typeOf<${returnType.toCode()}>()")
        writer.newLine()

        val data = "${name.functionGeneratedClass()}${
            if (argumentTypes.isEmpty()) "" else "(${
                argumentTypes.joinToString {
                    when {
                        it.isVararg -> "${it.name}.toList()"
                        else -> it.name
                    }
                }
            })"
        }"

        val prefix = if (returnType.isUnit()) "" else "val result = "
        writer.write("${prefix}engine.call(RPCCallInfo(\"${name}\\\$method\", $data, typeOf<${name.functionGeneratedClass()}>(), returnType))")
        writer.newLine()

        if (!returnType.isUnit()) {
            writer.write("check(result is ${returnType.toCodeWithStarTypeArguments()})")
            writer.newLine()
            writer.write("return@withContext result as ${returnType.toCode()}")
            writer.newLine()
        }
    }

    private fun RPCServiceDeclaration.Function.generateFunctionClass(writer: CodeWriter) {
        writer.write("@Serializable")
        writer.newLine()
        writer.write("@Suppress(\"unused\")")
        writer.newLine()
        val classOrObject = if (argumentTypes.isEmpty()) "object" else "class"
        writer.write("internal $classOrObject ${name.functionGeneratedClass()}${if (argumentTypes.isEmpty()) " : RPCMethodClassArguments {" else "("}")
        if (argumentTypes.isNotEmpty()) {
            writer.newLine()
            with(writer.nested()) {
                argumentTypes.forEach { arg ->
                    val prefix = if (arg.isContextual) {
                        "@Contextual "
                    } else ""

                    val type = if (arg.isVararg) "List<${arg.type.toCode()}>" else arg.type.toCode()
                    write("${prefix}val ${arg.name}: $type,")
                    newLine()
                }
            }
            writer.write(") : RPCMethodClassArguments {")
        }
        writer.newLine()
        with(writer.nested()) {
            val array = if (argumentTypes.isEmpty()) "emptyArray()" else {
                "arrayOf(${argumentTypes.joinToString { it.name }})"
            }
            write("override fun asArray(): Array<Any?> = $array")
            newLine()
        }
        writer.write("}")
        writer.newLine()
        writer.newLine()
    }

    private fun generateProviders(writer: CodeWriter, service: RPCServiceDeclaration) {
        writer.newLine()
        writer.write("companion object : RPCClientObject<${service.fullName}> {")
        writer.newLine()
        with(writer.nested()) {
            val mapFunction = if (service.functions.isEmpty()) "emptyMap()" else "mapOf("
            write("private val methodNames: Map<String, kotlin.reflect.KType> = $mapFunction")
            if (service.functions.isNotEmpty()) {
                newLine()
                with(nested()) {
                    service.functions.forEach { function ->
                        write("${service.fullName}::${function.name}.name to typeOf<${service.simpleName.withClientImplSuffix()}.${function.name.functionGeneratedClass()}>(),")
                        newLine()
                    }
                }
                write(")")
            }
            newLine()
            newLine()

            write("@Suppress(\"unused\")")
            newLine()
            write("override fun methodTypeOf(methodName: String): kotlin.reflect.KType? = methodNames[methodName]")
            newLine()
            newLine()

            write("override fun client(engine: RPCClientEngine): ${service.fullName} = ${service.simpleName.withClientImplSuffix()}(engine)")
            newLine()
            newLine()

            write("override fun rpcFields(client: ${service.fullName}): List<RPCField<*>> = with(client) {")
            newLine()
            with(nested()) {
                if (service.fields.isEmpty()) {
                    write("return emptyList()")
                } else {
                    write("return listOf(")
                    newLine()
                    with(nested()) {
                        service.fields.forEach {
                            write("${it.name},")
                            newLine()
                        }
                    }
                    write(") as List<RPCField<*>>")
                }
                newLine()
            }
            write("}")
            newLine()
        }
        writer.write("}")
        writer.newLine()
    }
}

fun String.withClientImplSuffix() = "${this}Client"

fun String.functionGeneratedClass() = "${replaceFirstChar { it.uppercaseChar() }}_RPCData"
