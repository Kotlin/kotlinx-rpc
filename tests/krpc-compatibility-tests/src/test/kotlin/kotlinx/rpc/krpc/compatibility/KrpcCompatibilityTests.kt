/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.compatibility

import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.job
import kotlinx.coroutines.test.runTest
import kotlinx.rpc.krpc.rpcClientConfig
import kotlinx.rpc.krpc.rpcServerConfig
import kotlinx.rpc.krpc.serialization.json.json
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import java.net.URLClassLoader
import java.util.stream.Stream

class KrpcCompatibilityTests {
    private class ClientServer(private val clientClassLoader: URLClassLoader, val serverClassLoader: URLClassLoader) :
        AutoCloseable {
        val client = clientClassLoader
            .loadClass("tests.CompatibilityTests")
            .getDeclaredConstructor()
            .newInstance() as CompatibilityTest

        val server = serverClassLoader
            .loadClass("tests.ApiServer")
            .getDeclaredConstructor()
            .newInstance() as TestApiServer

        override fun close() {
            clientClassLoader.close()
            serverClassLoader.close()
        }
    }

    private fun prepareClientServer(oldClient: Boolean): ClientServer {
        val newResourcePath = javaClass.classLoader.getResource("new/")!!
        val oldResourcePath = javaClass.classLoader.getResource("old/")!!

        val oldApiClassLoader = URLClassLoader(arrayOf(oldResourcePath), javaClass.classLoader)
        val newApiClassLoader = URLClassLoader(arrayOf(newResourcePath), javaClass.classLoader)

        val clientClassLoader = if (oldClient) oldApiClassLoader else newApiClassLoader
        val serverClassLoader = if (oldClient) newApiClassLoader else oldApiClassLoader

        return ClientServer(clientClassLoader = clientClassLoader, serverClassLoader = serverClassLoader)
    }

    private val rpcServerConfig = rpcServerConfig {
        serialization {
            json()
        }
    }

    private val rpcClientConfig = rpcClientConfig {
        serialization {
            json {
                ignoreUnknownKeys = true
            }
        }
    }

    private fun compatibilityTests(clientServer: ClientServer): Stream<DynamicTest> {
        return clientServer.client.getAllTests().map { (name, test) ->
            DynamicTest.dynamicTest(name) {
                runTest {
                    val localTransport = LocalTransport()
                    val server = KrpcTestServer(rpcServerConfig, localTransport.server)
                    val client = KrpcTestClient(rpcClientConfig, localTransport.client)
                    clientServer.server.serveAllInterfaces(server)

                    try {
                        test(client)
                    } finally {
                        server.close()
                        client.close()
                        server.awaitCompletion()
                        client.awaitCompletion()
                        localTransport.coroutineContext.job.cancelAndJoin()
                    }
                }
            }
        }.stream()
    }

    @TestFactory
    fun testCompatibilityNew(): Stream<DynamicTest> {
        val clientServer = prepareClientServer(oldClient = false)
        return compatibilityTests(clientServer)
    }

    @TestFactory
    fun testCompatibilityOld(): Stream<DynamicTest> {
        val clientServer = prepareClientServer(oldClient = true)
        return compatibilityTests(clientServer)
    }
}
