/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.spring.security.starter.test

import kotlinx.rpc.krpc.spring.security.starter.KrpcSecurityAutoConfiguration
import kotlinx.rpc.krpc.spring.security.starter.KrpcSecurityStarterProperties
import kotlinx.rpc.krpc.spring.server.starter.KrpcServerAutoConfiguration
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.context.annotation.ImportCandidates
import java.beans.Introspector
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class SecurityStarterValidityTest {
    @Test
    fun `the auto-configuration imports list only the security auto-configuration`() {
        val classNames = starterResource(AUTO_CONFIGURATION_IMPORTS).lines()
            .map { it.trim() }
            .filter { it.isNotEmpty() && !it.startsWith("#") }

        assertEquals(listOf(KrpcSecurityAutoConfiguration::class.java.name), classNames)
        classNames.forEach { className ->
            val autoConfiguration = Class.forName(className)
            assertNotNull(autoConfiguration.getAnnotation(AutoConfiguration::class.java), className)
        }
    }

    @Test
    fun `Boot discovers the security auto-configuration after the server auto-configuration`() {
        val candidates = ImportCandidates.load(AutoConfiguration::class.java, javaClass.classLoader).toList()
        assertTrue(KrpcSecurityAutoConfiguration::class.java.name in candidates, candidates.toString())
        assertTrue(KrpcServerAutoConfiguration::class.java.name in candidates, candidates.toString())

        val annotation = KrpcSecurityAutoConfiguration::class.java.getAnnotation(AutoConfiguration::class.java)
        assertEquals(listOf(KrpcServerAutoConfiguration::class.java.name), annotation.afterName.toList())
    }

    @Test
    fun `the configuration metadata matches the properties class`() {
        val metadata = Json.parseToJsonElement(starterResource(CONFIGURATION_METADATA)).jsonObject
        val defaults = KrpcSecurityStarterProperties()
        val propertyDescriptors = Introspector.getBeanInfo(KrpcSecurityStarterProperties::class.java)
            .propertyDescriptors
            .filter { it.writeMethod != null }
            .associateBy { "kotlinx.rpc.krpc.server.security.${it.name}" }

        val properties = metadata.getValue("properties").jsonArray.map { it.jsonObject }

        assertEquals(propertyDescriptors.keys, properties.map { it.string("name") }.toSet())
        properties.forEach { property ->
            val descriptor = propertyDescriptors.getValue(property.string("name"))
            assertEquals(KrpcSecurityStarterProperties::class.java.name, property.string("sourceType"))
            assertEquals(descriptor.propertyType.kotlin.javaObjectType.name, property.string("type"))

            val expectedDefault = descriptor.readMethod.invoke(defaults)
            val actualDefault = property["defaultValue"]?.jsonPrimitive
                ?.let { it.booleanOrNull ?: it.content }
            assertEquals(expectedDefault, actualDefault)
        }

        val groups = metadata.getValue("groups").jsonArray.map { it.jsonObject }
        assertEquals(listOf("kotlinx.rpc.krpc.server.security"), groups.map { it.string("name") })
        assertEquals(KrpcSecurityStarterProperties::class.java.name, groups.single().string("type"))
    }

    private fun starterResource(path: String): String {
        val resources = javaClass.classLoader.getResources(path).toList()
        val resource = resources.singleOrNull { it.toString().contains("krpc-spring-security-starter") }
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
