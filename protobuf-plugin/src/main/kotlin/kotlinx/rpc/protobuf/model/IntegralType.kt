/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.model

enum class IntegralType(override val simpleName: String) : FqName {
    STRING("String"),
    BYTES("ByteArray"),
    BOOL("Boolean"),
    FLOAT("Float"),
    DOUBLE("Double"),
    INT32("Int"),
    INT64("Long"),
    UINT32("UInt"),
    UINT64("ULong"),
    FIXED32("UInt"),
    FIXED64("ULong"),
    SINT32("Int"),
    SINT64("Long"),
    SFIXED32("Int"),
    SFIXED64("Long");

    override val packageName: String = "kotlin"
    override val parentName: FqName? = null
}
