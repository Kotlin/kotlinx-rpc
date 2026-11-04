/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.benchmarks.client

import kotlin.test.Test
import kotlin.test.assertEquals

class JsonTest {
    @Test
    fun escapesStrings() {
        assertEquals("\"a\\\"b\\\\c\\nd\"", encodeJson(JsonString("a\"b\\c\nd")))
        assertEquals("\"\\u0001\"", encodeJson(JsonString("\u0001")))
    }

    @Test
    fun encodesCompactObjectsAndArrays() {
        val value = JsonObject.of(
            "count" to JsonLong(3),
            "name" to JsonString("unary-latency"),
            "samples" to JsonArray.ofNanos(longArrayOf(10, 20)),
            "missing" to JsonNullValue,
        )

        assertEquals(
            """{"count":3,"name":"unary-latency","samples":[10,20],"missing":null}""",
            encodeJson(value),
        )
    }

    @Test
    fun encodesPrettyObjectsWithTwoSpaceIndent() {
        val value = JsonObject.of(
            "outer" to JsonObject.of("inner" to JsonLong(1)),
        )

        assertEquals(
            "{\n  \"outer\": {\n    \"inner\": 1\n  }\n}",
            encodeJson(value, pretty = true),
        )
    }

    @Test
    fun encodesEmptyCollections() {
        assertEquals("[]", encodeJson(JsonArray(emptyList())))
        assertEquals("{}", encodeJson(JsonObject(emptyList())))
    }
}
