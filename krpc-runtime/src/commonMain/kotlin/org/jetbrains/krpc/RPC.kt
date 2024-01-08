/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc

import kotlinx.coroutines.CoroutineScope

/**
 * Marker interface for an RPC service.
 * For each service that inherits this interface kRPC will generate an implementation to use it on the client side.
 *
 * [CoroutineScope] defines service lifetime.
 *
 * Example usage:
 * ```kotlin
 * // common code
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
 * class MyServiceImpl: MyService {
 *     override suspend fun sayHello(firstName: String, lastName: String, age: Int): String {
 *         return "Hello, $firstName $lastName, of age $age. I am your server!"
 *     }
 * }
 *
 * val server: RPCServer
 * server.registerService<MyService>(MyServiceImpl())
 * ```
 *
 * @see RPCClient
 * @see RPCServer
 */
public interface RPC : CoroutineScope
