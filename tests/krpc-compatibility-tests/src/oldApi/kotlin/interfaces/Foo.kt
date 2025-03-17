/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package interfaces

import kotlinx.rpc.RemoteService
import kotlinx.rpc.annotations.Rpc
import kotlinx.serialization.Serializable
import kotlin.coroutines.CoroutineContext

@Serializable
data class Foo(val field: String)

@Rpc
interface FooInterface : RemoteService {
    suspend fun get(): Foo
}

class FooInterfaceImpl(override val coroutineContext: CoroutineContext) : FooInterface {
    override suspend fun get(): Foo {
        return Foo("")
    }
}
