/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf

import kotlinx.rpc.protobuf.CodeGenerator.DeclarationType
import kotlinx.rpc.protobuf.model.*
import org.slf4j.Logger

private const val RPC_INTERNAL_PACKAGE_SUFFIX = "_rpc_internal"

class ModelToKotlinGenerator(
    private val model: Model,
    private val logger: Logger,
    private val codeGenerationParameters: CodeGenerationParameters,
) {
    fun generateKotlinFiles(): List<FileGenerator> {
        return model.files.flatMap { it.generateKotlinFiles() }
    }

    private fun FileDeclaration.generateKotlinFiles(): List<FileGenerator> {
        additionalPublicImports.clear()
        additionalInternalImports.clear()

        return listOf(
            generatePublicKotlinFile(),
            generateInternalKotlinFile(),
        )
    }

    private var currentPackage: FqName = FqName.Package.Root

    private fun FileDeclaration.generatePublicKotlinFile(): FileGenerator {
        currentPackage = packageName

        return file(codeGenerationParameters, logger = logger) {
            filename = this@generatePublicKotlinFile.name
            packageName = this@generatePublicKotlinFile.packageName.safeFullName()
            packagePath = this@generatePublicKotlinFile.packageName.safeFullName()

            dependencies.forEach { dependency ->
                importPackage(dependency.packageName.safeFullName())
            }

            fileOptIns = listOf("ExperimentalRpcApi::class", "InternalRpcApi::class")

            generatePublicDeclaredEntities(this@generatePublicKotlinFile)

            import("kotlinx.rpc.internal.utils.*")

            additionalPublicImports.forEach {
                import(it)
            }
        }
    }

    private fun FileDeclaration.generateInternalKotlinFile(): FileGenerator {
        currentPackage = packageName

        return file(codeGenerationParameters, logger = logger) {
            filename = this@generateInternalKotlinFile.name
            packageName = this@generateInternalKotlinFile.packageName.safeFullName()
            packagePath =
                this@generateInternalKotlinFile.packageName.safeFullName()
                    .packageNameSuffixed(RPC_INTERNAL_PACKAGE_SUFFIX)

            fileOptIns = listOf("ExperimentalRpcApi::class", "InternalRpcApi::class")

            dependencies.forEach { dependency ->
                importPackage(dependency.packageName.safeFullName())
            }

            generateInternalDeclaredEntities(this@generateInternalKotlinFile)

            import("kotlinx.rpc.internal.utils.*")

            additionalInternalImports.forEach {
                import(it)
            }
        }
    }

    private val additionalPublicImports = mutableSetOf<String>()
    private val additionalInternalImports = mutableSetOf<String>()

    private fun CodeGenerator.generatePublicDeclaredEntities(fileDeclaration: FileDeclaration) {
        fileDeclaration.messageDeclarations.forEach { generatePublicMessage(it) }
        fileDeclaration.enumDeclarations.forEach { generatePublicEnum(it) }
        fileDeclaration.serviceDeclarations.forEach { generatePublicService(it) }
    }

    private fun CodeGenerator.generateInternalDeclaredEntities(fileDeclaration: FileDeclaration) {
        fileDeclaration.messageDeclarations.forEach { generateInternalMessage(it) }
        fileDeclaration.serviceDeclarations.forEach { generateInternalService(it) }

        fileDeclaration.messageDeclarations.forEach {
            generateToAndFromPlatformCastsRec(it)
        }

        fileDeclaration.enumDeclarations.forEach {
            generateToAndFromPlatformCastsEnum(it)
        }
    }

    private fun CodeGenerator.generateToAndFromPlatformCastsRec(declaration: MessageDeclaration) {
        generateToAndFromPlatformCasts(declaration)

        declaration.nestedDeclarations.forEach { nested ->
            generateToAndFromPlatformCastsRec(nested)
        }

        declaration.enumDeclarations.forEach { nested ->
            generateToAndFromPlatformCastsEnum(nested)
        }
    }

    private fun MessageDeclaration.fields() = actualFields.map {
        it.transformToFieldDeclaration() to it
    }

    @Suppress("detekt.CyclomaticComplexMethod")
    private fun CodeGenerator.generatePublicMessage(declaration: MessageDeclaration) {
        clazz(
            name = declaration.name.simpleName,
            declarationType = DeclarationType.Interface,
        ) {
            declaration.fields().forEach { (fieldDeclaration, _) ->
                code("val $fieldDeclaration")
                newLine()
            }

            newLine()

            // KRPC-147 OneOf Types
//            declaration.oneOfDeclarations.forEach { oneOf ->
//                generateOneOf(oneOf)
//            }
//
            declaration.nestedDeclarations.forEach { nested ->
                generatePublicMessage(nested)
            }

            declaration.enumDeclarations.forEach { enum ->
                generatePublicEnum(enum)
            }

            clazz("", modifiers = "companion", declarationType = DeclarationType.Object)
        }
    }

    @Suppress("detekt.CyclomaticComplexMethod")
    private fun CodeGenerator.generateInternalMessage(declaration: MessageDeclaration) {
        clazz(
            name = "${declaration.name.simpleName}Builder",
            declarationType = DeclarationType.Class,
            superTypes = listOf(declaration.name.safeFullName()),
        ) {
            declaration.fields().forEach { (fieldDeclaration, field) ->
                val value = when {
                    field.type is FieldType.Reference && field.nullable -> {
                        "= null"
                    }

                    field.type is FieldType.Reference -> {
                        additionalInternalImports.add("kotlin.properties.Delegates")
                        "by Delegates.notNull()"
                    }

                    else -> {
                        "= ${field.type.defaultValue}"
                    }
                }

                code("override var $fieldDeclaration $value")
                newLine()
            }

            declaration.nestedDeclarations.forEach { nested ->
                generateInternalMessage(nested)
            }
        }
    }

    private fun CodeGenerator.generateToAndFromPlatformCasts(declaration: MessageDeclaration) {
        function(
            name = "invoke",
            modifiers = "operator",
            args = "body: ${declaration.name.safeFullName("Builder")}.() -> Unit",
            contextReceiver = "${declaration.name.safeFullName()}.Companion",
            returnType = declaration.name.safeFullName(),
        ) {
            code("return ${declaration.name.safeFullName("Builder")}().apply(body)")
        }

        val platformType = "${declaration.outerClassName.safeFullName()}.${declaration.name.fullNestedName()}"
        function(
            name = "toPlatform",
            contextReceiver = declaration.name.safeFullName(),
            returnType = platformType,
        ) {
            scope("return $platformType.newBuilder().apply", ".build()") {
                declaration.actualFields.forEach { field ->
                    val uppercaseName = field.name.replaceFirstChar { ch -> ch.uppercase() }
                    val setFieldCall = when (field.type) {
                        is FieldType.List -> "addAll$uppercaseName"
                        else -> "set$uppercaseName"
                    }

                    if (field.nullable) {
                        code("this@toPlatform.${field.name}?.let { $setFieldCall(it${field.type.toPlatformCast()}) }")
                    } else {
                        code("$setFieldCall(this@toPlatform.${field.name}${field.type.toPlatformCast()})")
                    }
                }
            }
        }

        function(
            name = "toKotlin",
            contextReceiver = platformType,
            returnType = declaration.name.safeFullName(),
        ) {
            scope("return ${declaration.name.safeFullName()}") {
                declaration.actualFields.forEach { field ->
                    val javaName = when (field.type) {
                        is FieldType.List -> "${field.name}List"
                        else -> field.name
                    }

                    val getter = "this@toKotlin.$javaName${field.type.toKotlinCast()}"
                    if (field.nullable) {
                        ifBranch(
                            prefix = "${field.name} = ",
                            condition = "has${field.name.replaceFirstChar { ch -> ch.uppercase() }}()",
                            ifBlock = {
                                code(getter)
                            },
                            elseBlock = {
                                code("null")
                            }
                        )
                    } else {
                        code("${field.name} = $getter")
                    }
                }
            }
        }
    }

    private fun FieldType.toPlatformCast(): String {
        return when (this) {
            FieldType.IntegralType.FIXED32 -> ".toInt()"
            FieldType.IntegralType.FIXED64 -> ".toLong()"
            FieldType.IntegralType.UINT32 -> ".toInt()"
            FieldType.IntegralType.UINT64 -> ".toLong()"
            FieldType.IntegralType.BYTES -> ".let { bytes -> com.google.protobuf.ByteString.copyFrom(bytes) }"
            is FieldType.Reference -> ".toPlatform()".also {
                val fq by value
                importRootDeclarationIfNeeded(fq, "toPlatform", true)
            }
            is FieldType.List -> {
                when (value) {
                    is FieldType.Reference -> ".map { it.toPlatform() }".also {
                        val fq by value.value
                        importRootDeclarationIfNeeded(fq, "toPlatform", true)
                    }
                    is FieldType.IntegralType -> ""
                    else -> error("Unsupported type: $value")
                }
            }

            else -> ""
        }
    }

    private fun FieldType.toKotlinCast(): String {
        return when (this) {
            FieldType.IntegralType.FIXED32 -> ".toUInt()"
            FieldType.IntegralType.FIXED64 -> ".toULong()"
            FieldType.IntegralType.UINT32 -> ".toUInt()"
            FieldType.IntegralType.UINT64 -> ".toULong()"
            FieldType.IntegralType.BYTES -> ".toByteArray()"
            is FieldType.Reference -> ".toKotlin()".also {
                val fq by value
                importRootDeclarationIfNeeded(fq, "toKotlin", true)
            }
            is FieldType.List -> {
                when (value) {
                    is FieldType.Reference -> ".map { it.toKotlin() }".also {
                        val fq by value.value
                        importRootDeclarationIfNeeded(fq, "toKotlin", true)
                    }
                    is FieldType.IntegralType -> ".toList()"
                    else -> error("Unsupported type: $value")
                }
            }

            else -> ""
        }
    }

    private fun FieldDeclaration.transformToFieldDeclaration(): String {
        return "${name}: ${typeFqName()}"
    }

    private fun FieldDeclaration.typeFqName(): String {
        return when (type) {
            // KRPC-156 Reference Types
            is FieldType.Reference -> {
                val value by type.value
                value.safeFullName()
            }

            is FieldType.IntegralType -> {
                type.fqName.simpleName
            }

            is FieldType.List -> {
                val fqValue = when (val value = type.value) {
                    is FieldType.Reference -> value.value.value
                    is FieldType.IntegralType -> value.fqName
                    else -> error("Unsupported type: $value")
                }

                "List<${fqValue.safeFullName()}>"
            }

            // KRPC-145 Map Types
//            is FieldType.Map -> {
//                "Map<${type.keyName.simpleName}, ${type.valueName.simpleName}>"
//            }
            else -> {
                error("Unsupported type: $this")
            }
        }.withNullability(nullable)
    }

    private fun String.withNullability(nullable: Boolean): String {
        return "$this${if (nullable) "?" else ""}"
    }

    @Suppress("unused")
    private fun CodeGenerator.generateOneOf(declaration: OneOfDeclaration) {
        val interfaceName = declaration.name.simpleName

        clazz(declaration.name.simpleName, "sealed", declarationType = DeclarationType.Interface) {
            declaration.variants.forEach { variant ->
                clazz(
                    name = variant.name,
                    modifiers = "value",
                    constructorArgs = listOf("val value: ${variant.typeFqName()}"),
                    annotations = listOf("@JvmInline"),
                    superTypes = listOf(interfaceName),
                )

                additionalPublicImports.add("kotlin.jvm.JvmInline")
            }
        }
    }

    private fun CodeGenerator.generatePublicEnum(declaration: EnumDeclaration) {
        clazz(declaration.name.simpleName, modifiers = "enum") {
            declaration.originalEntries.forEach { entry ->
                code("${entry.name.simpleName},")
                newLine()
            }
            code(";")
            newLine()

            if (declaration.aliases.isNotEmpty()) {
                newLine()

                clazz("", modifiers = "companion", declarationType = DeclarationType.Object) {
                    declaration.aliases.forEach { alias: EnumDeclaration.Alias ->
                        code(
                            "val ${alias.name.simpleName}: ${declaration.name.simpleName} " +
                                    "= ${alias.original.name.simpleName}"
                        )
                    }
                }
            }
        }
    }

    @Suppress("unused")
    private fun CodeGenerator.generateToAndFromPlatformCastsEnum(declaration: EnumDeclaration) {
        val platformType = "${declaration.outerClassName.safeFullName()}.${declaration.name.fullNestedName()}"

        function(
            name = "toPlatform",
            contextReceiver = declaration.name.safeFullName(),
            returnType = platformType,
        ) {
            scope("return when (this)") {
                declaration.aliases.forEach { field ->
                    code("${declaration.name.fullNestedName()}.${field.name.simpleName} -> $platformType.${field.name.simpleName}")
                }

                declaration.originalEntries.forEach { field ->
                    code("${declaration.name.fullNestedName()}.${field.name.simpleName} -> $platformType.${field.name.simpleName}")
                }
            }
        }

        function(
            name = "toKotlin",
            contextReceiver = platformType,
            returnType = declaration.name.safeFullName(),
        ) {
            scope("return when (this)") {
                declaration.aliases.forEach { field ->
                    code("$platformType.${field.name.simpleName} -> ${declaration.name.fullNestedName()}.${field.name.simpleName}")
                }

                declaration.originalEntries.forEach { field ->
                    code("$platformType.${field.name.simpleName} -> ${declaration.name.fullNestedName()}.${field.name.simpleName}")
                }
            }
        }
    }

    @Suppress("detekt.LongMethod")
    private fun CodeGenerator.generatePublicService(service: ServiceDeclaration) {
        code("@kotlinx.rpc.grpc.annotations.Grpc")
        clazz(service.name.simpleName, declarationType = DeclarationType.Interface) {
            service.methods.forEach { method ->
                // no streaming for now
                val inputType by method.inputType
                val outputType by method.outputType
                function(
                    name = method.name,
                    modifiers = "suspend",
                    args = "message: ${inputType.name.safeFullName()}",
                    returnType = outputType.name.safeFullName(),
                )
            }
        }
    }

    private fun CodeGenerator.generateInternalService(service: ServiceDeclaration) {
        code("@Suppress(\"unused\", \"all\")")
        clazz(
            modifiers = "private",
            name = "${service.name.simpleName}Delegate",
            declarationType = DeclarationType.Object,
            superTypes = listOf("kotlinx.rpc.grpc.descriptor.GrpcDelegate<${service.name.safeFullName()}>"),
        ) {
            function(
                name = "clientProvider",
                modifiers = "override",
                args = "channel: kotlinx.rpc.grpc.ManagedChannel",
                returnType = "kotlinx.rpc.grpc.descriptor.GrpcClientDelegate",
            ) {
                code("return ${service.name.simpleName}ClientDelegate(channel)")
            }

            function(
                name = "definitionFor",
                modifiers = "override",
                args = "impl: ${service.name.safeFullName()}",
                returnType = "kotlinx.rpc.grpc.ServerServiceDefinition",
            ) {
                scope("return ${service.name.simpleName}ServerDelegate(impl).bindService()")
            }
        }

        code("@Suppress(\"unused\", \"all\")")
        clazz(
            modifiers = "private",
            name = "${service.name.simpleName}ServerDelegate",
            declarationType = DeclarationType.Class,
            superTypes = listOf("${service.name.simpleName}GrpcKt.${service.name.simpleName}CoroutineImplBase()"),
            constructorArgs = listOf("private val impl: ${service.name.safeFullName()}"),
        ) {
            service.methods.forEach { method ->
                val grpcName = method.name.replaceFirstChar { it.lowercase() }

                val inputType by method.inputType
                val outputType by method.outputType

                function(
                    name = grpcName,
                    modifiers = "override suspend",
                    args = "request: ${inputType.toPlatformMessageType()}",
                    returnType = outputType.toPlatformMessageType(),
                ) {
                    code("return impl.${method.name}(request.toKotlin()).toPlatform()")

                    importRootDeclarationIfNeeded(inputType.name, "toPlatform", true)
                    importRootDeclarationIfNeeded(outputType.name, "toKotlin", true)
                }
            }
        }

        code("@Suppress(\"unused\", \"all\")")
        clazz(
            modifiers = "private",
            name = "${service.name.simpleName}ClientDelegate",
            declarationType = DeclarationType.Class,
            superTypes = listOf("kotlinx.rpc.grpc.descriptor.GrpcClientDelegate"),
            constructorArgs = listOf("private val channel: kotlinx.rpc.grpc.ManagedChannel"),
        ) {
            val stubType = "${service.name.simpleName}GrpcKt.${service.name.simpleName}CoroutineStub"

            property(
                name = "stub",
                modifiers = "private",
                type = stubType,
                delegate = true,
                value = "lazy",
            ) {
                code("$stubType(channel.platformApi)")
            }

            function(
                name = "call",
                modifiers = "override suspend",
                args = "call: kotlinx.rpc.RpcCall",
                typeParameters = "R",
                returnType = "R",
            ) {
                code("val message = (call.data as kotlinx.rpc.internal.RpcMethodClass).asArray()[0]")
                code("@Suppress(\"UNCHECKED_CAST\")")
                scope("return when (call.callableName)") {
                    service.methods.forEach { method ->
                        val inputType by method.inputType
                        val outputType by method.outputType
                        val grpcName = method.name.replaceFirstChar { it.lowercase() }
                        val result = "stub.$grpcName((message as ${inputType.name.safeFullName()}).toPlatform())"
                        code("\"${method.name}\" -> $result.toKotlin() as R")

                        importRootDeclarationIfNeeded(inputType.name, "toPlatform", true)
                        importRootDeclarationIfNeeded(outputType.name, "toKotlin", true)
                    }

                    code("else -> error(\"Illegal call: \${call.callableName}\")")
                }
            }

            function(
                name = "callAsync",
                modifiers = "override",
                args = "call: kotlinx.rpc.RpcCall",
                typeParameters = "R",
                returnType = "kotlinx.coroutines.Deferred<R>",
            ) {
                code("error(\"Async calls are not supported\")")
            }
        }
    }

    private fun MessageDeclaration.toPlatformMessageType(): String {
        return "${outerClassName.safeFullName()}.${name.fullNestedName()}"
    }

    private fun FqName.safeFullName(classSuffix: String = ""): String {
        importRootDeclarationIfNeeded(this)

        return fullName(classSuffix)
    }

    private fun importRootDeclarationIfNeeded(
        declaration: FqName,
        nameToImport: String = declaration.simpleName,
        internalOnly: Boolean = false,
    ) {
        if (declaration.parent == FqName.Package.Root && currentPackage != FqName.Package.Root && nameToImport.isNotBlank()) {
            additionalInternalImports.add(nameToImport)
            if (!internalOnly) {
                additionalPublicImports.add(nameToImport)
            }
        }
    }
}

internal fun String.packageNameSuffixed(suffix: String): String {
    return if (isEmpty()) suffix else "$this.$suffix"
}
