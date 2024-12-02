/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.annotations

/**
 * Marks an annotation as a one that marks
 * a type argument as a one that requires its resolved type to be annotated this annotation.
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
