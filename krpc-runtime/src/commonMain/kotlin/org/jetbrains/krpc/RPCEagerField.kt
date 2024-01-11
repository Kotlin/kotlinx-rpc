/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc

/**
 * The field marked with this annotation will be initialized with the service creation.
 */
@Target(AnnotationTarget.PROPERTY)
annotation class RPCEagerField
