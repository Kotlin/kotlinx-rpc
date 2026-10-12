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
 * [DisposableEndpointManager] uses the proxy to simulate a lost connection:
 *
 * - A shutdown of the gRPC server ends its connections with HTTP/2 frames, for example `GOAWAY` or stream resets.
 *   A client can handle these frames as an orderly shutdown.
 * - The proxy closes both sockets with a TCP `RST` below HTTP/2. The client gets no more frames, as in a network
 *   failure.
 * - The endpoint closes the proxy before its server, so no shutdown frames get to the client first.
 *
 * The proxy copies raw bytes and doesn't parse gRPC. Each client connection gets its own connection to [target],
 * with one thread for each direction.
 *
 * @param target The address of the gRPC server. The proxy listens on the same host.
 */
internal class ResettingTcpProxy(
    target: InetSocketAddress,
) : AutoCloseable {
    private val stopped = AtomicBoolean()

    /**
     * Connections that are still open. The thread that removes a connection from this set ends it, so each
     * connection ends exactly once. A forwarding thread closes the connection, and [close] resets it.
     */
    private val connections = ConcurrentHashMap.newKeySet<Connection>()
    private val listener = ServerSocket().apply {
        reuseAddress = true
        bind(InetSocketAddress(target.address, 0))
    }
    private val forwardExecutor = Executors.newCachedThreadPool(daemonThreadFactory("grpc-test-proxy-forward"))
    private val acceptExecutor = Executors.newSingleThreadExecutor(daemonThreadFactory("grpc-test-proxy-accept"))

    /** The ephemeral port that clients connect to. */
    val port: Int = listener.localPort

    init {
        acceptExecutor.execute { acceptConnections(target) }
    }

    /**
     * Accepts client connections until [close] stops the listener, which makes [ServerSocket.accept] throw.
     *
     * The loop reads [stopped] again after each step, so it resets a connection that it accepts during [close]
     * and doesn't forward it.
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
     * The proxy doesn't forward a TCP half-close, so an end of stream in one direction closes both directions.
     * gRPC doesn't need half-close, because HTTP/2 signals the end of a stream in frames.
     */
    private fun forward(connection: Connection, source: Socket, destination: Socket) {
        try {
            source.getInputStream().copyTo(destination.getOutputStream())
        } catch (_: IOException) {
            // A close or a reset of either socket stops both forwarding directions.
        } finally {
            if (connections.remove(connection)) connection.close()
        }
    }

    /** Stops new connections, then resets all connections that the listener accepted before it stopped. */
    override fun close() {
        if (!stopped.compareAndSet(false, true)) return

        listener.closeQuietly()
        acceptExecutor.shutdown()
        check(acceptExecutor.awaitTermination(STOP_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
            "TCP proxy accept loop didn't stop"
        }

        connections.toList().forEach { connection ->
            if (connections.remove(connection)) connection.reset()
        }
        forwardExecutor.shutdown()
        check(forwardExecutor.awaitTermination(STOP_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
            "TCP proxy forwarding didn't stop"
        }
    }

    /**
     * The client socket that the proxy accepted and the server socket that the proxy opened for it.
     *
     * [close] ends both sockets with a TCP `FIN`. [reset] aborts both with a TCP `RST`. Only the first call of the
     * two functions has an effect.
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

/** Creates numbered daemon threads, so that proxy threads don't stop the JVM from exiting. */
private fun daemonThreadFactory(prefix: String): (Runnable) -> Thread {
    val nextId = AtomicInteger()
    return { task ->
        Thread(task, "$prefix-${nextId.incrementAndGet()}").apply { isDaemon = true }
    }
}

/**
 * Makes the next [Socket.close] send a TCP `RST` and discard unsent data, instead of a `FIN` after a flush.
 * A linger timeout of zero (`SO_LINGER=0`) selects this abortive close.
 */
private fun Socket.enableResetOnClose() {
    try {
        setSoLinger(true, 0)
    } catch (_: SocketException) {
        // The peer can close first. Then close still releases the local socket.
    }
}

private fun AutoCloseable.closeQuietly() {
    try {
        close()
    } catch (_: Exception) {
        // The cleanup is best effort.
    }
}
