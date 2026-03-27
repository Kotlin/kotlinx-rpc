@file:OptIn(
    kotlinx.cinterop.ExperimentalForeignApi::class,
    kotlinx.rpc.grpc.internal.InternalNativeRpcApi::class,
)

import kotlinx.rpc.grpc.internal.cinterop.grpc_init

fun useGrpcShimWithOptIn() {
    grpc_init()
}
