/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc

import kotlin.reflect.KType

/**
 * Represents a method or field call of the RPC interface.
 * Contains all types and values information for the call, so it can be passed to a server.
 *
 * @property serviceTypeString The service type as a string.
 * @property serviceId The id of a service that is unique within [RPCClient] services
 * @property callableName The name of the callable. Can be the name of the method or field.
 * @property type The type of call;
 * @property data The data for the call.
 * It may be a generated class with all parameters and their values or empty class for fields.
 * @property dataType The [KType] of the [data].
 * @property returnType The [KType] of the return type.
 */
public data class RPCCall(
    val serviceTypeString: String,
    val serviceId: Long,
    val callableName: String,
    val type: Type,
    val data: Any,
    val dataType: KType,
    val returnType: KType,
) {
    public enum class Type {
        Method, Field;
    }
}

/**
 * Represents a field of the RPC service.
 * Can be internally converted to a [RPCCall], but it depends on a specific [RPCClient] implementation.
 *
 * @property serviceTypeString The service type as a string.
 * @property serviceId The id of a service that is unique within [RPCClient] services
 * @property name The name of the field.
 * @property type The [KType] of the field.
 */
public data class RPCField(
    val serviceTypeString: String,
    val serviceId: Long,
    val name: String,
    val type: KType
)
