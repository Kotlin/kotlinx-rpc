/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("detekt.MatchingDeclarationName")

package kotlinx.rpc.internal

import kotlin.reflect.KClass

@InternalKRPCApi
public actual val KClass<*>.qualifiedClassNameOrNull: String?
    get() = toString()
