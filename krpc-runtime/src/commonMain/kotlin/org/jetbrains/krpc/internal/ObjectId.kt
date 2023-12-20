package org.jetbrains.krpc.internal

private const val HEX_RADIX = 16

@InternalKRPCApi
fun Any.objectId(vararg tags: String): String {
    val tagsString = tags.takeIf { it.isNotEmpty() }?.joinToString { "[$it]" } ?: ""
    return "${this::class.simpleName}$tagsString[${hashCode().toString(HEX_RADIX)}]"
}
