package org.jetbrains.krpc

import kotlin.reflect.KType

data class RPCCall(
    val serviceTypeString: String,
    val callableName: String,
    val type: Type,
    val data: Any,
    val dataType: KType,
    val returnType: KType,
) {
    enum class Type {
        Method, Field;
    }
}

data class RPCField(
    val serviceTypeString: String,
    val name: String,
    val type: KType
)
