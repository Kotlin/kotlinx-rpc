/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.benchmarks.client

/** One value of a JSON document. */
internal sealed interface JsonValue {
    fun encodeTo(builder: StringBuilder, pretty: Boolean, indent: Int)
}

internal class JsonString(private val value: String) : JsonValue {
    override fun encodeTo(builder: StringBuilder, pretty: Boolean, indent: Int) {
        builder.append('"')
        for (character in value) {
            when (character) {
                '"' -> builder.append("\\\"")
                '\\' -> builder.append("\\\\")
                '\n' -> builder.append("\\n")
                '\r' -> builder.append("\\r")
                '\t' -> builder.append("\\t")
                '\b' -> builder.append("\\b")
                '\u000C' -> builder.append("\\f")
                else ->
                    if (character < ' ') {
                        builder.append("\\u").append(character.code.toString(16).padStart(4, '0'))
                    } else {
                        builder.append(character)
                    }
            }
        }
        builder.append('"')
    }
}

internal class JsonLong(private val value: Long) : JsonValue {
    override fun encodeTo(builder: StringBuilder, pretty: Boolean, indent: Int) {
        builder.append(value)
    }
}

internal class JsonDouble(private val value: Double) : JsonValue {
    override fun encodeTo(builder: StringBuilder, pretty: Boolean, indent: Int) {
        if (value.isFinite()) {
            builder.append(value.toString())
        } else {
            builder.append("null")
        }
    }
}

internal class JsonBoolean(private val value: Boolean) : JsonValue {
    override fun encodeTo(builder: StringBuilder, pretty: Boolean, indent: Int) {
        builder.append(value)
    }
}

internal object JsonNullValue : JsonValue {
    override fun encodeTo(builder: StringBuilder, pretty: Boolean, indent: Int) {
        builder.append("null")
    }
}

internal class JsonArray(private val items: List<JsonValue>) : JsonValue {
    override fun encodeTo(builder: StringBuilder, pretty: Boolean, indent: Int) {
        if (items.isEmpty()) {
            builder.append("[]")
            return
        }
        builder.append('[')
        for ((index, item) in items.withIndex()) {
            if (index > 0) builder.append(',')
            item.encodeTo(builder, pretty, indent + 1)
        }
        builder.append(']')
    }

    companion object {
        fun ofNanos(values: LongArray): JsonArray = JsonArray(values.map(::JsonLong))

        fun ofNanos(values: List<Long>): JsonArray = JsonArray(values.map(::JsonLong))
    }
}

internal class JsonObject(private val members: List<Pair<String, JsonValue>>) : JsonValue {
    override fun encodeTo(builder: StringBuilder, pretty: Boolean, indent: Int) {
        if (members.isEmpty()) {
            builder.append("{}")
            return
        }
        builder.append('{')
        if (pretty) {
            appendPrettyMembers(builder, indent)
        } else {
            appendCompactMembers(builder)
        }
        builder.append('}')
    }

    private fun appendPrettyMembers(builder: StringBuilder, indent: Int) {
        for ((index, member) in members.withIndex()) {
            if (index > 0) builder.append(',')
            builder.append('\n').append("  ".repeat(indent + 1))
            JsonString(member.first).encodeTo(builder, pretty = false, indent = 0)
            builder.append(": ")
            member.second.encodeTo(builder, pretty = true, indent = indent + 1)
        }
        builder.append('\n').append("  ".repeat(indent))
    }

    private fun appendCompactMembers(builder: StringBuilder) {
        for ((index, member) in members.withIndex()) {
            if (index > 0) builder.append(',')
            JsonString(member.first).encodeTo(builder, pretty = false, indent = 0)
            builder.append(':')
            member.second.encodeTo(builder, pretty = false, indent = 0)
        }
    }

    companion object {
        fun of(vararg members: Pair<String, JsonValue>): JsonObject = JsonObject(members.toList())
    }
}

internal fun encodeJson(value: JsonValue, pretty: Boolean = false): String {
    val builder = StringBuilder()
    value.encodeTo(builder, pretty, indent = 0)
    return builder.toString()
}
