/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class, ObsoleteWorkersApi::class)

package kotlinx.rpc.grpc.internal

import kotlinx.cinterop.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.posix.sleep
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.concurrent.ObsoleteWorkersApi
import kotlin.native.concurrent.TransferMode
import kotlin.native.concurrent.Worker
import kotlin.native.ref.createCleaner
import kotlin.test.Test
import kotlin.time.TimeSource

fun startApiCall(callBack: CPointer<CFunction<(COpaquePointer) -> Unit>>, ctx: COpaquePointer) {
    val worker = Worker.start()

    worker.execute(TransferMode.SAFE, { Pair(callBack, ctx) }) { (cbPtr, ctx) ->
        sleep(15u)
        cbPtr.invoke(ctx)
    }
}

class MyCallbackApiWrapper {

    val myResource = Any()
    val myResourceCleaner = createCleaner(myResource) {
        // clean my resource
        val timeSinceStart = TimeSource.Monotonic.markNow()
        println("$timeSinceStart: My resource got cleaned")
    }

    val callback = staticCFunction { ptr: COpaquePointer ->
        val stableRef = ptr.asStableRef<Continuation<Unit>>()
        stableRef.get().resume(Unit)
        stableRef.dispose()
    }

    suspend fun callMyApi() = suspendCancellableCoroutine<Unit> { cont ->
        val contRef = StableRef.create(cont)
        startApiCall(callback, contRef.asCPointer())
    }
}


class UnexpectedCleanerTest {
    @Test
    fun test() {
        runBlocking {
            val apiWrapper = MyCallbackApiWrapper()
            apiWrapper.callMyApi()
            val timeSinceStart = TimeSource.Monotonic.markNow()
            println("$timeSinceStart: My API call returned")
        }
    }
}