/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server.testing

import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.Socket
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.utils.io.readAvailable
import io.ktor.utils.io.readFully
import io.ktor.utils.io.writeFully
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withTimeout

internal suspend fun <T> withHttp2Peer(port: Int, block: suspend Http2TestPeer.() -> T): T {
    val selector = SelectorManager(Dispatchers.Default)
    selector.use { selector ->
        val socket = withTimeout(AWAIT_TIMEOUT) { aSocket(selector).tcp().connect("127.0.0.1", port) }
        socket.use { socket ->
            val peer = Http2TestPeer(socket)
            peer.connect()
            return peer.block()
        }
    }
}

/** Sends HTTP/2 frames without automatic PING replies or backend-specific channel access. */
internal class Http2TestPeer(socket: Socket) {
    private val input = socket.openReadChannel()
    private val output = socket.openWriteChannel()

    suspend fun connect() {
        output.writeFully("PRI * HTTP/2.0\r\n\r\nSM\r\n\r\n".encodeToByteArray())
        writeFrame(SETTINGS)
        withTimeout(AWAIT_TIMEOUT) {
            while (true) {
                val frame = checkNotNull(readFrame()) { "The server closed before sending SETTINGS" }
                if (frame.type == SETTINGS && frame.flags and ACK == 0) {
                    return@withTimeout
                }
            }
        }
    }

    suspend fun acknowledgePing(frame: Frame) {
        require(frame.type == PING && frame.flags and ACK == 0)
        writeFrame(PING, ACK, frame.payload)
    }

    suspend fun awaitPing(): Frame {
        while (true) {
            val frame = checkNotNull(readFrame()) { "The server closed before sending a PING" }
            if (frame.type == PING && frame.flags and ACK == 0) {
                return frame
            }
        }
    }

    suspend fun awaitClosed(acknowledgePings: Boolean = true) {
        while (true) {
            val frame = readFrame() ?: return
            if (acknowledgePings && frame.type == PING && frame.flags and ACK == 0) {
                acknowledgePing(frame)
            }
        }
    }

    private suspend fun readFrame(): Frame? {
        val header = ByteArray(9)
        if (input.readAvailable(header, 0, 1) == -1) {
            return null
        }
        input.readFully(header, 1, header.size)
        fun byte(index: Int): Int = header[index].toInt() and 0xff
        val size = (byte(0) shl 16) or (byte(1) shl 8) or byte(2)
        val frame = Frame(byte(3), byte(4), ByteArray(size))
        input.readFully(frame.payload)
        if (frame.type == SETTINGS && frame.flags and ACK == 0) {
            writeFrame(SETTINGS, ACK)
        }
        return frame
    }

    private suspend fun writeFrame(type: Int, flags: Int = 0, payload: ByteArray = ByteArray(0)) {
        output.writeFully(byteArrayOf(
            (payload.size ushr 16).toByte(), (payload.size ushr 8).toByte(), payload.size.toByte(),
            type.toByte(), flags.toByte(),
            0, 0, 0, 0,
        ))
        output.writeFully(payload)
        output.flush()
    }

    class Frame(val type: Int, val flags: Int, val payload: ByteArray)

    private companion object {
        const val SETTINGS = 4
        const val PING = 6
        const val ACK = 1
    }
}
