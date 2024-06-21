/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.test.api

import kotlinx.rpc.internal.IndexedEnum
import kotlinx.rpc.test.api.ApiVersioningTest.Companion.INDEXED_ENUM_DUMPS_DIR
import kotlinx.rpc.test.api.util.GoldComparable
import kotlinx.rpc.test.api.util.GoldComparisonResult
import kotlinx.rpc.test.api.util.GoldUtils
import kotlinx.rpc.test.api.util.checkGold
import kotlin.test.assertTrue
import kotlin.test.fail

inline fun <reified E> testEnum() where E : IndexedEnum, E : Enum<E> {
    testEnum(enumValues<E>(), E::class.simpleName!!) { EnumGoldContent.fromText(it) }
}

fun <E> testEnum(
    values: Array<E>,
    name: String,
    fromText: (String) -> EnumGoldContent<E>,
) where E : IndexedEnum, E : Enum<E> {
    val indexes = values.map { it.uniqueIndex }
    for (i in indexes) {
        assertTrue("All indexes should be in [0..65500] range") { i in 0..65500 }
    }
    val stats = mutableMapOf<Int, Int>()
    for (i in indexes) {
        stats.merge(i, 1) { a, b -> a + b }
    }

    stats.filterValues { it > 1 }.takeIf { it.isNotEmpty() }?.let {
        fail(
            "Indexes should be unique: ${
                it.entries.joinToString(", ") { (k, v) ->
                    "'$k' appears $v times"
                }
            }"
        )
    }

    val log = checkGold(
        latestDir = INDEXED_ENUM_DUMPS_DIR,
        currentDir = INDEXED_ENUM_DUMPS_DIR,
        filename = name,
        content = EnumGoldContent(values.toSet()),
        parseGoldFile = fromText,
    ) ?: return

    fail(log)
}

class EnumGoldContent<E>(private val values: Set<E>) :
    GoldComparable<EnumGoldContent<E>> where E : IndexedEnum, E : Enum<E> {
    override fun compare(other: EnumGoldContent<E>): GoldComparisonResult {
        val diff = values - other.values
        return if (diff.isEmpty()) {
            GoldComparisonResult.Ok
        } else {
            GoldComparisonResult.Failure("New enum entries added: ${diff.joinToString { it.name }}")
        }
    }

    override fun dump(): String {
        return values.joinToString(GoldUtils.NewLine) {
            "${it.name} - ${it.uniqueIndex}"
        }
    }

    companion object {
        inline fun <reified E> fromText(text: String): EnumGoldContent<E> where E : IndexedEnum, E : Enum<E> {
            return fromText(text, E::class.simpleName!!) { enumValueOf<E>(it) }
        }

        fun <E> fromText(
            text: String,
            enumName: String,
            enumValueOf: (String) -> E
        ): EnumGoldContent<E> where E : IndexedEnum, E : Enum<E> {
            val values = text.split(GoldUtils.NewLine).map {
                val (name, index) = it.split(" - ")
                val enum = try {
                    enumValueOf(name)
                } catch (e: IllegalArgumentException) {
                    fail(
                        "Failed to read enum dump: $name entry is not present in $enumName. " +
                                "Probably it was deleted, which is forbidden. " +
                                "Mark values as deprecated, but never delete them." +
                                "Original exception: ${e.message}"
                    )
                }

                if (enum.uniqueIndex != index.toInt()) {
                    fail("uniqueIndex for $name entry of $enumName has changed, which is forbidden.")
                }

                enum
            }.toSet()

            return EnumGoldContent(values)
        }
    }
}
