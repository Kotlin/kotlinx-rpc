/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc

/**
 * When applied to an RPC service field - the field will be initialized eagerly rather then lazily
 */
@Target(AnnotationTarget.PROPERTY)
annotation class RPCEagerField
