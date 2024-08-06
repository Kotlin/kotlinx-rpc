/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.internal

@InternalRPCApi
public interface RPCMethodClassArguments {
    public fun asArray(): Array<out Any?>
}
