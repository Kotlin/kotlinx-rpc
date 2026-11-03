/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

// RUN_PIPELINE_TILL: FRONTEND

@file:OptIn(ExperimentalRpcApi::class)

// PROTOCOL_IMPORT
import kotlinx.rpc.descriptor.serviceDescriptorOf
import kotlinx.rpc.internal.utils.ExperimentalRpcApi

@Protocol
interface MyService

class NotAService

class MyServiceImpl : MyService

inline suspend fun <reified T : ProtocolService<T>> ok(client: ProtocolClient, server: ProtocolServer, impl: T) {
    client.service<MyService>()
    client.service<T>()

    server.registerService<MyService> { MyServiceImpl() }
    server.registerService<T> { impl }

    serviceDescriptorOf<MyService>()
    serviceDescriptorOf<T>()
}

inline suspend fun <reified T : Any> fail(client: ProtocolClient, server: ProtocolServer, impl: T) {
    client.service<<!UPPER_BOUND_VIOLATED!>MyServiceImpl<!>>()
    client.service<<!UPPER_BOUND_VIOLATED!>NotAService<!>>()
    client.service<<!UPPER_BOUND_VIOLATED!>T<!>>()

    server.registerService<<!UPPER_BOUND_VIOLATED!>MyServiceImpl<!>> <!ARGUMENT_TYPE_MISMATCH!>{ MyServiceImpl() }<!>
    server.registerService<<!UPPER_BOUND_VIOLATED!>NotAService<!>> <!ARGUMENT_TYPE_MISMATCH!>{ NotAService() }<!>
    server.registerService<<!UPPER_BOUND_VIOLATED!>T<!>> <!ARGUMENT_TYPE_MISMATCH!>{ impl }<!>

    serviceDescriptorOf<<!UPPER_BOUND_VIOLATED!>MyServiceImpl<!>>()
    serviceDescriptorOf<<!UPPER_BOUND_VIOLATED!>NotAService<!>>()
    serviceDescriptorOf<<!UPPER_BOUND_VIOLATED!>T<!>>()
}

<!WRONG_RPC_ANNOTATION_TARGET!>@Protocol<!>
class WrongTarget

/* GENERATED_FIR_TAGS: annotationUseSiteTargetFile, classDeclaration, classReference, functionDeclaration, inline,
interfaceDeclaration, lambdaLiteral, reified, suspend, typeConstraint, typeParameter */
