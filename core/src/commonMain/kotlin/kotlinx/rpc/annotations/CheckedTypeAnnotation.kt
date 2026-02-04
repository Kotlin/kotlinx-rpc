/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.annotations

import kotlin.reflect.KClass

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
 * ```
 *
 * @property checkFor
 */
@Target(AnnotationTarget.ANNOTATION_CLASS)
public annotation class CheckedTypeAnnotation(
    /**
     * By default, checked type annotations (applied via [CheckedTypeAnnotation])
     * validate the presence of the annotation on the type itself.
     *
     * In some cases, however, we need to validate a *different* annotation on the type.
     * This is necessary when that annotation requires parameters and therefore
     * cannot be used directly in function declarations.
     * Example:
     * ```
     * @WithCodec(MessageCodec::class)
     * class Message
     *
     * fun <reified @HasWithCodec T : Any> codec(): T { ... }
     * ```
     */
    val checkFor: KClass<*> = CheckedTypeAnnotation::class
)