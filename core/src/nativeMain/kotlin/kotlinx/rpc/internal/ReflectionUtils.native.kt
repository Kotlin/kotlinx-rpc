/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("detekt.MatchingDeclarationName")

package kotlinx.rpc.internal

import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlin.reflect.KClass

@InternalRpcApi
public actual val KClass<*>.qualifiedClassNameOrNull: String?
    get() = qualifiedName

@InternalRpcApi
public actual val KClass<*>.typeName: String?
    get() = qualifiedClassNameOrNull
