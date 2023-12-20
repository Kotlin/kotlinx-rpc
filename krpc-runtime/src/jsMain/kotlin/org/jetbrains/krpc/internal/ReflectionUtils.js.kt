@file:Suppress("detekt.MatchingDeclarationName")

package org.jetbrains.krpc.internal

import kotlin.reflect.KClass

@InternalKRPCApi
actual val KClass<*>.qualifiedClassNameOrNull: String?
    get() = toString()
