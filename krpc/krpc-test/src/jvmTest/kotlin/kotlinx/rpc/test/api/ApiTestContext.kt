/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.test.api

import kotlinx.rpc.test.api.ApiVersioningTest.Companion.CURRENT_CLASS_DUMPS_DIR
import kotlinx.rpc.test.api.ApiVersioningTest.Companion.LATEST_CLASS_DUMPS_DIR
import kotlinx.rpc.test.api.util.StringGoldContent
import kotlinx.rpc.test.api.util.checkGold
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
            latestDir = LATEST_CLASS_DUMPS_DIR,
            currentDir = CURRENT_CLASS_DUMPS_DIR,
            filename = clazz.simpleName!!,
            content = StringGoldContent(currentContent),
            parseGoldFile = { StringGoldContent(it) },
        )

        if (log != null) {
            fails.add("Protocol API class '${clazz.simpleName}' was changed. $log")
        }
    }
}
