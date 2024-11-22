/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.internal

import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.serialization.Serializable

/**
 * Used for field initialization call
 */
@Serializable
@InternalRpcApi
public object FieldDataObject
