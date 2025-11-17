/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.test.Test

class PlaygroundTest {

    @Test
    fun testCoroutineNew() {
        runBlocking {

            val scope = CoroutineScope(Dispatchers.Default)

//            val job = Job(scope.coroutineContext.job)
            val jobScope = CoroutineScope(scope.coroutineContext)

            jobScope.launch {
                println("Hello")
            }.join()

            scope.cancel("With this message")

            jobScope.launch {
                println("World")
            }.apply {
                invokeOnCompletion {
                    println("Job is completed $it")
                }
            }.join()

            println("Scope is active ${scope.coroutineContext.job.isActive}")
        }
    }

}