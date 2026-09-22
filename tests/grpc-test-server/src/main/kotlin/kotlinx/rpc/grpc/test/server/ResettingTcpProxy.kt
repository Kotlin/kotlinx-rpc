/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.server

import java.io.IOException
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

/** Forwards loopback TCP connections until [close] resets every accepted connection. */
internal class ResettingTcpProxy(
    target: InetSocketAddress,
) : AutoCloseable {
    private val stopped = AtomicBoolean()
    private val connections = ConcurrentHashMap.newKeySet<Connection>()
    private val listener = ServerSocket().apply {
        reuseAddress = true
        bind(InetSocketAddress(target.address, 0))
    }
    private val forwardExecutor = Executors.newCachedThreadPool(daemonThreadFactory("grpc-test-proxy-forward"))
    private val acceptExecutor = Executors.newSingleThreadExecutor(daemonThreadFactory("grpc-test-proxy-accept"))

    /** Ephemeral port clients connect to. */
    val port: Int = listener.localPort

    init {
        acceptExecutor.execute { acceptConnections(target) }
    }

    private fun acceptConnections(target: InetSocketAddress) {
        while (!stopped.get()) {
            val client = try {
                listener.accept().apply { tcpNoDelay = true }
            } catch (error: SocketException) {
                if (stopped.get()) return
                throw error
            }

            if (stopped.get()) {
                client.enableResetOnClose()
                client.closeQuietly()
                continue
            }

            val server = try {
                Socket().apply {
                    tcpNoDelay = true
                    connect(target)
                }
            } catch (error: IOException) {
                client.closeQuietly()
                if (stopped.get()) return
                throw error
            }
            val connection = Connection(client, server)
            connections.add(connection)

            if (stopped.get()) {
                if (connections.remove(connection)) connection.reset()
                continue
            }

            forwardExecutor.execute { forward(connection, client, server) }
            forwardExecutor.execute { forward(connection, server, client) }
        }
    }

    private fun forward(connection: Connection, source: Socket, destination: Socket) {
        try {
            source.getInputStream().copyTo(destination.getOutputStream())
        } catch (_: IOException) {
            // Closing or resetting either socket terminates both forwarding directions.
        } finally {
            if (connections.remove(connection)) connection.close()
        }
    }

    /** Stops new connections, then resets all connections accepted before listener shutdown. */
    override fun close() {
        if (!stopped.compareAndSet(false, true)) return

        listener.closeQuietly()
        acceptExecutor.shutdown()
        check(acceptExecutor.awaitTermination(STOP_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
            "TCP proxy accept loop did not terminate"
        }

        connections.toList().forEach { connection ->
            if (connections.remove(connection)) connection.reset()
        }
        forwardExecutor.shutdown()
        check(forwardExecutor.awaitTermination(STOP_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
            "TCP proxy forwarding did not terminate"
        }
    }

    private class Connection(
        private val client: Socket,
        private val server: Socket,
    ) {
        private val closed = AtomicBoolean()

        fun close() {
            if (!closed.compareAndSet(false, true)) return
            client.closeQuietly()
            server.closeQuietly()
        }

        fun reset() {
            if (!closed.compareAndSet(false, true)) return
            client.enableResetOnClose()
            server.enableResetOnClose()
            client.closeQuietly()
            server.closeQuietly()
        }
    }

    private companion object {
        const val STOP_TIMEOUT_SECONDS: Long = 5
    }
}

private fun daemonThreadFactory(prefix: String): (Runnable) -> Thread {
    val nextId = AtomicInteger()
    return { task ->
        Thread(task, "$prefix-${nextId.incrementAndGet()}").apply { isDaemon = true }
    }
}

private fun Socket.enableResetOnClose() {
    try {
        setSoLinger(true, 0)
    } catch (_: SocketException) {
        // The peer may have closed first; close still releases the local socket.
    }
}

private fun AutoCloseable.closeQuietly() {
    try {
        close()
    } catch (_: Exception) {
        // Best-effort cleanup after connection termination.
    }
}
