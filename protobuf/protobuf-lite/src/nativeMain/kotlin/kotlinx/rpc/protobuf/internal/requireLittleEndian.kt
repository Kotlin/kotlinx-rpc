/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.internal

import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.Platform

/**
 * The native wire encoder and decoder copy fixed-size values in memory order, which must be little-endian.
 */
@OptIn(ExperimentalNativeApi::class)
internal fun requireLittleEndian() {
    require(Platform.isLittleEndian) {
        "kotlinx-rpc protobuf native implementation requires a little-endian platform"
    }
}
