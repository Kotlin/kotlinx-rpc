/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.annotations

/**
 * Every [Rpc] annotated interface will have a code generation process run on it,
 * making the interface effectively usable for RPC calls.
 *
 * Example usage:
 * ```kotlin
 * // common code
 * @Rpc
 * interface MyService {
 *    suspend fun sayHello(firstName: String, lastName: String, age: Int): String
 * }
 * // client code
 * val rpcClient: RpcClient
 * val myService = rpcClient.withService<MyService>()
 * val greetingFromServer = myService.sayHello("Alex", "Smith", 35)
 * // server code
 * class MyServiceImpl : MyService {
 *     override suspend fun sayHello(firstName: String, lastName: String, age: Int): String {
 *         return "Hello, $firstName $lastName, of age $age. I am your server!"
 *     }
 * }
 * val server: RpcServer
 * server.registerService<MyService> { MyServiceImpl() }
 * ```
 */
@CheckedTypeAnnotation
@Target(AnnotationTarget.CLASS, AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.TYPE_PARAMETER)
//@Retention(AnnotationRetention.RUNTIME) // Runtime is the default retention, also see KT-41082
public annotation class Rpc
