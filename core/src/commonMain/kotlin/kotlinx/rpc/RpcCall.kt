/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc

import kotlinx.rpc.descriptor.RpcServiceDescriptor
import org.jetbrains.kotlinx.library.api.watchdog.ExemptionReason
import org.jetbrains.kotlinx.library.api.watchdog.IntentionallyMutableCollection
import org.jetbrains.kotlinx.library.api.watchdog.IntentionallyWithoutEqualsHashCodeOrToString

/**
 * Represents a method call from an RPC service.
 *
 * @property descriptor [RpcServiceDescriptor] of a service that made the call.
 * @property callableName The name of the method being called.
 * @property arguments array of arguments for the call
 * @property serviceId The id of the service that made the call.
 */
@IntentionallyWithoutEqualsHashCodeOrToString(description = "Revisit before 1.0")
public class RpcCall(
    public val descriptor: RpcServiceDescriptor<*>,
    public val callableName: String,
    @IntentionallyMutableCollection(reason = ExemptionReason.TODO, description = "review before 1.0")
    public val arguments: Array<Any?>,
    public val serviceId: Long,
)
