/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.internal

import kotlin.reflect.KClass

@InternalRPCApi
public expect fun <R> findRPCProviderInCompanion(kClass: KClass<*>): R
