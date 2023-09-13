package org.jetbrains.krpc.codegen

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.symbol.KSType

class RPCClientServiceGenerator(
    private val codegen: CodeGenerator,
) {
    fun generate(service: RPCServiceDeclaration) {
        val writer = codegen.createNewFile(
            dependencies = Dependencies(aggregating = true, service.file),
            packageName = "org.jetbrains.krpc",
            fileName = service.simpleName.withClientImplSuffix(),
            extensionName = "kt",
        ).bufferedWriter(charset = Charsets.UTF_8).codeWriter()

        writer.write("@file:Suppress(\"RedundantUnitReturnType\", \"RemoveRedundantQualifierName\", \"USELESS_CAST\", \"UNCHECKED_CAST\", \"ClassName\", \"MemberVisibilityCanBePrivate\")")
        writer.newLine()
        writer.newLine()

        writer.write("package org.jetbrains.krpc")
        writer.newLine()
        writer.newLine()

        writer.write("import kotlinx.serialization.Serializable")
        writer.newLine()
        writer.write("import kotlin.reflect.typeOf")
        writer.newLine()
        writer.write("import ${service.fullName}")
        writer.newLine()
        writer.newLine()

        writer.write("class ${service.simpleName.withClientImplSuffix()}(private val engine: RPCEngine) : ${service.fullName} {")
        writer.newLine()
        val nested = writer.nested()
        service.functions.forEach {
            it.toCode(nested)
        }
        writer.write("}")

        writer.flush()
    }

    private fun RPCServiceDeclaration.Function.toCode(writer: CodeWriter) {
        generateFunctionClass(writer)

        val returnTypeGenerated = if (returnType.isUnit()) "" else ": ${returnType!!.toCode()}"
        writer.write("override suspend fun ${name}(${argumentTypes.joinToString { it.toCode() }})$returnTypeGenerated {")
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

    private fun KSType.toCode(): String {
        val qualifier = declaration.qualifiedName?.asString() ?: codegenError("Expected qualifier for KSType")

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

    private fun KSType.toCodeSimplified(): String {
        val qualifier = declaration.qualifiedName?.asString() ?: codegenError("Expected qualifier for KSType")

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
        writer.write("val returnType = typeOf<${returnType?.toCode() ?: "Unit"}>()")
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
        writer.write("${prefix}engine.call(RPCCallInfo(\"$name\", $data, typeOf<${name.functionGeneratedClass()}>(), returnType))")
        writer.newLine()

        if (!returnType.isUnit()) {
            writer.write("check(result is ${returnType!!.toCodeSimplified()})")
            writer.newLine()
            writer.write("return result as ${returnType.toCode()}")
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
                    val type = if (arg.isVararg) "List<${arg.type.toCode()}>" else arg.type.toCode()
                    write("val ${arg.name}: $type,")
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
}

fun String.withClientImplSuffix() = "${this}Client"
fun String.functionGeneratedClass() = "${replaceFirstChar { it.uppercaseChar() }}_RPCData"
