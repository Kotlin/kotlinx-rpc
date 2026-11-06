/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc

/**
 * Usage of API marked with thi annotation that can lead to unexpected results and failures.
 */
@RequiresOptIn("This API is dangerous. It is not recommended to use it, unless you know what you are doing.")
public annotation class RpcDangerousApi
