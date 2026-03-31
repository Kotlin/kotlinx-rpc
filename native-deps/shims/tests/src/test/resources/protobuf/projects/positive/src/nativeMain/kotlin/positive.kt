@file:OptIn(
    kotlinx.cinterop.ExperimentalForeignApi::class,
    kotlinx.rpc.protobuf.internal.shim.InternalNativeProtobufApi::class,
)

import kotlinx.rpc.protobuf.internal.cinterop.pw_size_int32

fun useProtobufShimWithOptIn() {
    pw_size_int32(1)
}
