/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.test.compat

import ch.qos.logback.classic.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.debug.DebugProbes
import kotlinx.coroutines.test.TestScope
import org.junit.jupiter.api.DynamicTest
import org.slf4j.LoggerFactory
import java.net.URLClassLoader
import java.util.stream.Stream
import kotlin.test.assertTrue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Suppress("EnumEntryName", "detekt.EnumNaming")
enum class Versions {
    v0_9,
    v0_8,
    ;
}

enum class Role {
    Server, Client;
}

class VersionRolePair(
    val version: Versions,
    val role: Role,
)

@Suppress("unused")
val Versions.client get() = VersionRolePair(this, Role.Client)
@Suppress("unused")
val Versions.server get() = VersionRolePair(this, Role.Server)

abstract class KrpcProtocolCompatibilityTestsBase {
    class LoadedStarter(val version: Versions, val classLoader: URLClassLoader) {
        val starter = classLoader
            .loadClass("kotlinx.rpc.krpc.test.compat.service.TestStarter")
            .getDeclaredConstructor()
            .newInstance() as Starter

        suspend fun close() {
            classLoader.close()
            starter.stopClient()
            starter.stopServer()
        }
    }

    private fun prepareStarters(exclude: List<Versions>): List<LoadedStarter> {
        return Versions.entries.filter { it !in exclude }.map { version ->
            val versionResourcePath = javaClass.classLoader.getResource(version.name)!!
            val versionClassLoader = URLClassLoader(arrayOf(versionResourcePath), javaClass.classLoader)

            LoadedStarter(version, versionClassLoader)
        }
    }

    class TestEnv(
        val old: Starter,
        val new: Starter,
        val appender: TestLogAppender,
        val testScope: TestScope,
    ) : CoroutineScope by testScope {
        fun assertNoErrorsInLogs() {
            assertTrue { appender.errors.isEmpty() }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun runTest(
        role: Role,
        exclude: List<Versions>,
        timeout: Duration = 10.seconds,
        body: suspend TestEnv.() -> Unit,
    ): Stream<DynamicTest> {
        return prepareStarters(exclude).map {
            DynamicTest.dynamicTest("$role ${it.version}") {
                kotlinx.coroutines.test.runTest(timeout = timeout) {
                    DebugProbes.withDebugProbes {
                        val root = LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME) as Logger
                        val testAppender = root.getAppender("TEST") as TestLogAppender
                        testAppender.events.clear()
                        try {
                            val env = TestEnv(it.starter, it.starter, testAppender, this)
                            body(env)
                        } finally {
                            testAppender.events.clear()
                            it.close()
                        }
                    }
                }
            }
        }.stream()
    }

    private fun testTransport() = LocalTransport()

    private fun testOldClientWithNewServer(
        perCallBufferSize: Int = 1,
        timeout: Duration = 10.seconds,
        exclude: List<Versions>,
        body: suspend TestEnv.(CompatService, CompatServiceImpl) -> Unit,
    ) = runTest(Role.Client, exclude, timeout) {
        val transport = testTransport()
        val config = TestConfig(perCallBufferSize)
        val service = old.startClient(transport.client, config)
        val impl = new.startServer(transport.server, config)
        body(service, impl)
    }

    private fun testOldServersWithNewClient(
        perCallBufferSize: Int = 1,
        timeout: Duration = 10.seconds,
        exclude: List<Versions>,
        body: suspend TestEnv.(CompatService, CompatServiceImpl) -> Unit,
    ) = runTest(Role.Server, exclude, timeout) {
        val transport = testTransport()
        val config = TestConfig(perCallBufferSize)
        val service = new.startClient(transport.client, config)
        val impl = old.startServer(transport.server, config)
        body(service, impl)
    }

    protected fun matrixTest(
        perCallBufferSize: Int = 1,
        timeout: Duration = 10.seconds,
        exclude: List<VersionRolePair> = emptyList(),
        body: suspend TestEnv.(CompatService, CompatServiceImpl) -> Unit,
    ): Stream<DynamicTest> {
        val clientExclude = exclude.filter { it.role == Role.Client }.map { it.version }
        val serverExclude = exclude.filter { it.role == Role.Server }.map { it.version }
        return Stream.concat(
            testOldClientWithNewServer(perCallBufferSize, timeout, clientExclude, body),
            testOldServersWithNewClient(perCallBufferSize, timeout, serverExclude, body),
        )
    }
}
