@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

import kotlinx.rpc.protobuf.internal.cinterop.pw_size_int32

fun useProtobufShimWithoutOptIn() {
    pw_size_int32(1)
}
