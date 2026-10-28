/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.spring.server.starter.test

import kotlinx.coroutines.test.runTest
import kotlinx.rpc.krpc.ktor.client.rpc
import kotlinx.rpc.krpc.ktor.client.rpcConfig
import kotlinx.rpc.krpc.serialization.cbor.cbor
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.rpc.krpc.spring.server.starter.KrpcServerAutoConfiguration
import kotlinx.rpc.krpc.spring.server.starter.KrpcServerStarterProperties
import kotlinx.rpc.withService
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.builder.SpringApplicationBuilder
import java.beans.Introspector
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class StarterValidityTest {
    @Test
    fun `the auto-configuration imports list only existing auto-configuration classes`() {
        val classNames = starterResource(AUTO_CONFIGURATION_IMPORTS).lines()
            .map { it.trim() }
            .filter { it.isNotEmpty() && !it.startsWith("#") }

        assertEquals(listOf(KrpcServerAutoConfiguration::class.java.name), classNames)
        classNames.forEach { className ->
            val autoConfiguration = Class.forName(className)
            assertNotNull(autoConfiguration.getAnnotation(AutoConfiguration::class.java), className)
        }
    }

    @Test
    fun `the configuration metadata matches the properties class`() {
        val metadata = Json.parseToJsonElement(starterResource(CONFIGURATION_METADATA)).jsonObject
        val defaults = KrpcServerStarterProperties()
        val propertyDescriptors = Introspector.getBeanInfo(KrpcServerStarterProperties::class.java)
            .propertyDescriptors
            .filter { it.writeMethod != null }
            .associateBy { "kotlinx.rpc.krpc.server.${it.name}" }

        val properties = metadata.getValue("properties").jsonArray.map { it.jsonObject }

        assertEquals(propertyDescriptors.keys, properties.map { it.string("name") }.toSet())
        properties.forEach { property ->
            val descriptor = propertyDescriptors.getValue(property.string("name"))
            assertEquals(KrpcServerStarterProperties::class.java.name, property.string("sourceType"))
            assertEquals(descriptor.propertyType.kotlin.javaObjectType.name, property.string("type"))

            val expectedDefault = descriptor.readMethod.invoke(defaults)
            val actualDefault = property["defaultValue"]?.jsonPrimitive
                ?.let { it.booleanOrNull ?: it.content }
            assertEquals(expectedDefault, actualDefault)
        }
    }

    @Test
    fun `a Boot application without kRPC configuration serves RPC calls`() = runTest {
        disableTomcatUrlStreamHandlerFactory()
        val context = SpringApplicationBuilder(StarterTestApplication::class.java).run(
            "--server.port=0",
            "--spring.main.banner-mode=off",
            "--logging.level.root=ERROR",
        )
        val httpClient = webSocketHttpClient()

        try {
            val port = context.environment.getRequiredProperty("local.server.port").toInt()
            val client = httpClient.krpcClient(port)
            val service = client.withService<EchoService>()

            assertEquals("hello", withRpcTimeout { service.echo("hello") })
            assertEquals(Greeting("Hello, starter!"), withRpcTimeout { service.greet(Greeting("starter")) })

            client.close()
        } finally {
            httpClient.close()
            context.close()
        }
    }

    @Test
    @OptIn(ExperimentalSerializationApi::class)
    fun `a Boot application serves RPC calls with the selected CBOR format`() = runTest {
        disableTomcatUrlStreamHandlerFactory()
        val context = SpringApplicationBuilder(StarterTestApplication::class.java).run(
            "--server.port=0",
            "--spring.main.banner-mode=off",
            "--logging.level.root=ERROR",
            "--kotlinx.rpc.krpc.server.serialization=cbor",
        )
        val httpClient = webSocketHttpClient()

        try {
            val port = context.environment.getRequiredProperty("local.server.port").toInt()
            val client = httpClient.rpc("ws://127.0.0.1:$port/krpc") {
                rpcConfig {
                    serialization { cbor() }
                }
            }
            val service = client.withService<EchoService>()

            assertEquals("hello", withRpcTimeout { service.echo("hello") })
            assertEquals(Greeting("Hello, starter!"), withRpcTimeout { service.greet(Greeting("starter")) })

            client.close()
        } finally {
            httpClient.close()
            context.close()
        }
    }

    private fun starterResource(path: String): String {
        val resources = javaClass.classLoader.getResources(path).toList()
        val resource = resources.singleOrNull { it.toString().contains("krpc-spring-server-starter") }
        assertNotNull(resource, "The starter must have exactly one '$path' resource, found: $resources")
        return resource.readText()
    }

    private fun JsonObject.string(key: String): String {
        val value = getValue(key)
        assertTrue(value is JsonPrimitive && value.isString, "'$key' must be a string")
        return value.content
    }

    private companion object {
        const val AUTO_CONFIGURATION_IMPORTS =
            "META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports"
        const val CONFIGURATION_METADATA = "META-INF/spring-configuration-metadata.json"
    }
}
