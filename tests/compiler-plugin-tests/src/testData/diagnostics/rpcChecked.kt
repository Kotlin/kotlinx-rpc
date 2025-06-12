/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalRpcApi::class)

import kotlin.coroutines.*
import kotlinx.coroutines.*
import kotlinx.rpc.annotations.Rpc
import kotlinx.rpc.withService
import kotlinx.rpc.RpcClient
import kotlinx.rpc.RpcServer
import kotlinx.rpc.registerService
import kotlinx.rpc.descriptor.serviceDescriptorOf
import kotlinx.rpc.internal.utils.ExperimentalRpcApi

@Rpc
interface MyService

class NotAService

class MyServiceImpl : MyService

inline suspend fun <@Rpc reified T : Any> ok(client: RpcClient, server: RpcServer, impl: T, myServiceImpl: MyService) {
    client.withService<MyService>()
    client.withService<T>()

    server.registerService<MyService> { MyServiceImpl() }
    server.registerService<T> { impl }

    serviceDescriptorOf<MyService>()
    serviceDescriptorOf<T>()
}

inline suspend fun <reified T : Any> fail(client: RpcClient, server: RpcServer, impl: T, myServiceImpl: MyServiceImpl, notAServiceImpl: NotAService) {
    client.withService<<!CHECKED_ANNOTATION_VIOLATION!>MyServiceImpl<!>>()
    client.withService<<!CHECKED_ANNOTATION_VIOLATION!>NotAService<!>>()
    client.withService<<!CHECKED_ANNOTATION_VIOLATION!>T<!>>()

    server.registerService<<!CHECKED_ANNOTATION_VIOLATION!>MyServiceImpl<!>> { MyServiceImpl() }
    server.registerService<<!CHECKED_ANNOTATION_VIOLATION!>NotAService<!>> { NotAService() }
    server.registerService<<!CHECKED_ANNOTATION_VIOLATION!>T<!>> { impl }

    serviceDescriptorOf<<!CHECKED_ANNOTATION_VIOLATION!>MyServiceImpl<!>>()
    serviceDescriptorOf<<!CHECKED_ANNOTATION_VIOLATION!>NotAService<!>>()
    serviceDescriptorOf<<!CHECKED_ANNOTATION_VIOLATION!>T<!>>()
}

@Rpc
annotation class Grpc

@Grpc
interface MyGrpcService

<!WRONG_RPC_ANNOTATION_TARGET!>@Grpc<!>
class WrongGrpcTarget

<!WRONG_RPC_ANNOTATION_TARGET!>@Rpc<!>
class WrongRpcTarget
