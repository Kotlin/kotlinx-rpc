@file:OptIn(
    kotlinx.cinterop.ExperimentalForeignApi::class,
    kotlinx.rpc.grpc.nativedeps.InternalNativeRpcApi::class,
)

import grpcCoreInterop.grpc_init

fun useGrpcShimWithOptIn() {
    grpc_init()
}
