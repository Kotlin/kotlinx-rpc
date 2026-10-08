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

/**
 * Forwards loopback TCP connections to [target] until [close] resets every accepted connection.
 *
 * Simulates a lost connection for [DisposableEndpointManager]. Shutting down the gRPC server itself would end its
 * connections through HTTP/2, for example with `GOAWAY` or stream resets, which a client can handle as an orderly
 * shutdown. The proxy sits below HTTP/2 and closes both sockets with a TCP `RST` instead, so the client sees the
 * connection disappear without any further frames, as with a network failure. The endpoint closes the proxy before
 * its server, so no shutdown frames reach the client first.
 *
 * Forwarding copies raw bytes and knows nothing about gRPC. Each client connection gets its own connection to
 * [target], served by one thread per direction.
 *
 * @param target Address of the gRPC server to forward to. The proxy listens on the same host.
 */
internal class ResettingTcpProxy(
    target: InetSocketAddress,
) : AutoCloseable {
    private val stopped = AtomicBoolean()

    /**
     * Connections that are still open. Whoever removes a connection from this set ends it: a forwarding thread
     * closes it normally, [close] resets it. This way every connection ends exactly once.
     */
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

    /**
     * Accepts client connections until [close] stops the listener, which makes [ServerSocket.accept] throw.
     *
     * [stopped] is checked again after each step, so that a connection accepted while the proxy is closing is reset
     * instead of forwarded.
     */
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

    /**
     * Copies bytes from [source] to [destination] until either side ends, then closes the whole connection.
     *
     * An end of stream in one direction closes both directions, since TCP half-close is not forwarded. gRPC does
     * not rely on it, as HTTP/2 signals the end of a stream in frames.
     */
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

    /**
     * The client socket accepted by the proxy and the server socket opened for it.
     *
     * [close] ends both sockets normally with a TCP `FIN`; [reset] aborts both with a TCP `RST`. Only the first call
     * of either has an effect.
     */
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

/** Creates numbered daemon threads, so that proxy threads never keep the server process alive. */
private fun daemonThreadFactory(prefix: String): (Runnable) -> Thread {
    val nextId = AtomicInteger()
    return { task ->
        Thread(task, "$prefix-${nextId.incrementAndGet()}").apply { isDaemon = true }
    }
}

/**
 * Makes the next [Socket.close] send a TCP `RST` and discard unsent data, instead of a `FIN` after flushing.
 * A linger timeout of zero selects this abortive close.
 */
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
