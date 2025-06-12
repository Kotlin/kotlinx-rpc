/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.annotations

/**
 * Meta annotation.
 * Used to perform [annotation type-safety](https://kotlin.github.io/kotlinx-rpc/annotation-type-safety.html) checks.
 *
 * When an annotation class (for example, `@X`) is marked with `@CheckedTypeAnnotation` -
 * Any other type can be marked with `@X` to perform safety checks on type parameters that are also marked with `@X`.
 *
 * Example:
 * ```kotlin
 * @CheckedTypeAnnotation
 * annotation class Rpc
 *
 * @Rpc
 * interface MyService
 *
 * interface NotService
 *
 * fun <@Rpc T> acceptRpcService() { ... }
 *
 * acceptRpcService<MyService>() // OK
 * acceptRpcService<NotService>() // Error
 *
 * fun <@Rpc T> nested() {
 *     acceptRpcService<T>() // OK
 * }
 * ```
 *
 * Also works for nested annotations:
 * ```kotlin
 * @Rpc
 * annotation class Grpc
 *
 * @Grpc
 * interface MyGrpcService
 *
 * acceptRpcService<MyGrpcService>() // OK
 */
@Target(AnnotationTarget.ANNOTATION_CLASS)
public annotation class CheckedTypeAnnotation
