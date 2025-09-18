/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package kotlinx.rpc.grpc.server

import kotlinx.rpc.grpc.internal.ServiceDescriptor
import kotlinx.rpc.internal.utils.InternalRpcApi

/**
 * Definition of a service to be exposed via a Server.
 */
public expect class ServerServiceDefinition {
    public fun getServiceDescriptor(): ServiceDescriptor
    public fun getMethods(): Collection<kotlinx.rpc.grpc.server.internal.ServerMethodDefinition<*, *>>

    public fun getMethod(methodName: String): kotlinx.rpc.grpc.server.internal.ServerMethodDefinition<*, *>?
}

@InternalRpcApi
public expect fun serverServiceDefinition(
    serviceDescriptor: ServiceDescriptor,
    methods: Collection<kotlinx.rpc.grpc.server.internal.ServerMethodDefinition<*, *>>,
): ServerServiceDefinition
