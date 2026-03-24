@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

import libkgrpc.grpc_init

fun useGrpcShimWithoutOptIn() {
    grpc_init()
}
