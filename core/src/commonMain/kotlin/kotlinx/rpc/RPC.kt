/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc

import kotlinx.coroutines.CoroutineScope

/**
 * Marker interface for an RPC service.
 * For each service that inherits this interface library will generate an implementation to use it on the client side.
 *
 * [CoroutineScope] defines service lifetime.
 *
 * Example usage:
 * ```kotlin
 * // common code
 * @Rpc
 * interface MyService : RPC {
 *    suspend fun sayHello(firstName: String, lastName: String, age: Int): String
 * }
 *
 * // client code
 * val rpcClient: RPCClient
 * val myService = rpcClient.withService<MyService>()
 * val greetingFromServer = myService.sayHello("Alex", "Smith", 35)
 *
 * // server code
 * class MyServiceImpl(override val coroutineContext: CoroutineContext) : MyService {
 *     override suspend fun sayHello(firstName: String, lastName: String, age: Int): String {
 *         return "Hello, $firstName $lastName, of age $age. I am your server!"
 *     }
 * }
 *
 * val server: RPCServer
 * server.registerService<MyService> { ctx -> MyServiceImpl(ctx) }
 * ```
 *
 * Every [RPC] service MUST be annotated with [Rpc] annotation.
 *
 * @see RPCClient
 * @see RPCServer
 */
public interface RPC : CoroutineScope

/**
 * Every [Rpc] annotated interface will have a code generation process run on it,
 * making the interface effectively usable for RPC calls.
 *
 * Every [Rpc] annotated interface MAY inherit from the [RPC] interface.
 * If it is not done explicitly, the supertype will be added during the compilation process.
 * In that case an IDE will highlight false-positive type mismatch errors,
 * so it is recommended to add the [RPC] parent explicitly, until proper IDE support is provided.
 *
 * @see [RPC]
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
public annotation class Rpc
