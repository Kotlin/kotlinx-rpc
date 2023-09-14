package org.jetbrains.krpc.codegen

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies

object RPCServiceMetaInfoGenerator {
    fun generate(codeGenerator: CodeGenerator, services: List<RPCServiceDeclaration>) {
        if (services.isEmpty()) {
            return
        }

        val codeWriter = codeGenerator.createNewFile(
            dependencies = Dependencies(aggregating = true, *services.map { it.file }.toTypedArray()),
            packageName = "org.jetbrains.krpc",
            fileName = "rpc",
            extensionName = "kt",
        ).bufferedWriter(Charsets.UTF_8).codeWriter()

        generate(codeWriter, services)

        codeWriter.flush()
    }

    private fun generate(writer: CodeWriter, services: List<RPCServiceDeclaration>) {
        writer.write("@file:Suppress(\"unused\")")
        writer.newLine()
        writer.newLine()

        writer.write("package org.jetbrains.krpc")
        writer.newLine()
        writer.newLine()

        writer.write("import kotlin.reflect.*")
        services.forEach {
            writer.write("import ${it.fullName}")
        }
        writer.newLine()
        writer.newLine()

        writer.write("private val RPC_SERVICES = mapOf(")
        writer.newLine()
        with(writer.nested()) {
            services.forEach { service ->
                write("typeOf<${service.fullName}>() to { engine: RPCEngine -> ${service.simpleName.withClientImplSuffix()}(engine) },")
                newLine()
            }
        }
        writer.write(")")
        writer.newLine()
        writer.newLine()

        writer.write("inline fun <reified T : RPC> rpcServiceOf(engine: RPCEngine): T {")
        writer.newLine()
        writer.nested().write("return rpcServiceOf(typeOf<T>(), engine)")
        writer.newLine()
        writer.write("}")

        writer.newLine()
        writer.newLine()

        writer.write("fun <T : RPC> rpcServiceOf(serviceType: KType, engine: RPCEngine): T {")
        writer.newLine()
        with(writer.nested()) {
            write("val serviceGetter = RPC_SERVICES[serviceType] ?: error(\"Failed to find service for \$serviceType\")")
            newLine()
            write("@Suppress(\"UNCHECKED_CAST\")")
            newLine()
            write("return serviceGetter(engine) as T")
            newLine()
        }
        writer.write("}")
        writer.newLine()
        writer.newLine()

        writer.write("private val SERVICE_METHODS = mapOf(")
        writer.newLine()
        with(writer.nested()) {
            services.forEach { service ->
                write("typeOf<${service.fullName}>() to mapOf(")
                newLine()
                service.functions.forEach function@{ function ->
                    with(nested()) {
                        write("${service.fullName}::${function.name}.name to typeOf<${service.simpleName.withClientImplSuffix()}.${function.name.functionGeneratedClass()}>(),")
                        newLine()
                    }
                }
                write("),")
                newLine()
            }
        }
        writer.write(")")
        writer.newLine()
        writer.newLine()

        writer.write("inline fun <reified T : RPC> serviceMethodOf(methodName: String): KType? {")
        writer.newLine()
        writer.nested().write("return serviceMethodOf(typeOf<T>(), methodName)")
        writer.newLine()
        writer.write("}")
        writer.newLine()
        writer.newLine()

        writer.write("fun serviceMethodOf(serviceType: KType, methodName: String) : KType? {")
        writer.newLine()
        with(writer.nested()) {
            write("val serviceMap = SERVICE_METHODS[serviceType] ?: error(\"Failed to find service for \$serviceType\")")
            newLine()
            write("return serviceMap[methodName]")
            newLine()
        }
        writer.write("}")
        writer.newLine()
        writer.newLine()
    }
}
