/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc

/**
 * [RPCEagerField] annotation is used to define service field as eagerly initialized one.
 * By default, all service fields are initialized after lazily, after first access to them.
 * But if the field is marked with the [RPCEagerField] annotation - it will be initialized with the service creation.
 */
@Target(AnnotationTarget.PROPERTY)
annotation class RPCEagerField
