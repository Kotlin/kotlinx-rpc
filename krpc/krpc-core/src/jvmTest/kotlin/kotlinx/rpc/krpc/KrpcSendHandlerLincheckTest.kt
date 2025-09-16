/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc

import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedSendChannelException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.rpc.krpc.internal.KrpcSendHandler
import org.jetbrains.lincheck.datastructures.IntGen
import org.jetbrains.lincheck.datastructures.ModelCheckingOptions
import org.jetbrains.lincheck.datastructures.Operation
import org.jetbrains.lincheck.datastructures.Param
import org.jetbrains.lincheck.datastructures.StressOptions
import kotlin.coroutines.resume
import kotlin.test.Ignore
import kotlin.test.Test

@Suppress("unused")
@Param(name = "update", gen = IntGen::class, conf = "1:3")
class KrpcSendHandlerLincheckTest {
    private val channel = Channel<KrpcTransportMessage>(Channel.UNLIMITED)
    private val handler = KrpcSendHandler(channel)

    @Operation
    fun updateWindow(
        @Param(name = "update") update: Int,
    ) = handler.updateWindowSize(update)

    @Operation
    suspend fun send(message: String): Unit = handler.sendMessage(message.asMessage())

    @Operation
    fun close() = handler.close()

    @Operation
    suspend fun receive(): String = channel.receive().value

    @Test
    fun modelTest() = ModelCheckingOptions()
        .actorsBefore(2)
        .threads(5)
        .actorsPerThread(4)
        .actorsAfter(2)
        .iterations(100)
        .invocationsPerIteration(100)
//        .checkObstructionFreedom(true) // todo we are not lock free
        .minimizeFailedScenario(true)
        .sequentialSpecification(SequentialKrpcSendHandler::class.java)
        .check(this::class)

    @Test
    @Ignore
    fun stressTest() = StressOptions()
        .actorsBefore(2)
        .threads(3)
        .actorsPerThread(6)
        .actorsAfter(2)
        .iterations(10) // 10 is a magic number here, 11 hangs
        .invocationsPerIteration(1000)
        .minimizeFailedScenario(true)
        .sequentialSpecification(SequentialKrpcSendHandler::class.java)
        .check(this::class)
}

@Suppress("unused")
class SequentialKrpcSendHandler {
    private var closed = false
    private val channel = Channel<String>(Channel.UNLIMITED)
    private var window: Int = -1
    private val continuations = mutableListOf<CancellableContinuation<Unit>>()

    fun updateWindow(update: Int) {
        if (closed) {
            return
        }

        window = if (window == -1) update else window + update

        continuations.forEach { it.resume(Unit) }
        continuations.clear()
    }

    suspend fun send(message: String) {
        if (closed) {
            throw ClosedSendChannelException("KrpcSendHandler closed")
        }

        val window = window
        if (window == -1) {
            channel.send(message)
            return
        }

        while (true) {
            if (window == 0) {
                suspendCancellableCoroutine<Unit> { cont ->
                    continuations.add(cont)
                }
            } else {
                channel.send(message)
                updateWindow(-1)
                break
            }
        }
    }

    fun close() {
        closed = true
    }

    suspend fun receive(): String {
        return channel.receive()
    }
}
