/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.annotations

import kotlinx.rpc.annotations.Rpc

/**
 * Marks an interface as a gRPC service definition.
 *
 * The annotation supports customizing the gRPC service name and package used in the protocol.
 * By default, the service name is derived from the interface name and the package from the Kotlin package.
 *
 * Example:
 * ```kotlin
 * @Grpc(protoPackage = "com.example.api", protoServiceName = "RpcUserService")
 * interface UserService {
 *     suspend fun getUser(request: GetUserRequest): User
 *
 *     @Grpc.Method(name = "CustomName", safe = true, idempotent = true)
 *     suspend fun searchUsers(query: SearchQuery): SearchResults
 * }
 * ```
 *
 * @property protoPackage The Protocol Buffers package name for this service. If empty (default),
 *   the Kotlin package name is used. This affects the fully qualified service name in gRPC
 *   (e.g., "com.example.api.RpcUserService").
 * @property protoServiceName The Protocol Buffers service name. If empty (default), the interface
 *   name is used. Useful when the service name in the proto definition differs from the Kotlin
 *   interface name, such as when multiple client/server interfaces map to the same proto service.
 *
 * @see Grpc.Method
 * @see kotlinx.rpc.annotations.Rpc
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.TYPE_PARAMETER)
@Rpc
public annotation class Grpc(
    val protoPackage: String = "",
    val protoServiceName: String = ""
) {
    /**
     * Configures gRPC-specific metadata for a service method.
     *
     * This annotation allows fine-grained control over method behavior and characteristics
     * that affect how the gRPC method is handled by servers and clients.
     *
     * @property name Custom name for the gRPC method. If empty (default), the Kotlin function name is used.
     * @property safe Indicates whether the method is safe (has no side effects). Safe methods can be
     *   cached or retried without concern. Default is `false`.
     * @property idempotent Indicates whether the method is idempotent (multiple identical requests have
     *   the same effect as a single request). Idempotent methods can be safely retried. Default is `false`.
     * @property sampledToLocalTracing Controls whether RPCs for this method may be sampled into the local
     *   tracing store.
     *
     * @see Grpc
     */
    @Target(AnnotationTarget.FUNCTION)
    public annotation class Method(
        val name: String = "",
        val safe: Boolean = false,
        val idempotent: Boolean = false,
        val sampledToLocalTracing: Boolean = true,
    )
}
