/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.annotations

import kotlinx.rpc.RemoteService

/**
 * Every [Rpc] annotated interface will have a code generation process run on it,
 * making the interface effectively usable for RPC calls.
 *
 * Every [Rpc] annotated interface MAY inherit from the [RemoteService] interface.
 * If it is not done explicitly, the supertype will be added during the compilation process.
 * In that case an IDE will highlight false-positive type mismatch errors,
 * so it is recommended to add the [RemoteService] parent explicitly, until proper IDE support is provided.
 *
 * Example usage:
 * ```kotlin
 * // common code
 * @Rpc
 * interface MyService : RemoteService {
 *    suspend fun sayHello(firstName: String, lastName: String, age: Int): String
 * }
 * // client code
 * val rpcClient: RpcClient
 * val myService = rpcClient.withService<MyService>()
 * val greetingFromServer = myService.sayHello("Alex", "Smith", 35)
 * // server code
 * class MyServiceImpl(override val coroutineContext: CoroutineContext) : MyService {
 *     override suspend fun sayHello(firstName: String, lastName: String, age: Int): String {
 *         return "Hello, $firstName $lastName, of age $age. I am your server!"
 *     }
 * }
 * val server: RPCServer
 * server.registerService<MyService> { ctx -> MyServiceImpl(ctx) }
 * ```
 *
 * @see [RemoteService]
 */
@Target(AnnotationTarget.CLASS)
//@Retention(AnnotationRetention.RUNTIME) // Runtime is the default retention, also see KT-41082
public annotation class Rpc
