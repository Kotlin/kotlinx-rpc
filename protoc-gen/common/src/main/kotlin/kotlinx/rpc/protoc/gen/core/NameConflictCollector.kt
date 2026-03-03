/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protoc.gen.core

/**
 * Represents a name conflict between a proto-supplied name and a public generated name.
 */
data class NameConflictError(
    val protoFilePath: String,
    val messageName: String,
    val conflictingProtoName: String,
    val conflictingGeneratedName: String,
    val description: String,
)

/**
 * Collects name conflict errors during generation.
 * When a proto-supplied name clashes with a public generated name (e.g., `Builder`, `Companion`),
 * the error is collected and reported back to the user via the protoc plugin API.
 */
class NameConflictCollector {
    private val errors = mutableListOf<NameConflictError>()

    fun addError(error: NameConflictError) {
        errors.add(error)
    }

    fun hasErrors(): Boolean = errors.isNotEmpty()

    fun formatErrors(): String = errors.joinToString("\n") { error ->
        "${error.protoFilePath}: ${error.messageName}: " +
            "Proto name '${error.conflictingProtoName}' conflicts with " +
            "generated name '${error.conflictingGeneratedName}'. ${error.description}"
    }
}
