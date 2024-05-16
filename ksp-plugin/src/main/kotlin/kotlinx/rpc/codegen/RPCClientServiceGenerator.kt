/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("detekt:all")

package kotlinx.rpc.codegen

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.symbol.KSType

class RPCClientServiceGenerator(private val codegen: CodeGenerator) {
    companion object {
        private const val REGISTER_PLAIN_FLOW_FIELD_METHOD = "registerPlainFlowField"
        private const val REGISTER_SHARED_FLOW_FIELD_METHOD = "registerSharedFlowField"
        private const val REGISTER_STATE_FLOW_FIELD_METHOD = "registerStateFlowField"

        private const val ID_PROPERTY_NAME = "__rpc_stub_id"
        private const val CLIENT_PROPERTY_NAME = "__rpc_client"
        private const val SCOPE_PROPERTY_NAME = "__rpc_scope"
    }

    fun generate(service: RPCServiceDeclaration) {
        val writer = codegen.createNewFile(
            dependencies = Dependencies(aggregating = true, service.file),
            packageName = service.packageName,
            fileName = service.simpleName.withStubImplSuffix(useKSPSuffix = false),
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

        // todo remove after all compiler parts are done
        //  left commented out for further debugging
//        writer.write("class ${service.simpleName.withClientImplSuffix()}(")
//        writer.newLine()
//        with(writer.nested()) {
//            write("private val $ID_PROPERTY_NAME: Long,")
//            newLine()
//            write("private val $CLIENT_PROPERTY_NAME: RPCClient,")
//            newLine()
//        }
//        writer.write(") : ${service.fullName} {")
//        writer.newLine()
//
//        val nested = writer.nested()
//
//        nested.write("override val coroutineContext: CoroutineContext = $CLIENT_PROPERTY_NAME.provideStubContext($ID_PROPERTY_NAME)")
//        nested.newLine()
//        nested.newLine()
//
//        nested.write("private val $SCOPE_PROPERTY_NAME: CoroutineScope = this")
//        nested.newLine()
//        nested.newLine()
//
//        service.fields.forEach {
//            it.toCode(service.fullName, nested)
//        }
//
//        service.functions.forEach {
//            it.toCode(service.fullName, nested)
//        }
//        generateProviders(writer.nested(), service)
//        writer.write("}")

        generateStubClass(service, writer)

        writer.flush()
    }

    private fun generateStubClass(service: RPCServiceDeclaration, writer: CodeWriter) {
        writer.writeLine("@Deprecated(level = DeprecationLevel.HIDDEN, message = \"Internal kotlinx.rpc API, do not use!\")")
        writer.writeLine("class ${service.simpleName.withStubImplSuffix(useKSPSuffix = false)} {")

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

    private fun RPCServiceDeclaration.Function.toCode(service: RPCServiceDeclaration, writer: CodeWriter) {
        val returnTypeGenerated = if (returnType.isUnit()) ": Unit" else ": ${returnType.toCode()}"
        writer.writeLine("override suspend fun ${name}(${argumentTypes.joinToString { it.toCode() }})$returnTypeGenerated = scopedClientCall($SCOPE_PROPERTY_NAME) {")
        generateBody(service, writer.nested())
        writer.writeLine("}")
        writer.newLine()
    }

    private fun RPCServiceDeclaration.Function.Argument.toCode(): String {
        val prefix = if (isVararg) "vararg " else ""
        return "$prefix$name: ${type.toCode()}"
    }

    private fun RPCServiceDeclaration.FlowField.toCode(serviceType: String, writer: CodeWriter) {
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

        val rpcFiled = "RPCField(\"$serviceType\", $ID_PROPERTY_NAME, \"$name\", typeOf<$codeType>())"

        val codeDeclaration = "final override val $name: $codeType $prefix $CLIENT_PROPERTY_NAME.$method($SCOPE_PROPERTY_NAME, $rpcFiled)$suffix"

        writer.writeLine(codeDeclaration)
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

    private fun KSType?.isUnit(): Boolean {
        return this == null || this.declaration.simpleName.getShortName() == "Unit"
    }

    private fun RPCServiceDeclaration.Function.generateBody(service: RPCServiceDeclaration, writer: CodeWriter) {
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

        val rpcCall =
            "RPCCall(\"${service.fullName}\", $ID_PROPERTY_NAME, \"$name\", RPCCall.Type.Method, $data, typeOf<${
                name.functionGeneratedClass()
            }>(), typeOf<${returnType.toCode()}>())"
        writer.writeLine("$CLIENT_PROPERTY_NAME.call($rpcCall)")
    }

    private fun RPCServiceDeclaration.Function.generateFunctionClass(writer: CodeWriter) {
        writer.writeLine("@Serializable")
        val classOrObject = if (argumentTypes.isEmpty()) "object" else "class"
        writer.writeLine("internal $classOrObject ${name.functionGeneratedClass()}${if (argumentTypes.isEmpty()) " : RPCMethodClassArguments {" else "("}")
        if (argumentTypes.isNotEmpty()) {
            with(writer.nested()) {
                argumentTypes.forEach { arg ->
                    val prefix = if (arg.isContextual) {
                        "@Contextual "
                    } else ""

                    val type = if (arg.isVararg) "List<${arg.type.toCode()}>" else arg.type.toCode()
                    writeLine("${prefix}val ${arg.name}: $type,")
                }
            }
            writer.writeLine(") : RPCMethodClassArguments {")
        }
        with(writer.nested()) {
            val array = if (argumentTypes.isEmpty()) "emptyArray()" else {
                "arrayOf(${argumentTypes.joinToString { it.name }})"
            }
            writeLine("override fun asArray(): Array<Any?> = $array")
        }
        writer.writeLine("}")
        writer.newLine()
    }

    private fun generateProviders(writer: CodeWriter, service: RPCServiceDeclaration) {
        writer.newLine()
        writer.writeLine("companion object : RPCStubObject<${service.fullName}> {")
        with(writer.nested()) {
            val mapFunction = if (service.functions.isEmpty()) "emptyMap()" else "mapOf("
            writeLine("private val methodNames: Map<String, kotlin.reflect.KType> = $mapFunction")
            if (service.functions.isNotEmpty()) {
                with(nested()) {
                    service.functions.forEach { function ->
                        writeLine("\"${function.name}\" to typeOf<${function.name.functionGeneratedClass()}>(),")
                    }
                }
                writeLine(")")
            }
            newLine()

            writeLine("override fun withClient(serviceId: Long, client: RPCClient): ${service.fullName} = ${service.simpleName.withStubImplSuffix()}(serviceId, client)")
            newLine()

            writeLine("override fun methodTypeOf(methodName: String): kotlin.reflect.KType? = methodNames[methodName]")
            newLine()

            writeLine("override fun rpcFields(service: ${service.fullName}): List<RPCDeferredField<*>> {")
            with(nested()) {
                if (service.fields.isEmpty()) {
                    writeLine("return emptyList()")
                } else {
                    writeLine("return listOf<Any?>(")
                    with(nested()) {
                        service.fields.forEach {
                            writeLine("service.${it.name},")
                        }
                    }
                    writeLine(") as List<RPCDeferredField<*>>")
                }
            }
            writeLine("}")
        }
        writer.writeLine("}")
    }
}

fun String.withStubImplSuffix(useKSPSuffix: Boolean = true) = "${this}Stub${if (useKSPSuffix) "_KSP" else ""}"

fun String.functionGeneratedClass() = "${replaceFirstChar { it.uppercaseChar() }}_RPCData"
