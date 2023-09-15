package org.jetbrains.krpc.codegen

object RPCMetadataGenerator {


    fun generate(
        writer: CodeWriter,
        moduleId: String,
        krpcDirectDependencies: List<String>,
        services: List<RPCServiceDeclaration>,
    ) {
        writer.write("@file:Suppress(\"unused\")")
        writer.newLine()
        writer.newLine()
        writer.write("package org.jetbrains.krpc")
        writer.newLine()
        writer.newLine()
        writer.write("import kotlin.reflect.*")
        writer.newLine()
        writer.newLine()
        writer.write("@Suppress(\"ClassName\")")
        writer.newLine()
        writer.write("object kRPCMetadata_$moduleId {")
        writer.newLine()
        with(writer.nested()) {
            rpcServiceOfGen(this, krpcDirectDependencies, services)

            newLine()
            newLine()

            serviceMethodOf(this, krpcDirectDependencies, services)
        }
        writer.newLine()
        writer.write("}")
        writer.newLine()
    }

    private fun rpcServiceOfGen(
        writer: CodeWriter,
        krpcDirectDependencies: List<String>,
        services: List<RPCServiceDeclaration>,
    ) {
        with(writer) {
            write("internal inline fun <reified T : RPC> rpcServiceOf(engine: RPCEngine): T {")
            newLine()
            with(nested()) {
                write("return rpcServiceOf(typeOf<T>(), engine)")
            }
            newLine()
            write("}")
            newLine()
            newLine()

            write("internal fun <T : RPC> rpcServiceOf(serviceType: KType, engine: RPCEngine): T {")
            newLine()
            with(nested()) {
                write("return rpcServiceOfOrNull(serviceType, engine) ?: error(\"kRPC internal error: Expected service of type \$serviceType to be present\")")
            }
            newLine()
            write("}")
            newLine()
            newLine()

            write("@Suppress(\"UNCHECKED_CAST\")")
            newLine()
            write("fun <T : RPC> rpcServiceOfOrNull(serviceType: KType, engine: RPCEngine): T? {")
            newLine()
            with(nested()) {
                write("return when (serviceType) {")
                newLine()
                with(nested()) {
                    services.forEach {
                        write("typeOf<${it.fullName}>() -> ${it.simpleName}Client(engine) as T")
                        newLine()
                    }
                    val deps = krpcDirectDependencies.joinToString(" ?: ") { dependencyModuleId ->
                        "kRPCMetadata_$dependencyModuleId.rpcServiceOfOrNull(serviceType, engine)"
                    }

                    val elseBranch = if (deps.isBlank()) {
                        "null"
                    } else "$deps ?: null"

                    write("else -> $elseBranch")
                }
                newLine()
                write("}")
            }
            newLine()
            write("}")
        }
    }

    private fun serviceMethodOf(
        writer: CodeWriter,
        krpcDirectDependencies: List<String>,
        services: List<RPCServiceDeclaration>,
    ) {
        with(writer) {
            write("internal inline fun <reified T : RPC> serviceMethodOf(methodName: String): KType? {")
            newLine()
            with(nested()) {
                write("return serviceMethodOf(typeOf<T>(), methodName)")
            }
            newLine()
            write("}")
            newLine()
            newLine()

            write("internal fun serviceMethodOf(serviceType: KType, methodName: String): KType? {")
            newLine()
            with(nested()) {
                write("return serviceMethodOfOrNull(serviceType, methodName)")
            }
            newLine()
            write("}")
            newLine()
            newLine()

            write("fun serviceMethodOfOrNull(serviceType: KType, methodName: String): KType? {")
            newLine()
            with(nested()) {
                write("return when (serviceType) {")
                newLine()
                with(nested()) {
                    services.forEach {
                        write("typeOf<${it.fullName}>() -> ${it.simpleName}Client.methodType(methodName)")
                        newLine()
                    }
                    val deps = krpcDirectDependencies.joinToString(" ?: ") { dependencyModuleId ->
                        "kRPCMetadata_$dependencyModuleId.serviceMethodOfOrNull(serviceType, methodName)"
                    }

                    val elseBranch = if (deps.isBlank()) {
                        "null"
                    } else "$deps ?: null"

                    write("else -> $elseBranch")
                }
                newLine()
                write("}")
            }
            newLine()
            write("}")
        }
    }
}
