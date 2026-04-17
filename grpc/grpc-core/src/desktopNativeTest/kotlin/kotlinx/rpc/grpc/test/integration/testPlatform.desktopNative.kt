/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalNativeApi::class)

package kotlinx.rpc.grpc.test.integration

import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.CpuArchitecture
import kotlin.native.OsFamily
import kotlin.native.Platform

internal actual val isLinuxX64Native: Boolean =
    Platform.osFamily == OsFamily.LINUX && Platform.cpuArchitecture == CpuArchitecture.X64
