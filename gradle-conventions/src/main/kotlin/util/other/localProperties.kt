/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util.other

import org.gradle.api.Project
import org.gradle.kotlin.dsl.extra
import java.util.Properties
import java.util.concurrent.atomic.AtomicReference
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.io.path.inputStream

private val ref = AtomicReference<Properties>()

fun Project.localProperties(): Properties {
    if (ref.get() == null) {
        ref.compareAndSet(null, Properties().apply {
            val globalRootDir = extra["globalRootDir"] as String

            val filepath = Path(globalRootDir, "local.properties")

            if (!filepath.exists()) {
                return@apply
            }

            load(filepath.inputStream())
        })
    }

    return ref.get()
}
