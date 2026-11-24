/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server.testing

import io.grpc.testing.integration.TestService
import io.grpc.testing.integration.UnimplementedService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.rpc.RpcServer
import kotlinx.rpc.grpc.client.GrpcClient
import kotlinx.rpc.grpc.client.GrpcClientConfiguration
import kotlinx.rpc.grpc.server.GrpcServer
import kotlinx.rpc.grpc.server.GrpcServerConfiguration
import kotlinx.rpc.registerService
import kotlinx.rpc.test.EventLog
import kotlinx.rpc.test.TestCleanup
import kotlinx.rpc.test.TestGate
import kotlinx.rpc.test.combineFailures
import kotlinx.rpc.test.runTestWithCoroutinesProbes
import kotlinx.rpc.withService
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal fun grpcServerTest(block: suspend GrpcServerTestFixture.() -> Unit): TestResult {
    val repeats = testRepeatCount()
    return runTestWithCoroutinesProbes(timeout = TEST_TIMEOUT * repeats) {
        repeat(repeats) {
            withContext(Dispatchers.Default) { runFixture(block) }
        }
    }
}

internal suspend fun runFixture(block: suspend GrpcServerTestFixture.() -> Unit) {
    val fixture = GrpcServerTestFixture()
    val testFailure = runCatching { fixture.block() }.exceptionOrNull()
    val cleanupFailure = withContext(NonCancellable) { fixture.close(testFailure) }
    combineFailures(testFailure, cleanupFailure)?.let { throw it }
}

/** Owns one server, one client, and the synchronization objects of a real-time test. */
internal class GrpcServerTestFixture {
    private val serverParentJob = Job()
    private val gates = mutableListOf<TestGate>()
    private var startAttempted = false
    private var serverStoppedByTest = false
    private var fixtureServer: GrpcServer? = null
    private var fixtureClient: GrpcClient? = null
    private var fixtureTestService: TestService? = null
    private var fixtureUnimplementedService: UnimplementedService? = null

    val observer: ServerCallObserver = ServerCallObserver()
    val marks: EventLog<String> = EventLog()

    val server: GrpcServer get() = checkNotNull(fixtureServer) { "Call start() before accessing the server" }
    val client: GrpcClient get() = checkNotNull(fixtureClient) { "Call start() before accessing the client" }
    val testService: TestService
        get() = checkNotNull(fixtureTestService) { "Call start() before accessing the service" }
    val unimplementedService: UnimplementedService
        get() = checkNotNull(fixtureUnimplementedService) { "Call start() before accessing the service" }

    /** Registers a gate for the teardown checks and cleanup. */
    fun registerNewGate(name: String): TestGate = TestGate(name).also { gates += it }

    /**
     * Starts the server and client once. The observer runs before the test interceptors.
     * Use [services] to register additional services. Don't call `services { }` in [serverConfig].
     */
    fun start(
        serverConfig: GrpcServerConfiguration.() -> Unit = {},
        clientConfig: GrpcClientConfiguration.() -> Unit = {},
        services: RpcServer.() -> Unit = {},
        service: ScriptedTestServiceBuilder.() -> Unit = {},
    ) {
        check(!startAttempted) { "The fixture can start only once" }
        startAttempted = true
        val handlers = ScriptedTestServiceBuilder().apply(service)
        fixtureServer = GrpcServer(port = 0, parentContext = serverParentJob) {
            intercept(observer)
            serverConfig()
            services {
                registerService<TestService> { ScriptedTestService(handlers) }
                services()
            }
        }
        server.start()
        fixtureClient = GrpcClient("127.0.0.1", server.port) {
            credentials = plaintext()
            clientConfig()
        }
        fixtureTestService = client.withService<TestService>()
        fixtureUnimplementedService = client.withService<UnimplementedService>()
    }

    /** Skips the observed-close and server-termination assertions after a shutdown test stops the server. */
    fun markServerStoppedByTest() {
        serverStoppedByTest = true
    }

    /** Attempts every cleanup step and returns its failures, with later failures suppressed. */
    suspend fun close(testFailure: Throwable?): Throwable? {
        val cleanup = TestCleanup(stepTimeout = TEARDOWN_STEP_TIMEOUT)

        if (testFailure == null) {
            cleanup.run("check gates") {
                gates.forEach { gate ->
                    assertFalse(
                        gate.hasWaiters,
                        "A coroutine waits at the gate '${gate.name}'. Open the gate in the test.",
                    )
                }
            }
        }
        cleanup.run("open gates") { gates.forEach { it.open() } }
        if (testFailure == null && fixtureServer != null && !serverStoppedByTest) {
            cleanup.run("check observed calls") {
                observer.calls.forEach { it.awaitClose() }
            }
        }
        if (testFailure == null) {
            cleanup.run("check server call jobs") {
                try {
                    withTimeout(TEARDOWN_STEP_TIMEOUT) { serverCallJobs().joinAll() }
                } catch (error: TimeoutCancellationException) {
                    val activeCount = serverCallJobs().count { !it.isCompleted }
                    throw AssertionError("$activeCount server call jobs didn't end in $TEARDOWN_STEP_TIMEOUT", error)
                }
            }
        }
        cleanup.run("stop client") {
            fixtureClient?.let {
                it.shutdownNow()
                it.awaitTermination(TEARDOWN_STEP_TIMEOUT)
            }
        }
        cleanup.run("stop server") {
            fixtureServer?.let {
                it.shutdown()
                it.awaitTermination(TEARDOWN_STEP_TIMEOUT)
                if (testFailure == null && !serverStoppedByTest) {
                    assertTrue(it.isTerminated, "The server didn't terminate in $TEARDOWN_STEP_TIMEOUT")
                }
            }
        }
        cleanup.run("cancel server parent") { serverParentJob.cancel() }
        return cleanup.failure()
    }

    private fun serverCallJobs(): List<Job> = serverParentJob.children.flatMap { it.children }.toList()
}
