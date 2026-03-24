@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

import grpcCoreInterop.grpc_init

fun useGrpcShimWithoutOptIn() {
    grpc_init()
}
