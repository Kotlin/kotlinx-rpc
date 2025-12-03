/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protoc.android

import com.android.build.api.variant.Variant

internal enum class AndroidRootSourceSets(val stringValue: String) {
    // todo KMP?
    Main("main"),
    Test("test"),
    AndroidTest("androidTest"),
    TestFixtures("testFixtures"),
    ;

    override fun toString(): String = stringValue
}

internal fun Variant.sourceSets(rootName: AndroidRootSourceSets) = buildList {
    fun String.prefixed() = prefixed(rootName)

    add(rootName.stringValue)

    productFlavors.reversed().forEach { (_, flavorName) ->
        add(flavorName.prefixed())
    }

    if (productFlavors.size > 1) {
        val combinationName = productFlavors
            .joinToString("") { (_, name) -> name.replaceFirstChar { it.uppercase() } }
            .replaceFirstChar { it.lowercase() }

        add(combinationName.prefixed())
    }

    buildType?.let { add(it.prefixed()) }
    add(name.prefixed())
}

internal fun String.prefixed(rootName: AndroidRootSourceSets) = if (rootName == AndroidRootSourceSets.Main) {
    this
} else {
    "${rootName.stringValue}${this.replaceFirstChar { it.uppercase() }}"
}
