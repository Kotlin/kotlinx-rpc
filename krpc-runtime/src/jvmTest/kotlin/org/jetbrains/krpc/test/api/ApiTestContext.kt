/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc.test.api

import org.jetbrains.krpc.test.api.ApiVersioningTest.Companion.CLASS_DUMPS_DIR
import org.jetbrains.krpc.test.api.util.StringGoldContent
import org.jetbrains.krpc.test.api.util.checkGold
import kotlin.reflect.KClass

class ApiTestContext {
    val fails = mutableListOf<String>()

    private val sampled = mutableSetOf<KClass<*>>()

    fun KClass<*>.isSampled(): Boolean {
        return sampled.contains(this)
    }

    fun KClass<*>.markSampled() {
        sampled.add(this)
    }

    fun compareAndDump(clazz: KClass<*>, currentContent: String) {
        sampled.add(clazz)

        val log = checkGold(
            fileDir = CLASS_DUMPS_DIR,
            filename = clazz.simpleName!!,
            content = StringGoldContent(currentContent),
            parseGoldFile = { StringGoldContent(it) },
        )

        if (log != null) {
            fails.add("Protocol API class '${clazz.simpleName}' was changed. $log")
        }
    }
}
