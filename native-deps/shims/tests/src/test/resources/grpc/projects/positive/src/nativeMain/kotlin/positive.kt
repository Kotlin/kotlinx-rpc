@file:OptIn(
    kotlinx.cinterop.ExperimentalForeignApi::class,
    kotlinx.rpc.grpc.internal.shim.InternalNativeRpcApi::class,
)

import kotlinx.rpc.grpc.internal.cinterop.grpc_init

fun useGrpcShimWithOptIn() {
    grpc_init()
}
