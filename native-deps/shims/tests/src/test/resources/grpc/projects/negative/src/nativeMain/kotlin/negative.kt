@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

import kotlinx.rpc.grpc.internal.cinterop.grpc_init

fun useGrpcShimWithoutOptIn() {
    grpc_init()
}
