/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.annotations

/**
 * Every [Rpc] annotated interface will have a code generation process run on it,
 * making the interface effectively usable for RPC calls.
 *
 * Example usage.
 *
 * Define an interface and mark it with `@Rpc`.
 * Compiler plugin will ensure that all declarations inside the interface are valid.
 * ```kotlin
 * @Rpc
 * interface MyService {
 *    suspend fun sayHello(firstName: String, lastName: String, age: Int): String
 * }
 * ```
 * On the client side use [kotlinx.rpc.RpcClient] to get a generated instance of the service, and use it to make calls:
 * ```kotlin
 * val rpcClient: RpcClient
 * val myService = rpcClient.withService<MyService>()
 * val greetingFromServer = myService.sayHello("Alex", "Smith", 35)
 * ```
 * On the server side, define an implementation of this interface and register it on an [kotlinx.rpc.RpcServer]:
 * ```kotlin
 * class MyServiceImpl : MyService {
 *     override suspend fun sayHello(firstName: String, lastName: String, age: Int): String {
 *         return "Hello, $firstName $lastName, of age $age. I am your server!"
 *     }
 * }
 * val server: RpcServer
 * server.registerService<MyService> { MyServiceImpl() }
 * ```
 *
 * @see kotlinx.rpc.RpcClient
 * @see kotlinx.rpc.RpcServer
 * @see CheckedTypeAnnotation
 * @see kotlinx.rpc.withService
 */
@CheckedTypeAnnotation
@Target(AnnotationTarget.CLASS, AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.TYPE_PARAMETER)
//@Retention(AnnotationRetention.RUNTIME) // Runtime is the default retention, also see KT-41082
public annotation class Rpc
