/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.annotations

import kotlinx.rpc.annotations.RpcMeta
import kotlinx.rpc.krpc.KrpcProtocol
import kotlinx.rpc.krpc.KrpcService
import kotlinx.rpc.krpc.descriptor.KrpcServiceDescriptor

// TODO: Adapt KDoc
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
 * val myService = rpcClient.service<MyService>()
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
 */
@Target(AnnotationTarget.CLASS)
//@Retention(AnnotationRetention.RUNTIME) // Runtime is the default retention, also see KT-41082
@RpcMeta(KrpcService::class, KrpcProtocol::class, KrpcServiceDescriptor::class)
public annotation class Krpc
